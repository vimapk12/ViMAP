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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;


public final class CreateProcedureDialog 
    extends JDialog implements ActionListener, ItemListener {
    
    private static final long serialVersionUID = -5288309289222258522L;
    private MasterCodeController codeController;

    enum ProcedureNameState {
        VALID, 
        NO_CHARACTERS, 
        TOO_LONG, 
        HAS_WHITESPACE, 
        ALREADY_USED
    }

    private final AgentTypeSelectionPanel agentTypeSelectionPanel;
    private static final String CANCEL_STRING = "Cancel";
    private static final String OKAY_STRING = "OK";
    private static final String TITLE_STRING = "Create Procedure";
    private ProcedureNameState procedureNameState;
    private JButton okayButton;
    private final JLabel textFieldLabel;
    private static final int TEXT_FIELD_SIZE = 15;
    private final JTextField textField;
    private final ProceduresMenu proceduresMenu;
    
    public static final int MAX_NAME_LENGTH = 12;

    public CreateProcedureDialog(
        final JFrame aFrame,
        final CreateProcedureVerifier aCreateProcedureVerifier,
        final ProceduresMenu aProceduresMenu
   ) {
        super(
            aFrame, 
            TITLE_STRING, 
            true // modal dialog (can't click anything else)
       );
                
        assert SwingUtilities.isEventDispatchThread();
        final int windowFraction = 3;
        this.setLocation(
            aFrame.getWidth() / windowFraction, 
            aFrame.getHeight() / windowFraction
        );
        this.setLayout(new BorderLayout());
        
        this.procedureNameState = ProcedureNameState.NO_CHARACTERS;
        this.textField = new JTextField(TEXT_FIELD_SIZE);
        
        this.textFieldLabel = new JLabel();
        this.textFieldLabel.setForeground(Color.RED);
        
        DomainModel mainServer = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");
        this.agentTypeSelectionPanel = 
            new AgentTypeSelectionPanel(mainServer.getAgentTypeNames());    
        this.agentTypeSelectionPanel.setListener(this);
        
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
        
        this.add(getAgentChoicePanel(), BorderLayout.NORTH); 
        
        JPanel methodAndTextPanel = new JPanel(new BorderLayout());
        methodAndTextPanel.add(getMethodChoicePanel(), BorderLayout.NORTH);
        methodAndTextPanel.add(
            getTextFieldPanel(aCreateProcedureVerifier), 
            BorderLayout.CENTER
        );
        
        this.add(methodAndTextPanel, BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        
        pack();
        this.setResizable(false);

        this.proceduresMenu = aProceduresMenu;
        aCreateProcedureVerifier.setListener(this);
        this.textField.requestFocusInWindow(); 
    }


    public void setProcedureNameState(
        final ProcedureNameState aProcedureNameState
    ) {
        this.procedureNameState = aProcedureNameState;
    }
    
    
    /*
     * May only be called on Event Dispatch Thread, for thread safety.
     */
    public void handleEvent() {
        assert SwingUtilities.isEventDispatchThread();
        
        this.updateButtonState();
        
        switch (this.procedureNameState) {
            case VALID:
                if (this.agentTypeSelectionPanel.getSelectedList().isEmpty()) {
                    this.textFieldLabel.setText("Check at least one box.");
                } else {
                    this.textFieldLabel.setText("");
                }
                break;
            case NO_CHARACTERS:
                this.textFieldLabel.setText("Enter a name.");
                break;
            case TOO_LONG:
                this.textFieldLabel.setText("Name is too long.");
                break;
            case ALREADY_USED:
                this.textFieldLabel.setText("That name is taken.");
                break;
            case HAS_WHITESPACE:
                this.textFieldLabel.setText("No spaces allowed.");
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Override
    public void itemStateChanged(final ItemEvent event) {
        handleEvent();
    }
    
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(CANCEL_STRING)) {
            closeWindow();
        } else if (command.equals(OKAY_STRING)) {
            this.codeController.addUserProcedure(
                this.textField.getText(), 
                this.agentTypeSelectionPanel.getSelectedList() 
            );
            
            this.proceduresMenu.updateEnabledState();
            closeWindow();
        } else {
            throw new IllegalStateException();
        }
    }
    
    
    private JPanel getMethodChoicePanel() {
        JPanel result = new JPanel();
        
        final JLabel methodLabel = new JLabel("What is the method's name?");
        result.add(methodLabel);
        
        return result;
    }


    private JPanel getAgentChoicePanel() {
        final JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        
        final JLabel agentLabel = 
            new JLabel("Which agents will have the method?");
        result.add(agentLabel);
        result.add(this.agentTypeSelectionPanel);
        
        return result;
    }


    private JPanel getTextFieldPanel(
        final CreateProcedureVerifier createProcedureVerifier
    ) {
        final JPanel result = new JPanel(new BorderLayout());
        
        this.textField.setInputVerifier(createProcedureVerifier);
        result.add(this.textField, BorderLayout.CENTER);
        
        this.textFieldLabel.setText("Enter a name and hit tab.");
        result.add(this.textFieldLabel, BorderLayout.EAST);
        return result;
    }
    
    
    /*
     * May only be called from Event Dispatch Thread, for thread safety.
     */
    private void updateButtonState() {
        if (
            this.procedureNameState == ProcedureNameState.VALID 
            && !this.agentTypeSelectionPanel.getSelectedList().isEmpty() 
       ) {
            this.okayButton.setEnabled(true);
        } else {
            this.okayButton.setEnabled(false);
        }
    }
    
    
    private JPanel getButtonPanel() {
        final JPanel result = new JPanel();
        
        final JButton cancelButton = new JButton(CANCEL_STRING);
        cancelButton.setActionCommand(CANCEL_STRING);
        cancelButton.addActionListener(this);
        result.add(cancelButton);
        
        this.okayButton = new JButton(OKAY_STRING);
        this.okayButton.setActionCommand(OKAY_STRING);
        this.okayButton.addActionListener(this);
        this.okayButton.setEnabled(false);
        result.add(this.okayButton);
    
        return result;
    }
    
    
    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
}
