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


package edu.vanderbilt.simulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.nlogo.api.CompilerException;
import org.nlogo.api.LogoList;
import org.nlogo.lite.InterfaceComponent;
import org.nlogo.window.AppletAdPanel;
import org.nlogo.window.InterfacePanelLite;
import org.nlogo.window.SpeedSliderPanel;
import org.nlogo.window.View;
import org.nlogo.window.ViewControlStrip;
import org.nlogo.window.ViewWidget;

import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.driverandlayout.Loader;
import edu.vanderbilt.measure.MeasureOption;
import edu.vanderbilt.measure.MeasureWorld;
import edu.vanderbilt.measure.OneWorldContainer;
import edu.vanderbilt.measure.TwoWorldContainer;
import edu.vanderbilt.runcontroller.Report;
import edu.vanderbilt.util.Util;

/**
 * This class encapsulates the InterfaceComponent 
 * that is used to interact with the
 * simulation environment. Calls to the simulation 
 * environment are made through this
 * class. This class can return data from the simulation environment.
 *
 * Methods in this class call the simulation environment 
 * from a single thread and
 * do not return until the simulation environment has 
 * returned. This means that code that
 * waits on SimulationCaller methods to return is guaranteed to run in sequence.
 *
 */
public abstract class SimulationCaller {
    
    // NetLogo method for resetting a model after a run
    private static final String RESET_COMMAND = "reset"; 
    
    // prefix used in ViMAP's NetLogo models for model-defined methods
    private static final String PREFIX = "java-"; 
    
    // used to tell the NetLogo model to set itself up
    private static final String SETUP_METHOD_STRING = "setup"; 
    
    // used to call NetLogo
    private static InterfaceComponent comp; 
    
    private static View view;
    
    public static final int MIN_WIDTH = 430;
    public static final int MIN_HEIGHT = 500;
    
    // the single thread for calling NetLogo
    private static ExecutorService executor = 
        Executors.newSingleThreadExecutor(); 
    // private static String calledSet;
    
    // must be volatile, because it's a static field that is lazily initialized.
    // volatile makes sure threads synchronize when accessing this field,
    // so it can't be instantiated twice.
    private static volatile Vector<MeasureWorld> measureWorlds;    
    
    /**
     * Creates a new InterfaceComponent to store as a static field.
     * This method must be called before other methods can be used to access
     * NetLogo.
     */
    private static void newInterfaceComponent() {
        comp = new InterfaceComponent(GraphicalInterface.getFrame());
        view = findViewInComp(comp);
    }
    
    
    private static void setupInterfaceComponentIfNeeded() {
        if (comp == null) {
            newInterfaceComponent();
        }
    }
    
    
  //  public static void setCalledSet(final String aCalledSet) {
  //      calledSet = aCalledSet;
  //  }
    
    public static Point getViewLocationOnScreen() {
        return view.getLocationOnScreen();
    }
    
    public static Dimension getViewDimension() {
        return new Dimension(view.getWidth(), view.getHeight());
    }
    
    public static double getViewXcor(final double screenXcor) {
        return screenXcor - getViewLocationOnScreen().x;
    }
    
    public static double getViewYcor(final double screenYcor) {
        return screenYcor - getViewLocationOnScreen().y;
    }
    
