package org.drivebydebug;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Map;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class Configurator {

    private Configurable configurable;
    private long lastModified;
    private Timer timer = new Timer();

    public Configurator(Configurable configurable){
        this.configurable = configurable;
    }

    public void watch(final File file){
        timer.schedule(new TimerTask(){
            public void run(){
                try {
                    if(Configurator.this.lastModified < file.lastModified()){
                        System.out.println("Configuration changed, reloading");
                        parse(file);                        
                        Configurator.this.lastModified = file.lastModified();
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }, 10000L, 10000L);        
    }

    public void stop(){
        timer.cancel();
    }

    public void parse(File file) throws Exception {
        FileReader fred = new FileReader(file);
        BufferedReader bread = new BufferedReader(fred);
        String line = null;
        Configuration config = new Configuration();
        HasProps current = config;
        int lineNumber = 0;
        while((line = bread.readLine()) != null){
            lineNumber++;
            if(line.startsWith("break on ")){
                String[] fileAndLine = line.substring("break on ".length()).split(":");
                int lineNo = 0;
                try {
                    lineNo = Integer.parseInt(fileAndLine[1]);
                } catch (NumberFormatException nex){
                    throw new IllegalArgumentException(
                        "Wrong file:line specification [" + fileAndLine[0] + ":" + fileAndLine[1]+ "]");
                }
                BreakpointEventSubscription subscription = 
                    new BreakpointEventSubscription(fileAndLine[0], lineNo);
                config.subscription(subscription);
                current = subscription;
            } else if(!line.startsWith("#") && line.length() != 0){
                int colonIndex = line.indexOf("=");
                if(colonIndex == -1 || colonIndex == line.length()-1){
                    throw new IllegalArgumentException("Property on line " 
                    + lineNumber + " has no value");
                } 
                current.prop(line.substring(0, colonIndex), line.substring(colonIndex+1));
            }
        }
        configurable.configure(config);
    }

} 