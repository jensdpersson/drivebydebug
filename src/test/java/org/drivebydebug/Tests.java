package org.drivebydebug;

import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
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
        EventSubscriber subscriber = new EventSubscriber(logger);
        Configuration cfg = new Configurator(subscriber).parse(new File(folder, "driveby.cfg"));
        subscriber.configure(cfg);
        
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