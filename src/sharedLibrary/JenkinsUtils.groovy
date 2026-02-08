package sharedLibrary

import groovy.json.JsonSlurper
/**
 * Helper reutilizable para pipelines Jenkins (shared library).
 *
 * Nota: en Jenkins, las clases en `src/` deben vivir en archivos cuyo nombre coincida con la clase pública.
 */
class JenkinsUtils implements Serializable {
    private static final long serialVersionUID = 1L

    def steps
    def script

    JenkinsUtils(def steps) {
        this(steps, steps)
    }

    JenkinsUtils(def steps, def script) {
        this.steps = steps
        this.script = script
    }

    def echo(String message) {
        steps.echo(message)
    }

    void printMessage(String message) {
        this.echo("[KDON-DevSecOps]: ${message}")
    }

    /**
     * Ejecuta un comando local en el agente Jenkins.
     *
     * @param cmd comando a ejecutar
     * @param returnStdout si true, devuelve stdout (String)
     * @param returnStatus si true, devuelve exit code (int)
     */
    def shCommand(String cmd, boolean returnStdout = false, boolean returnStatus = false) {
        if (returnStdout && returnStatus) {
            throw new IllegalArgumentException('No se puede usar returnStdout y returnStatus a la vez')
        }
        if (returnStdout) {
            return script.sh(script: cmd, returnStdout: true).trim()
        }
        if (returnStatus) {
            return script.sh(script: cmd, returnStatus: true) as int
        }
        steps.sh(cmd)
    }

    def withCredential(String id, String type = 'string', Closure body) {

        def bindings = []

        switch(type) {
            case 'string':
                bindings << steps.string(credentialsId: id, variable: 'SECRET')
                break

            case 'usernamePassword':
                bindings << steps.usernamePassword(
                        credentialsId: id,
                        usernameVariable: 'CRED_USER',
                        passwordVariable: 'CRED_PASS'
                )
                break

            case 'ssh':
                bindings << steps.sshUserPrivateKey(
                        credentialsId: id,
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                )
                break

            case 'file':
                bindings << steps.file(credentialsId: id, variable: 'CRED_FILE')
                break

            case 'github':
                bindings << steps.usernamePassword(
                        credentialsId: id,
                        usernameVariable: 'GITHUB_USER',
                        passwordVariable: 'GITHUB_TOKEN'
                )
                break

            default:
                error "Tipo de credencial no soportado: ${type}"
        }

        steps.withCredentials(bindings) {
            body()
        }
    }

    def httpGet(String url, Map headers = [:]) {
        def connection = new URL(url).openConnection()
        connection.setRequestMethod("GET")

        headers.each { k, v ->
            connection.setRequestProperty(k, v)
        }

        connection.connect()

        def status = connection.responseCode
        def stream = (status >= 200 && status < 300) ? connection.inputStream : connection.errorStream
        def response = stream.text

        return [status: status, body: response]
    }

    /**
     * Valida que existan comandos en el agente Jenkins.
     * Lanza error con un mensaje claro si falta algo (evita exit 127 misterioso).
     */
    void assertCommandsAvailable(List<String> commands) {
        commands.each { c ->
            int status = shCommand("command -v ${c} >/dev/null 2>&1", false, true)
            if (status != 0) {
                throw new RuntimeException("⚠️ Comando requerido NO encontrado en el agente Jenkins: '${c}'\n" +
                        "Solución: instala 'openssh-client' en la imagen del agente o usa un agente con ssh/scp.\n" +
                        "  - Debian/Ubuntu: apt-get install -y openssh-client\n" +
                        "  - Alpine: apk add --no-cache openssh-client")
            }
        }
    }

    /**
     * Preflight check para operaciones remotas (ssh/scp).
     */
    void sshPreflight() {
        printMessage("Validando que ssh/scp estén disponibles en el agente...")
        assertCommandsAvailable(['ssh', 'scp'])
    }

    /** Ejecuta un bloque dentro de sshagent. */
    def withSshAgent(String credentialId, Closure body) {
        steps.sshagent([credentialId]) {
            body()
        }
    }

    /**
     * Ejecuta comandos remotos via SSH (multi-línea soportado).
     *
     * @param preflight si true (default), valida que ssh exista antes de ejecutar
     */
    def ssh(String credentialId,
            String host,
            String remoteCommand,
            String user = 'kdon',
            int port = 22,
            boolean strictHostKeyChecking = false,
            String extraSshOptions = '',
            boolean preflight = true) {

        if (preflight) {
            sshPreflight()
        }

        def strictOpt = strictHostKeyChecking ? '' : '-o StrictHostKeyChecking=no'
        def portOpt = port ? "-p ${port}" : ''
        def options = [strictOpt, extraSshOptions, portOpt].findAll { it?.trim() }.join(' ')

        return withSshAgent(credentialId) {
            steps.sh("""#!/bin/sh
set -eu
ssh ${options} ${user}@${host} 'sh -se' <<'__KDON_REMOTE__'
${remoteCommand}
__KDON_REMOTE__
""")
        }
    }

    /**
     * Copia un archivo o directorio al host remoto usando scp.
     *
     * @param preflight si true (default), valida que scp exista antes de ejecutar
     */
    def scpTo(String credentialId,
              String source,
              String host,
              String destination,
              String user = 'kdon',
              int port = 22,
              boolean recursive = false,
              boolean strictHostKeyChecking = false,
              String extraScpOptions = '',
              boolean preflight = true) {

        if (preflight) {
            sshPreflight()
        }

        def strictOpt = strictHostKeyChecking ? '' : '-o StrictHostKeyChecking=no'
        def portOpt = port ? "-P ${port}" : ''
        def recOpt = recursive ? '-r' : ''
        def options = [strictOpt, extraScpOptions, portOpt, recOpt].findAll { it?.trim() }.join(' ')

        return withSshAgent(credentialId) {
            steps.sh("""#!/bin/sh
set -eu
scp ${options} ${source} ${user}@${host}:${destination}
""")
        }
    }

    def loadMsConfig(String msName, String env = 'dev') {
        def jsonText = steps.libraryResource("ms/properties-ms.json")
        def json = new JsonSlurper().parseText(jsonText)

        if (!json.containsKey(msName)) {
            throw new RuntimeException("Microservicio '${msName}' no existe en properties-ms.json")
        }

        def ms = json[msName]

        if (!ms.containsKey(env)) {
            throw new RuntimeException("El entorno '${env}' no existe para '${msName}'")
        }

        def data = [
                MS_NAME          : msName,
                MS_SERVICE_NAME  : ms.serviceName,
                MS_CREDENTIAL_ID : ms.idCredential,
                MS_DNS           : ms[env].dns
        ]

        // Exportar como variables de entorno Jenkins
        data.each { k, v ->
            script.env[k] = v.toString()
        }

        printMessage("Configuración cargada para ${msName} (${env})")
        data.each { k,v -> printMessage("${k}=${v}") }

        return data
    }

}
