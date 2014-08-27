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


package edu.vanderbilt.runcontroller;

public interface RunController {
    
    void init();
    
    // cycles is ignored if isForever is true.
    void run(
        boolean runSetup, 
        boolean runGo, 
        boolean highlight, 
        int cycles,
        boolean isForever
    );
    
    void stop();
    
    void setDelayPerBlockInMillis(int delayInMillis);
}
