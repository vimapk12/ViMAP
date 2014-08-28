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


package edu.vanderbilt.codeview;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;


public final class AgentProcedureSelectView 
    extends JPanel implements ItemListener {

    private static final long serialVersionUID = -4096327839443664470L;
    private final JComboBox<String> agentComboBox;
    private JComboBox<String> procedureComboBox;
    private final MasterCodeController codeController;
    private AtomicBoolean acceptInput;
    
    public AgentProcedureSelectView(
        final String aCodeController
    ) {
        super(new FlowLayout());

        assert SwingUtilities.isEventDispatchThread();
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, aCodeController);
        
        this.agentComboBox = new JComboBox<String>();
        this.agentComboBox.addItemListener(this);
        JLabel agentLabel = new JLabel("Agent: ");
        agentLabel.setToolTipText("Choose agent to edit");
        int counter = 0;
        this.add(agentLabel, counter);
        counter++;
        this.add(this.agentComboBox, counter);
        counter++;
        this.agentComboBox.setToolTipText("Choose agent to edit");
                
        this.procedureComboBox = new JComboBox<String>();
        this.procedureComboBox.addItemListener(this);
        JLabel procedureLabel = new JLabel("Procedure:");
        procedureLabel.setToolTipText("Choose procedure to edit");
        this.add(procedureLabel, counter);
        counter++;
        this.add(this.procedureComboBox, counter);
        this.procedureComboBox.setToolTipText("Choose procedure to edit");
        this.acceptInput = new AtomicBoolean(true);
    }
    
    public void setMenusEnabled(final boolean isEnabled) { 
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getAgentComboBox().setEnabled(isEnabled);
               getProcedureComboBox().setEnabled(isEnabled);              
           }
        });
    }
    
    public void setAgentMenuItems(final List<String> agents) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                acceptInput(false);
                getAgentComboBox().removeAllItems();
                for (String agent: agents) {
                    getAgentComboBox().addItem(agent);
                }
                acceptInput(true);
            }
         });       
    }
    
    public void setProcedureMenuItems(final List<String> procedures) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               acceptInput(false);
               getProcedureComboBox().removeAllItems();
               for (String method: procedures) {
                   getProcedureComboBox().addItem(method);
               }
               acceptInput(true);
           }
        });
    }
    
    public void setSelectedAgent(final String agent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                acceptInput(false);
                getAgentComboBox().setSelectedItem(agent);
                acceptInput(true);
            }
         });        
    }
    
    public void setSelectedMethod(final String method) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                acceptInput(false);
                getProcedureComboBox().setSelectedItem(method);
                acceptInput(true);
            }
         });        
    }

    public void setSelectedItems(final String agent, final String method) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               acceptInput(false);
               getAgentComboBox().setSelectedItem(agent);
               getProcedureComboBox().setSelectedItem(method);
               acceptInput(true);
           }
        });
    }
    
    public void setSelectedIndices(
        final int agentIndex, 
        final int methodIndex
    ) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               acceptInput(false);
               getAgentComboBox().setSelectedIndex(agentIndex);
               getProcedureComboBox().setSelectedIndex(methodIndex);
               acceptInput(true);
           }
        });
    }

    @Override
    public void itemStateChanged(final ItemEvent event) { 
        if (!this.acceptInput.get()) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        final JComboBox<String> comboBox = 
            (JComboBox<String>) event.getSource();
        if (comboBox.equals(this.agentComboBox)) {
            handleAgentEvent(event);
        } else if (comboBox.equals(this.procedureComboBox)) {
            handleProcedureEvent(event);
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    JComboBox<String> getAgentComboBox() {
        return this.agentComboBox;
    }
    
    JComboBox<String> getProcedureComboBox() {
        return this.procedureComboBox;
    }
    
    void setMethodComboBox(final JComboBox<String> comboBox) {
        this.procedureComboBox = comboBox;
    }
    
    void acceptInput(final boolean accept) {
        this.acceptInput.set(accept);
    }
    
    private void handleAgentEvent(final ItemEvent event) {
        if (event.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        @SuppressWarnings("unchecked")
        final JComboBox<String> comboBox = 
            (JComboBox<String>) event.getSource();
        final String newAgentType = comboBox.getSelectedItem().toString();
        this.codeController.setAgent(newAgentType, false);
    }
    
    private void handleProcedureEvent(final ItemEvent event) {
        if (event.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        final JComboBox<String> comboBox = 
            (JComboBox<String>) event.getSource();        
        final String newProcedure = comboBox.getSelectedItem().toString();
        this.codeController.setProcedure(newProcedure);
    }
}
