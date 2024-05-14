package sharedLibrary

import java.io.Serializable
import hudson.util.Secret

class toolsJenkins implements Serializable{

    def steps
    def script

    toolsJenkisn(steps, script) {
        this.steps = steps
        this.script = script
    }

    public void printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    public void executeSh() {

        this.printMessage("Se inicia con la ejecucion de archivos sh")

        String reportExample = steps.libraryResource(encoding: 'utf-8', resource: "example/example.sh").stripIndent()

        steps.writeFile(
            file: "${script.env.WORKSPACE}/example.sh", //aca se crea el archivo
            text: reportExample //aca se escribe el archivo
        )

        steps.sh "chmod 750 *.sh"
        steps.sh "./example.sh ${script.env.BUILD_NUMBER} ${script.env.JOB_BASE_NAME}"
    }
}
