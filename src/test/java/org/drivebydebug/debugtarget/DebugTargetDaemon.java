package org.drivebydebug.debugtarget;

import java.util.Timer;
import java.util.TimerTask;

public class DebugTargetDaemon {

    public static void main(String[] argv){
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run() { 
                System.out.println("About to run...");
                Long longVariable = new Long(23);
                System.out.println("Running...");
                longVariable = new Long(51);
                System.out.println("Ran...");
            }
        };
        timer.schedule(task, 10, 10);
    }


}