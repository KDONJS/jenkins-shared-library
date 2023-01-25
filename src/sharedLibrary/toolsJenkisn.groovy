package sharedLibrary

class toolsJenkisn extends baseUtil{

    def call(String name) {
        sh script: 'ls', returnStatus: true
    }
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
