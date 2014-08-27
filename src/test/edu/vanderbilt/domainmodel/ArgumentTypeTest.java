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

import org.junit.Test;

import edu.vanderbilt.domainmodel.ArgumentType.ArgumentValueType;

public class ArgumentTypeTest extends junit.framework.TestCase {
    
    @Test
    public final void testIsEnum() {
        List<String> values = new ArrayList<String>();
        values.add("Hello");
        values.add("world");
        ArgumentType enumType = 
            new ArgumentType(ArgumentValueType.STRING, values, values);
        assertEquals(enumType.isEnum(), true);
        
        Set<Constraint> constraints = new HashSet<Constraint>();
        ArgumentType nonEnum = 
            new ArgumentType(ArgumentValueType.STRING, "Hello", constraints);
        assertEquals(nonEnum.isEnum(), false);
    }
}
