//--//--//--//--//--//--//
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
//--//--//--//--//--//--// 


package edu.vanderbilt.domainmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vanderbilt.domainmodel.ArgumentType.ArgumentValueType;
import edu.vanderbilt.simulation.SimulationCaller;

public abstract class ArgumentTypeFactory {
    
    /*
     * Helper method that makes an ArgumentType 
     * object based on the index of the block 
     * whose argument it will be in blocks-list in NetLogo, and on a string 
     * that specifies which argument it is in the list of the BlockTemplate.
     * 
     * @param aBlockIndex
     * @param argIndex
     * 
     * @return returns the Arg that has been created.
     */
    public static ArgumentType makeArgumentType(
        final int aBlockIndex, 
        final int argIndex,
        final boolean isSetType
    ) {
        try {
            String argType;
            if (isSetType) {
                argType = SimulationCaller.reportString(
                    "property-of-arg-for-set-type " 
                    + argIndex + " " + aBlockIndex + " \"arg-type\"" 
                );
            } else {
                argType = 
                    SimulationCaller.reportString("property-of-arg-for-block " 
                    + argIndex + " " + aBlockIndex + " \"arg-type\"" 
               );
            }
            
            if (argType.equals("0.0")) {
                return null;
            }
            
            if (argType.equals("int")) {
                return makeIntArgumentType(aBlockIndex, argIndex, isSetType);
            }
            
            if (argType.equals("double")) {
                return makeDoubleArgumentType(aBlockIndex, argIndex, isSetType);
            }
            
            if (argType.equals("enum")) {
                return makeEnumArgumentType(aBlockIndex, argIndex, isSetType);
            }
            
            throw new IllegalArgumentException(
                argType + " " + aBlockIndex + " " + argIndex
            );
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }    
    }
    
    
    private static ArgumentType makeIntArgumentType(
        final int blockIndex, 
        final int argIndex,
        final boolean isSetType
    ) {
        try {
            String methodName;
            if (isSetType) {
                methodName = "property-of-arg-for-set-type ";
            } else {
                methodName = "property-of-arg-for-block ";
            }
            
            final int defaultValue = 
                SimulationCaller.reportInt(methodName 
                + argIndex + " " + blockIndex + " " + "\"default-value\"");
                        
            final int maxValue = 
                SimulationCaller.reportInt(methodName 
                + argIndex + " " + blockIndex + " " + "\"max-value\"");
 
                final int minValue = 
                    SimulationCaller.reportInt(methodName 
                + argIndex + " " + blockIndex + " " + "\"min-value\"");
            
            return makeIntArgumentType(minValue, maxValue, defaultValue);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArgumentType makeIntArgumentType(
        final int minValue,
        final int maxValue,
        final int defaultValue
    ) {
        Constraint rangeConstraint = new RangeConstraint(minValue, maxValue);
        Set<Constraint> constraints = new HashSet<Constraint>();
        constraints.add(rangeConstraint);            
        
        return new ArgumentType(
            ArgumentValueType.INT, 
            defaultValue, 
            constraints
        );
    }
    
    public static ArgumentType makeDoubleArgumentType(
        final double minValue,
        final double maxValue,
        final double defaultValue
    ) {
        Constraint rangeConstraint = new RangeConstraint(minValue, maxValue);
        Set<Constraint> constraints = new HashSet<Constraint>();
        constraints.add(rangeConstraint);            
        
        return new ArgumentType(
            ArgumentValueType.REAL, 
            defaultValue, 
            constraints
        );
    }
    
    
    private static ArgumentType makeDoubleArgumentType(
        final int blockIndex, 
        final int argIndex,
        final boolean isSetType
    ) {
        try {   
           String methodName;
           if (isSetType) {
               methodName = "property-of-arg-for-set-type ";
           } else {
               methodName = "property-of-arg-for-block ";
           }
           final double defaultValue = 
                SimulationCaller.reportDouble(methodName 
                + argIndex + " " + blockIndex + " " + "\"default-value\"");
                        
           final double maxValue = 
                SimulationCaller.reportDouble(methodName 
                + argIndex + " " + blockIndex + " " + "\"max-value\"");
                        
           final double minValue = 
                SimulationCaller.reportDouble(methodName 
                + argIndex + " " + blockIndex + " " 
                + "\"min-value\""); 
         
            
            return makeDoubleArgumentType(minValue, maxValue, defaultValue);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArgumentType makeEnumArgumentType(
        final List<String> items
    ) {
        List<String> values = new ArrayList<String>();
        values.addAll(items);
        if (items.size() == 0) {
            return new ArgumentType(ArgumentValueType.STRING, null, values);
        }
        
        return new ArgumentType(ArgumentValueType.STRING, items.get(0), values);
    }
    
    private static ArgumentType makeEnumArgumentType(
        final int blockIndex, 
        final int argIndex,
        final boolean isSetType
    ) {
        try {
            String methodName;
            if (isSetType) {
                methodName = "property-of-arg-for-set-type ";
            } else {
                methodName = "property-of-arg-for-block ";
            }
            final int listLength = 
                SimulationCaller.reportInt(methodName 
                + argIndex + " " + blockIndex 
                + " \"length enum-list\"");
            final String[] enumList = new String[ listLength ];
            for (
                int enumItemIndex = 0; 
                enumItemIndex < listLength; 
                enumItemIndex++
                ) {
                enumList[ enumItemIndex ] = 
                    SimulationCaller.reportString(
                        methodName + argIndex 
                        + " " + blockIndex + " \"item " + enumItemIndex 
                        + " enum-list\""
                   );
            }

            String defaultValue = enumList[0];
            
            List<String> valueSet = new ArrayList<String>();
            for (int i = 0; i < enumList.length; i++) {
                valueSet.add(enumList[i]);
            }
            
            return new ArgumentType(
                ArgumentValueType.STRING, 
                defaultValue, 
                valueSet
            );
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
