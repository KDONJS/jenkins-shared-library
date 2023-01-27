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
        steps.sh "cd .. && cd pipelineGroovy@tmp && ls -la"

        //mostrar el directorio donde se encuentra la libreria compartida
        this.printMessage("Directorio de la libreria compartida: ${steps.env.WORKSPACE}")
        //ingresar a un directorio
        steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
            //ejecutar comando de linux en jenkins
            steps.sh "cd .. && ls -la"
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
