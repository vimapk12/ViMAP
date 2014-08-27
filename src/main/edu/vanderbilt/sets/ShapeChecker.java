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

import java.util.List;

/**
 * Checks whether a list of coordinates in R2, alternating x and y values,
 * corresponds to a valid shape of a certain type.
 */
public interface ShapeChecker {
    
    /**
     * @param coordinates a list of coordinates in R2, x first then y.
     * 
     * @return empty String if a valid list, otherwise a specific error message
     * explaining why the list is not valid. Needs to handle only those cases 
     * that can occur in the editor.
     */
    String checkShape(List<Double> coordinates);
}
