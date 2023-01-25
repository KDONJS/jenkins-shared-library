package sharedLibrary

class baseUtil extends Script{

    private Script script

    def setScript(Script script) {
        this.script = script
    }

    def printMessage(String message) {
        this.script.echo "[KDON-DevSecOps] ${message}"
    }
}

class Script {
    void echo(String message) {
        println(message)
    }
}