package sharedLibrary

abstract baseUtil implements Serializable {

    protected script;
    protected steps;

    protected baseUtil(){}

    protected baseUtil(script, String type= '') {

        this.script = script
        this.type = type

        protected void printMessage(String message){
            this.script.steps.echo "[KDON-DevSecOps] ${message}"
        }
    }
}