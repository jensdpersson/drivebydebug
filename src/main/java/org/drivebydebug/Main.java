package org.drivebydebug;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

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

        // Ok, from here on there be dragons...
        // these classloading shenanigans
        // are in place to be able to add tools.jar to classpath 
        // in the running jvm without lots of logic in wrapper scripts.
        // So, CAVEAT MUTATOR, this all means that there is a brittleness to the system
        // The Main class, and classes referenced by it, cannot mention a class within
        // com.sun.jdi nor any class that does mention any of that. 

        // tools.jar should be at fix location relative to java.home:
        String javaHome = System.getProperty("java.home");
        String pathToToolsJar = javaHome + "/../lib/tools.jar";
        File toolsJar = new File(pathToToolsJar);
        if(!toolsJar.exists()){
            Io.die("Could not find tools.jar. Is this a JRE? It needs the full JDK.");
        }
        
        // We load the rest of the system through a tools,jar-aware child classloader.
        // This one needs to find our application classes or it will delegate finding those
        // to parent, parent will not find tools.jar. So we add our jar file to the path
        URL urlOfThisJar = Main.class.getProtectionDomain().getCodeSource().getLocation();

        Io.echo("Adding " + urlOfThisJar + " to classpath");

        // Now create new classloader
        try {
            URL toolsJarURL = toolsJar.getAbsoluteFile().toURI().toURL();
            Io.echo("Adding " + toolsJarURL + " to classpath");
            ChildFirstClassLoader cl = new ChildFirstClassLoader(
                new URL[]{
                    urlOfThisJar,
                    toolsJarURL
                },
                Main.class.getClassLoader()
            );

            // And load the rest of the system through it
            Class debuggerClass = cl.loadClass("org.drivebydebug.Debugger");
            Constructor ctor = debuggerClass.getConstructor(File.class);

            //Invoking the ctor kicks of the system
            ctor.newInstance(file);
        } catch(Exception ex){
            Io.die(ex);
        }
    }    

    static class ChildFirstClassLoader extends URLClassLoader {

        ChildFirstClassLoader(URL[] classpath, ClassLoader parent){
            super(classpath, parent);
        }

        @Override
        public Class loadClass(String name) throws ClassNotFoundException {
            //Io.echo("Ext.loadClass(" + name + "");
            try {
                if(findLoadedClass(name) == null){
                    findClass(name);
                }
            } catch (ClassNotFoundException cnex){}
            return super.loadClass(name);
        }
    }

}