package org.drivebydebug;

public class Io {
    public static void echo(Object message){
        System.out.println(String.valueOf(message));
    }
    public static void die(Object message, int code){
        echo(message);
        System.exit(code);
    }
    public static void die(Object message){
        die(message, 1);
    }
    public static void die(Exception ex){
        System.out.println(ex.getMessage());
        ex.printStackTrace(System.out);
        System.exit(1);
    }
}