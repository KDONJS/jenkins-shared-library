package sharedLibrary

import hudson.util.Secret

class toolsJenkisn implements Serializable{

    def steps
    def script

    toolsJenkisn(steps) {
        this.steps = steps
    }

    toolsJenkisn(script) {
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

    def connectSSH(String credentialsId, String remoteHost) {
        script.withCredentials([script[$class: 'SSHUserPrivateKeyBinding', credentialsId: credentialsId, variable: 'SSH_KEY']]) {
            def username = script.sh(script: "echo \$SSH_USER", returnStdout: true).trim()
            def privateKey = script.sh(script: "echo \$SSH_KEY", returnStdout: true).trim()

            try {
                script.sshagent(credentials: [credentialsId]) {
                    script.sh "ssh -o StrictHostKeyChecking=no ${username}@${remoteHost} 'echo connected'"
                }
                printMessage("Conexión SSH establecida con éxito")
            } catch (Exception e) {
                printMessage("Error al conectar: ${e.message}")
                notifySlack("Error en conexión SSH: ${e.message}")
            }
        }
    }

}
