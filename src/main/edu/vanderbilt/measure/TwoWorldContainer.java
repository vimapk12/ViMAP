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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.nlogo.lite.InterfaceComponent;

public final class TwoWorldContainer extends JFrame {

    private static final long serialVersionUID = -8389560335490243145L;
    private Vector<MeasureWorld> myWorlds = new Vector<MeasureWorld>();
    private JPanel myPanel;
    
    public TwoWorldContainer(
        final String modFile, 
        final String[] names, 
        final int numberOfWorlds,
        final List<MeasureOption> measureOptions,
        final boolean canHighlightAgents,
        final InterfaceComponent enactmentWorld
    ) {
        super("Measure Worlds");
        
        assert SwingUtilities.isEventDispatchThread();
        GridLayout layout = new GridLayout(numberOfWorlds, 1);
        final int verticalGap = 20;
        layout.setVgap(verticalGap);
        
        this.myPanel = new JPanel(layout);
        for (int i = 0; i < numberOfWorlds; i++) {
            MeasureWorld w = new MeasureWorld(
                this, 
                modFile, 
                names, 
                i + 1,
                measureOptions,
                canHighlightAgents,
                enactmentWorld
            );
            this.myWorlds.add(w);
            this.myPanel.add(w);
        }
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        JPanel alwaysTopPanel = getAlwaysOnTopPanel();
        outerPanel.add(alwaysTopPanel);
        outerPanel.add(this.myPanel);
        alwaysTopPanel.setAlignmentX(LEFT_ALIGNMENT);
        this.myPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        add(outerPanel);
        pack();
        setResizable(false);
        setVisible(true);
    }
    
    public JPanel getAlwaysOnTopPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        result.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JCheckBox checkBox = new JCheckBox("Always On Top");
        result.add(checkBox);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (((JCheckBox) e.getSource()).isSelected()) {
                    TwoWorldContainer.this.setAlwaysOnTop(true);
                } else {
                    TwoWorldContainer.this.setAlwaysOnTop(false);
                }
            }
        });
        
        result.setBackground(Color.WHITE);
        result.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return result;
    }
    
    public Vector<MeasureWorld> getMeasureWorlds() { 
        return this.myWorlds; 
    }
}
