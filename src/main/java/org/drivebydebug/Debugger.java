package org.drivebydebug;

import java.io.File;

public class Debugger {

    public Debugger(File file){
        new Configurator(new EventPump(new StdoutLogger())).watch(file);
    }


}