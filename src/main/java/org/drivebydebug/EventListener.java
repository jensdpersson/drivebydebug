package org.drivebydebug;

import com.sun.jdi.event.Event;

public interface EventListener {

    public boolean onEvent(Event event);

}