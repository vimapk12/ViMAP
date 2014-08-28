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


package edu.vanderbilt.saving;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.driverandlayout.GraphicalInterface;


public class SaveWarningDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 490352686818354365L;

    private final DialogListener dialogListener;
    
    public static final String DISCARD_STRING = "Don't Save";
    public static final String CANCEL_STRING = "Cancel";
    public static final String SAVE_AS_STRING = "Save...";
    public static final String SAVE_STRING = "Save";
    public static final String TITLE_STRING = "Save Changes?";
    
    
    public SaveWarningDialog(
        final DialogListener aDialogListener,
        final boolean isSaveAs
    ) {
        super(
            GraphicalInterface.getFrame(), 
            TITLE_STRING,
            true // modal
        );
        
        assert SwingUtilities.isEventDispatchThread();
        this.setLayout(new BorderLayout());
        this.setLocation(
            GraphicalInterface.getFrame().getWidth() / 2, 
            GraphicalInterface.getFrame().getHeight() / 2
        );
        
        this.dialogListener = aDialogListener;
        this.add(getButtonPanel(isSaveAs), BorderLayout.SOUTH);
        pack();
        this.setResizable(false);
    }

    
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        this.dialogListener.react(command);
        closeWindow();
    }
    
    
    /*
     * Should be called only from constructor, for thread safety.
     */
    private JPanel getButtonPanel(final boolean isSaveAs) {
        final JPanel result = new JPanel();
        
        final JButton discardButton = new JButton(DISCARD_STRING);
        discardButton.addActionListener(this);
        result.add(discardButton);
        
        final JButton cancelButton = new JButton(CANCEL_STRING);
        cancelButton.addActionListener(this);
        result.add(cancelButton);
        
        if (isSaveAs) {
            final JButton saveAsButton = new JButton(SAVE_AS_STRING);
            saveAsButton.addActionListener(this);
            result.add(saveAsButton);
        } else {
            final JButton saveButton = new JButton(SAVE_STRING);
            saveButton.addActionListener(this);
            result.add(saveButton);  
        }

    
        return result;
    }
    
    
    /*
     * Should be called only on Event Dispatch Thread.
     */
    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
}
