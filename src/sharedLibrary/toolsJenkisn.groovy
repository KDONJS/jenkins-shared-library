package sharedLibrary

class toolsJenkisn extends baseUtil{

    def call(String name) {
        this.printMessage("Hello ${name}")
    }

}