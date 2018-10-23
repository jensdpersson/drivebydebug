package org.drivebydebug;

import com.sun.jdi.event.Event;

public interface EventListener {

    public void onEvent(Event event);

}