package org.drivebydebug;

import java.util.List;
import java.io.File;
import java.io.IOException;
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
    private Process fixture;
    private String configFile;

    public Tests(File test){
        this.name = test.getName();
        
    }

    @Before
    public void setup() throws IOException {
       this.fixture = Runtime.getRuntime().exec("run-fixture.sh");
    }

    @After
    public void teardown() {
        this.fixture.destroy();
    }

    @Test
    public void runTest(){ 
        System.out.println("Here we will launch " + name 
        + " and connect with " + configurationFile);
        
    }

}