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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.vanderbilt.codeview.AgentProcedureSelectView;
import edu.vanderbilt.codeview.CategorySelectPanel;
import edu.vanderbilt.codeview.CategorySelector;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.codeview.MasterCodeView;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.runcontroller.RunController;
import edu.vanderbilt.runview.RunButtonPanel;
import edu.vanderbilt.runview.SpeedSliderPanel;
import edu.vanderbilt.saving.FileMenuAndWindowListener;
import edu.vanderbilt.sets.SetPanel;
import edu.vanderbilt.simulation.SimulationCaller;


public final class GraphicalInterface {
    
    public static final int SMALL_PANEL_WIDTH = 270;
    
    public static final int SMALL_PANEL_HEIGHT = 470;
    
    // width of left column in frame
    public static final int LEFT_COLUMN_WIDTH = 400; 
    
    // width of middle column in frame
    public static final int MIDDLE_COLUMN_WIDTH = 400; 
    
    // height of panels for code or charts
    public static final int CODE_OR_CHART_PANEL_HEIGHT = 600; 
    
    private static final int MIN_FRAME_HEIGHT = 650;
    
    private static final int MIN_FRAME_WIDTH = 1000;

    
    // prefix used in title bar
    public static final String TITLE_PREFIX_STRING = "ViMAP " 
        + Loader.VERSION_STRING + " -- "; 
    
    // default name of untitled models
    public static final String UNTITLED_STRING = "Untitled"; 
    
    private MasterCodeView middleRowPanel;
    private static RunController runController;
    private static JPanel selectPanel = new JPanel();
    private static AgentProcedureSelectView agentProcedurePanel;
    private static GraphicalInterface myGUI;
    private static RunButtonPanel runButtonPanel;
    
    // the left x-coordinate of the panel with the code palette
    private static double codePanelX; 
    
    // the upper y-coordinate of the panel with the code palette
    private static double codePanelY; 
    
    // the frame that holds the program
    private static JFrame frame = 
        new JFrame(TITLE_PREFIX_STRING + UNTITLED_STRING);
    
    // holds the NetLogo model's display area
    private static final JPanel NETLOGO_PANEL = new JPanel(); 
    
