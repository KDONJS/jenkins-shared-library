package sharedLibrary

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;



class toolsJenkisn implements Serializable{

    def steps

    toolsJenkisn(steps) {
        this.steps = steps
    }

    def printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    def call(String name) {
        //conectarme mediante ssh a la maquina remota con el usuario y clave de la maquina remota con el puerto 2222
        steps.sh "sshpass -p '240702' ssh -p 2222 yorlin@127.0.0.1"
        steps.sh "ls -la"
        //copiar el archivo de la ruta local a la ruta remota
    }

    //metodo copiado de archivos de la libreria shared-library al workspace de jenkins 
    def copyFiles(String source, String destination) {
        steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
            steps.sh "cp -r ${source} ${destination}"
        }
    }
    
    
    //metodo de caputura de errores
    def notifySlack (String message) {
        print "[KDON-DevSecOps] ${message}"
    }

}
