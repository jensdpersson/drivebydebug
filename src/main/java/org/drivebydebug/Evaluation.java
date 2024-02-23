package org.drivebydebug;

import java.util.ArrayList;
import java.util.List;

public class Evaluation {

    private String expression;
    private List<String> values = new ArrayList<String>();
    private Throwable up;

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

    public Throwable getFailure() {
        return up;
    }

    public void setFailure(Throwable up){
        this.up = up;
    }


}