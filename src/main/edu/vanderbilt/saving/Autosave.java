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


package edu.vanderbilt.saving;

public interface Autosave {

    /**
     * Schedule autosaving.
     */
    void init();
    
    /**
     * Cancel autosaving.
     */
    void stop();
    
    /**
     * Delete last autosave file and update file name to autosave as.
     */
    void deleteLastAndChangeName();
}
