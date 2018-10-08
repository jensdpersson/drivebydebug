package org.drivebydebug;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Value;
import com.sun.jdi.LongValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import com.sun.jdi.ClassType;
import com.sun.jdi.LongType;

public class Evaluation {

    private String expression;
    private List<String> values = new ArrayList<String>();

    public Evaluation(String expression){
        this.expression = expression;
    }

    public String getExpression(){
        return expression;
    }

    public String getValueAString(){
        if(values.size() > 0){
            return values.get(values.size()-1);
        }
        return null;
    }

   

    public void addResult(String result){
        values.add(result);
    }

    public void setFailure(Throwable up){}


}