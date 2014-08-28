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


package edu.vanderbilt.measure;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;

public final class SettingsDialog extends JDialog {

    private static final long serialVersionUID = 3441364369748689062L;
    private GraphSettings graphSettings;
    private SettingsPanel settingsPanel;
    private String commandString = "";
    
    private OkAction theOkAction = new OkAction();
    private CancelAction theCancelAction = new CancelAction();

    public SettingsDialog(
        final Frame frame, 
        final Component comp, 
        final GraphSettings aGraphSettings
    ) {
        super(frame, true);
        init(comp, aGraphSettings);
    }
    
    GraphSettings getGraphSettings() {
        return this.graphSettings;
    }

    SettingsPanel getSettingsPanel() {
        return this.settingsPanel;
    }

    OkAction getTheOkAction() {
        return this.theOkAction;
    }

    CancelAction getTheCancelAction() {
        return this.theCancelAction;
    }
    
    String getCommandString() {
        return this.commandString;
    }
    
    void setCommandString(final String str) {
        this.commandString = str;
    }
    
    private void init(
        final Component parent, 
        final GraphSettings aGraphSettings
    ) {
        this.graphSettings =  aGraphSettings;
        this.setTitle("Graph Settings");
        this.initUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
    }

    private void initUI() {
        final JPanel contentPane = (JPanel) this.getContentPane();
        final int borderWidth = 20;
        contentPane.setBorder(new EmptyBorder(
            borderWidth, 
            borderWidth, 
            borderWidth, 
            borderWidth
        ));
        contentPane.setLayout(new BorderLayout());
        this.settingsPanel = new SettingsPanel();
        contentPane.add(this.settingsPanel.getPanel(), BorderLayout.CENTER);
        final JPanel buttonPane = new ButtonPanel();
        contentPane.add(buttonPane, BorderLayout.SOUTH);
    }
    
    /////////////////////////////////////////////////////////////////
    // INNER CLASS: BUTTON PANEL
    
