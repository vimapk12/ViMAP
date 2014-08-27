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
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A tool bar that stretches across the top of the frame.
 * 
 * May be used to control when a run stops and starts, or set what is displayed
 * in the frame.
 */
public final class TopToolbar extends JPanel {

    private static final long serialVersionUID = 8842666757860013495L;
    
    // tools that should line up above the middle column
    private final JPanel middleColumnPanel; 
    
    // tools that should line up above the right column
    private final JPanel rightColumnPanel; 
    
    /**
     * Constructor.
     * 
     * @param aMiddleColumnPanel panel holding tools that 
     * should line up above the middle column
     * @param aRightColumnPanel panel holding tools that 
     * should line up above the right column
     */
    public TopToolbar(
        final JPanel aMiddleColumnPanel,
        final JPanel aRightColumnPanel
   ) {
        super(new BorderLayout());
        
        assert SwingUtilities.isEventDispatchThread();
        
        this.middleColumnPanel = aMiddleColumnPanel;
        this.rightColumnPanel = aRightColumnPanel;
        
        addPanelsAndSetDimensions();
    }
    
    public void adjustDimensions() {
        final int yPadding = 6;
        this.middleColumnPanel.setPreferredSize(
            new Dimension(
                GraphicalInterface.MIDDLE_COLUMN_WIDTH,
                this.middleColumnPanel.getPreferredSize().height + yPadding
           )
        );
        Component child = this.middleColumnPanel.getComponent(0);
        setPreferredSize(new Dimension(
            getPreferredSize().width, 
            child.getMinimumSize().height + yPadding)
        );
        revalidate();
        repaint();
    }
    
    
    /**
     * Adds the contained panels to this panel and sets their dimensions.
     */
    private void addPanelsAndSetDimensions() {        
        this.add(this.middleColumnPanel, BorderLayout.CENTER);
        this.add(this.rightColumnPanel, BorderLayout.EAST);
    }
}
