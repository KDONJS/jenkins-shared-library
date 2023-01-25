package sharedLibrary

class toolsJenkisn implements Serializable{

    def steps

    toolsJenkisn(steps) {
        this.steps = steps
    }

    def printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    def call(String name) {
        this.printMessage("Hola ${name}")
        this.printMessage("Nombre del job: ${steps.env.JOB_NAME}")
        //ejecutar comando de linux en jenkins
        steps.sh "cd .. && ls -la"
    }
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
