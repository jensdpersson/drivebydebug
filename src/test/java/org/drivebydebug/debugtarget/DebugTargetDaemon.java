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
                String stringVariable = "apa";
                SomeObject objectVariable = new SomeObject();
                System.out.println("Ran... ");
                System.exit(0);
            }
        };
        timer.schedule(task, 1000);
    }

    public static class SomeObject {
        public String someField = "valueOfThatField";
        public AnotherObject anotherField = new AnotherObject();
        public String someMethod(){
            return "returnValueOfThatMethod";
        }
    }

    public static class AnotherObject {
        private String yetAnotherField = "valueOfThatOtherField";
    }

}