package org.drivebydebug;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.AttachingConnector;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class EventPump implements Configurable {

    private boolean started;
    private boolean stopped;
    private EventQueue eventQueue;
    private Callable<EventQueue> eventQueueSource;

    private List<EventListener> eventHandlers = new ArrayList<EventListener>();
    
    private Logger logger;
  
    public EventPump(Logger logger){
        this.logger = logger;
        this.eventHandlers.add(new SubscriptionNotifyingEventListener());
    }
  
    public void on(EventListener handler){
        this.eventHandlers.add(handler);
    }

    private void reset(Callable<EventQueue> eventQueueSource){
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
                        EventSet eventSet = eventQueue.remove(1000);
                        if(eventSet == null){
                            continue;
                        }
                        for(Event event : eventSet){
                            System.out.println("Got event " + event);
                            for(EventListener handler : eventHandlers){
                                handler.onEvent(event);
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


    public void configure(final Configuration cfg) throws Exception {
        this.reset(new Callable<EventQueue>(){
            public EventQueue call(){
                VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
                AttachingConnector connector = null;
                List<AttachingConnector> connectors = vmm.attachingConnectors();
                if(connectors.size() > 0){
                    connector = connectors.get(0);
                } else {
                    throw new RuntimeException("No attaching connector installed, exiting");
                }
                System.out.println("Got connector " + connector);
                
                Map<String,Connector.Argument> args = connector.defaultArguments();
                for(Map.Entry<String,String> entry : cfg.props().entrySet()){
                    Connector.Argument argument = args.get(entry.getKey());
                    if(argument == null){
                        throw new IllegalArgumentException("Unsupported key " + entry.getKey());
                    }
                    argument.setValue(entry.getValue());
                }
                
                VirtualMachine vm = null;

                try {
                    vm = connector.attach(args);
                } catch (IOException iex){
                    logger.onError(iex);
                    return null;
                } catch (IllegalConnectorArgumentsException iex){
                    logger.onError(iex);
                    return null;
                }

                for(EventSubscription subscription : cfg.subscriptions()){
                    System.out.println("Activating " + subscription);
                    subscription.setLogger(logger);
                    EventRequest request = subscription.activate(vm);
                    request.putProperty(EventSubscription.PROPERTY_KEY, subscription);
                    request.setEnabled(true);
                }

                EventQueue eventQueue = vm.eventQueue();
                return eventQueue;
            }
        });
    }
}