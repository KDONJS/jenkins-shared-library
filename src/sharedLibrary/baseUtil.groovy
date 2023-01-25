package sharedLibrary

class baseUtil extends helper{

    private Script script

    def setScript(Script script) {
        this.script = script
    }

    def printMessage(String message) {
        this.script.echo "[KDON-DevSecOps] ${message}"
    }
}