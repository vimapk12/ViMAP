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


package edu.vanderbilt.driverandlayout;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import edu.vanderbilt.codecomponentview.BlockViewFactory;
import edu.vanderbilt.codecomponentview.PaletteView;
import edu.vanderbilt.codecomponentview.SwingUserCodeView;
import edu.vanderbilt.codecomponentview.UserCodeView;
import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.codeview.MasterCodeView;
import edu.vanderbilt.codeview.SwingMasterCodeView;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.edit.EditMenu;
import edu.vanderbilt.edit.EditMenuListener;
import edu.vanderbilt.mac.MacGeneralSetup;
import edu.vanderbilt.mac.MacUtil;
import edu.vanderbilt.runcontroller.DefaultRunController;
import edu.vanderbilt.runcontroller.RunController;
import edu.vanderbilt.runview.RunButtonPanel;
import edu.vanderbilt.runview.SpeedSliderPanel;
import edu.vanderbilt.saving.FileMenu;
import edu.vanderbilt.saving.FileMenuAndWindowListener;
import edu.vanderbilt.saving.MyAutosave;
import edu.vanderbilt.sets.SetPanel;
import edu.vanderbilt.sets.SetsMenu;
import edu.vanderbilt.sets.SetsMenuListener;
import edu.vanderbilt.simulation.SimulationCaller;
import edu.vanderbilt.usermodel.UserModel;
import edu.vanderbilt.userprocedures.ProceduresMenu;
import edu.vanderbilt.userprocedures.ProceduresMenuListener;

/**
 * Test-driver for Java VPL control of a NetLogo file.
 * 
 * To set up to load from an external NetLogo file:
 * 1. In VimapTestDrive.init(), use  
 * final String netLogoFile = promptUserForFileName()
 * 
 * 2. In VimapTestDrive.promptUserForFileName(), comment out the lines
 * that append a "/" to the beginning of the file name.
 * 
 * 3. In Loader.openModel(), use 
 * String fileText = getFileTextFromLocalFile(aNetLogoFile)
 * to get the text from the NetLogo file.
 * 
 * To set up to load from an internal file:
 * 1. In VimapTestDrive.init(), use 
 * final String netLogoFile = "/bird-butterfly.nlogo"
 * or similar to get the NetLogo file name.
 * 
 * 2. In Loader.openModel(), use 
 * String fileText = getFileTextFromUrl(aNetLogoFile)
 * to get the NetLogo file text.
 */
public abstract class ViMAP {
    
    private static String fileName;
    private static ProgressDialog startupProgress;
    
    /**
     * Creates a Loader object that opens and starts a NetLogo model.
     * 
     * @param args not used
     */
    public static void main(final String[] args) {
        setupFirstPartForOS();
        setLookAndFeel();
        showProgressIndicator();
                
        loadObjects(); // 1534 ms (2 steps)
        
        // load step 3: Setting Up For OS...
        setupForOS(); // 106 ms (1 step)
        incrementProgressBarOnEDT("Opening NetLogo File...");

        init(); // 2640 ms (about 6 steps)
        
        hideProgressIndicator();
    }
    
    static ProgressDialog getProgressDialog() {
        return startupProgress;
    }
    
    static void setProgressDialog(final ProgressDialog dialog) {
        startupProgress = dialog;
    }
    
