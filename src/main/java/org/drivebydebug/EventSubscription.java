package org.drivebydebug;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;

public interface EventSubscription extends HasProps, EventListener {
    public static final String PROPERTY_KEY = "EventSubscription";
    public void setLogger(Logger logger);
    public EventRequest activate(VirtualMachine vm);
}