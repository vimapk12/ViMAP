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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public final class BlockTextFieldListener implements FocusListener {
    
    private final edu.vanderbilt.codecomponentview.SwingUserCodeView codeView;

    public BlockTextFieldListener(
        final edu.vanderbilt.codecomponentview.SwingUserCodeView aCodeView
    ) {
        this.codeView = aCodeView;
    }
    
    @Override
    public void focusGained(final FocusEvent event) {
        // do nothing
    }

    @Override
    public void focusLost(final FocusEvent event) {
        BlockTextField source = (BlockTextField) event.getSource();
        handleUpdate(source);        
    }
    
    private void handleUpdate(final BlockTextField blockTextField) {
        this.codeView.numberValueChanged(
            blockTextField.getBlockId(),
            blockTextField.getName(),
            blockTextField.getText()
        );
    }
}
