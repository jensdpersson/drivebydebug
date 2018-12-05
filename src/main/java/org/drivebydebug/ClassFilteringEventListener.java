package org.drivebydebug;

import com.sun.jdi.event.Event;

public class ClassFilteringEventListener implements EventListener {

    private Class<?> clas;
    private EventListener next;

    public ClassFilteringEventListener(Class<?> clas, EventListener next){
        this.clas = clas;
        this.next = next;
    }

    @Override
    public void onEvent(Event event){
        if(clas.isInstance(event)){
            next.onEvent(event);
        }
    }

}