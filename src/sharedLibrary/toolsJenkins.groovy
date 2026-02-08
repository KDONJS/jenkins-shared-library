package sharedLibrary

import sharedLibrary.JenkinsUtils
import java.io.Serializable
import groovy.json.JsonSlurper

class toolsJenkins implements Serializable {

    def steps
    def script
    JenkinsUtils utils

    // Defaults (puedes moverlos a parámetros si quieres hacerlo 100% configurable)
    private String defaultSshCredentialId = 'ssh-host-lim'
    private String defaultSshHost = ''
    private String defaultSshUser = ''
    private final String defaultProject = 'lucqa-it'
    String msName = ''
    String msEnv = ''

    // Constructor compatible: acepta solo steps
    toolsJenkins(def steps) {
        this(steps, steps)
    }

    // Constructor principal: acepta steps y script
    toolsJenkins(def steps, def script) {
        this.steps = steps
        this.script = script
        this.utils = new JenkinsUtils(steps, script)
    }

    // Constructor alternativo: aceptar una instancia de JenkinsUtils
    toolsJenkins(JenkinsUtils utils) {
        this.utils = utils
        this.steps = utils.steps
        this.script = utils.script
    }

    public void setMsName(String msName) {
        this.msName = msName
    }

    public void setMsEnv(String msEnv) {
        this.msEnv = msEnv
    }

    public void setDefaultSshHost() {
        if (msEnv == 'prod') {
            this.defaultSshHost = "158.69.208.170"
        } else {
            this.defaultSshHost = "192.168.10.10"
        }

        utils.printMessage("Host SSH configurado a ${this.defaultSshHost} para entorno ${msEnv}")
    }

    public void setDefaultSshUser() {
        if (msEnv == 'prod') {
            this.defaultSshUser = "ubuntu"
        } else {
            this.defaultSshUser = "kdon"
        }
    }

    public void setDefaultSshCredentialId() {
        if (msEnv == 'prod') {
            this.defaultSshCredentialId = "ssh-host-prd"
        } else {
            this.defaultSshCredentialId = "ssh-host-lim"
        }
    }

    public void setupFromProperties() {
        def ms = this.msName ?: script.params.MS_NAME

        if (!ms) {
            throw new RuntimeException("MS_NAME no definido")
        }

        def cfg = utils.loadMsConfig(ms, msEnv)

        this.msName = cfg.MS_SERVICE_NAME
    }

    public void setup( String ms, String env='dev') {
        utils.printMessage("Configurando herramienta para ${ms} en entorno ${env}")
        this.setMsEnv(env)
        this.setDefaultSshCredentialId()
        this.setDefaultSshUser()
        this.setDefaultSshHost()
        this.setMsName(ms)
        this.setupFromProperties()
        this.generateEnvFile()
    }

    public String lisPackagesGhcr() {

        utils.withCredential("github-ghcr-token", "usernamePassword") {

            def token = script.env.CRED_PASS

            def res = utils.httpGet(
                    "https://api.github.com/orgs/${defaultProject}/packages/container/${msName}/versions",
                    [
                            "Authorization": "Bearer ${token}",
                            "Accept": "application/vnd.github+json"
                    ]
            )

            utils.printMessage("HTTP ${res.status}")

            if (res.status != 200) {
                utils.printMessage("Error GHCR: ${res.body}")
                return
            }

            def json = new JsonSlurper().parseText(res.body)

            json.each { v ->
                def realTags = (v.metadata.container.tags ?: []).findAll { it != 'latest' }

                if (realTags) {
                    utils.printMessage("ID: ${v.id}")
                    utils.printMessage("Created at: ${v.created_at}")
                    utils.printMessage("Tags: ${realTags.join(', ')}")
                }
            }
        }
    }

    public String printUltimaVersionGhcr() {

        return utils.withCredential("github-ghcr-token", "usernamePassword") {

            def token = script.env.CRED_PASS

            def res = utils.httpGet(
                    "https://api.github.com/orgs/${defaultProject}/packages/container/${msName}/versions",
                    [
                            "Authorization": "Bearer ${token}",
                            "Accept": "application/vnd.github+json"
                    ]
            )

            if (res.status != 200) {
                throw new RuntimeException("GHCR API error ${res.status}: ${res.body}")
            }

            def json = new JsonSlurper().parseText(res.body)

            for (v in json) {
                def realTags = (v.metadata.container.tags ?: []).findAll { it != 'latest' }
                if (realTags) {
                    return realTags[0]
                }
            }

            throw new RuntimeException("No se encontraron versiones válidas para ${msName}")
        }
    }

