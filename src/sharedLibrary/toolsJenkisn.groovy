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
        //conectarme mediante ssh a la maquina remota usando sshagent
            printMessage("No se pudo conectar a la maquina remota")
    }

    def analisisCodigo(){
        printMessage("Esta es una prueba")
    }

    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
