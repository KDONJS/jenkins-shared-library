package sharedLibrary

abstract class baseUtil {

    protected void printMessage(String message){
        this.script.steps.echo "[KDON-DevSecOps] ${message}"
    }
}