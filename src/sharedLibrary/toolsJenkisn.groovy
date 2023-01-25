package sharedLibrary

class toolsJenkisn extends baseUtil{

    def call(String name) {
        this.printMessage("Hello ${name}")
    }
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        this.script.steps.echo "[KDON-DevSecOps] ${message}"
    }

}