    public static void convertPointFromScreenToView(final Point point) {
        SwingUtilities.convertPointFromScreen(
            point, 
            view
        );
    }
    
    
    /**
     * Calls NetLogo to reset the model for a new run.
     */
    public static void runResetCommand(
        final Runnable callback,
        final List<String> setInstanceNames
    ) {
        waitForExecutorToRunCommand(RESET_COMMAND);
        if (!setInstanceNames.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (String name: setInstanceNames) {
                sb.append("create-set-data \"").append(name).append("\" ");
            }
            Util.printIfDebugging("update sets: " + sb.toString());
            waitForExecutorToRunCommand(sb.toString());
        }
        
        try {
            for (MeasureWorld measureWorld: measureWorlds) {
                measureWorld.runMeasureCommandLater(RESET_COMMAND);
            }
        } catch (CompilerException e) {
            e.printStackTrace();
        }
        callback.run();
    }
    
    
    public static void reportStringWithCallback(
        final String reporter, 
        final Report report
    ) {
        String result = reportString(reporter);
        report.reportAndContinue(result);
    }
    
    
    /**
     * Returns a string from a NetLogo report procedure.
     * 
     * This method assumes the following about the NetLogo model:
     * -it has a reporter called "to-report [reporter]", 
     * where [reporter] is your argument string
     * 
     * @param reporter the command to pass to NetLogo
     * @return the result of the report
     */
    public static String reportString(final String reporter) {
        Future<String> future = executor.submit(
            new Callable<String>() {
                @Override
                public String call() {
                    String result = runReporterFromExecutor(reporter);
                    return result;
                }
            }
       );
             
        try {
            String result = future.get();
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }        
    }
    
    
    /**
     * Returns the boolean version of a string from a NetLogo report procedure.
     * 
     * This method assumes the following about the NetLogo model:
     * -it has a reporter called "to-report [PREFIX][predicate]" 
     * that takes the agent number as an argument,
     * where [PREFIX] is the value of the PREFIX variable, 
     * and [predicate] is your string argument.
     * -the reporter procedure returns a string of value 
     * either "true" or "false"
     * 
     * @param predicate the condition to test
     * @param who index of the turtle to make the test with
     * @return the boolean value from the report
     */
    public static boolean reportBoolean(
        final String predicate, 
        final int who
    ) {        
        final StringBuilder builder = new StringBuilder();
        builder.append(PREFIX).append(predicate).append(" ").append(who);
        String booleanAsString = reportString(builder.toString());
        
        // if the value is "true" or "false," return the 
        // associated boolean. else, error condition
        if (booleanAsString.equals("true")) {
            return true;
        } else if (booleanAsString.equals("false")) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    
    public static boolean reportBooleanNoPrefix(final String predicate) {
        String booleanAsString = reportString(predicate);
        
        // if the value is "true" or "false," 
        // return the associated boolean. else, error condition
        if (booleanAsString.equals("true")) {
            return true;
        } else if (booleanAsString.equals("false")) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    
    public static boolean reportBoolean(final String predicate) {
        return reportBooleanNoPrefix(PREFIX + predicate);
    }
    
    public static void addSet(final String setName) {
        final String command = "create-set-data \"" + setName + "\"";
        Util.printIfDebugging("add set: " + command);
        runCommandLater(command);
    }
    
    public static void deleteSet(final String setName) {
        final String command = "delete-set-data \"" + setName + "\"";
        Util.printIfDebugging("delete set: " + command);
        runCommandLater(command);
    }    
    
    public static void setImageFileName(final String fileName) {
        // for Windows machines, must get rid of unescaped backslash 
        // file separators before calling NetLogo's file commands, 
        // or NetLogo will not handle the backslash file separators correctly.
        final String fileNameNoBackslashes = fileName.replace("\\", "/");
        final String command = "set image-to-import \"" 
            + fileNameNoBackslashes + "\" reset";
        runCommandLater(command);
    }
    
    /**
     * Reports the int version of the string reported back from NetLogo.
     * 
     * This method assumes the following about the NetLogo model:
     * -it has a reporter called "to-report [report]", where [report] 
     * is your argument string
     * -the reporter procedure returns the string version of an int
     * 
     * @param reporter the report string to run
     * @return the int value from the report
     */
    public static int reportInt(final String reporter) {
        String intAsString = reportString(reporter);
        return (int) Double.parseDouble(intAsString);
    }
    
    
    /**
     * Reports the double version of a report from NetLogo.
     * 
     * This method assumes the following about the NetLogo model:
     * -it has a reporter called "to-report [report]", where [report] 
     * is your argument string
     * -the reporter procedure returns the string version of a double
     * 
     * @param reporter the report string to run
     * @return the double value from the report
     */
    public static double reportDouble(final String reporter) {
        String doubleAsString = reportString(reporter);
        return Double.parseDouble(doubleAsString);
    }
    
    
    /**
     * Adds the private InterfaceComponent to the SOUTH region of 
     * the panel passed in.
     * Loads NetLogo in the InterfaceComponent.
     * Calls "setup" on the NetLogo model, then returns when "setup" finishes.
     * 
     * @param baseModelSource the name of the file to load
     * @param frame the frame in which to load the NetLogo view
     * @param modelPanel a panel to contain the NetLogo window component
     */
    public static void loadBaseModel(
        final String baseModelSource, 
        final JFrame frame, 
        final JPanel modelPanel 
   ) {
        setupInterfaceComponentIfNeeded();
        
        modelPanel.add(comp, BorderLayout.SOUTH);
        
        System.setProperty("org.nlogo.noGenerator", "true");
        
        try {
            // must be called from Event Dispatch Thread
            java.awt.EventQueue.invokeAndWait(
                new Runnable() { 
                    @Override
                    public void run() {                            
                        try {
                            // load the NetLogo model
                            getComp().openFromSource(
                                "name", 
                                "path", 
                                baseModelSource
                            );
                            Dimension preferredSize = 
                                modelPanel.getPreferredSize();
                            modelPanel.setMaximumSize(preferredSize);
                            JPanel outerPanel = new JPanel();
                            outerPanel.add(modelPanel);
                            outerPanel.setMinimumSize(
                                new Dimension(MIN_WIDTH, MIN_HEIGHT)
                            );
                            
                            GridBagConstraints c = new GridBagConstraints();
                            c.gridheight = 2;
                            c.gridx = 2;
                            c.gridy = 1;
                            if (
                                frame.getContentPane().getLayout() 
                                    instanceof BorderLayout
                            ) {
                                frame.getContentPane().setLayout(
                                    new GridBagLayout()
                                );
                            }
                            frame.getContentPane().add(outerPanel, c);
                            frame.setVisible(true);
                            cleanUpInterfaceComponent(
                                getComp(), 
                                frame.getBackground()
                            );
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Loader.errorShutDown("Error loading NetLogo file.");
                        }
                    } 
                } 
           );
            
            waitForExecutorToRunCommand(SETUP_METHOD_STRING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
    
    public static void cleanUpInterfaceComponent(
        final InterfaceComponent interfaceComponent, 
        final Color neutralColor
    ) {
        hideSpeedSliderPanel(interfaceComponent);
        hideAppletAdPanel(interfaceComponent);
        hideViewWidget(interfaceComponent);
        hideWhiteBorder(interfaceComponent, neutralColor);
        hideViewControlStrip(interfaceComponent);
    }
    
    public static void hideWhiteBorder(
        final Container co, 
        final Color neutralColor
    ) {
        for (Component myComp: co.getComponents()) {
            if (myComp instanceof InterfacePanelLite) {
                myComp.setBackground(neutralColor);
            }
        }
    }
    
    public static void hideSpeedSliderPanel(final Component co) {
        SpeedSliderPanel sliderPanel = 
            findSliderInComp(co);
        if (sliderPanel != null) {
            sliderPanel.setVisible(false);
        }
    }
    
    public static void hideAppletAdPanel(final Component co) {
        AppletAdPanel adPanel = 
            findAppletAdPanelInComp(co);
        if (adPanel != null) {
            adPanel.setVisible(false);
        }
    }

    public static void hideViewControlStrip(
        final Component co
    ) {
        ViewControlStrip controlStrip = findViewControlStripInComp(co);
        if (controlStrip != null) {
            Container parent = controlStrip.getParent();
            parent.remove(controlStrip);
            final Color viewBackground = new Color(180, 180, 180);
            parent.setBackground(viewBackground);
        }
    }
    
    public static void hideViewWidget(final Component co) {
        ViewWidget viewWidget = 
            findViewWidgetInComp(co);
        makeViewWidgetInvisible(viewWidget);
    }
    
    static void makeViewWidgetInvisible(final ViewWidget viewWidget) {
        viewWidget.setBackground(Color.WHITE);
    }
    
    static ViewWidget findViewWidgetInComp(final Component co) {
        int i = 0;
        if (co instanceof Container) {
            Container c = (Container) co;
            while (i < c.getComponentCount()) {
                if (c.getComponent(i) instanceof ViewWidget) {
                    return (ViewWidget) c.getComponent(i); 
                    }
                ViewWidget x = findViewWidgetInComp(c.getComponent(i));
                if (x != null) {
                    return x; 
                    }
                i++;
            }
        }
        return null;
    }
    
    static ViewControlStrip findViewControlStripInComp(final Component co) {
        int i = 0;
        if (co instanceof Container) {
            Container c = (Container) co;
            while (i < c.getComponentCount()) {
                if (c.getComponent(i) instanceof ViewControlStrip) {
                    return (ViewControlStrip) c.getComponent(i); 
                    }
                ViewControlStrip x = 
                    findViewControlStripInComp(c.getComponent(i));
                if (x != null) {
                    return x; 
                    }
                i++;
            }
        }
        return null;
    }
    
    static AppletAdPanel findAppletAdPanelInComp(final Component co) {
        int i = 0;
        if (co instanceof Container) {
            Container c = (Container) co;
            while (i < c.getComponentCount()) {
                if (c.getComponent(i) instanceof AppletAdPanel) {
                    return (AppletAdPanel) c.getComponent(i); 
                    }
                AppletAdPanel x = findAppletAdPanelInComp(c.getComponent(i));
                if (x != null) {
                    return x; 
                    }
                i++;
            }
        }
        return null;
    }
    
    static View findViewInComp(final Component co) {
        int i = 0;
        if (co instanceof Container) {
            Container c = (Container) co;
            while (i < c.getComponentCount()) {
                if (c.getComponent(i) instanceof View) {
                    return (View) c.getComponent(i); 
                    }
                View x = findViewInComp(c.getComponent(i));
                if (x != null) {
                    return x; 
                    }
                i++;
            }
        }
        return null;
    }
    
    /**
     * Searches through the Components that are children of the 
     * input argument Component co, to find an instance of 
     * SpeedSliderPanel.  This panel's visibility can be set to
     * false, removing the speed slider from the ViMAP view.
     * 
     * @param co the interface component
     * @return SpeedSliderPanel, or null if none found
     */
    static SpeedSliderPanel findSliderInComp(final Component co) {
        int i = 0;
        if (co instanceof Container) {
            Container c = (Container) co;
            while (i < c.getComponentCount()) {
                if (c.getComponent(i) instanceof SpeedSliderPanel) {
                    return (SpeedSliderPanel) c.getComponent(i); 
                    }
                SpeedSliderPanel x = findSliderInComp(c.getComponent(i));
                if (x != null) {
                    return x; 
                    }
                i++;
            }
        }
        return null;
    }


    /**
     * Reports the agent type of the turtle of a given index.
     * 
     * @param aWho the index of a turtle
     * @return the agent type of that turtle
     */
    public static String reportAgentType(final int aWho) {
        return reportString(
            "turtle-property " + aWho + " \"agent-kind-string\""
        );
    }
    
    
    /**
     * Reports data from the previous "cycle" or "tick" of code 
     * execution, as a string of numbers delimited by commas and semicolons.
     * 
     * @return the data string reported by NetLogo
     */
    public static String reportDataString() {
        return reportString("data-string");
    }
    
    
    /**
     * Reports a sorted list of the "who" numbers of all 
     * wabbits currently alive in the model.
     * 
     */
    public static void reportAgentWhoNumbers(final Report report) {
        waitForExecutorToRunCommand("create-wabbits-list");
        
        final List<Integer> whoNumberList = new ArrayList<Integer>();
        
        final int length = reportInt("list-length wabbits-list");
        
        // go through all wabbits currently active, finding their "who" numbers
        for (int i = 0; i < length; i++) {
            int toAdd = reportInt(("list-item wabbits-list " + i));
            whoNumberList.add(toAdd);
        }
        
        report.reportAndContinue(whoNumberList);
    }
    
    
    /**
     * Can provide the InterfaceComponent to new 
     * threads created from this class.
     * 
     * @return the interface component for the NetLogo model
     */
    static InterfaceComponent getComp() {
        setupInterfaceComponentIfNeeded();
        
        return comp;
    }
    
    
    /**
     * This method actually calls NetLogo to run a command. This method
     * should be called only by the one single-threaded executor instance,
     * to avoid calls running out of order.
     * 
     * @param command the command string to run
     */
    static void runCommandFromExecutor(final String command) {
        setupInterfaceComponentIfNeeded();
        
        try {
            comp.command(command);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * This method actually calls NetLogo to run a reporter 
     * procedure. This method should be called only by the one 
     * single-threaded executor instance,
     * to avoid calls running out of order.
     * 
     * @param reporter the reporter string to run
     * @return the value returned by the reporter
     */
    public static String runReporterFromExecutor(final String reporter) {
        setupInterfaceComponentIfNeeded();
        
        try {
            return comp.report(reporter).toString();
        } catch (final CompilerException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void runCommandWithCallback(
        final String command, 
        final Runnable callback
    ) {
        Future<?> future = executor.submit(
            new Runnable() {
                @Override
                public void run() {
                    runCommandFromExecutor(command);
                }
           }
       );
        try {
            future.get();
            if (callback != null) {
                callback.run();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }       
    }
    
    static void runCommandLater(final String command) {
        executor.submit(
            new Runnable() {
                @Override
                public void run() { 
                    runCommandFromExecutor(command);
                }
           }
       );
    }
    
    /**
     * This method calls the single-threaded executor to run a command
     * in NetLogo, and this method waits for NetLogo to return before
     * allowing execution to proceed.
     * 
     * @param command the command string to run
     */
    static void waitForExecutorToRunCommand(final String command) {
        Future<?> future = executor.submit(
            new Runnable() {
                @Override
                public void run() { 
                    runCommandFromExecutor(command);
                }
           }
       );
          
        try {
            future.get();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////
    // MEASURE CODE
    
    public static void openMeasureWorld(final String baseMeasureFileName) {
        assert !SwingUtilities.isEventDispatchThread();
        String[] agentNames = {};
        String[] measureOptionCommands = {};
        String[] varNames = {};
        List<String[]> measureOptionStringLists = new ArrayList<String[]>();
        if (measureWorlds == null) {
            measureWorlds = new Vector<MeasureWorld>();
            try {
                String types = (String) 
                    comp.report("get-agent-kinds-as-csv");
                agentNames = types.split(",");
                
                String measureOptions = (String) 
                    comp.report("get-list-as-csv measure-option-command-list");
                measureOptionCommands = measureOptions.split(",");
         
                int length = Integer.parseInt((String) 
                    comp.report("list-length measure-option-string-lists"));
                for (int i = 0; i < length; i++) {
                    String tempMeasureText = (String) 
                        comp.report(
                            "get-list-as-csv (item " 
                            + i 
                            + " measure-option-string-lists)"
                        );
                    String[] tempArray = tempMeasureText.split(",");
                    measureOptionStringLists.add(tempArray);
                }
                
                String names = 
                    (String) comp.report("get-list-as-csv var-name-list");
                varNames = names.split(",");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
        
        final String[] finalAgentNames = agentNames;  
        final List<MeasureOption> measureOptions = 
            new ArrayList<MeasureOption>();
        for (int i = 0; i < measureOptionCommands.length; i++) {
            measureOptions.add(
                new MeasureOption(
                    measureOptionStringLists.get(i), 
                    measureOptionCommands[i]
                )
            );
        }
        final String[] finalVarNames = varNames;
        final int maxSize = 2;
        if (measureWorlds.size() <= maxSize) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                   @Override
                   public void run() {
                       // must be run on EDT, because it
                       // constructs a Swing component.
                       OneWorldContainer owc = 
                           new OneWorldContainer(
                               "/" + baseMeasureFileName, 
                               finalAgentNames, 
                               getMeasureWorlds().size() + 1,
                               measureOptions,
                               getComp()
                           );
                       getMeasureWorlds().add(owc.getMeasureWorld());
                   }
                });
                measureWorlds.get(measureWorlds.size() - 1).
                    setupUIOffEDT(finalVarNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void openMeasureWorldSuite(
        final String baseMeasureFileName, 
        final int stackof,
        final boolean extra
    ) {
        assert !SwingUtilities.isEventDispatchThread();
        String[] agentNames = {};
        String[] measureOptionCommands = {};
        List<String[]> measureOptionStringLists = new ArrayList<String[]>();
        String[] varNames = {};
        boolean canHighlightAgents = false;
        final String smallerMeasureModelFile = "/" 
            + "Smaller" + baseMeasureFileName; 
        if (measureWorlds == null) {
            measureWorlds = new Vector<MeasureWorld>();
            try {
                String types = (String) 
                    comp.report("get-agent-kinds-as-csv");
                agentNames = types.split(",");
                
                String measureOptions = (String) 
                    comp.report("get-list-as-csv measure-option-command-list");
                measureOptionCommands = measureOptions.split(",");

                if (
                    measureOptionCommands.length == 1 
                    && measureOptionCommands[0].equals("")
                ) {
                    return;
                }
                
                int length = 
                    (int) (0 + ((Double) comp.report(
                        "list-length measure-option-string-lists"
                    )));
                for (int i = 0; i < length; i++) {
                    String tempMeasureText = (String) 
                        comp.report(
                            "get-list-as-csv (item " 
                            + i 
                            + " measure-option-string-lists)"
                        );
                    String[] tempArray = tempMeasureText.split(",");
                    measureOptionStringLists.add(tempArray);
                }
                
                String names = 
                    (String) comp.report("get-list-as-csv var-name-list");
                varNames = names.split(",");
                
                canHighlightAgents = 
                    (Boolean) comp.report("can-highlight-agents");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
        
        final String[] finalAgentNames = agentNames;  
        final List<MeasureOption> measureOptions = 
            new ArrayList<MeasureOption>();
        for (int i = 0; i < measureOptionCommands.length; i++) {
            measureOptions.add(
                new MeasureOption(
                    measureOptionStringLists.get(i), 
                    measureOptionCommands[i]
                )
            );
        }
        
        final boolean finalCanHighlightAgents = canHighlightAgents;
        
        //set up double-decker measure world
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // must be called on EDT, because 
                    // it constructs a Swing component.
                    TwoWorldContainer twc = 
                        new TwoWorldContainer(
                            smallerMeasureModelFile, 
                            finalAgentNames, 
                            stackof,
                            measureOptions,
                            finalCanHighlightAgents,
                            getComp()
                        );
                    getMeasureWorlds().addAll(twc.getMeasureWorlds());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        measureWorlds.get(measureWorlds.size() - 2).setupUIOffEDT(varNames);
        measureWorlds.get(measureWorlds.size() - 1).setupUIOffEDT(varNames);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getMeasureWorlds().
                    get(getMeasureWorlds().size() - 2).pickFirstGraph();
                getMeasureWorlds().
                    get(getMeasureWorlds().size() - 1).pickFirstGraph();
            }
        });

        if (extra) {
            openMeasureWorld(baseMeasureFileName);
        }
    }
    
    public static void setupCycle() {
        assert !SwingUtilities.isEventDispatchThread();
        
        try {
            comp.commandLater("setup-cycle");
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
    
    public static void takedownCycle() {
        assert !SwingUtilities.isEventDispatchThread();

        try {
            comp.commandLater("takedown-cycle");
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
    
    public static void addSetToMeasureWorlds(final String setName) {
        assert comp != null;
        assert measureWorlds != null;

        for (final MeasureWorld measureWorld: measureWorlds) {            
            executor.submit(
                new Runnable() {
                    @Override
                    public void run() { 
                        measureWorld.addSetAndUpdate(setName);
                    }
                }
            );
        }
    }
    
    public static void removeSetFromMeasureWorlds(final String setName) {
        assert comp != null;
        assert measureWorlds != null;

        for (final MeasureWorld measureWorld: measureWorlds) {
            executor.submit(
                new Runnable() {
                    @Override
                    public void run() { 
                        measureWorld.removeSetAndUpdate(setName);
                    }
                }
            );
        }       
    }    
    
    public static void doTickInEmbodimentWorld() {
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null
        ) {
            try {
                comp.commandLater("tick");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
            for (MeasureWorld mwf: measureWorlds) {
                mwf.repaint();
            }
        }
    }

    public static void updateAllMeasureWorlds() {        
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null
        ) {
            for (MeasureWorld measureWorld: measureWorlds) {
                String agent = measureWorld.getSettings().getAgent();
                try {
                    Object report = null;
                    if (measureWorld.getSettings().isShowAllMeasurePoints()) {
                        report = 
                            comp.report("get-measures-for \"" + agent + "\"");
                    } else {
                        report = 
                            comp.report(
                "get-measures-for-filtered \"" + agent + "\" \"" + agent + "\""
                            );
                    }
                    if (
                        report instanceof LogoList 
                        && ((LogoList) report).size() > 0
                    ) {
                        LogoList measureList = (LogoList) report;
                        String command = 
                            "update-measures " + measureList.toString();
                        command = command.replace(',', ' ');
                        measureWorld.runMeasureCommand(command);
                        measureWorld.repaint();
                    } else {
                        // no measure points placed yet
                        measureWorld.runMeasureCommand("clear-measures");
                        measureWorld.repaint();
                    }
                } catch (CompilerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void updateMeasureWorld(final int index) {
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null 
            && measureWorlds.size() >= index
        ) {
            MeasureWorld world = measureWorlds.get(index - 1);
            String agent = world.getSettings().getAgent();
            try {
                Object report = null;
                if (world.getSettings().isShowAllMeasurePoints()) {
                    report = comp.report("get-measures-for \"" + agent + "\"");
                } else {
                    report = 
                        comp.report(
                            "get-measures-for-filtered \"" 
                            + agent + "\" \"" + agent + "\""
                        );
                }
                if (
                    report instanceof LogoList 
                    && ((LogoList) report).size() > 0
                ) {
                    LogoList logoList = (LogoList) report;
                    final String command = 
                        "update-measures " 
                        + logoList.toString().replace(',', ' '
                    );
                    
                    world.runMeasureCommand(command);
                    world.repaint();
                } else {
                    // no measure points placed yet
                    world.runMeasureCommand("clear-measures");
                    world.repaint();
                }
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
    }
    
    static Vector<MeasureWorld> getMeasureWorlds() {
        return measureWorlds;
    }
}
