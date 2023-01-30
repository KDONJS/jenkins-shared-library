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
        // String taxonomia = steps.libraryResource(encoding: 'UTF-8', resource: 'scripts/taxonomia.sh')

        // steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
        //     steps.writeFile file: 'taxonomia.sh', text: taxonomia
        //     steps.sh "chmod +x taxonomia.sh"
        //     steps.sh "./taxonomia.sh ${name}"
        // }

        def fileScripZip = new File(libraryResource('scripts').toString())
        def zipFile = new File("${steps.env.WORKSPACE}/pipelineGroovy@tmp", "scripts.zip")

        zipFile.delete()

        def fos = new FileOutputStream(zipFile)
        def zos = new ZipOutputStream(fos)

        fileScripZip.eachFileRecurse{ file ->
             if (!file.isDirectory()){
                def entry = new ZipEntry(file.name)
                zos.putNextEntry(entry)
                def fis = new FileInputStream(file)
                def buffer = new byte[1024]
                int length
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length)
                }
                zos.closeEntry()
                fis.close()
             }
        }

        zos.close()
        fos.close()

        steps.dir("${steps.env.WORKSPACE}/pipelineGroovy@tmp") {
            steps.sh "unzip scripts.zip"
            steps.sh "chmod +x scripts/taxonomia.sh"
            steps.sh "./scripts/taxonomia.sh ${name}"
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