    private class ButtonPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        static final int HORIZONTAL_GAP = 10;
        public ButtonPanel() {
            super(new FlowLayout(FlowLayout.TRAILING, HORIZONTAL_GAP, 0));
            setBorder(new EmptyBorder(HORIZONTAL_GAP, 0, 0, 0));
            final JButton okButton = 
                new JButton(getTheOkAction());
            final JButton cancelButton = 
                new JButton(getTheCancelAction());
        
            add(cancelButton);
            add(okButton);
        
        }
    }
    
    /////////////////////////////////////////////////////////////////
    // INNER CLASS: OK ACTION
    
    private class OkAction extends AbstractAction {
        private static final long serialVersionUID = 4004359183013019513L;

        public OkAction() {
            super("OK");
            putValue(SHORT_DESCRIPTION, null);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            boolean oldShowAll = getGraphSettings().isShowAllMeasurePoints();
            final boolean changedShowAll = 
                getSettingsPanel().getShowAllCheckBox().isSelected() 
                    != oldShowAll;
            getGraphSettings().setUsingColor(
                getSettingsPanel().getColorCheckbox().isSelected()
            );
            getGraphSettings().setUsingAllTimeAverage(
                getSettingsPanel().getAllTimeAvgCheckbox().isSelected()
            );
            getGraphSettings().setShowAllMeasurePoints(
                getSettingsPanel().getShowAllCheckBox().isSelected()
            );
            getGraphSettings().setWindowSize(
                (String) getSettingsPanel().
                    getMovingAverageWindowCombo().getSelectedItem()
            );
            String newCommandString  = 
                " set using-color? " 
                + getGraphSettings().isUsingColor();
            newCommandString +=
                " set window-size "
                + getSettingsPanel().getMovingAverageWindowCombo().
                    getSelectedItem();
            newCommandString += 
                " set all-time-avg? " 
                + getGraphSettings().isAllTimeAverage();
            
            //changing agent is catastrophic - make sure it's necessary.
            String oldAgent = getGraphSettings().getAgent();
            String newAgent = (String) 
                getSettingsPanel().getAgentCombo().getSelectedItem();
            if (!newAgent.equals(oldAgent)) {
                getGraphSettings().setAgent(newAgent);
                newCommandString += " set my-agent-type \"" 
                    + newAgent + "\" start-fresh ";
            } else if (changedShowAll) {
                newCommandString += " start-fresh ";
            }
            
            setCommandString(newCommandString);
            SettingsDialog.this.dispose();
        }
    }
    
    /////////////////////////////////////////////////////////////////
    // INNER CLASS: CANCEL ACTION
    
    private class CancelAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public CancelAction() {
            super("Cancel");
            putValue(SHORT_DESCRIPTION, null);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            setCommandString("");
            SettingsDialog.this.dispose();
        }
    }
    
    /////////////////////////////////////////////////////////////////
    // INNER CLASS: SETTINGS PANEL

    private class SettingsPanel {
        private JPanel panel;
        private JComboBox<String> agentCombo;
        private JCheckBox colorCheckBox;
        private JCheckBox allTimeAvgCheckBox;
        private JComboBox<String> movingAverageWindowCombo;
        private JCheckBox showAllCheckBox;
        
        public SettingsPanel() {
            this.panel = initUI();
        }

        public JPanel getPanel() {
            return this.panel;
        }
        
        public JComboBox<String> getAgentCombo() {
            return this.agentCombo;
        }
        
        public JCheckBox getColorCheckbox() {
            return this.colorCheckBox;
        }
        
        public JCheckBox getShowAllCheckBox() {
            return this.showAllCheckBox;
        }
        
        public JComboBox<String> getMovingAverageWindowCombo() {
            return this.movingAverageWindowCombo;
        }
        
        public JCheckBox getAllTimeAvgCheckbox() {
            return this.allTimeAvgCheckBox;
        }

        private JPanel initUI() {
            final int rows = 5;
            final int cols = 2;
            JPanel retPanel = new JPanel(new GridLayout(rows, cols));
           
            this.colorCheckBox = new JCheckBox();
            final JLabel colorLabel = new JLabel("Use Color? ");
            colorLabel.setAlignmentY(RIGHT_ALIGNMENT);
            colorLabel.setLabelFor(this.colorCheckBox);
            this.colorCheckBox.setSelected(
                getGraphSettings().isUsingColor()
            );
            
            this.allTimeAvgCheckBox = new JCheckBox();
            final JLabel avgLabel = new JLabel("Show All-Time Avg? ");
            avgLabel.setAlignmentY(RIGHT_ALIGNMENT);
            avgLabel.setLabelFor(this.allTimeAvgCheckBox);
            this.allTimeAvgCheckBox.setSelected(
                getGraphSettings().isAllTimeAverage()
            );
            
            this.agentCombo = new JComboBox<String>(
                getGraphSettings().getAgentNames()
            );
            
            this.showAllCheckBox = new JCheckBox();
            final JLabel showAllLabel = new JLabel("Show All Points?");
            showAllLabel.setAlignmentY(RIGHT_ALIGNMENT);
            showAllLabel.setLabelFor(this.showAllCheckBox);    
            this.showAllCheckBox.setSelected(
                getGraphSettings().isShowAllMeasurePoints()
            );
            
            DependencyManager deps = DependencyManager.getDependencyManager();
            DomainModel domainModel = 
                deps.getObject(DomainModel.class, "domainModel");
            final boolean isImageComp = domainModel.isImageComputation();

            JLabel agentLabel = new JLabel("Watch agent ");
            if (isImageComp) {
                agentLabel = new JLabel("Watch group ");
            }
            agentLabel.setAlignmentY(RIGHT_ALIGNMENT);
            agentLabel.setLabelFor(this.agentCombo);
            this.agentCombo.setSelectedItem(
                getGraphSettings().getAgent()
            );
            
            final String[] windowSizeChoices = {"1", "3", "5", "10"};
            this.movingAverageWindowCombo = 
                new JComboBox<String>(windowSizeChoices);
            final JLabel windowSizeLabel = new JLabel("Recent Average Range");
            windowSizeLabel.setAlignmentY(RIGHT_ALIGNMENT);
            windowSizeLabel.setLabelFor(this.movingAverageWindowCombo);
            this.movingAverageWindowCombo.setSelectedItem(
                getGraphSettings().getWindowSize()
            );
            
            retPanel.add(colorLabel);
            retPanel.add(this.colorCheckBox);
            retPanel.add(avgLabel);
            retPanel.add(this.allTimeAvgCheckBox);
            retPanel.add(showAllLabel);
            retPanel.add(this.showAllCheckBox);
            retPanel.add(agentLabel);
            retPanel.add(this.agentCombo);
            retPanel.add(windowSizeLabel);
            retPanel.add(this.movingAverageWindowCombo);

            setupEntryFields();
               
            retPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
                "escape"
            );
            retPanel.getActionMap().put(
                "escape", 
                getTheCancelAction()
            );
        
            retPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
                "enter"
            );
            retPanel.getActionMap().put(
                "enter", 
                getTheOkAction()
            );
               
            return retPanel;
        }
        
        private void setupEntryFields() {
            this.colorCheckBox.setSelected(
                getGraphSettings().isUsingColor()
            );
            this.agentCombo.setSelectedItem(
                getGraphSettings().getAgent()
            );  
        }
    }
}
