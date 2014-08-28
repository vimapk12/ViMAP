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
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;

public final class ChooseImageDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 4046194413569318974L;
    
    // title for title bar
    private static final String TITLE_STRING = "Set Background Image";
    private MasterCodeController codeController;
    
    private static final String CANCEL_STRING = "Cancel";
    private static final String USE_DEFAULT_STRING = "Use Default";
    private static final String SELECT_IMAGE_STRING = "Select Image";
    
    private final JFileChooser imageFileChooser;

    
    public ChooseImageDialog(final JFrame aFrame) {
        super(aFrame, TITLE_STRING, true); // modal
        
        assert SwingUtilities.isEventDispatchThread();
        this.imageFileChooser = new JFileChooser(new File("."));
        setupImageFileChooser();
        
        // display 1/3 from top-left of screen
        final int windowFraction = 3;
        this.setLocation(
            aFrame.getWidth() / windowFraction, 
            aFrame.getHeight() / windowFraction
        );
        this.setLayout(new BorderLayout());
        
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
        
        final DomainModel domainModel = 
            DependencyManager.getDependencyManager().
                getObject(DomainModel.class, "domainModel");
        
        String labelText = "Current Image: " 
            + domainModel.getImageFileName() + " ";
        this.add(new JLabel(labelText), BorderLayout.NORTH);
        
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        pack();
        this.setResizable(false);
    }


    @Override
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        if (command.equals(CANCEL_STRING)) {
            closeWindow();
        } else if (command.equals(USE_DEFAULT_STRING)) {
            closeWindow();
            
            this.codeController.resetImageFileName();
        } else if (command.equals(SELECT_IMAGE_STRING)) {
            closeWindow();
            
            final int returnVal = 
                this.imageFileChooser.showOpenDialog(
                    GraphicalInterface.getFrame()
                );
                
                // if the user does not click OK in the chooser
                if (!(returnVal == JFileChooser.APPROVE_OPTION)) {
                    return;
                }
                
                try {
                    // have to use canonical path so 
                    // NetLogo will find the same file by name
                    final String newName = 
                        this.imageFileChooser.getSelectedFile().
                            getCanonicalPath();
                    this.codeController.setImageFileName(newName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
            throw new IllegalStateException();
        }        
    }
    
    private void setupImageFileChooser() {
        this.imageFileChooser.setAcceptAllFileFilterUsed(false);
        this.imageFileChooser.setFileFilter(new ImageFileFilter());
    }

    private JPanel getButtonPanel() {
        final JPanel result = new JPanel();
        
        final JButton cancelButton = new JButton(CANCEL_STRING);
        cancelButton.setActionCommand(CANCEL_STRING);
        cancelButton.addActionListener(this);
        result.add(cancelButton);
       
        final JButton useDefaultButton = new JButton(USE_DEFAULT_STRING);
        useDefaultButton.setActionCommand(USE_DEFAULT_STRING);
        useDefaultButton.addActionListener(this);
        result.add(useDefaultButton);
        
        final JButton selectImageButton = new JButton(SELECT_IMAGE_STRING);
        selectImageButton.setActionCommand(SELECT_IMAGE_STRING);
        selectImageButton.addActionListener(this);
        result.add(selectImageButton);
    
        return result;
    }
    
    private void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }
}
