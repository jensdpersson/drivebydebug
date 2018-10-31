package org.drivebydebug;

import java.io.File;

public class Main {
    
    public static void main(String[] argv){
        Io.echo("Starting up...");
        
        if(argv.length < 1){
            Io.die("Must specify config file");
        }
        File file = new File(argv[0]);
        if(!file.exists()){
            Io.die("Config file " + file.getAbsolutePath() + " does not exist");
        }

        try {
            new Configurator(new EventPump(new StdoutLogger())).watch(file);
        } catch(Exception ex){
            Io.die(ex);
        }
    }    
}