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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public final class CreateSetDialog extends JDialog 
    implements ActionListener, DocumentListener {
    
    private static final long serialVersionUID = -3731188733799951902L;
    private static final String CANCEL_STRING = "Cancel";
    private static final String OKAY_STRING = "OK";
    private static final String TITLE_STRING = "Create group";
    private final JTextField textField;
    private static final int TEXT_FIELD_SIZE = 15;
    private final JLabel textFieldLabel;
    private JButton okayButton;
    private JavaBlockStrategy strategy;


    public CreateSetDialog(
        final JFrame frame, 
        final JavaBlockStrategy aStrategy
    ) {
        super(
            frame, 
            TITLE_STRING, 
            true // modal dialog (can't click anything else)
       );
                
        assert SwingUtilities.isEventDispatchThread();
        this.setLayout(new BorderLayout());
        
        this.textField = new JTextField(TEXT_FIELD_SIZE);
        this.textField.getDocument().addDocumentListener(this);
        
        this.textFieldLabel = new JLabel();
        this.textFieldLabel.setForeground(Color.RED);
        
        this.add(getTextFieldPanel(), BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        
        this.strategy = aStrategy;
        
        pack();
    }
    
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(CANCEL_STRING)) {
            closeWindow();
        } else if (command.equals(OKAY_STRING)) {
            this.strategy.createSetNamed(this.textField.getText());
            closeWindow();
        } else {
            throw new IllegalStateException();
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
    
    
    private JPanel getTextFieldPanel() {
        final JPanel result = new JPanel(new BorderLayout());
        
        result.add(this.textField, BorderLayout.CENTER);
        
        this.textFieldLabel.setText("Enter a name and hit tab.");
        result.add(this.textFieldLabel, BorderLayout.EAST);
        return result;
    }
    

    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
    

    @Override
    public void changedUpdate(final DocumentEvent arg0) {
        // not used
    }

    
    @Override
    public void insertUpdate(final DocumentEvent arg0) {
        assert SwingUtilities.isEventDispatchThread();
        handleTextChange();
    }

    
    @Override
    public void removeUpdate(final DocumentEvent arg0) {
        assert SwingUtilities.isEventDispatchThread();
        handleTextChange();
    }
    
    
    private void handleTextChange() {
        if (this.textField.getText().length() == 0) {
            this.okayButton.setEnabled(false);
        } else {
            this.okayButton.setEnabled(true);
        }
    }
}
