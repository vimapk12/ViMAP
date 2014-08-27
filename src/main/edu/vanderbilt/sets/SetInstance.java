/////////////
//
//   Copyright 2014  
//   Mind, Matter & Media Lab, Vanderbilt University.
//   This is a source file for the ViMAP open source project.
//   Principal Investigator: Pratim Sengupta 
//   Lead Developer: Mason Wright
//   
//   Simulations powered by NetLogo. 
//   The copyright information for NetLogo can be found here: 
//   https://ccl.northwestern.edu/netlogo/docs/copyright.html  
//
/////////////  


package edu.vanderbilt.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.vanderbilt.domainmodel.ArgumentType;

/**
 * An instance of a SetTemplate. A set instance 
 * could be a particular rectangular
 * set, for example.
 */
public final class SetInstance {

    /**
     * The base template from which this instance was created.
     */
    private final SetTemplate setTemplate;
    
    /**
     * This name will be unique among all SetInstances.
     */
    private final String setName;
    
    /**
     * Holds values of the arguments, corresponding 
     * to types defined in the SetTemplate.
     * For example, this could hope a List<Double> of 
     * points, if the set is a shape.
     */
    private Map<String, Object> arguments;
    
    public SetInstance(
        final SetTemplate aSetTemplate,
        final String aSetName,
        final Map<String, Object> aArguments
    ) {
        if (
            aSetTemplate == null
            || aSetName == null
            || aArguments == null
        ) {
            throw new IllegalArgumentException();
        }
        
        this.setTemplate = aSetTemplate;
        this.setName = aSetName;
        
        this.arguments = new HashMap<String, Object>();
        for (Entry<String, Object> entry: aArguments.entrySet()) {
            this.arguments.put(entry.getKey(), entry.getValue());
        }
    }
    
    public SetTemplate getSetTemplate() {
        return this.setTemplate;
    }
    
    public String getSetName() {
        return this.setName;
    }
    
    public Object getArgumentValue(final String key) {
        return this.arguments.get(key);
    }
    
    public Map<String, Object> getArguments() {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<String, Object> entry: this.arguments.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    public boolean setArgument(final String key, final Object value) {
        Map<String, ArgumentType> argumentTypes = 
            this.setTemplate.getArgumentTypes();
        
        if (!argumentTypes.containsKey(key)) {
            return false;
        }
        ArgumentType type = argumentTypes.get(key);
        if (!type.verify(value)) {
            return false;
        }
        this.arguments.put(key, value);
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SetInstance [setTemplate=");
        builder.append(this.setTemplate);
        builder.append(", setName=");
        builder.append(this.setName);
        builder.append(", arguments=");
        builder.append(this.arguments);
        builder.append("]");
        return builder.toString();
    }
}
