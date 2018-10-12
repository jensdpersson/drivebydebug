package org.drivebydebug;

import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;

public class SubscriptionNotifyingEventListener implements EventListener {

    public boolean onEvent(Event event){
        EventRequest request = event.request();
        if(request != null){
            EventSubscription subscription = (EventSubscription) 
                    event.request().getProperty(EventSubscription.PROPERTY_KEY);
            if(subscription != null){
                return subscription.onEvent(event);
            }
        }
        return false;
    }

}