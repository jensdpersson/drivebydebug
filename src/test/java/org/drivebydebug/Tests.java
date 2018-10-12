package org.drivebydebug;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMDisconnectEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class Tests {

    @Parameterized.Parameters(name="{0}")
    public static List<File[]> parameters(){
        File testFolder = new File("src/test/resources/tests");
        List<File[]> ret = new ArrayList<File[]>();
        for(File test : testFolder.listFiles()){
            ret.add(new File[]{test});
        }
        return ret;
    }

    private String name;
    private File folder;
    private Process fixture;
    private String configFile;

    public Tests(File test){
        this.name = test.getName();
        this.folder = test;
    }

    @Before
    public void setup() throws IOException {
        String[] env = new String[]{};
        this.fixture = Runtime.getRuntime().exec("./run-fixture.sh", env, folder);
        InputStream imp = fixture.getInputStream();
        System.out.print((char)imp.read());
        new Thread(new Runnable(){
            public void run(){
                char c;
                try {
                    while((c = (char)imp.read()) != -1){
                        System.out.print(c);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        InputStream err = fixture.getErrorStream();
        new Thread(new Runnable(){
            public void run(){
                char c;
                try {
                    while((c = (char)err.read()) != -1){
                        System.out.print(c);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
            public void run(){
                try {
                    System.out.println("Forcibly killing fixture");
                    fixture.destroyForcibly().waitFor(60, TimeUnit.SECONDS);
                    if(fixture.isAlive()){
                        System.out.println("Failed killing fixture ");
                    }
                } catch (InterruptedException iex){
                    iex.printStackTrace();
                }
            }
        }));
    }

    @After
    public void teardown() throws IOException {
        this.fixture.destroy();
    }

    @Test
    public void runTest() throws Exception { 
        System.out.println("Here we will launch " + name 
        + " and connect with " + configFile);
        TestLogger logger = new TestLogger(new File(folder, "facit"));
        
        final boolean[] vmDisconnected = new boolean[1];
        Object lock = new Object();
        synchronized(lock){            
            EventPump subscriber = new EventPump(logger);
            subscriber.on(new ClassFilteringEventListener(
                VMDisconnectEvent.class, 
                new EventListener(){
                    public boolean onEvent(Event event){
                        System.out.println("Got VMDisconnect");
                        vmDisconnected[0] = true;
                        synchronized(lock){
                            lock.notify();
                        }
                        return false;
                    }
                })
            );         
            new Configurator(subscriber).parse(new File(folder, "driveby.cfg"));
            lock.wait(60_000);
        }
        if(!vmDisconnected[0]){
            Assert.fail("Timed out waiting for debug target vm to exit");
        }
        Assert.assertEquals("", logger.listComplaints());
    }


    class TestLogger implements Logger {

        Properties facit = new Properties();
        List<Throwable> errors = new ArrayList<Throwable>();

        TestLogger(File facitFile) throws IOException {
            facit.load(new FileReader(facitFile)); 
        }

        @Override
        public void onError(Throwable ball) {
            errors.add(ball);
        }

        @Override
        public void onEvaluation(Evaluation evaluation) {
            String expr = evaluation.getExpression();
            System.out.println("Removing expression ["+expr+"] " + "from " + facit);
            String expected = (String) facit.remove(expr);
            if(expected == null){
                errors.add(new Exception("Unknown expression [" + expr + "]"));
                return;
            }
            String actual = evaluation.getValueAString();
            if(!expected.equals(actual)){
                errors.add(new Exception("Expression [" 
                    + expr + "] was supposed to evaluate to [" + expected + "], not [" +actual+ "]"));
            }
        }

        public String listComplaints(){
            for(String leftOver : facit.stringPropertyNames()){
                errors.add(new Exception("No evaluation of expression [" + leftOver + "]"));
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.print("[");
            for(Throwable error : errors){
                error.printStackTrace(pw);
            }
            pw.print("]");
            pw.flush();
            return sw.toString();
        }
    }
}