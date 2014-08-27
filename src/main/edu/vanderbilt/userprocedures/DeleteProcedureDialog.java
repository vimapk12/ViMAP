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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;


public final class DeleteProcedureDialog 
    extends JDialog implements ActionListener {

    private static final long serialVersionUID = -2186342435014019068L;
    private final JComboBox<String> procedureNameMenu;
    private static final String CANCEL_STRING = "Cancel";
    private static final String OKAY_STRING = "OK";
    private static final String TITLE_STRING = "Delete Procedure";
    private JButton okayButton;
    private final ProceduresMenu proceduresMenu;
    private MasterCodeController codeController = 
        DependencyManager.getDependencyManager().
        getObject(MyMasterCodeController.class, "controller");
    
    /**
     * Constructor.
     * 
     * @param aFrame the frame to use as the background frame of the dialog
     * @param aProceduresMenu the menu to update after the list of procedures
     * has changed
     */
    public DeleteProcedureDialog(
        final JFrame aFrame,
        final ProceduresMenu aProceduresMenu
   ) {
        super(
            aFrame, 
            TITLE_STRING, 
            true // modal dialog (can't click anything else)
       );
        
        assert SwingUtilities.isEventDispatchThread();

        this.setLayout(new BorderLayout());
        final int windowFraction = 3;
        this.setLocation(
            aFrame.getWidth() / windowFraction, 
            aFrame.getHeight() / windowFraction
        );
        
        this.proceduresMenu = aProceduresMenu;
        
        DomainModel domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");
        Set<String> userProcedures = domainModel.getUniqueUserProcedureNames();
        String[] menuItems = new String[userProcedures.size()];
        Iterator<String> iter = userProcedures.iterator();
        int index = 0;
        while (iter.hasNext()) {
            menuItems[index] = iter.next();
            index++;
        }
        this.procedureNameMenu = new JComboBox<String>(menuItems);
        this.add(this.procedureNameMenu, BorderLayout.NORTH);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        
        final int preferredWidth = 250;
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
            final String procedureToRemove = 
                this.procedureNameMenu.getSelectedItem().toString();
            
            this.codeController.deleteUserProcedure(procedureToRemove);
            this.proceduresMenu.updateEnabledState();
            closeWindow();
        } else {
            throw new IllegalArgumentException();
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
        result.add(this.okayButton);
    
        return result;
    }
    
    
    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
}
