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


package edu.vanderbilt.driverandlayout;

import java.awt.KeyboardFocusManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.domainmodel.DomainModelImporter;
import edu.vanderbilt.runcontroller.RunController;
import edu.vanderbilt.saving.Autosave;
import edu.vanderbilt.saving.SavableModel;
import edu.vanderbilt.simulation.SimulationCaller;
import edu.vanderbilt.usermodel.UserModel;


/**
 * Helper class that creates a new Model class and 
 * tells it to set itself up. This
 * class can open, close, and reload NetLogo models.
 */
public abstract class Loader {
   
    public static final String VERSION_STRING = "0.3.0";
    
    public static final String VIMAP_LOGO_FILE = 
        "/images/vimapLogo.png";
       
    private static boolean shouldSave;
    private static boolean isNamed;
    
    /**
     * Creates a new Model by opening the NetLogo 
     * file named in the String, setting it up,
     * and running the code editor.
     * 
     * @param aNetLogoFile (required) the NetLogo file to open.
     */
    public static void openModel(final String aNetLogoFile) {
        assert !SwingUtilities.isEventDispatchThread();
        
        String fileText = getFileTextFromUrl(aNetLogoFile);
        // String fileText = getFileTextFromLocalFile(aNetLogoFile);
        
        shouldSave = false;
        isNamed = false;
        
        SimulationCaller.loadBaseModel(
            fileText, 
            GraphicalInterface.getFrame(), 
            GraphicalInterface.getNetLogoPanel() 
       );
    }
    
    public static boolean isRunningJavaWebStart() {
        boolean hasJNLP = false;
        try {
          Class.forName("javax.jnlp.ServiceManager");
          hasJNLP = true;
        } catch (ClassNotFoundException ex) {
          hasJNLP = false;
        }
        return hasJNLP;
    }
    
    // @SuppressWarnings("unused")
    private static String getFileTextFromUrl(final String fileName) {
        URL url = null;
        if (Loader.isRunningJavaWebStart()) {
            url = Loader.class.getClassLoader().
                getResource(fileName.substring(1));
        } else {
            url = Thread.currentThread().getClass().getResource(fileName);
        }
        
        if (url == null) {
            Loader.errorShutDown("Could not open file: " + fileName);
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                sb.append('\n');
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Loader.errorShutDown("Could not open file: " + fileName);
            return null;
        }
        
        return sb.toString();
    }
    
    @SuppressWarnings("unused")
    private static String getFileTextFromLocalFile(final String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = 
                new BufferedReader(new FileReader(fileName.substring(1)));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                sb.append('\n');
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Loader.errorShutDown(
                "Could not open file: " + fileName.substring(1)
            );
        }
        
        return sb.toString();
    }
    
    public static void newModelOpened() {
        shouldSave = false;
        isNamed = false;
    }
    
    private static void displayWrongModelMessage() {
        JOptionPane.showMessageDialog(
            GraphicalInterface.getFrame(),
            "That file was created for a different NetLogo model.",
            "Can't open file",
            JOptionPane.ERROR_MESSAGE
       );
    }
    
    public static void loadSavedModel(
        final SavableModel model, 
        final String fileName
    ) {
        assert model.getNetLogoFileName() != null;
            
        DependencyManager dep = 
            DependencyManager.getDependencyManager();
        DomainModel domainModel = 
            dep.getObject(DomainModel.class, "domainModel");
        
        if (
            !model.getNetLogoFileName().
                equals(domainModel.getNetLogoFileName())
        ) {
            displayWrongModelMessage();
            return;
        }
        
        modelSaved();
        
        GraphicalInterface.getFrame().setTitle(
            GraphicalInterface.TITLE_PREFIX_STRING + fileName
        );
        
        MasterCodeController codeController =
            dep.getObject(MasterCodeController.class, "controller");
        // must be called before loading new domain model, or grapher will
        // not be updated to new set instances
        codeController.clear();
        
        domainModel.loadData(model.getDomainModel());
        domainModel.resetImageFileName(); // in case image file name was saved
        
        UserModel userModel =
            dep.getObject(UserModel.class, "userModel");
        userModel.resetAll(model.getUserModel());
        

        codeController.startUp();
    }
    
    public static void modelEdited() {
        shouldSave = true;
    }
    
    public static void modelSaved() {
        shouldSave = false;
        isNamed = true;
    }
    
    public static boolean shouldName() {
        return !isNamed;
    }
    
    public static boolean shouldSave() {
        return shouldSave;
    }


    /**
     * Closes the current embedded NetLogo file and 
     * clears its space in the frame.
     */
    public static void closeNetLogoFile() {
        assert SwingUtilities.isEventDispatchThread();
        GraphicalInterface.getNetLogoPanel().removeAll();
        GraphicalInterface.getFrame().getContentPane().removeAll();
    }
    
    
    /**
     * Loads the data from the current NetLogo file, and sets up the graphical
     * interface for interacting with the NetLogo model.
     */
    public static void startModel(final String netLogoFileName) {
        assert !SwingUtilities.isEventDispatchThread();
        DomainModelImporter.importModelData("domainModel", netLogoFileName);
        DependencyManager dep = DependencyManager.getDependencyManager();
        
        DomainModel domainModel = 
            dep.getObject(DomainModel.class, "domainModel");
        if (domainModel.isImageComputation()) {
            MyMenuBar myMenuBar = dep.getObject(MyMenuBar.class, "menuBar");
            myMenuBar.addSetsMenu();
            myMenuBar.addSetImageMenuItem();
        }
        
        MasterCodeController codeController =
            dep.getObject(MasterCodeController.class, "controller");
        codeController.init();
        
        Autosave autosave = dep.getObject(Autosave.class, "autosave");
        autosave.init();
    }
    
    
    public static void initView() {
        setupGraphicalInterface();
    }
    
    
    public static void shutDown() {
        System.exit(0);
    }
    
    
    public static void errorShutDown(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            showErrorDialog(message);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog(message);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        
        System.exit(1);
    }
    
    static void showErrorDialog(final String message) {
        assert SwingUtilities.isEventDispatchThread();
        try {
            JOptionPane.showMessageDialog(
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getActiveWindow(), 
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private static void setupGraphicalInterface() {      
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    GraphicalInterface.getGraphicalInterfaceInstance();
                    MasterCodeController codeController =
                        DependencyManager.getDependencyManager().
                        getObject(MasterCodeController.class, "controller");
                    codeController.startUp();
                    GraphicalInterface.getGraphicalInterfaceInstance().
                        adjustDimensions();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error loading window.");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error loading window.");
        }
        
        DependencyManager.getDependencyManager().
            getObject(RunController.class, "runController").
                run(true, false, false, 1, false);
    }
}
