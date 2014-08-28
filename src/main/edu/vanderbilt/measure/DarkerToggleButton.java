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


package edu.vanderbilt.measure;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JToggleButton;

/**
 * A version of JToggleButton whose text is colored red iff the
 * button is selected.
 */
public final class DarkerToggleButton extends JToggleButton {
    
    private static final long serialVersionUID = 3396121179874137413L;

    public DarkerToggleButton(final String text) {
        super(text);
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
        if (isSelected()) {
            setForeground(Color.GREEN.darker());
        } else {
            setForeground(Color.BLACK);
        }
        super.paintComponent(graphics);
    }
}
