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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

/**
 * A dialog box that is displayed to allow the user to specify
 * the set type and set name of a set to be created.
 * 
 * Checks to be sure the name entered in its text field is legal.
 */
public final class CreateSetDialog extends JDialog 
    implements ActionListener, ItemListener {

    private static final long serialVersionUID = -211253017867544623L;
    
    // title for title bar
    private static final String TITLE_STRING = "Create Group";
    private MasterCodeController codeController;
    private final JComboBox<String> setTypeComboBox;
    private final SetsMenu setsMenu;
    
    /**
     * Whether the string entered by the user is valid.
     */
    enum SetNameState {
        VALID, 
        NO_CHARACTERS, // empty string entered
        TOO_LONG, // too many characters
        INVALID_CHARACTERS, // illegal characters
        ALREADY_USED // name is taken
    }

    private static final String CANCEL_STRING = "Cancel";
    private static final String OKAY_STRING = "OK";
    private SetNameState setNameState;
    private JButton okayButton;
    
    // used to inform user if their text is invalid
    private final JLabel textFieldLabel;
    private static final int TEXT_FIELD_SIZE = 15;
    private final JTextField textField;

    public CreateSetDialog(
        final JFrame aFrame, 
        final CreateSetVerifier aCreateSetVerifier,
        final SetsMenu aSetsMenu
    ) {
        super(aFrame, TITLE_STRING, true); // modal
        
        assert SwingUtilities.isEventDispatchThread();
        this.setsMenu = aSetsMenu;
        
        // display 1/3 from top-left of screen
        final int windowFraction = 3;
        this.setLocation(
            aFrame.getWidth() / windowFraction, 
            aFrame.getHeight() / windowFraction
        );
        this.setLayout(new BorderLayout());
        
        // no characters entered yet
        this.setNameState = SetNameState.NO_CHARACTERS;
        this.textField = new JTextField(TEXT_FIELD_SIZE);
        
        this.textFieldLabel = new JLabel();
        this.textFieldLabel.setForeground(Color.RED);
        
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
    
        DomainModel domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");        
        String[] items = new String[domainModel.getSetTemplates().size() - 1];
        int i = 0;
        for (SetTemplate setTemplate: domainModel.getSetTemplates()) {
            // don't include "default" as an available set type to use,
            // because the user can't create default sets (all or other)
            if (!setTemplate.isDefault()) {
                items[i] = setTemplate.getName();
                i++;
            }
        }
        this.setTypeComboBox = new JComboBox<String>(items);
        
        this.add(getSetTypePanel(), BorderLayout.NORTH);
        this.add(getNamePanel(aCreateSetVerifier), BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        pack();
        this.setResizable(false);
        
        aCreateSetVerifier.setListener(this);
        this.textField.requestFocusInWindow();
    }
    
    private JPanel getNamePanel(final CreateSetVerifier verifier) {
        final JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        
        result.add(new JLabel("Group Name: "));
        this.textField.setInputVerifier(verifier);
        result.add(this.textField);
        
        this.textFieldLabel.setText("Enter a name and hit tab.");
        result.add(this.textFieldLabel);
        return result;
    }
    
    private JPanel getSetTypePanel() {
        final JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        
        final JLabel setTypeLabel = 
            new JLabel("Group Type: ");
        result.add(setTypeLabel);
        result.add(this.setTypeComboBox);
        return result;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(CANCEL_STRING)) {
            closeWindow();
        } else if (command.equals(OKAY_STRING)) {
            this.setsMenu.updateEnabledState();
            closeWindow();
            
            // must call after closeWindow, or the instructions
            // dialog box will be shown at the same time as this one.
            this.codeController.requestAddSetBeforeArgsKnown(
                (String) this.setTypeComboBox.getSelectedItem(), 
                this.textField.getText()
            );
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent event) {
        handleEvent();
    }
    
    public void setSetNameState(final SetNameState state) {
        this.setNameState = state;
    }
    
    public void handleEvent() {
        assert SwingUtilities.isEventDispatchThread();
        
        this.updateButtonState();
        
        switch (this.setNameState) {
        case VALID:
            this.textFieldLabel.setText("");
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
        case INVALID_CHARACTERS:
            this.textFieldLabel.setText("Name contains illegal characters.");
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
    
    private void updateButtonState() {
        if (this.setNameState == SetNameState.VALID) {
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