    private static void showProgressIndicator() {
        SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {
                final int steps = 10;
                setProgressDialog(
                    new ProgressDialog(
                        "ViMAP", 
                        "Loading Model Objects...", 
                        0, 
                        steps
                    ));
                getProgressDialog().show();
        } });
    }
    
    private static void hideProgressIndicator() {
        SwingUtilities.invokeLater(new Runnable() {   
            @Override
            public void run() {
                if (getProgressDialog() != null) {
                    getProgressDialog().dispose();
                }
        } });
    }
    
    private static void setLookAndFeel() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
               @Override
               public void run() {
                   try {
                       for (
                           LookAndFeelInfo info
                           : UIManager.getInstalledLookAndFeels()
                       ) {
                           if ("Nimbus".equals(info.getName())) {
                               UIManager.setLookAndFeel(info.getClassName());
                               break;
                           }
                       }
                   } catch (UnsupportedLookAndFeelException e) {
                       e.printStackTrace();
                   } catch (ClassNotFoundException e) {
                       e.printStackTrace();
                   } catch (InstantiationException e) {
                       e.printStackTrace();
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   }
               }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadObjects() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
               @Override
               public void run() {
                   // load step 1: Loading Model Objects...
                   DependencyManager dep =
                       DependencyManager.getDependencyManager();
                   dep.addObject(new DomainModel(), "domainModel");
                   dep.addObject(new SwingUserCodeView(), "userCode");
                   dep.addObject(
                       new BlockViewFactory("domainModel", "userCode"), 
                       "factory"
                   );
                   dep.addObject(new UserModel(), "userModel");
                   getProgressDialog().incrementValue();
                   getProgressDialog().setMessage("Loading View Objects...");
                   
                   // load step 2: Loading View Objects...
                   dep.addObject(
                       new edu.vanderbilt.codecomponentview.SwingPaletteView(), 
                       "palette"
                   );
                   DraggingGlassPane pane = new DraggingGlassPane();
                   dep.addObject(pane, "pane");
                   dep.getObject(SwingUserCodeView.class, "userCode").
                       setMyGlassPane(pane);
                   dep.addObject(new SwingMasterCodeView(), "masterCodeView");
                   dep.addObject(new ManualFrame(), "manualFrame");
                   
                   DefaultRunController runController = 
                       new DefaultRunController();
                   dep.addObject(runController, "runController");
                   dep.addObject(
                       new SpeedSliderPanel(runController), 
                       "speedSlider"
                   );
                   dep.addObject(new SetPanel(), "setPanel");
                   dep.addObject(new RunButtonPanel(), "runControls");
                   
                   dep.addObject(new MyAutosave(), "autosave");
                   dep.addObject(
                       new FileMenuAndWindowListener(), 
                       "menuListener"
                   );
                   dep.addObject(new FileMenu(), "fileMenu");
                   
                   EditMenuListener editMenuListener = new EditMenuListener();
                   EditMenu editMenu = new EditMenu(editMenuListener);
                   dep.addObject(editMenu, "editMenu");
                   
                   ProceduresMenuListener proceduresMenuListener = 
                       new ProceduresMenuListener();
                   ProceduresMenu proceduresMenu = 
                       new ProceduresMenu(proceduresMenuListener);
                   proceduresMenuListener.setProceduresMenu(proceduresMenu);
                   dep.addObject(proceduresMenu, "proceduresMenu");
                   SetsMenuListener setsMenuListener = new SetsMenuListener();
                   SetsMenu setsMenu = new SetsMenu(setsMenuListener);
                   setsMenuListener.setSetsMenu(setsMenu);
                   dep.addObject(setsMenu, "setsMenu");
                   dep.addObject(new MyMenuBar(), "menuBar");
                       
                   dep.addObject(new MyMasterCodeController(
                       "domainModel", 
                       "userModel", 
                       "masterCodeView"
                   ), 
                   "controller");
                   
                   getProgressDialog().incrementValue();
                   getProgressDialog().setMessage("Setting Up For OS...");
               }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error loading program.");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error loading program.");
        }
    }
    
    private static void incrementProgressBarOnEDT(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getProgressDialog().incrementValue();
                getProgressDialog().setMessage(message);
            }
        });
    }
    
    private static void init() {
        // name of the NetLogo model to load
        // final String netLogoFile = "/fox-rabbit.nlogo";
        // final String netLogoFile = "/fox-rabbit-mid.nlogo";
        // final String netLogoFile = "/fox.nlogo";
        // final String netLogoFile = "/wolf-sheep.nlogo";
        // final String netLogoFile = "/running.nlogo";
        // final String netLogoFile = "/electricity-vimap.nlogo";
        // final String netLogoFile = "/ants.nlogo";
        // final String netLogoFile = "/oneTurtle-measure-surge.nlogo";
        // final String netLogoFile = "/imageComp.nlogo";               
        // final String netLogoFile = "/oneTurtle-measure-surge.nlogo";
        // final String netLogoFile = "/imageComp.nlogo";
        // final String netLogoFile = "/ramps-measure.nlogo";
        // final String netLogoFile = "/oneT-ard-2sensor.nlogo";
        // final String netLogoFile = "/oneT-ard-2sensor-v2.nlogo";
        // final String netLogoFile = "/oneT-ard-2sensor-v2.nlogo";
        // final String netLogoFile = "/ethnocentrism.nlogo";
        // final String netLogoFile = "/ethnocentrism-measure.nlogo";
        
        // Ants
        // final String netLogoFile = "/ants.nlogo"; 
        
        // Bird-Butterfly
        // final String netLogoFile = "/bird-butterfly2.nlogo";
        
        // Image Computation
        // final String netLogoFile = "/imageCompData.nlogo";
        
        // Image Computation with Sensing
        // final String netLogoFile = "/imageCompDataArd.nlogo";
        
        // 1 Turtle
         final String netLogoFile = "/oneTurtle-measure.nlogo";
        
        // Kit's Ant Models
    	// final String netLogoFile = "/ant-food-grab2.nlogo";
         //final String netLogoFile = "/ant-food-grab5.nlogo";
         
        // Two Turtle
        // final String netLogoFile = "/twoTurtle-measure.nlogo";
        
        // 1 Turtle, 1 sensor
        // final String netLogoFile = "/oneT-ard-measure2.nlogo";
        
        // 2 Turtle, 1 sensor
        // final String netLogoFile = "/twoTurtle-arduino.nlogo";
        
        // 1 Turtle, 2 sensor
        // final String netLogoFile = "/oneT-ard-2sensor-v3.nlogo";
        
        // 2 turtle, 2 sensor
        // final String netLogoFile = "/twoTurtle-arduino-2s.nlogo";
        
        // Ethnocentrism
        // final String netLogoFile = "/ethnocentrism-new-measure2.nlogo";
        
        // enter desired file name in dialog box.
        // final String netLogoFile = promptUserForFileName();
        
        // load step 4: Opening NetLogo File...
        Loader.openModel(netLogoFile);
        incrementProgressBarOnEDT("Starting NetLogo Model...");
        
        // load step 5: Starting NetLogo Model...
        Loader.startModel(netLogoFile);
        incrementProgressBarOnEDT("Opening Measure Window...");
        
        // load step 6: Opening Measure Window...
        final String baseMeasureFileName = "measureWorld.nlogo";
        int stackof = 2;
        boolean extra = false;
        
        SimulationCaller.openMeasureWorldSuite(
            baseMeasureFileName, 
            stackof, 
            extra
        );
        incrementProgressBarOnEDT("Initializing Objects...");
        
        // load step 7: Initializing Objects...
        DependencyManager dep = DependencyManager.getDependencyManager();
        dep.getObject(RunController.class, "runController").init();
        dep.getObject(UserCodeView.class, "userCode").init();
        dep.getObject(PaletteView.class, "palette").init();
        dep.getObject(RunButtonPanel.class, "runControls").setExecutor(
                dep.getObject(RunController.class, "runController")
            );
        dep.getObject(MasterCodeView.class, "masterCodeView").
            setMasterCodeController(
            dep.getObject(MasterCodeController.class, "controller")
        );
        dep.getObject(SetPanel.class, "setPanel").init();
        dep.getObject(EditMenu.class, "editMenu").init();
        dep.getObject(
            BlockViewFactory.class, 
            "factory"
        ).init();
        incrementProgressBarOnEDT("Initializing View...");
        
        // load step 8: Initializing View...
        Loader.initView();
        incrementProgressBarOnEDT("Done");
    }
    
    /**
     * Set up operating-system specific settings, such as the "about
     * this program" message and the dock icon. So far, this method
     * handles only Mac OS.
     */
    private static void setupForOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version").toLowerCase();
        
        if (MacUtil.isMacOsX(osName)) {
            if (MacUtil.isSupportedMacVersion(osVersion)) {
                MacGeneralSetup.setupForMac(); 
            }
        }
    }
    
    private static void setupFirstPartForOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version").toLowerCase();
        
        if (MacUtil.isMacOsX(osName)) {
            if (MacUtil.isSupportedMacVersion(osVersion)) {
                MacGeneralSetup.setupFirstPartForMac(); 
            }
        }
    }
    
    static void setFileName(final String name) {
        fileName = name;
    }
    
    /**
     * Gets the name of the NetLogo file to open from the user,
     * through a dialog box. Exits the program abnormally
     * if no string is entered. If the name does not end ".nlogo",
     * the suffix is added before returning the string.
     * 
     * @return the name entered by the user, with ".nlogo"
     * appended to the end if not there already.
     */
    @SuppressWarnings("unused")
    private static String promptUserForFileName() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    setFileName(
                        (String) JOptionPane.showInputDialog(
                            frame,
                            "Enter the name of your NetLogo file.",
                            "Select NetLogo file",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            null
                    ));
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error getting text input.");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Loader.errorShutDown("Error getting text input.");
        }

        // if a string was returned, make sure it ends ".nlogo"
        if ((fileName != null) && (fileName.length() > 0)) {
            final int suffixLength = 6;
            if (
                fileName.length() < suffixLength + 1
                || !fileName.substring(fileName.length() - suffixLength).
                    equals(".nlogo")
            ) {
                fileName = fileName + ".nlogo";
            }
                        
            // add leading slash to prepare filename for use with ClassLoader
            
            if (fileName.charAt(0) != '/') {
                fileName = "/" + fileName;
            }
            
            return fileName;
        }

        // if you're here, the return value was null or empty.
        Loader.errorShutDown("Error: You did not enter a file name.");    
        return null;
    }
}
