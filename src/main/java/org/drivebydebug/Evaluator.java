package org.drivebydebug;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class Evaluator {

    private String var;
    private Sub[] subs;
    
    public Evaluation eval(StackFrame frame){
        Evaluation evaluation = new Evaluation(var);
        try {
            LocalVariable var = frame.visibleVariableByName(this.var);
            Value value = frame.getValue(var);
            evaluation.addResult(value);
            for(Sub sub : this.subs){
                Type type = value.type();
                if(type instanceof ReferenceType){

                }
            }
        } catch (AbsentInformationException abex){
            evaluation.setFailure(abex);
        }
        return evaluation;
    }

    public Evaluator(String input){
        String[] parts = input.split("\\.");
        this.var = parts[0];
        for(int i = 1; i<parts.length;i++){
            if(parts[i].endsWith("()")){
                this.subs[i-1] = new MethodSub(parts[i]);
            } else {
                this.subs[i-1] = new FieldSub(parts[i]);
            }
        }
    }

    private interface Sub {
       public ObjectReference eval(ObjectReference ref);
    }

    private class FieldSub implements Sub {
        private String name;
        public FieldSub(String name){
            this.name = name;
        }
        public ObjectReference eval(ObjectReference ref) {
            Field field = ref.referenceType().fieldByName(this.name);
            //return ref.getValue(field);
            return null;
        }
    }

    private class MethodSub implements Sub {
        private String name;
        public MethodSub(String name){
            this.name = name;
        }
        public ObjectReference eval(ObjectReference ref) {
            return null;
        }
    }

}