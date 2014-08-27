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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.vanderbilt.domainmodel.ArgumentType;

/**
 * This class describes a type of set that can be created,
 * such as rectangle sets.
 */
public final class SetTemplate {

    /**
     * Name of the set type.
     */
    private final String name;
    
    /**
     * True if the set is based on a shape drawn by the user, such
     * as a rectangle.
     */
    private final boolean isShape;
    
    /**
     * True if the set is included in the model by default, such as
     * the set of all turtles.
     */
    private final boolean isDefault;
    
    private final LinkedHashMap<String, ArgumentType> argumentTypes;
    
    /**
     * This will be null if !isShape
     */
    private final ShapedSet shapedSet;
    
    public SetTemplate(
        final String aName,
        final boolean aIsShape,
        final LinkedHashMap<String, ArgumentType> aArgumentTypes,
        final ShapedSet aShapedSet,
        final boolean aIsDefault
    ) {
        if (
            aName == null
            || aArgumentTypes == null
        ) {
          throw new IllegalArgumentException();  
        }
        
        if (aIsShape && (aShapedSet == null)) {
            throw new IllegalArgumentException("Must have a ShapedSet");
        }
        
        this.isDefault = aIsDefault;
        this.name = aName;
        this.isShape = aIsShape;
        this.argumentTypes = new LinkedHashMap<String, ArgumentType>();
        
        for (Entry<String, ArgumentType> entry: aArgumentTypes.entrySet()) {
            this.argumentTypes.put(entry.getKey(), entry.getValue());
        }
        
        this.shapedSet = aShapedSet;
    }
    
    public boolean isDefault() {
        return this.isDefault;
    }
    
    public boolean isUserDefined() {
        // user can't define "default" blocks or shaped blocks.
        // any non-default, non-shape blocks are user-defined.
        return !this.isShape && !this.isDefault;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isShape() {
        return this.isShape;
    }
    
    public List<String> getArgumentNames() {
        List<String> result = new ArrayList<String>();
        for (String currentName: this.argumentTypes.keySet()) {
            result.add(currentName);
        }
        
        return result;
    }
    
    public LinkedHashMap<String, ArgumentType> getArgumentTypes() {
        LinkedHashMap<String, ArgumentType> result = 
            new LinkedHashMap<String, ArgumentType>();
        for (Entry<String, ArgumentType> entry: this.argumentTypes.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    public ArgumentType getArgumentType(final String argumentName) {
        assert this.argumentTypes.containsKey(argumentName);
        return this.argumentTypes.get(argumentName);
    }
    
    public ShapedSet getShapedSet() {
        return this.shapedSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SetTemplate [name=");
        builder.append(this.name);
        builder.append(", isShape=");
        builder.append(this.isShape);
        builder.append(", argumentTypes=");
        builder.append(this.argumentTypes);
        builder.append(", shapedSet=");
        builder.append(this.shapedSet);
        builder.append("]");
        return builder.toString();
    }
}
