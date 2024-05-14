package sharedLibrary

import java.io.Serializable
import hudson.util.Secret

class toolsJenkins implements Serializable{

    def steps
    def script

    toolsJenkins(steps, script) {
        this.steps = steps
        this.script = script
    }

    private void printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    public void verifyPython() {

        def pythonCommand = "python"

        try {
            steps.sh 'python --version'
        } catch (Exception e) {
            pythonCommand = "python3"
            steps.sh 'python3 --version'
        }

        script.env.PYTHON_COMMAND = pythonCommand
    }

    public void executePy() {
        
        this.printMessage("Se inicia con la ejecucion de archivos py")

        String mainPy = steps.libraryResource(encoding: 'utf-8', resource: "dataLake/py/main.py").stripIndent()

        steps.writeFile(
            file: "${script.env.WORKSPACE}/main.py", //aca se crea el archivo
            text: mainPy //aca se escribe el archivo
        )

        steps.sh "chmod 750 *.py"
        steps.sh "python3 ${script.env.BUILD_NUMBER}/main.py"

    }

    public void executeSh() {

        this.printMessage("Se inicia con la ejecucion de archivos sh")

        String reportExample = steps.libraryResource(encoding: 'utf-8', resource: "dataLake/sh/local.sh").stripIndent()

        steps.writeFile(
            file: "${script.env.WORKSPACE}/example.sh", //aca se crea el archivo
            text: reportExample //aca se escribe el archivo
        )

        steps.sh "chmod 750 *.sh"
        steps.sh "./example.sh ${script.env.BUILD_NUMBER} ${script.env.JOB_BASE_NAME}"
    }
}
