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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;

public final class SetsMenuListener implements ActionListener {

    private SetsMenu setsMenu;
    private final DomainModel domainModel;
    private CreateSetVerifier createSetVerifier;
    
    public SetsMenuListener() {
        super();
        
        this.domainModel = DependencyManager.getDependencyManager().
                getObject(DomainModel.class, "domainModel");
        this.createSetVerifier = new CreateSetVerifier();
    }
    
    public void setSetsMenu(final SetsMenu aSetsMenu) {
        this.setsMenu = aSetsMenu;
    }
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();

        if (command.equals(SetsMenu.CREATE_SET_STRING)) {
            createSetDialog();
        } else if (command.equals(SetsMenu.DELETE_SET_STRING)) {
            deleteSetDialog();
        } else if (command.equals(SetsMenu.EDIT_SET_SHAPE_STRING)) {
            editShapeOfSetDialog();
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    private void editShapeOfSetDialog() {
        assert SwingUtilities.isEventDispatchThread();

        new EditShapeOfSetDialog(
            GraphicalInterface.getFrame()
        ).setVisible(true);
    }
    
    private void createSetDialog() {
        assert SwingUtilities.isEventDispatchThread();

        new CreateSetDialog(
            GraphicalInterface.getFrame(),
            this.createSetVerifier,
            this.setsMenu
       ).setVisible(true);    
    }
    
    private void deleteSetDialog() {
        assert SwingUtilities.isEventDispatchThread();

        new DeleteSetDialog(
            GraphicalInterface.getFrame(),
            this.setsMenu
        ).setVisible(true);
    }
    
    public boolean shouldEnableDelete() {
        for (SetInstance setInstance: this.domainModel.getSetInstances()) {
            if (!setInstance.getSetTemplate().isDefault()) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean shouldEnableEdit() {
        for (SetInstance setInstance: this.domainModel.getSetInstances()) {
            if (setInstance.getSetTemplate().isShape()) {
                return true;
            }
        }
        
        return false;
    }
}
