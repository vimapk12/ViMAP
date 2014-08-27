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
