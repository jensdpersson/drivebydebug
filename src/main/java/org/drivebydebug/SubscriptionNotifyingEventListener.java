package org.drivebydebug;

import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequest;

public class SubscriptionNotifyingEventListener implements EventListener {

    public void onEvent(Event event){
        EventRequest request = event.request();
        if(request != null){
            EventSubscription subscription = (EventSubscription) 
                    event.request().getProperty(EventSubscription.PROPERTY_KEY);
            if(subscription != null){
               subscription.onEvent(event);
            }
        }
    }

}