package org.drivebydebug;

import com.sun.jdi.event.Event;

public class ResumingEventListener implements EventListener {
    public boolean onEvent(Event event){
        return true;
    }
}