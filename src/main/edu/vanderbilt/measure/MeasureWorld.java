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


package edu.vanderbilt.measure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.nlogo.api.CompilerException;
import org.nlogo.lite.InterfaceComponent;
import org.nlogo.window.InvalidVersionException;
import org.nlogo.window.SpeedSliderPanel;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.Loader;
import edu.vanderbilt.simulation.SimulationCaller;

public final class MeasureWorld extends JPanel {

    private static final long serialVersionUID = 4410646209214507090L;
    private String modelFile;
    private GraphSettings graphSettings;
    private int worldIndex = 0;
    
    private JFrame containingFrame;
    private InterfaceComponent measureNetLogo;
    private InterfaceComponent enactmentNetLogo;
    private final ButtonGroup graphSelectionButtonGroup;
    private JToggleButton firstButton;
    private GraphComboBox graphComboBox;
    private int selectedGraphIndex;
    private JLabel agentLabel;    
    
    public MeasureWorld(
        final JFrame aContainingFrame,
        final String aModelFile, 
        final String[] names, 
        final int index,
        final List<MeasureOption> measureOptions,
        final boolean canHighlightAgents,
        final InterfaceComponent aEnactmentWorld
    ) {
        assert SwingUtilities.isEventDispatchThread();

        this.graphSettings = new GraphSettings(names);
        this.worldIndex = index;
        this.modelFile = aModelFile;
        this.containingFrame = aContainingFrame;
        this.measureNetLogo = new InterfaceComponent(containingFrame);
        this.graphSelectionButtonGroup = new ButtonGroup();
        this.enactmentNetLogo = aEnactmentWorld;
        setupUI(measureOptions, canHighlightAgents);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    public void removeSetAndUpdate(final String setName) {
        final boolean removedCurrentSet = 
            this.graphSettings.removeAgentName(setName);
        if (removedCurrentSet) {
            settingsChangedCallback(getUpdateString());
            updateLabel();
        }
    }
    
    private void updateLabel() {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getAgentLabel().setText(getLabelString());
           }
        });
    }
    
    JLabel getAgentLabel() {
        return this.agentLabel;
    }
    
    public void addSetAndUpdate(final String setName) {
        this.graphSettings.addAgentName(setName);
    }
    
    public void runMeasureCommand(final String command) 
        throws CompilerException {
        this.measureNetLogo.command(command);
    }
    
    public void runMeasureCommandLater(final String command) 
        throws CompilerException {
        this.measureNetLogo.commandLater(command);
    }
    
    public GraphSettings getSettings() {
        return this.graphSettings;
    }
    
    /*
     * Must be called after the NetLogo world has been set up
     * through a call to setupUIOffEDT using invokeAndWait.
     */
    public void pickFirstGraph() {
        assert SwingUtilities.isEventDispatchThread();
        
        if (this.firstButton != null) {
            this.firstButton.doClick();
        }
        if (this.graphComboBox != null) {
            this.graphComboBox.setSelectedIndex(0);
            this.graphComboBox.reactToSelectedItem();
        }
    }
    
    public void setupUIOffEDT(final String[] varNameList) {
        assert !SwingUtilities.isEventDispatchThread();
        String setupCommand = "setup set my-agent-type \"";
        setupCommand += this.graphSettings.getAgent() + "\"";
        try {
            this.measureNetLogo.command(setupCommand);
            SimulationCaller.cleanUpInterfaceComponent(
                this.measureNetLogo, 
                Color.WHITE
            );
            
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (String varName: varNameList) {
                builder.append("\"");
                builder.append(varName);
                builder.append("\" ");
            }
            builder.append("]");
                        
            // append list of "extra" variable names to 
            // the list of names for default variables
            this.measureNetLogo.command(
                "set var-name-list sentence var-name-list " + builder.toString()
            );
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
    
    InterfaceComponent getMeasureNetLogo() { 
        return this.measureNetLogo; 
    }
    
    JFrame getMyFrame() {
        return this.containingFrame;
    }
    
    GraphSettings getMySettings() {
        return this.graphSettings;
    }
    
    int getWorldIndex() {
        return this.worldIndex;
    }
    
    InterfaceComponent getMyNetLogo() {
        return this.measureNetLogo;
    }

    int getSelectedGraphIndex() {
        return this.selectedGraphIndex;
    }
    
    void setSelectedGraphIndex(final int index) {
        this.selectedGraphIndex = index;
    }
    
    InterfaceComponent getEnactmentWorld() {
        return this.enactmentNetLogo;
    }
    
    /**
    * Searches through the Components that are children of the 
    * input argument Component co, to find an instance of 
    * SpeedSliderPanel.  This panel's visibility can be set to
    * false, removing the speed slider from the ViMAP view.
    * 
    * @param co
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
    
    private String getUpdateString() {
        String result  = 
            " set using-color? " 
            + this.graphSettings.isUsingColor();
        result += 
            " set all-time-avg? " 
            + this.graphSettings.isAllTimeAverage();
            
        //changing agent is catastrophic - make sure it's necessary
        String agent = this.graphSettings.getAgent();
        result += " set my-agent-type \"" 
            + agent + "\" start-fresh ";
        return result;
    }
    
    void settingsChangedCallback(final String cmd) {
        updateLabel();
        Thread t = new Thread(new Runnable() { 
            @Override
            public void run() {
                try {
                    getMeasureNetLogo().command(cmd);
                    SimulationCaller.updateMeasureWorld(getWorldIndex());
                } catch (final CompilerException e) {
                    e.printStackTrace();
                }
            } 
        });
        
        t.start();
    }
    
    String getLabelString() {
        final int maxLength = 15;
        String agentName = this.graphSettings.getAgent();
        if (agentName.length() > maxLength) {
            agentName = agentName.substring(0, maxLength);
        }
        
        DependencyManager deps = DependencyManager.getDependencyManager();
        DomainModel domainModel = 
            deps.getObject(DomainModel.class, "domainModel");
        final boolean isImageComp = domainModel.isImageComputation();
        if (isImageComp) {
            return "<html><center>Group: <b>" 
                    + agentName + "</b></center></html>";
        }
        
        return "<html><center>Agent: <b>" 
            + agentName + "</b></center></html>";
    }
    
    private void setupUI(
        final List<MeasureOption> measureOptions,
        final boolean canHighlightAgents
    ) {
        assert SwingUtilities.isEventDispatchThread();
        
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);

        this.agentLabel = new JLabel(getLabelString());
        final float fontSize = 18.0f;
        this.agentLabel.setFont(
            this.agentLabel.getFont().deriveFont(fontSize)
        );
        buttonPanel.add(this.agentLabel);
        
        final int maxButtons = 4;
        if (measureOptions.size() <= maxButtons) {
            this.graphComboBox = null;
            this.firstButton = null;
            for (int i = 0; i < measureOptions.size(); i++) {
                MeasureOption measureOption = measureOptions.get(i);
                JToggleButton button = setupButton(
                    measureOption.getButtonTextLines(), 
                    measureOption.getCommand(),
                    i
                );
                buttonPanel.add(button);
                if (this.firstButton == null) {
                    this.firstButton = button;
                }
            }
        } else {
            this.firstButton = null;
            this.graphComboBox = setupComboBox(measureOptions);
            buttonPanel.add(this.graphComboBox);
        }
                
        final int rigidAreaHeight = 30;
        final int rigidAreaWidth = 30;
        buttonPanel.add(Box.createRigidArea(
            new Dimension(rigidAreaHeight, rigidAreaWidth)
        ));
        
        final JButton settingsButton = new JButton(
            "<html><center>Graph<br>Settings</center></html>"
        );
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                SettingsDialog dialog = 
                    new SettingsDialog(
                        getMyFrame(), 
                        MeasureWorld.this, 
                        getMySettings()
                    );
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.setVisible(true);
                String cmd = dialog.getCommandString();
                if (cmd.length() != 0) {
                    settingsChangedCallback(cmd);
                }
            }
        });
        
        
        URL url = null;
        if (Loader.isRunningJavaWebStart()) {
            url = MeasureWorld.class.getClassLoader().
                getResource(this.modelFile.substring(1));
        } else {
            url = Thread.currentThread().getClass().getResource(this.modelFile);
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
             Loader.errorShutDown("OOPS");
         }
         
        final String modelSourceLocation = sb.toString();
        try {
            getMeasureNetLogo().openFromSource(
                "name", 
                "path", 
                modelSourceLocation
            );
        } catch (InvalidVersionException e) {
            e.printStackTrace();
        }
        
        buttonPanel.add(settingsButton);
        
        if (canHighlightAgents) {
            final JButton highlightButton = new JButton(
                "<html><center>Highlight<br>Agents</center></html>"
            );
            highlightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    String agentType = getMySettings().getAgent();
                    
                    try {
                        getEnactmentWorld().commandLater(
                            "highlight-agents-for-graph " 
                            + getSelectedGraphIndex()
                            + " " + agentType
                        );
                    } catch (CompilerException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            final JButton unighlightButton = new JButton(
                "<html><center>Unhighlight<br>Agents</center></html>"
            );
            unighlightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent event) {
                    try {
                        getEnactmentWorld().commandLater("unhighlight-agents");
                    } catch (CompilerException e) {
                        e.printStackTrace();
                    }
                }
            }); 
            
            buttonPanel.add(highlightButton);
            buttonPanel.add(unighlightButton);
        }
        
        MeasureWorld.this.add(buttonPanel, BorderLayout.WEST);
        MeasureWorld.this.add(getMeasureNetLogo(), BorderLayout.CENTER);
        SpeedSliderPanel ssp = 
            findSliderInComp(MeasureWorld.this);
        if (ssp != null) {
            ssp.setVisible(false);
        }      
    }
    
    private String htmlStringArray(final String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (int i = 0; i < stringArray.length; i++) {
            if (i == 0) {
                builder.append("<b>");
            }
            builder.append(stringArray[i]);
            if (i == 0) {
                builder.append("</b>");
            }
            
            if (i < stringArray.length) {
                builder.append("<br>");
            }
        }
        builder.append("</html>");
        
        return builder.toString();
    }
    
    @SuppressWarnings("unused")
    private String concatenateStringArray(final String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        for (String str: stringArray) {
            builder.append(str).append(" ");
        }
        return builder.toString();
    }
    
    private GraphComboBox setupComboBox(
        final List<MeasureOption> measureOptions
    ) {
        if (measureOptions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        String[] titles = new String[measureOptions.size()];
        String [] commands = new String[measureOptions.size()];
        String[] fullTitles = new String[measureOptions.size()];
        int i = 0;
        for (MeasureOption measureOption: measureOptions) {
            titles[i] = measureOption.getButtonTextLines()[0];
            commands[i] = measureOption.getCommand();
            fullTitles[i] = 
                htmlStringArray(measureOption.getButtonTextLines());
            i++;
        }
        
        return new GraphComboBox(
            titles, 
            commands, 
            fullTitles, 
            this.measureNetLogo
        );
    }
    
    private JToggleButton setupButton(
        final String[] textLines, 
        final String command,
        final int buttonIndex
    ) {
        if (textLines.length < 1) {
            throw new IllegalArgumentException();
        }
        
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("<html><center><b>");
        textBuilder.append(textLines[0]);
        textBuilder.append("</b>");
        for (int i = 1; i < textLines.length; i++) {
            textBuilder.append("<br>");
            textBuilder.append(textLines[i]);
        }
        textBuilder.append("</center></html>");
        
        final JToggleButton result = 
            new DarkerToggleButton(textBuilder.toString());
        
        result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent ae) {
                try {
                    getMeasureNetLogo().commandLater(command);
                    setSelectedGraphIndex(buttonIndex);
                } catch (CompilerException e) {
                    e.printStackTrace();
                }
            }
        });
        this.graphSelectionButtonGroup.add(result);
        return result;
    }
}
