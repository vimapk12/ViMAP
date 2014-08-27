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


package edu.vanderbilt.domainmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ArgumentType {

    private final Set<Constraint> constraints;
    private Object defaultValue;
    private final List<Object> values;
    private final ArgumentValueType argValueType;
    
    public static enum ArgumentValueType {
        INT,
        REAL, 
        STRING, 
        LIST,
        INT_BLOCK, 
        REAL_BLOCK, 
        NUMBER_BLOCK, 
        BOOLEAN_BLOCK
    }
    
    public ArgumentType(
        final ArgumentValueType aArgValueType, 
        final Object aDefaultValue,
        final Set<Constraint> aConstraints
    ) {
        this.argValueType = aArgValueType;
        this.constraints = new HashSet<Constraint>();
        this.constraints.addAll(aConstraints);
        this.defaultValue = aDefaultValue;
        this.values = null;
    }
    
    public ArgumentType(
        final ArgumentValueType aArgValueType, 
        final Object aDefaultValue,
        final List<? extends Object> aValues
    ) {
        this.argValueType = aArgValueType;
        
        this.constraints = null;
        
        this.defaultValue = aDefaultValue;
        
        this.values = new ArrayList<Object>();
        this.values.addAll(aValues);
    }
    
    public boolean isEnum() {
        return this.values != null;
    }
    
    public List<Object> getEnumValues() {
        List<Object> result = new ArrayList<Object>();
        result.addAll(this.values);
        return result;
    }
    
    public void addEnumValue(final Object newValue) {
        this.values.add(newValue);
    }
    
    public void removeEnumValue(final Object newValue) {
        this.values.remove(newValue);
    }
    
    public ArgumentValueType getArgumentValueType() {
        return this.argValueType;
    }
    
    public boolean verify(final Object o) {
        if (o == null) {
            return false;
        }
        
        switch (this.argValueType) {
        case INT:
            if (!Integer.class.isAssignableFrom(o.getClass())) {
                return false;
            }
            break;
        case STRING:
            if (!String.class.isAssignableFrom(o.getClass())) {
                return false;
            }
            break;           
        case LIST:
            if (!List.class.isAssignableFrom(o.getClass())) {
                return false;
            }
            break;

        case REAL:
            if (!Double.class.isAssignableFrom(o.getClass())) {
                return false;
            }
            break;
        case INT_BLOCK:
        case NUMBER_BLOCK:
        case REAL_BLOCK:
        case BOOLEAN_BLOCK:
            throw new IllegalStateException();
        default:
            throw new IllegalStateException();
        }
        
        if (this.values == null) {
            if (this.constraints == null) {
                return true;
            }
            
            // not an enum
            for (Constraint constraint: this.constraints) {
                if (!constraint.verify(o)) {
                    return false;
                }
            }
            return true;
        }
               
        // an enum
        return this.values.contains(o);
    }
    
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    /*
     * Used for blocks whose argument lists may change.
     */
    public void setDefaultValue(final Object newValue) {
        if (isEnum()) {
            if (!this.values.contains(newValue)) {
                throw new IllegalArgumentException();
            }
        }
        
        this.defaultValue = newValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ArgumentType [argType=");
        builder.append(this.argValueType);
        builder.append(", constraints=");
        builder.append(this.constraints);
        builder.append(", defaultValue=");
        builder.append(this.defaultValue);
        builder.append(", values=");
        builder.append(this.values);
        builder.append("]");
        return builder.toString();
    }
}
