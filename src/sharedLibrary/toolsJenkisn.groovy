package sharedLibrary

import java.io.Serializable
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

    def callToActions(String name) {
        steps.sh "ls -la"
        
        this.printMessage("${script.env.WORKSPACE}")
    }

    public void executeSh() {
        String reportExample = steps.libraryResource(encoding: 'utf-8', resource: "yorlin/example.sh").stripIndent()

        steps.writeFile(
            file: "${script.env.WORKSPACE}/example.sh", //aca se crea el archivo
            text: reportExample //aca se escribe el archivo
        )

        steps.sh "chmod 750 *.sh"
        steps.sh "./example.sh ${script.env.BUILD_NUMBER} ${script.env.JOB_BASE_NAME}"
    }
}