    public static synchronized GraphicalInterface 
        getGraphicalInterfaceInstance() {
        assert SwingUtilities.isEventDispatchThread();

        if (myGUI == null) {
            myGUI = new GraphicalInterface();
            myGUI.realizeFrame();
            
            return myGUI;
        }
        
        return myGUI;
    }
    
    
    private GraphicalInterface() {
        assert SwingUtilities.isEventDispatchThread();
        
        if (runController == null) {
            setupNetLogoPanelAndRunController();
        }
        
        loadCodePanel();
        loadControls(); // must be called after loadCodePanel

        createTopButtonBar();

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    
    public static JFrame getFrame() {
        return frame;
    }
    
    static void setupNetLogoPanelAndRunController() {
        assert SwingUtilities.isEventDispatchThread();
        
        NETLOGO_PANEL.setLayout(new BoxLayout(NETLOGO_PANEL, BoxLayout.Y_AXIS));
        
        DependencyManager dep = DependencyManager.getDependencyManager();
        
        runController = 
            dep.getObject(RunController.class, "runController");
        
        NETLOGO_PANEL.add(
            dep.getObject(SpeedSliderPanel.class, "speedSlider")
        );
    }
    
    
    public static JPanel getNetLogoPanel() {
        if (myGUI == null) {
            // NETLOGO_PANEL has not had speed slider added yet,
            // so NetLogo panel could be added before it
            
            if (SwingUtilities.isEventDispatchThread()) {
                setupNetLogoPanelAndRunController();
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            setupNetLogoPanelAndRunController();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return NETLOGO_PANEL;
    }
    
    
    /**
     * @return left x-coordinate of the user code panel
     */
    public static double getCodePanelX() {
        return codePanelX;
    }
    
    
    /**
     * @return upper y-coordinate of the user code panel
     */
    public static double getCodePanelY() {
        return codePanelY;
    }
    
    
    private void realizeFrame() {
        addSetPanelIfImageComputation();
        if (frame.getContentPane().getLayout() instanceof BorderLayout) {
            frame.getContentPane().setLayout(new GridBagLayout());
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JComponent comp = (JComponent) myGUI.middleRowPanel;
        final int minWidthPadding = 20;
        comp.setMinimumSize(new Dimension(
            LEFT_COLUMN_WIDTH + minWidthPadding, 
            SimulationCaller.MIN_HEIGHT
        ));
        frame.getContentPane().add((JComponent) myGUI.middleRowPanel, c);
        
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(selectPanel);
        scrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        final int minHeight = 100;
        final int three = 3;
        scrollPane.setMinimumSize(
            new Dimension(LEFT_COLUMN_WIDTH * 2 / three, minHeight)
        );
        frame.getContentPane().add(scrollPane, c);
        
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(agentProcedurePanel, c);
        
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 0;
        frame.getContentPane().add(runButtonPanel, c);

        frame.pack();
        setupConstantsAfterRealizingFrame();
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
        
        this.middleRowPanel.updateLayoutSize();
    }
    
    private static void addSetPanelIfImageComputation() {
        assert SwingUtilities.isEventDispatchThread();
        
        // must be called after DomainModel is set up, and before
        // view is realized
        DependencyManager dep = DependencyManager.getDependencyManager();
        DomainModel domainModel = 
            dep.getObject(DomainModel.class, "domainModel");
        if (domainModel.isImageComputation()) {
            SetPanel setPanel = dep.getObject(SetPanel.class, "setPanel");
            NETLOGO_PANEL.add(setPanel, 1); // add above NetLogo window
        }
    }
    
    
    private static void setupConstantsAfterRealizingFrame() {
        codePanelX = ((JComponent) myGUI.middleRowPanel).getX();
        codePanelY = ((JComponent) myGUI.middleRowPanel).getY() 
            + frame.getJMenuBar().getHeight();
        
        DependencyManager dep = DependencyManager.getDependencyManager();
        DomainModel domainModel = 
            dep.getObject(DomainModel.class, "domainModel");
        if (domainModel.isImageComputation()) {
            SetPanel setPanel = dep.getObject(SetPanel.class, "setPanel");
            setPanel.setupBounds();
        }
        DraggingGlassPane.init();
    }
    
    public void adjustDimensions() {
        assert SwingUtilities.isEventDispatchThread();
        
        final int xPadding = 10;
        final int yPadding = 6;
        selectPanel.setPreferredSize(
            new Dimension(
                GraphicalInterface.MIDDLE_COLUMN_WIDTH + xPadding,
                selectPanel.getPreferredSize().height + yPadding
           )
        );

        frame.pack();
        setupConstantsAfterRealizingFrame();
        frame.pack();
    }
    
    
    private void createTopButtonBar() {
        assert SwingUtilities.isEventDispatchThread();
        runButtonPanel = DependencyManager.getDependencyManager().
            getObject(RunButtonPanel.class, "runControls");
    }
    
    
    private void loadCodePanel() {
        assert SwingUtilities.isEventDispatchThread();
        
        this.middleRowPanel = 
            DependencyManager.getDependencyManager().
                getObject(MasterCodeView.class, "masterCodeView");
    }
    
    
    private void loadControls() {
        // this.selectPanel = new AgentProcedureSelectView("controller");
        selectPanel.setLayout(
            new BoxLayout(selectPanel, BoxLayout.X_AXIS)
        );
        
        final int leftOffset = 5;
        final int height = 50;
        selectPanel.add(Box.createRigidArea(
            new Dimension(leftOffset, height)
        ));
        
        CategorySelector categoryPanel = new CategorySelectPanel();
        selectPanel.add(categoryPanel);
        
        agentProcedurePanel = 
            new AgentProcedureSelectView("controller");
        
        MasterCodeView codeView = DependencyManager.getDependencyManager().
            getObject(MasterCodeView.class, "masterCodeView");
        codeView.setAgentMethodSelectPanel(agentProcedurePanel);
        codeView.setCategorySelector(categoryPanel);
        setupMenuBar();
    }
    
    
    private void setupMenuBar() {
        DependencyManager dep = DependencyManager.getDependencyManager();
        frame.setJMenuBar(dep.getObject(MyMenuBar.class, "menuBar"));
        frame.addWindowListener(
            dep.getObject(FileMenuAndWindowListener.class, "menuListener")
        );
    }
}
