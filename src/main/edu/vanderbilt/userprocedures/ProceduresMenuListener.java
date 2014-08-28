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


package edu.vanderbilt.userprocedures;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;


public class ProceduresMenuListener implements ActionListener {
    
    private final CreateProcedureVerifier createProcedureVerifier;
    private static final int MAX_PROCEDURES = 50;
    private ProceduresMenu proceduresMenu;
    private final DomainModel domainModel;
    
    /**
     * Constructor.
     */
    public ProceduresMenuListener() {
        this.createProcedureVerifier = new CreateProcedureVerifier();
        this.domainModel = DependencyManager.getDependencyManager().
                getObject(DomainModel.class, "domainModel");
    }
    
    
    public final void setProceduresMenu(final ProceduresMenu aProceduresMenu) {
        this.proceduresMenu = aProceduresMenu;
    }
    
    
    /**
     * Returns true if the number of user procedures is up to the limit.
     * 
     * @return whether the number of user procedures 
     * in existence is up to the limit
     */
    public final boolean areTooManyProcedures() {
        return this.domainModel.maxUserProceduresForOneAgent() 
            >= MAX_PROCEDURES;
    }
    
    
    /**
     * Returns true if the procedures set is empty.
     * 
     * @return whether the user procedures set is empty
     */
    public final boolean areNoProcedures() {
        return !this.domainModel.hasUserProcedure();
    }
    
    
    /**
     * Listen for Procedures menu item events.
     * 
     * @param event the action event
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        // if user requests a new procedure
        if (command.equals(ProceduresMenu.CREATE_PROCEDURE_STRING)) {
            createProcedureDialog();
        } else if (command.equals(ProceduresMenu.DELETE_PROCEDURE_STRING)) {
            // if user requests deleting a procedure
            deleteProcedureDialog();
        } else {
            // else an invalid event has been fired
            throw new IllegalArgumentException();      
        }
    }
    
    
    private void createProcedureDialog() {
        assert SwingUtilities.isEventDispatchThread();

        new CreateProcedureDialog(
            GraphicalInterface.getFrame(),
            this.createProcedureVerifier,
            this.proceduresMenu
       ).setVisible(true);    
    }
    
    
    private void deleteProcedureDialog() {
        assert SwingUtilities.isEventDispatchThread();

        new DeleteProcedureDialog(
            GraphicalInterface.getFrame(),
            this.proceduresMenu
       ).setVisible(true);
    }
}
