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

            steps.sh "ls -l"
            steps.sh "mkdir jenkins-file && ls"
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
        try {
            steps.sshagent(credentials: [credentialsId]) {
                steps.sh "ssh -o StrictHostKeyChecking=no -l \$SSH_USER ${remoteHost} 'echo connected'"
            }
            printMessage("Conexión SSH establecida con éxito")
        } catch (Exception e) {
            printMessage("Error al conectar: ${e.message}")
            notifySlack("Error en conexión SSH: ${e.message}")
        }
    }

}
