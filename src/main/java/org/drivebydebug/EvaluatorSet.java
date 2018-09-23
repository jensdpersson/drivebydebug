package org.drivebydebug;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class EvaluatorSet implements HasProps, Iterable<Evaluator> {

    List<Evaluator> evaluators = new ArrayList<Evaluator>();

    public void prop(String key, String value){        
        if("eval".equals(key)){
            evaluators.add(new Evaluator(value));
        }
    }

    public Iterator<Evaluator> iterator(){
        return evaluators.iterator();
    } 

}