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
         //copiar todos los archivos de la libreria
        def directory = new File(steps.libraryResource('dataLake').toString())
        
        for (file in directory.listFiles()) {
            copyFiles(file.toString(), steps.env.WORKSPACE)
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
