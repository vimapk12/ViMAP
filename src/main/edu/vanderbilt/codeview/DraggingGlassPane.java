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


package edu.vanderbilt.codeview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.MyMenuBar;

public final class DraggingGlassPane extends JPanel {

    private static final long serialVersionUID = 4482696145422027935L;
    private edu.vanderbilt.codecomponentview.BlockView draggedBlockView;
    private MyMouseListener mouseListener;
    private edu.vanderbilt.codecomponentview.SwingPaletteView myPaletteView = 
        DependencyManager.getDependencyManager().
            getObject(
                edu.vanderbilt.codecomponentview.SwingPaletteView.class, 
                "palette"
            );
    private edu.vanderbilt.codecomponentview.SwingUserCodeView myUserCodeView = 
        DependencyManager.getDependencyManager().
            getObject(
                edu.vanderbilt.codecomponentview.SwingUserCodeView.class, 
                "userCode"
            );
    private static int menuHeight;
    
    private Rectangle highlightArea;
    private static final int HORIZ_OFFSET = 10;
    private static final int HIGHLIGHT_HEIGHT = 6;
    private Color highlightColor = Color.YELLOW;
    
    public DraggingGlassPane() {
        super();
        
        assert SwingUtilities.isEventDispatchThread();
        
        setLayout(null);
        setupMouseListener();
        this.setVisible(true);
    }
    
    public static void init() {
        menuHeight = DependencyManager.getDependencyManager().
            getObject(MyMenuBar.class, "menuBar").getHeight();
    }
    
    public static int getMenuHeight() {
        return menuHeight;
    }

    public void setDraggedBlockView(
        final edu.vanderbilt.codecomponentview.BlockView aDraggedBlockView
    ) {
        this.draggedBlockView = aDraggedBlockView;
        
        if (aDraggedBlockView != null) {
            this.add(aDraggedBlockView);
        }

        repaint();
    }
    
    private void setupMouseListener() {
        this.mouseListener = new MyMouseListener();
        addMouseListener(this.mouseListener);
        addMouseMotionListener(this.mouseListener);            
    }
    
    edu.vanderbilt.codecomponentview.BlockView getDraggedBlockView() {
        return this.draggedBlockView;
    }
    
    public void handleClick(final Point absoluteLocation) {
        Point paletteLocation = 
            new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(paletteLocation, myPaletteView);
        
        if (myPaletteView.contains(paletteLocation)) {
            myPaletteView.handleClick(absoluteLocation);
        } else {
            Point userCodeLocation = 
                new Point(absoluteLocation.x, absoluteLocation.y);
            SwingUtilities.convertPointFromScreen(
                paletteLocation, 
                myUserCodeView
            );
            
            if (myUserCodeView.contains(userCodeLocation)) {
                myUserCodeView.handleClick(absoluteLocation);
            }
        }
    }
    
    public boolean hasDraggedBlockView() {
        return draggedBlockView != null;
    }
    
    private void myConvertPointFromScreen(
        final Point absoluteLocation,
        final Component component
    ) {
        SwingUtilities.convertPointFromScreen(absoluteLocation, component);
        absoluteLocation.y += menuHeight;
    }
    
    public void handleDrag(final Point absoluteLocation) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        myConvertPointFromScreen(
            localLocation, 
            myPaletteView.getFrame().getContentPane()
        );      
        draggedBlockView.handleDrag(localLocation); 

        Point userLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        myConvertPointFromScreen(userLocation, myUserCodeView);
        
        // to test for inclusion in the user code area, don't include
        // a y-offset to account for the menu bar height.
        Point standardUserLocation = 
            new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(
            standardUserLocation, 
            myUserCodeView
        );
        if (myUserCodeView.contains(standardUserLocation)) {
            myUserCodeView.handleDragFromGlassPane(
                absoluteLocation,
                draggedBlockView.getDepthAddedWhenDropped()
            );
        } else {
            myUserCodeView.removeDropTargets();
        }
    }
    
    public void handleDrop(final Point absoluteLocation) {
        if (draggedBlockView == null) {
            return;
        }
        
        draggedBlockView.handleDrop();
        
        Point userLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(userLocation, myUserCodeView);
        if (
            myUserCodeView.contains(userLocation) 
        ) {
            myUserCodeView.setClickedBlockView(draggedBlockView);
            myUserCodeView.handleDrop();
        }   
        
        clearGlassPane();
    }
    
    public void clearGlassPane() {
        if (this.containsComponent(draggedBlockView)) {
            this.remove(draggedBlockView);
        }
        
        setDraggedBlockView(null);
        this.setVisible(false);
    }
    
    private boolean containsComponent(final Component target) {
        for (Component currentComponent: this.getComponents()) {
            if (currentComponent.equals(target)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void highlight(
        final edu.vanderbilt.codecomponentview.BlockView toHighlight, 
        final boolean isHighlight,
        final boolean isBelow
    ) {
        if (isHighlight) {
            Rectangle bounds = toHighlight.getBounds();

            Component ancestorContainer = 
                toHighlight.getParent();
                        
            bounds = SwingUtilities.convertRectangle(
                ancestorContainer, 
                bounds, 
                this
            );
            
            int y;
            if (isBelow) {
                y = (int) (bounds.getMaxY() - HIGHLIGHT_HEIGHT / 2);
            } else {
                y = (int) (bounds.getMinY() - HIGHLIGHT_HEIGHT / 2);
            }
            
            highlightArea = new Rectangle(
                (int) (bounds.getX() + HORIZ_OFFSET), 
                y,
                (int) bounds.getWidth() - 2 * HORIZ_OFFSET, 
                HIGHLIGHT_HEIGHT
            );                
        } else {
            highlightArea = null;
        }
            
        this.repaint();
    }
    
    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        
        if (this.highlightArea != null) {   
            graphics.setColor(this.highlightColor);
            graphics.fillRect(
                this.highlightArea.x,  
                this.highlightArea.y,
                this.highlightArea.width,
                this.highlightArea.height
            );
        }
    }
    
    
    // ************************************************************
    // LISTENER INNER CLASS


    /**
     * Listens for clicks, drags, and drops in the panel.
     */
    private class MyMouseListener extends MouseInputAdapter {
         
        public MyMouseListener() {
            super();
        }

        /*
         * When a click is made in the panel, set clickedBlock 
         * to be the innermost block under the click, if any. 
         * Set that block's state to show that it is clicked,
         * and redraw the panel after making adjustments for the click.
         */
        @Override
        public void mousePressed(final MouseEvent aEvent) {
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                DraggingGlassPane.this
            );
            
            handleClick(pointToConvert);
        }

        /*
         * When a drag occurs, don't let anything happen unless 
         * the pointer is over the panel.
         * Otherwise, let the clicked block (if any) handle 
         * the drag event itself.
         */
        @Override
        public void mouseDragged(final MouseEvent aEvent) {         
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                DraggingGlassPane.this
            );
            
            handleDrag(pointToConvert);
        }
        
        /*
         * When a drop occurs, move the clicked block and contents 
         * back to the default pane,
         * and let the clicked block handle its own drop behavior.
         */
        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                DraggingGlassPane.this
            );
            
            handleDrop(pointToConvert);
        }
    }
}