    public void generateEnvFile(){
        utils.withCredential(script.env.MS_CREDENTIAL_ID, "file") {
            def credFilePath = script.env.CRED_FILE
            def envContent = steps.readFile(file: credFilePath)
            steps.writeFile(file: "${script.env.WORKSPACE}/.env", text: envContent)
            steps.sh("cat ${script.env.WORKSPACE}/.env" )
        }
    }

    public void contructorDockerComposeFile() {

        utils.printMessage("Construyendo docker-compose para ${msName}")

        String msVersion = this.printUltimaVersionGhcr()
        utils.printMessage("Versión seleccionada: ${msVersion}")

        def env = this.msEnv
        String templateName = env == 'prod'
                ? "ms/docker-compose.template.prd.yml"
                : "ms/docker-compose.template.yml"

        String dockerCompose = steps.libraryResource(templateName).stripIndent()
        String bashContent   = steps.libraryResource("utils/sh/creator-compose.sh")

        def composePath = "${script.env.WORKSPACE}/docker-compose.template.yml"
        def scriptPath  = "${script.env.WORKSPACE}/creator-compose.sh"

        steps.writeFile(file: composePath, text: dockerCompose)
        steps.writeFile(file: scriptPath,  text: bashContent)
        steps.sh("chmod 750 ${scriptPath}")

        steps.sh("""
        set -e
        ${scriptPath} \
            ${script.env.MS_NAME} \
            ${msVersion} \
            .env \
            ${script.env.MS_DNS} \
            web \
            8080
        """)

        steps.sh("rm -f ${composePath}")

        utils.printMessage("docker-compose generado correctamente")
    }

    public void createDataBaseService(String msDataServiceName) {
        utils.printMessage("Creando servicio de base de datos para ${msName}")

        String rutaScript = "/home/kdon/servicios-cross/create-db"
        String dblocal = msDataServiceName.replaceAll('-', '_') + "_user"
        String dbName = msDataServiceName.replaceAll('-', '_') + "_db"

        utils.ssh(defaultSshCredentialId, defaultSshHost, """
            cd ${rutaScript}
            ./createdb.sh ${dblocal} ${dbName}
        """, defaultSshUser)
    }

    public void deployDockerCompose() {

        def env = this.msEnv

        String rutaDeploy = (env == 'prod')
                ? "/opt/${msName}"
                : "/opt/ms/${msName}"

        utils.printMessage("Desplegando ${msName} en ${defaultSshHost} (${env})")
        utils.printMessage("Ruta destino: ${rutaDeploy}")

        utils.ssh(defaultSshCredentialId, defaultSshHost, """
            command -v docker >/dev/null || { echo 'Docker no instalado'; exit 1; }
            command -v docker compose >/dev/null || { echo 'Docker Compose no disponible'; exit 1; }
        """, defaultSshUser)

        utils.ssh(defaultSshCredentialId, defaultSshHost, """
            cd ${rutaDeploy}
            if [ -f docker-compose.yml ]; then
                mkdir -p backups
                ts=\$(date +%Y%m%d%H%M%S)
                cp docker-compose.yml backups/docker-compose-\$ts.yml
                [ -f .env ] && cp .env backups/env-\$ts || true
            fi
        """, defaultSshUser)

        utils.scpTo(defaultSshCredentialId,
                "${script.env.WORKSPACE}/docker-compose.yml",
                defaultSshHost,
                "${rutaDeploy}/docker-compose.yml",
                defaultSshUser
        )

        utils.scpTo(defaultSshCredentialId,
                "${script.env.WORKSPACE}/.env",
                defaultSshHost,
                "${rutaDeploy}/.env",
                defaultSshUser
        )

        utils.ssh(defaultSshCredentialId, defaultSshHost, """
            set -e
            cd ${rutaDeploy}
    
            docker compose pull
            docker compose up -d
    
            docker compose ps
        """, defaultSshUser)

        utils.ssh(defaultSshCredentialId, defaultSshHost, """
            cd ${rutaDeploy}
            rm -f .env
        """, defaultSshUser)

        utils.printMessage("Deploy de ${msName} completado correctamente")
    }
}
