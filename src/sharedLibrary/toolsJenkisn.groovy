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
        String report = steps.libraryResource(encoding: 'utf-8', resource: "datalake/local.sh").stripIndent()

        steps.writeFile(
            file: "${env.WORKSPACE}/local.sh",
            text: report
        )

        steps.sh "chmod 750 local.sh"
        steps.sh "./local.sh"
    }
}
