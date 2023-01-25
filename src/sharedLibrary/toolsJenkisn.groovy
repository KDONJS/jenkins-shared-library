package sharedLibrary

class toolsJenkisn extends baseUtil{

    def call(String name) {
        steps.echo "Hello ${name}"
    }
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
