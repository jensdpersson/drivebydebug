package org.drivebydebug;

import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;


import java.util.List;

public class BreakpointEventSubscription implements EventSubscription {

    private String file;
    private int line;
    private String className;
    private EvaluatorSet evaluators = new EvaluatorSet();
    private Logger logger;

    public BreakpointEventSubscription(String file, int line){
        this.file = file;
        this.line = line;
        this.className = fileToClassName(file);
    }

    public void setLogger(Logger logger){
        this.logger = logger;
    }

    private String fileToClassName(String file){
        return file; //for now
    }

    public void prop(String key, String value){
        evaluators.prop(key, value);
    }

    public EventRequest activate(VirtualMachine vm){
        EventRequestManager erm = vm.eventRequestManager();
        List<ReferenceType> classes = vm.classesByName(className);
        if(classes.size() < 1){
            ClassPrepareRequest req = erm.createClassPrepareRequest();
            req.addClassFilter(className);
            return req;
        }
       
        ReferenceType c = classes.get(0);
        return createBreakpoint(erm, c);           
    }

    private BreakpointRequest createBreakpoint(EventRequestManager erm, ReferenceType c){
        try {
            List<Location> locations = c.locationsOfLine(line);
            if(locations.size() < 1){
                throw new IllegalArgumentException("No executable location for " 
                    + className + ":" + line );
            }
            Location loc = locations.get(0);
            BreakpointRequest request = erm.createBreakpointRequest(loc);
            return request;
        } catch(AbsentInformationException abex){
            throw new IllegalArgumentException(abex);
        }
    }

    public void onEvent(Event event){
        if(event instanceof ClassPrepareEvent){
            System.out.println("ClassPrepareEvent");
            ClassPrepareEvent cpe = (ClassPrepareEvent) event;
            BreakpointRequest request = 
                createBreakpoint(event.virtualMachine().eventRequestManager(), cpe.referenceType());
                request.putProperty(EventSubscription.PROPERTY_KEY, this);
                request.setEnabled(true);
            return;
        }

        BreakpointEvent e = (BreakpointEvent) event;
        ThreadReference t = e.thread();        
        try {
            StackFrame f = t.frame(0);
            for(Evaluator evaluator : evaluators){
                Evaluation evaluation = evaluator.eval(f);
                logger.onEvaluation(evaluation);
            }
        } catch(IncompatibleThreadStateException ix){
            logger.onError(ix);
        }
    }


}