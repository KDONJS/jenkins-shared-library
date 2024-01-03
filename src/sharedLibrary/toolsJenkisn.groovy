package sharedLibrary

import hudson.util.Secret

class toolsJenkisn implements Serializable{

    def steps
    def script

    toolsJenkisn(steps, script) {
        this.steps = steps
        this.script = script
    }

    def printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    def call(String name) {

            printMessage("No se pudo conectar a la maquina remota")
    }

    def analisisCodigo(){
        printMessage("Esta es una prueba")
    }

    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

    // Método para obtener credenciales y establecer conexión SSH
    def connectSSH(String credentialsId, String remoteHost) {
        def username
        def privateKey

        this.printMessage("Id de credencial: ${credentialsId} host remoto: ${remoteHost}")

        // Obtener credenciales desde Jenkins Vault
        script.withCredentials([[$class: 'SSHUserPrivateKeyBinding', credentialsId: credentialsId, variable: 'SSH_KEY']]) {
            username = steps.sh(script: "echo \$SSH_USER", returnStdout: true).trim()
            privateKey = steps.sh(script: "echo \$SSH_KEY", returnStdout: true).trim()
        }

        // Establecer conexión SSH y ejecutar comandos
        try {
            def remote = [:]
            remote.name = 'remoteHost'
            remote.host = remoteHost
            remote.user = username
            remote.allowAnyHosts = true
            remote.identityFile = privateKey
            steps.sshagent(credentials: [credentialsId]) {
                steps.sh "ssh -o StrictHostKeyChecking=no ${username}@${remoteHost} 'echo connected'"
            }
            printMessage("Conexión SSH establecida con éxito")
        } catch (Exception e) {
            printMessage("Error al conectar: ${e.message}")
            notifySlack("Error en conexión SSH: ${e.message}")
        }
    }

}
