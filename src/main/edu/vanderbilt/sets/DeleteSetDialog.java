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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.util.Util;

public final class DeleteSetDialog extends JDialog 
    implements ActionListener {

    private static final long serialVersionUID = 2689315880087030385L;
    private static final String TITLE_STRING = "Delete Group";
    private MasterCodeController codeController;
    private final JComboBox<String> setNameComboBox;
    private final SetsMenu setsMenu;
    private static final String DELIMITER = "   ";
    
    private static final String CANCEL_STRING = "Cancel";
    private static final String OKAY_STRING = "OK";
    private JButton okayButton;
        
    public DeleteSetDialog(
        final JFrame aFrame, 
        final SetsMenu aSetsMenu
    ) {
        super(aFrame, TITLE_STRING, true); // modal
        
        assert SwingUtilities.isEventDispatchThread();
        this.setsMenu = aSetsMenu;
        
        // display dialog box 1/3 from top-left of screen
        final int windowFraction = 3;
        this.setLocation(
            aFrame.getWidth() / windowFraction, 
            aFrame.getHeight() / windowFraction
        );
        this.setLayout(new BorderLayout());
        
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
        
        
        DomainModel domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");        
        List<String> items = new ArrayList<String>();
        for (SetInstance setInstance: domainModel.getSetInstances()) {
            // don't let the user delete a "default" type set,
            // such as "all" or "other"
            if (!setInstance.getSetTemplate().isDefault()) {
                // display options as "setName    <setType>"
                items.add(
                    setInstance.getSetName() + DELIMITER 
                    + "<" + setInstance.getSetTemplate().getName() + ">"
                );
            }
        }
        this.setNameComboBox = 
            new JComboBox<String>(Util.getStringArray(items));
                
        this.add(getSetNamePanel(), BorderLayout.NORTH);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        
        final int preferredWidth = 350;
        final int preferredHeight = 100;
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        
        pack();
        this.setResizable(false);
    }
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(CANCEL_STRING)) {
            closeWindow();
        } else if (command.equals(OKAY_STRING)) {
            // get the set name from the text in the field,
            // which has format "setName      <setType>"
            final String namePlusType = 
                (String) this.setNameComboBox.getSelectedItem();
            final int delimiterIndex = namePlusType.indexOf(DELIMITER);
            this.codeController.deleteSet(
                namePlusType.substring(0, delimiterIndex)
            );
            this.setsMenu.updateEnabledState();
            closeWindow();
        } else {
            throw new IllegalStateException();
        }
    }
    
    private JPanel getSetNamePanel() {
        final JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        
        final JLabel setTypeLabel = 
            new JLabel("Group Name: ");
        result.add(setTypeLabel);
        result.add(this.setNameComboBox);
        return result;
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
        result.add(this.okayButton);
    
        return result;
    }
    
    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
}
