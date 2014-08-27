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


package edu.vanderbilt.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;

public final class EditMenuListener implements ActionListener {
    
    private MasterCodeController codeController;

    public EditMenuListener() {
        super();
    }
    
    public void init() {
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(EditMenu.CUT_ALL_STRING)) {
            this.codeController.cutAll();
        } else if (command.equals(EditMenu.COPY_ALL_STRING)) {
            this.codeController.copyAll();
        } else if (command.equals(EditMenu.PASTE_TO_END_STRING)) {
            this.codeController.pasteToEnd();
        } else {
            // else an invalid event has been fired
            throw new IllegalArgumentException();      
        }
    }

}
