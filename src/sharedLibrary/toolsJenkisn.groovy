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
        String taxonomia = steps.libraryResource(encoding: 'UTF-8', resource: 'sharedLibrary/scripts/taxonomia.sh')

        steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
            steps.writeFile file: 'taxonomia.sh', text: taxonomia
            steps.sh "chmod +x taxonomia.sh"
            steps.sh "./taxonomia.sh ${name}"
        }
    }

    //metodo copiado de archivos de la libreria shared-library al workspace de jenkins 
    def copyFiles(String source, String destination) {
        steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
            steps.sh "cp -r ${source} ${destination}"
        }
    }
    
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
