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


package edu.vanderbilt.mac;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.driverandlayout.Loader;
import edu.vanderbilt.saving.DialogListener;
import edu.vanderbilt.saving.FileExtensionUtility;
import edu.vanderbilt.saving.SaveWarningDialog;
import edu.vanderbilt.saving.StringIO;

public final class MacQuitHandler 
    implements QuitHandler, DialogListener, WindowListener {

    private QuitResponse quitResponse;
    
    @Override
    public void handleQuitRequestWith(
        final QuitEvent event,
        final QuitResponse response
   ) {        
        if (!Loader.shouldSave()) {
            Loader.shutDown();
        }
    
        this.quitResponse = response;
    
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               SaveWarningDialog dialog = 
                   new SaveWarningDialog(
                       MacQuitHandler.this, 
                       Loader.shouldName()
                   );
               dialog.addWindowListener(MacQuitHandler.this);
               dialog.setVisible(true);
           }
        });
    }
    
    
    @Override
    public void react(final String reply) {
        assert this.quitResponse != null;

        if (reply == null) {
            return;
        } else if (reply.equals(SaveWarningDialog.DISCARD_STRING)) {
            Loader.shutDown();
        } else if (reply.equals(SaveWarningDialog.CANCEL_STRING)) {
            this.quitResponse.cancelQuit();
        } else if (reply.equals(SaveWarningDialog.SAVE_AS_STRING)) {    
            JFileChooser chooser = new JFileChooser(new File("."));
            
            // returnVal shows whether the user chosen a 
            // save location and clicked OK in the chooser
            final int returnVal = 
                chooser.showSaveDialog(GraphicalInterface.getFrame());
            
            // if the user did not click the save button 
            // in the chooser, return false
            if (!(returnVal == JFileChooser.APPROVE_OPTION)) {
                this.quitResponse.cancelQuit();
                return;
            }
            
            final File file = chooser.getSelectedFile();
            String location = 
                file.getName() + "." + FileExtensionUtility.VIMAP_EXTENSION;
            GraphicalInterface.getFrame().setTitle(
                GraphicalInterface.TITLE_PREFIX_STRING + file.getName()
            );
            final String textToSave = StringIO.getTextToSave();
            StringIO.printToFile(textToSave, location);
            Loader.modelSaved();
            
            Loader.shutDown();
        }
    }


    @Override
    public void windowActivated(final WindowEvent arg0) {
        // do nothing        
    }


    @Override
    public void windowClosed(final WindowEvent arg0) {
        // do nothing        
    }


    @Override
    public void windowClosing(final WindowEvent arg0) {
        assert this.quitResponse != null;
        this.quitResponse.cancelQuit();
    }


    @Override
    public void windowDeactivated(final WindowEvent arg0) {
        // do nothing
    }


    @Override
    public void windowDeiconified(final WindowEvent arg0) {
        // do nothing
    }


    @Override
    public void windowIconified(final WindowEvent arg0) {
        // do nothing
    }


    @Override
    public void windowOpened(final WindowEvent arg0) {
        // do nothing
    }
}
