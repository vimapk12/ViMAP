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


package edu.vanderbilt.sets;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.sets.CreateSetDialog.SetNameState;
import edu.vanderbilt.userprocedures.CreateProcedureDialog;

/**
 * Checks whether the proposed set name entered by the user
 * is valid and not taken.
 *
 */
public final class CreateSetVerifier extends InputVerifier {
    
    private CreateSetDialog listener;
    private final DomainModel domainModel;
    
    public CreateSetVerifier() {
        this.domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");
    }
    
    public void setListener(final CreateSetDialog aListener) {
        this.listener = aListener;
    }

    @Override
    public boolean verify(final JComponent component) {
        final JTextField source = (JTextField) component;
        
        final SetNameState result = checkField(source);
        
        if (this.listener != null) {
            this.listener.setSetNameState(result);
            this.listener.handleEvent();
        }
        
        return true;
    }
    
    private SetNameState checkField(final JTextField source) {
        final String text = source.getText();
        if (text.length() == 0) {
            return SetNameState.NO_CHARACTERS;
        }
        if (!DomainModel.areCharactersLegal(text)) {
            return SetNameState.INVALID_CHARACTERS;
        }
        if (text.length() > CreateProcedureDialog.MAX_NAME_LENGTH) {
            return SetNameState.TOO_LONG;
        }
        if (this.domainModel.isSetNameTaken(text)) {
            return SetNameState.ALREADY_USED;
        }
        return SetNameState.VALID;
    }
}
