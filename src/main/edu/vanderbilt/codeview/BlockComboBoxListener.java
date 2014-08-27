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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BlockComboBoxListener implements ActionListener {
    
    private final edu.vanderbilt.codecomponentview.SwingUserCodeView codeView;
    
    public BlockComboBoxListener(
        final edu.vanderbilt.codecomponentview.SwingUserCodeView aCodeView
    ) {
        this.codeView = aCodeView;
    }
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        final BlockComboBox comboBox = (BlockComboBox) event.getSource();
        this.codeView.enumValueChanged(
            comboBox.getBlockId(), 
            comboBox.getName(), 
            comboBox.getSelectedItem().toString()
        );
    }
}
