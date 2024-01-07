package sharedLibrary

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
        this.printMessage("${env.WORKSPACE}")
    }

}
