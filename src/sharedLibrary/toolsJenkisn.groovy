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

    def executeFuncion(){
        this.printMessage()
    }

}
