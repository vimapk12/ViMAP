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


package edu.vanderbilt.codecomponentview;

import java.util.UUID;

public interface Highlightable {

    void highlight();
    void unhighlight();
    
    /**
     * Call the last contained BlockView to highlight 
     * itself, indicating that the block
     * would drop into place after the current last item.
     */
    void highlightLast(boolean shouldHighlight);
    int getParentDepth();
    UUID getSequenceId();
}
