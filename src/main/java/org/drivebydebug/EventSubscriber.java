package org.drivebydebug;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Location;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.event.EventQueue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class EventSubscriber implements Configurable {
    
    private Logger logger;
    private EventPump eventPump = new EventPump();

    public EventSubscriber(Logger logger){
        this.logger = logger;
    }

    public void configure(final Configuration cfg) throws Exception {
        this.eventPump.reset(new Callable<EventQueue>(){
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
