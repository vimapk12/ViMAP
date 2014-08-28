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


package edu.vanderbilt.sets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;

/**
 * A panel to hold buttons for editing sets, and for displaying status
 * information about what set is currently displayed or what action
 * has just been taken, such as creating a set.
 */
public final class SetPanel extends JPanel implements ActionListener {
    
    private static final long serialVersionUID = -6763915048617209613L;
    
    /**
     * Panel for displaying messages about sets.
     */
    private JPanel messagePanel;
    private JLabel messageLabel;

    /**
     * Panel for holding buttons for working with sets.
     */
    private JPanel buttonPanel;
    
    /**
     * Clicked when a new set's shape has been completed.
     */
    private JButton doneDrawingShapeButton;
    
    /**
     * Clicked to cancel creating a new set.
     */
    private JButton cancelNewSetButton;
    
    /**
     * Clicked when done editing the shape of a pre-existing set.
     */
    private JButton doneEditingShapeButton;
    
    /**
     * Clicked to start from scratching drawing the shape for a set
     * whose shape is not fully editable once started.
     */
    private JButton redrawShapeButton;
    
    /**
     * Clicked to revert to the previous shape.
     */
    private JButton cancelRedrawShapeButton;
    
    private MasterCodeController codeController;
    
    public SetPanel() {
        super();
        
        assert SwingUtilities.isEventDispatchThread();
        this.setBackground(Color.WHITE);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setupMessage();
        setupButtons();
    }
    
    public void init() {
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
    }
    
    public void setupBounds() {
        assert SwingUtilities.isEventDispatchThread();
        assert this.isDisplayable();
        
        final Graphics2D myGraphics = (Graphics2D) this.getGraphics();
        final FontRenderContext frc = myGraphics.getFontRenderContext();
        final int fontSize = 20;
        final Font myFont = new Font("Helvetica", Font.PLAIN, fontSize);
        
        // don't provide space for more text than this...
        final TextLayout layout = 
            new TextLayout("Mmmmmmmmmmmmmmmmm", myFont, frc);
        final Rectangle2D bounds = layout.getBounds();
        
        final int padding = 10;
        Dimension minSize = new Dimension(
            (int) bounds.getWidth() + padding, 
            (int) bounds.getHeight() + padding
        );
        this.messagePanel.setPreferredSize(minSize);
        
        final int twoButtonWidth = 200;
        final int buttonHeight = 50;
        minSize = new Dimension(twoButtonWidth, buttonHeight);
        this.buttonPanel.setPreferredSize(minSize);
        
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        minSize = new Dimension(
            Math.max(
                this.messagePanel.getPreferredSize().width, 
                this.buttonPanel.getPreferredSize().width
            ),
            this.messagePanel.getPreferredSize().height 
                + this.buttonPanel.getPreferredSize().height
        );
        
        this.setPreferredSize(minSize);
    }
   
    
    private void setupMessage() {
        this.messagePanel = new JPanel();
        this.messagePanel.setBackground(Color.WHITE);
        
        this.messageLabel = new JLabel();
        final int fontSize = 20;
        Font myFont = new Font("Helvetica", Font.PLAIN, fontSize);
        this.messageLabel.setFont(myFont);
        
        this.messagePanel.add(this.messageLabel);
        this.add(this.messagePanel);
    }
    
    private void setupButtons() {
        this.buttonPanel = new JPanel();
        this.buttonPanel.setBackground(Color.WHITE);
        this.buttonPanel.setLayout(
            new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS)
        );
                
        this.doneDrawingShapeButton = new JButton("Done Drawing Shape");
        this.doneDrawingShapeButton.addActionListener(this);
        this.buttonPanel.add(this.doneDrawingShapeButton);
        
        this.cancelNewSetButton = new JButton("Cancel New Group");
        this.cancelNewSetButton.addActionListener(this);
        this.buttonPanel.add(this.cancelNewSetButton);
        
        this.doneEditingShapeButton = new JButton("Done Editing Shape");
        this.doneEditingShapeButton.addActionListener(this);
        this.buttonPanel.add(this.doneEditingShapeButton);
        
        this.redrawShapeButton = new JButton("Redraw Shape");
        this.redrawShapeButton.addActionListener(this);
        this.buttonPanel.add(this.redrawShapeButton);

        this.cancelRedrawShapeButton = new JButton("Cancel Redraw Shape");
        this.cancelRedrawShapeButton.addActionListener(this);
        this.buttonPanel.add(this.cancelRedrawShapeButton);
        
        setAvailableButtons(false, false, false, false, false);
        this.add(this.buttonPanel);
    }
    
    public void setMessage(final String message) {
        assert SwingUtilities.isEventDispatchThread();
        this.messageLabel.setText(message);
        this.messagePanel.revalidate();
        this.messagePanel.repaint();
    }
    
    public void setAvailableButtons(
        final boolean doneDrawingShape,
        final boolean cancelNewSet,
        final boolean doneEditingShape,
        final boolean redrawShape,
        final boolean cancelRedrawShape
    ) {
        assert SwingUtilities.isEventDispatchThread();

        this.doneDrawingShapeButton.setVisible(doneDrawingShape);
        this.cancelNewSetButton.setVisible(cancelNewSet);
        this.doneEditingShapeButton.setVisible(doneEditingShape);
        this.redrawShapeButton.setVisible(redrawShape);
        this.cancelRedrawShapeButton.setVisible(cancelRedrawShape);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        JButton button = (JButton) event.getSource();
        
        if (button == this.doneDrawingShapeButton) {
            this.codeController.doneDrawingShape();
        } else if (button == this.cancelNewSetButton) {
            this.codeController.cancelNewSet();
        } else if (button == this.doneEditingShapeButton) {
            this.codeController.doneEditingShape();
        } else {
            throw new IllegalArgumentException();
        }
        
        /*
        else if (button == this.redrawShapeButton) {
            // TODO
        } else if (button == this.cancelRedrawShapeButton) {
            // TODO
        } 
        */
    }
}
