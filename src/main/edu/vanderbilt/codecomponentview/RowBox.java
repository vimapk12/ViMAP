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

import javax.swing.Box;
import javax.swing.BoxLayout;

public final class RowBox extends Box {
    
    private static final long serialVersionUID = -4680732793048254247L;

    public RowBox() {
        super(BoxLayout.LINE_AXIS);
        setOpaque(false);
    }
}
