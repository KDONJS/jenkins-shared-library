package sharedLibrary

class baseUtil{

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