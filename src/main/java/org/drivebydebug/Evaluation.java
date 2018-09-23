package org.drivebydebug;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Value;

public class Evaluation {

    private String expression;
    private List<Value> values = new ArrayList<Value>();

    public Evaluation(String expression){
        this.expression = expression;
    }

    public String getExpression(){
        return expression;
    }

    public String getValueAString(){
        if(values.size() > 0){
            return String.valueOf(values.get(values.size()-1));
        }
        return null;
    }

    public void addResult(Value value){
        values.add(value);
    }

    public void setFailure(Throwable up){}

}