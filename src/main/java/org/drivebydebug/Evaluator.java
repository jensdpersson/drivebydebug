package org.drivebydebug;

import java.util.Collections;
import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class Evaluator {

    private String expr;
    private String var;
    private Sub[] subs;
    
    public Evaluation eval(StackFrame frame){
        Evaluation evaluation = new Evaluation(expr);
        try {
            LocalVariable var = frame.visibleVariableByName(this.var);
            Value value = frame.getValue(var);
            if(value instanceof ObjectReference){
                ObjectReference ref = (ObjectReference) value;
               
                for(Sub sub : this.subs){
                    Value subval = sub.eval(ref, frame);
                    if(subval instanceof ObjectReference){
                        ref = (ObjectReference) subval;
                    } else {
                        throw new IllegalArgumentException("Failed evaluating ["
                            + expr + "], [" 
                            + subval + "] is not an object reference");
                    }
                }
                List<Method> toString = ref.referenceType().methodsByName("toString", 
                                                                          "()Ljava/lang/String;");
                Value string = ref.invokeMethod(frame.thread(), 
                                                toString.get(0), 
                                                Collections.emptyList(), 
                                                ObjectReference.INVOKE_SINGLE_THREADED);
                String result = ((StringReference)string).value();
                evaluation.addResult(result);
            }
            
        } catch (Exception abex){
            evaluation.setFailure(abex);
        }
        return evaluation;
    }

    public Evaluator(String input){
        this.expr = input;
        String[] parts = input.split("\\.");
        this.var = parts[0];
        this.subs = new Sub[parts.length-1];
        for(int i = 1; i<parts.length;i++){
            if(parts[i].endsWith("()")){
                this.subs[i-1] = new MethodSub(parts[i].substring(0,parts[i].length()-2));
            } else {
                this.subs[i-1] = new FieldSub(parts[i]);
            }
        }
    }

    private interface Sub {
       public Value eval(ObjectReference ref, StackFrame frame) throws Exception;
    }

    private class FieldSub implements Sub {
        private String name;
        public FieldSub(String name){
            this.name = name;
        }
        public Value eval(ObjectReference ref, StackFrame frame) {
            System.out.println("eval field " + name + " in " + ref);
            Field field = ref.referenceType().fieldByName(this.name);
            return (ObjectReference) ref.getValue(field);
        }
    }

    private class MethodSub implements Sub {
        private String name;
        public MethodSub(String name){
            this.name = name;
        }
        public Value eval(ObjectReference ref, StackFrame frame) throws Exception {
            System.out.println("eval method " + name + " in " + ref);
            List<Method> methods = ref.referenceType().methodsByName(this.name);
            for(Method meth : methods){
                if(meth.argumentTypeNames().size() == 0){
                    return ref.invokeMethod(frame.thread(), 
                                            meth, 
                                            Collections.emptyList(), 
                                            ObjectReference.INVOKE_SINGLE_THREADED);
                }
            }
            throw new IllegalArgumentException("Failed evaluating ["
                + expr + "], no zero-arg method called [" + name +
                "] on [" + ref.referenceType() + "]");
        }
    }

}