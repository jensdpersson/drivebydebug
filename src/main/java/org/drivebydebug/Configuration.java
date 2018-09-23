package org.drivebydebug;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Configuration implements HasProps {
    
    private Map<String,String> props = new HashMap<String,String>();
    private List<EventSubscription> subscriptions = new ArrayList<EventSubscription>();
    
    public void prop(String key, String value){
        props.put(key, value);
    }

    public Map<String,String> props(){
        return this.props;
    }
    
    public void subscription(EventSubscription subscription){
        subscriptions.add(subscription);
    }

    public List<EventSubscription> subscriptions(){
        return subscriptions;
    }

}