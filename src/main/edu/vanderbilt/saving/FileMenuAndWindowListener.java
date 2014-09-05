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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.*;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.driverandlayout.Loader;
import edu.vanderbilt.driverandlayout.ManualFrame;
import edu.vanderbilt.runcontroller.RunController;
import edu.vanderbilt.runview.RunButtonPanel;


/**
 * Listens for events on the menu bar and close 
 * button of the window, and initiates responses
 * to those events.
 */
public class FileMenuAndWindowListener 
    implements ActionListener, WindowListener, DialogListener {

    // chooser for opening files, with a filter
    private final JFileChooser openFileChooser; 
    
    // chooser for saving files, with no filter
    private final JFileChooser saveFileChooser;
        
    // used for opening saved or new models (resetting charts and model)
    private final RunController runController; 
    
    // when a user request is interrupted by a prompt to save, 
    // this variable stores the requested command
    // to be run if the user completes the save action 
    // (as opposed to closing or canceling the save dialog)
    private String commandToRunAfterDialog;
    
    // used for saving to the same file repeatedly
    private String saveFileLocation;
    
    private Autosave autosave;
    
    /*
     * Constructor.
     *
     */
    public FileMenuAndWindowListener() {
        this.openFileChooser = new JFileChooser(new File("."));
        this.saveFileChooser = new JFileChooser(new File("."));
        
        DependencyManager dep = DependencyManager.getDependencyManager();
        this.runController = dep.getObject(
            RunController.class, 
            "runController"
        );
        this.autosave = DependencyManager.getDependencyManager().
            getObject(Autosave.class, "autosave");
        
        StringIO.init();
        setupOpenFileChooser();
    }
    
    
    /**
     * Listen for File menu item events.
     * 
     * @param event the action event
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        
        // if user requests a new model
        if (command.equals(FileMenu.NEW_STRING)) {
            
            // check if the current model is saved. return if not and the user
            // cancels or clicks save and cancels that
            if (!checkShouldContinue(command)) {
                return;
            }
            
            openNewModel();
        } else if (command.equals(FileMenu.OPEN_STRING)) {
            // if user requests opening a saved model
            
            // check if the current model is saved. return if not and the user
            // cancels or clicks save and cancels that
            if (!checkShouldContinue(command)) {
                return;
            }
            
            openWithFileChooser();
        } else if (command.equals(FileMenu.SAVE_STRING)) {
            // if user requests saving the model
            
            if (Loader.shouldName()) {
                // if the current model is not saved, 
                // save with chooser to name it
                saveWithFileChooser();
            } else {
                // else, just save
                save();
            }
        } else if (command.equals(FileMenu.SAVE_AS_STRING)) {
            // if user wants to save with rename, do so
            saveWithFileChooser();
        } else if (command.equals(FileMenu.QUIT_STRING)) {
            // if user wants to quit
            
            // check if the current model is saved. return if not and the user
            // cancels or clicks save and cancels that
            if (!checkShouldContinue(command)) {
                return;
            }
            
            quit();
        } else if (command.equals(FileMenu.SET_IMAGE_STRING)) {
            // if user requests to change the background image in NetLogo
            changeBackgroundImage();
        } else if (command.equals(FileMenu.MANUAL_STRING)) {
            // if user requests to view the manual
            DependencyManager dep = DependencyManager.getDependencyManager();
            final ManualFrame manualFrame = dep.getObject(
                ManualFrame.class, 
                "manualFrame"
            );
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   manualFrame.goVisible();
               }
            });
        } else if (command.equals(FileMenu.MANUAL_BROWSER_STRING)) {
            // if user requests to launch manual in web broswer
            loadManualInBrowser();
        } else {
            // else an invalid event has been fired
            throw new IllegalArgumentException();        
        }
    }
    
    private void loadManualInBrowser() {
        final String fileName = "resources/manual.html";
        URI myUri = (new File(fileName)).toURI();
             
        
        try {
            Desktop.getDesktop().browse(myUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Save after picking a name in a file chooser.
     * 
     * @return whether the user "approved" a 
     * save location in the save file chooser
     */
    private boolean saveWithFileChooser() {
        assert SwingUtilities.isEventDispatchThread();
        
        // returnVal shows whether the user chosen a 
        // save location and clicked OK in the chooser
        final int returnVal = 
            this.saveFileChooser.showSaveDialog(GraphicalInterface.getFrame());
        
        // if the user did not click the save 
        // button in the chooser, return false
        if (!(returnVal == JFileChooser.APPROVE_OPTION)) {
            return false;
        }
        
        final File file = this.saveFileChooser.getSelectedFile();
        this.saveFileLocation = 
            file.getName() + "." + FileExtensionUtility.VIMAP_EXTENSION;
        GraphicalInterface.getFrame().setTitle(
            GraphicalInterface.TITLE_PREFIX_STRING + file.getName()
        ); 

        save();
        
        return true;
    }
    
    
    @Override
    public final void react(final String reply) {
        assert SwingUtilities.isEventDispatchThread();
        
        if (reply.equals(SaveWarningDialog.DISCARD_STRING)) {
            runCommandAfterDialog();
        } else if (reply.equals(SaveWarningDialog.SAVE_AS_STRING)) {            
            boolean savedSuccessfully = saveWithFileChooser();
            
            if (savedSuccessfully) {
                runCommandAfterDialog();
            }
        } else if (reply.equals(SaveWarningDialog.SAVE_STRING)) {
            save();
            runCommandAfterDialog();           
        }
    }
    
    
    /**
     * Runs the command stored in commandToRunAfterDialog.
     */
    public final void runCommandAfterDialog() {
        if (this.commandToRunAfterDialog == null) {
            throw new IllegalStateException();
        } else if (this.commandToRunAfterDialog.equals(FileMenu.NEW_STRING)) {
            openNewModel();
        } else if (this.commandToRunAfterDialog.equals(FileMenu.OPEN_STRING)) {
            openWithFileChooser();
        } else if (this.commandToRunAfterDialog.equals(FileMenu.QUIT_STRING)) {
            quit();
        } else {
            throw new IllegalStateException();
        }
    }
    
    
    /**
     * Listen for when the window's close button has been clicked.
     * 
     * @param arg0 unused
     */
    @Override
    public final void windowClosing(final WindowEvent arg0) {
        
        // check if the current model is saved. return if not and the user
        // cancels or clicks save and cancels that
        if (!checkShouldContinue(FileMenu.QUIT_STRING)) {
            return;
        }
        
        quit();
    }


    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowActivated(final WindowEvent arg0) {
        // do nothing
    }


    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowClosed(final WindowEvent arg0) {
        // do nothing
    }
    

    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowDeactivated(final WindowEvent arg0) {
        // do nothing
    }


    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowDeiconified(final WindowEvent arg0) {
        // do nothing
    }


    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowIconified(final WindowEvent arg0) {
        // do nothing
    }


    /**
     * Not implemented.
     * 
     * @param arg0 unused
     */
    @Override
    public void windowOpened(final WindowEvent arg0) {
        // do nothing
    }
    
    
    final RunController getRunController() {
        return this.runController;
    }
    
    
    /**
     * Clears the user's data for the current NetLogo file, 
     * producing a "new model" based
     * on that file.
     */
    private void openNewModel() {
        DependencyManager.getDependencyManager().
            getObject(MasterCodeController.class, "controller").clear();
        
        this.runController.run(true, false, false, 1, false);
        resetRun();
        Loader.newModelOpened();
        GraphicalInterface.getFrame().setTitle(
            GraphicalInterface.TITLE_PREFIX_STRING 
            + GraphicalInterface.UNTITLED_STRING
        ); 
        this.autosave.deleteLastAndChangeName();
    }
    
    
    /**
     * Calls the executing module to set up the NetLogo model.
     */
    private void resetRun() {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                getRunController().run(true, false, false, 1, false);
                return null;
            }
        };
        worker.execute();
    }
    
    
    /**
     * Saves the user's current model to disk.
     */
    private void save() {        
        final String textToSave = StringIO.getTextToSave();
        StringIO.printToFile(textToSave, this.saveFileLocation);
        Loader.modelSaved();
        this.autosave.deleteLastAndChangeName();
    }

    
    /**
     * Set up the filter on the chooser for opening 
     * files, to permit opening only .vimap files.
     */
    private void setupOpenFileChooser() {
        this.openFileChooser.setAcceptAllFileFilterUsed(false);
        this.openFileChooser.setFileFilter(new VimapFileFilter());
    }
    
    private void changeBackgroundImage() {
        assert SwingUtilities.isEventDispatchThread();
        
        new ChooseImageDialog(GraphicalInterface.getFrame()).setVisible(true);  
    }
    
    
    /**
     * Provides a file chooser and opens the selected .vimap file.
     */
    private void openWithFileChooser() {
        final int returnVal = 
            this.openFileChooser.showOpenDialog(GraphicalInterface.getFrame());
        
        // if the user does not click OK in the chooser
        if (!(returnVal == JFileChooser.APPROVE_OPTION)) {
            return;
        }
        
        final File file = this.openFileChooser.getSelectedFile();
        StringIO.loadObjectFromFile(file);
        
        // returns the original file name if not an autosave file
        this.saveFileLocation = MyAutosave.getBaseName(file.getName());
        if (MyAutosave.isAutosaveName(file.getName())) {
            // if the user has opened an autosave file, the
            // file state should be "unsaved"
            Loader.modelEdited();
        }
        this.runController.run(true, false, false, 1, false);
        RunButtonPanel runControls = DependencyManager.getDependencyManager().
            getObject(RunButtonPanel.class, "runControls");
        runControls.enableStopDisableOthers();
        resetRun();
        this.autosave.deleteLastAndChangeName();
    }
    
    
    /**
     * Check if the model is saved, and if not, ask the user to save.
     * 
     * @param command the command string the user was trying to call
     * @return true if model is saved already or newly created, else false
     */
    private boolean checkShouldContinue(final String command) {
        assert SwingUtilities.isEventDispatchThread();
        
        if (!Loader.shouldSave()) {
            return true;
        }
    
        askToSave(command);
        return false;
    }
    
    
    /**
     * Ask the user if they want to save with a dialog. 
     * Store the command string the user
     * was trying to call so it can be run later, 
     * e.g. if the user clicks "Discard"
     * 
     * @param command the command the user was trying to call
     */
    private void askToSave(final String command) {
        assert SwingUtilities.isEventDispatchThread();

        this.commandToRunAfterDialog = command;
        
        new SaveWarningDialog(
            FileMenuAndWindowListener.this, 
            Loader.shouldName()
        ).setVisible(true);
    }
    
    
    /**
     * Should be called only on the Event Dispatch Thread.
     * 
     * Quit the program: Shut the window and exit.
     */
    private void quit() {
        GraphicalInterface.getFrame().setVisible(false);
        GraphicalInterface.getFrame().dispose();
        Loader.shutDown();
    }
}
