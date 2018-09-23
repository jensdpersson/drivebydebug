package org.drivebydebug;

import java.io.File;

public class Main {
    
    public static void main(String[] argv){
        System.out.println("Starting up...");
        
        if(argv.length < 1){
            die("Must specify config file");
        }
        File file = new File(argv[0]);
        if(!file.exists()){
            die("Config file " + file.getAbsolutePath() + " does not exist");
        }

        try {
            new Configurator(new EventSubscriber(new StdoutLogger())).watch(file);
        } catch(Exception ex){
            die(ex);
        }
    }

    private static void die(String msg){
        System.out.println(msg);
        System.exit(1);
    }

    private static void die(Exception ex){
        System.out.println(ex.getMessage());
        ex.printStackTrace(System.out);
        System.exit(1);
    }
}