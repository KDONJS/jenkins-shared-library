package sharedLibrary

import java.io.Serializable
import hudson.util.Secret

class toolsJenkisn implements Serializable{

    def steps
    def script
    def env

    toolsJenkisn(steps, script, env) {
        this.steps = steps
        this.script = script
        this.env = env
    }

    def printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    def callToActions(String name) {
        steps.sh "gcloud components list"
    }

}
