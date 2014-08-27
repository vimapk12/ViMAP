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


package edu.vanderbilt.userprocedures;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.userprocedures.CreateProcedureDialog.ProcedureNameState;


public final class CreateProcedureVerifier extends InputVerifier {
    private CreateProcedureDialog listener;
    private final DomainModel domainModel;
    
    public CreateProcedureVerifier() {
        this.domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");
    }
    
    
    @Override
    public boolean verify(final JComponent component) {
        final JTextField source = (JTextField) component;
        
        final ProcedureNameState result = checkField(source);
        
        if (this.listener != null) {
            this.listener.setProcedureNameState(result);
            this.listener.handleEvent();
        }
        
        return true;
    }
    
    
    public void setListener(final CreateProcedureDialog aListener) {
        this.listener = aListener;
    }

    
    private ProcedureNameState checkField(final JTextField source) {
        final String text = source.getText();
        if (text.length() == 0) {
            return ProcedureNameState.NO_CHARACTERS;
        }
        if (containsWhitespace(text)) {
            return ProcedureNameState.HAS_WHITESPACE;
        }
        if (text.length() > CreateProcedureDialog.MAX_NAME_LENGTH) {
            return ProcedureNameState.TOO_LONG;
        }
        if (this.domainModel.isBlockNameTaken(text)) {
            return ProcedureNameState.ALREADY_USED;
        }
        return ProcedureNameState.VALID;
    }

    
    private boolean containsWhitespace(final String text) {
        // any whitespace character
        final Pattern pattern = Pattern.compile("\\s"); 
        final Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return true;
        }
        
        return false;
    }
}
