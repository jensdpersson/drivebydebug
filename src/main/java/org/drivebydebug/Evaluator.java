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

    private String var;
    private Sub[] subs;
    
    public Evaluation eval(StackFrame frame){
        Evaluation evaluation = new Evaluation(var);
        try {
            LocalVariable var = frame.visibleVariableByName(this.var);
            Value value = frame.getValue(var);
            if(value instanceof ObjectReference){
                ObjectReference ref = (ObjectReference) value;
                List<Method> toString = ref.referenceType().methodsByName("toString", 
                                                                          "()Ljava/lang/String;");
                Value string = ref.invokeMethod(frame.thread(), 
                                                toString.get(0), 
                                                Collections.emptyList(), 
                                                0);
                String result = ((StringReference)string).value();
                evaluation.addResult(result);
            }
            //for(Sub sub : this.subs){
            //    Type type = value.type();
            //    if(type instanceof ReferenceType){
            //
            //    }
            //}
        } catch (Exception abex){
            evaluation.setFailure(abex);
        }
        return evaluation;
    }

    public Evaluator(String input){
        String[] parts = input.split("\\.");
        this.var = parts[0];
        this.subs = new Sub[parts.length];
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