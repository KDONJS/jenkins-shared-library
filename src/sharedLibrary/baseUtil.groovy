package sharedLibrary

abstract class baseUtil {

    protected script;
    protected steps;

    protected baseUtil(){}

    protected baseUtil(script, String type= '') {

        this.script = script
        this.type = type

        //metodo de captura de mensajes
        def printMessage (String message) {
            this.script.echo "[KDON-DevSecOps] ${message}"
        }
    }
}