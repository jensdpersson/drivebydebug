package org.drivebydebug;

import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;

import java.util.ArrayList;

public class Break {

    private String sourceName;
    private int line;

    public Break(Location location){
        try {
            this.sourceName = location.sourceName();
        } catch (AbsentInformationException ex){
            this.sourceName = "ABSENT";
        }
        this.line = location.lineNumber();
    }

    private List<Evaluation> evaluations = new ArrayList<>();

    public void add(Evaluation evaluation){
        this.evaluations.add(evaluation);
    }
    
    public String getSourceName(){
        return sourceName;
    }

    public int getLineNumber(){
        return line;
    }

    public List<Evaluation> evaluations(){
        return this.evaluations;
    }

}