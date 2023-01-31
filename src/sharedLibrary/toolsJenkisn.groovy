package sharedLibrary

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import hudson.plugins.sshslaves.SSHLaunchConfig;
import hudson.util.StreamTaskListener;



class toolsJenkisn implements Serializable{

    def steps

    toolsJenkisn(steps) {
        this.steps = steps
    }

    def printMessage(String message) {
        steps.echo "[KDON-DevSecOps]: ${message}"
    }

    def call(String name) {
        //conectarme mediante ssh a la maquina remota usando sshagent
        def SSHLaunchConfig = new SSHLaunchConfig("127.0.0.1", "yorlin" , 2222)
        SSHLaunchConfig.useAgent=true

        def TaskListener = new StreamTaskListener(System.out)
        def ssh = sshLaunchConfig.getSessions(TaskListener,TaskListener)
        ssh.connect()

        //si la conexion es exitosa ejecutar el comando
        if(ssh.isConnected()){
            ssh.exec("echo 'Hola mundo' > /tmp/hola.txt")
        }else{
            printMessage("No se pudo conectar a la maquina remota")
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
