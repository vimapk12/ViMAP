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


package edu.vanderbilt.codeview;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.UUID;

public interface BlockComponent {
    static enum ComponentType {
        BLOCK_TEXT_FIELD,
        BLOCK_COMBO_BOX
    }
    
    ComponentType getComponentType();
    
    void setBounds(final Rectangle rectangle);
    void setMaxWidth(Graphics g);
    void validate();
    int getStandardWidth();
    void setAvailable(final boolean isAvailable);
    void setImage(final BufferedImage image);
    BufferedImage getImage();
    String getName();
    UUID getBlockId();
    void activateListener();
}
