package org.drivebydebug;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequest;
import java.util.concurrent.Callable;

public class EventPump {


    private boolean started;
    private boolean stopped;
    private EventQueue eventQueue;
    private Callable<EventQueue> eventQueueSource;

    public void reset(Callable<EventQueue> eventQueueSource){
        this.eventQueueSource = eventQueueSource;
        eventQueue = null;
        if(!started){
            start();
            started = true;
        }
    }

    public void stop(){
        this.stopped = true;
    }

    private void start(){
        new Thread(new Runnable(){
            public void run(){
                while(!stopped){
                    try {
                        if(eventQueue == null && eventQueueSource != null){
                            eventQueue = eventQueueSource.call();
                            if(eventQueue == null){
                                return;
                            }
                        }
                        EventSet eventSet = eventQueue.remove(10000);
                        if(eventSet == null){
                            continue;
                        }
                        for(Event event : eventSet){
                            System.out.println("Got event " + event);
                            EventRequest request = event.request();
                            if(request != null){
                                EventSubscription subscription = (EventSubscription) 
                                        event.request().getProperty(EventSubscription.PROPERTY_KEY);
                                if(subscription != null){
                                    subscription.onEvent(event);
                                }
                            }
                        }
                        eventSet.virtualMachine().resume();
                    } catch(Exception ex){
                        System.out.println("EventPump got exception");
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        }).start();
    }

}