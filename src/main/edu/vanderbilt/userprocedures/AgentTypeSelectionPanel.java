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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public final class AgentTypeSelectionPanel extends JPanel {
    
    private static final long serialVersionUID = -3471236843110212583L;
    private final List<JCheckBox> jCheckBoxList;
    
    
    public AgentTypeSelectionPanel(final List<String> agentTypes) {
        super();
        
        assert SwingUtilities.isEventDispatchThread();
        this.jCheckBoxList = new ArrayList<JCheckBox>();
        
        for (String agentType: agentTypes) {
            final JCheckBox currentCheckBox = new JCheckBox(agentType);
            currentCheckBox.setActionCommand(agentType);
            currentCheckBox.setSelected(true);

            this.add(currentCheckBox);
            this.jCheckBoxList.add(currentCheckBox);
        }
    }
    
    
    public List<String> getSelectedList() {
        final List<String> result = new ArrayList<String>();
        
        for (final JCheckBox currentCheckBox: this.jCheckBoxList) {
            if (currentCheckBox.isSelected()) {
                result.add(currentCheckBox.getText());
            }
        }
        
        return result;
    }
    
    
    public List<JCheckBox> getJCheckBoxList() {
        return this.jCheckBoxList;
    }


    public void setListener(final CreateProcedureDialog createProcedureDialog) {
        if (createProcedureDialog == null) {
            throw new IllegalArgumentException();
        }
        
        for (JCheckBox currentBox: this.jCheckBoxList) {
            currentBox.addItemListener(createProcedureDialog);
        }
    }
}
