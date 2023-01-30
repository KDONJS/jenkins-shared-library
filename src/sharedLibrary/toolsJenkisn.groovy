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
        //leer cuantos archivos hay en el directorio
        def files = new File(steps.libraryResource('dataLake').listFiles()) {
            steps.echo "Cantidad de archivos: ${files.size()}"
        }
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
