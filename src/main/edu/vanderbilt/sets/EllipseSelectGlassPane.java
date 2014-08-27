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


package edu.vanderbilt.sets;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.vanderbilt.simulation.SimulationCaller;
import edu.vanderbilt.util.Util;

/**
 * A glass pane for creating and editing ellipse shapes
 * over the NetLogo window.
 */
public final class EllipseSelectGlassPane extends SelectGlassPane {

    private static final long serialVersionUID = -496202573369508357L;
    
    // diameter of the circles to show around "nodes" of a rectangle
    private static final int DIAMETER = SelectGlassPane.NODE_RADIUS * 2;
    
    /**
     * A corner of the rectangle that stays fixed while the opposite corner
     * is dragged.
     */
    private Point fixedPoint;
    
    /**
     * The rectangle that will be displayed on screen. Not necessarily
     * matching the "currentShape" until a push or pull.
     */
    private Rectangle rect; 
            
    public EllipseSelectGlassPane() {
        super();
        assert SwingUtilities.isEventDispatchThread();
        
        useCreateMouseListener();
    }
    
    @Override
    void useCreateMouseListener() {
        removeListeners();
        MouseInputAdapter mouseListener = 
            new EllipseSelectGlassPane.CreateRectMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        
        this.rect = null;
    }
    
    @Override
    void useEditMouseListener() {
        removeListeners();
        MouseInputAdapter mouseListener = 
            new EllipseSelectGlassPane.EditRectMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);  
    }
    
    private void removeListeners() {
        this.fixedPoint = null;
        
        MouseListener[] mouseListeners = getMouseListeners();
        for (MouseListener mouseListener: mouseListeners) {
            removeMouseListener(mouseListener);
        }
        
        MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
        for (MouseMotionListener mouseMotionListener: mouseMotionListeners) {
            removeMouseMotionListener(mouseMotionListener);
        }
    }
    
    Point getFixedPoint() {
        return this.fixedPoint;
    }
    
    void setFixedPoint(final Point aPoint) {
        this.fixedPoint = aPoint;
    }
    
    Rectangle getRect() {
        return this.rect;
    }
    
    void setRectAndEllipse(final Rectangle aRect) {
        this.rect = aRect;
    }
    
    @Override
    public void paintComponent(final Graphics aGraphics) {            
        if (this.rect == null) {
            // no need to draw anything if no rectangle has been drawn
            return;
        }
        
        final Graphics2D g2d = (Graphics2D) aGraphics;
        
        // draw a gray rectangle
        g2d.setColor(Color.GRAY);
        
        final int netLogoXWrtThis = 
            SimulationCaller.getViewLocationOnScreen().x 
                - this.getLocationOnScreen().x;
        final int netLogoYWrtThis = 
            SimulationCaller.getViewLocationOnScreen().y 
                - this.getLocationOnScreen().y;
        final Rectangle newRect = new Rectangle(
            this.rect.x + netLogoXWrtThis, 
            this.rect.y + netLogoYWrtThis, 
            this.rect.width, 
            this.rect.height
        );
        final Ellipse2D newEllipse = new Ellipse2D.Float(
            this.rect.x + netLogoXWrtThis, 
            this.rect.y + netLogoYWrtThis, 
            this.rect.width, 
            this.rect.height
        );
        
        g2d.draw(newRect);
        g2d.draw(newEllipse);
        
        if (isEditableLook()) {
            // if editable look, draw a circle at each "node" of the shape
            
            // offset by radius of circle to draw circles in correct places
            final int leftX = newRect.x - SelectGlassPane.NODE_RADIUS;
            final int topY = newRect.y - SelectGlassPane.NODE_RADIUS;
            final int rightX = leftX + newRect.width;
            final int bottomY = topY + newRect.height;
            
            g2d.fillOval(
                leftX, 
                topY, 
                DIAMETER, 
                DIAMETER
            );
            g2d.fillOval(
                leftX, 
                bottomY, 
                DIAMETER, 
                DIAMETER
            );
            g2d.fillOval(
                rightX, 
                topY, 
                DIAMETER, 
                DIAMETER
            );
            g2d.fillOval(
                rightX, 
                bottomY, 
                DIAMETER, 
                DIAMETER
            );
        }
    }
    
    @Override
    public void displayCurrentShape() {
        final int rectangleArgumentCount = 4;
        if (getCurrentShape().size() != rectangleArgumentCount) {
            this.rect = null;
            return;
        }
        
        int index = 0;
        final int leftXInSimulation = getCurrentShape().get(index++).intValue();
        final int topYInSimulation = getCurrentShape().get(index++).intValue();
        final int newWidth = 
            getCurrentShape().get(index++).intValue() - leftXInSimulation;
        
        // coordinates are from 0 at top of screen, topY must be subtracted
        final int newHeight = 
            getCurrentShape().get(index).intValue() - topYInSimulation;
        final Rectangle newRect = new Rectangle(
            leftXInSimulation,
            topYInSimulation,
            newWidth, 
            newHeight
        );
        setRectAndEllipse(newRect); 
    }

    @Override
    public List<Double> pullDisplayedShape() {
        List<Double> result = new ArrayList<Double>();
        if (this.rect == null) {
            return result;
        }
                
        final double leftX = this.rect.getX();
        final double topY = this.rect.getY();
        final double rightX = leftX + this.rect.getWidth();
        final double bottomY = topY + this.rect.getHeight();
        
        result.add(leftX);
        result.add(topY);
        result.add(rightX);
        result.add(bottomY);
        return result;
    }
    
    // ************************************************************
    // Listener Inner Class for EDITING Rectangle
    
    private class EditRectMouseListener extends MouseInputAdapter {
        
        private static final int MAX_SQUARED_DISTANCE = 
            SelectGlassPane.NODE_RADIUS * SelectGlassPane.NODE_RADIUS;
        
        public EditRectMouseListener() {
            super();
        }
        
        @Override
        public void mouseMoved(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                // pass through to other components
                dispatchEvent(aEvent);
                
                // show cursor as default pointer if not over NetLogo view
                setCursor(Cursor.getDefaultCursor());
                return;
            }
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
           
            SimulationCaller.convertPointFromScreenToView(p);
                        
            if (Util.distanceSquared(
                p.x, 
                p.y, 
                getRect().x, 
                getRect().y
            ) <= MAX_SQUARED_DISTANCE
            || Util.distanceSquared(
                p.x, 
                p.y, 
                getRect().x + getRect().width, 
                getRect().y
            ) <= MAX_SQUARED_DISTANCE
            || Util.distanceSquared(
                p.x, 
                p.y, 
                getRect().x, 
                getRect().y + getRect().height
            ) <= MAX_SQUARED_DISTANCE
            || Util.distanceSquared(
                p.x, 
                p.y, 
                getRect().x + getRect().width, 
                getRect().y + getRect().height
            ) <= MAX_SQUARED_DISTANCE) {
                // cursor is "near" a corner of the rectangle,
                // so show the hand cursor
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                );
            } else {
                // cursor is "far" from corners of the rectangle,
                // so show the default cursor
                setCursor(Cursor.getDefaultCursor());
            }
        }
       
        
        @Override
        public void mousePressed(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
                return;
            }
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
           
            SimulationCaller.convertPointFromScreenToView(p);
            
            // set the fixed point to be the opposite corner
            // of the rectangle from the clicked corner, if any
            
            if (Util.distanceSquared(
                    p.x, 
                    p.y, 
                    getRect().x, 
                    getRect().y
                ) <= MAX_SQUARED_DISTANCE) {
                // upper left clicked, so set lower right fixed
                setFixedPoint(
                    new Point(
                        getRect().x + getRect().width, 
                        getRect().y + getRect().height
                    )
                );
            } else if (Util.distanceSquared(
                    p.x, 
                    p.y, 
                    getRect().x + getRect().width, 
                    getRect().y
                ) <= MAX_SQUARED_DISTANCE) {
                // upper right clicked, so set lower left fixed
                setFixedPoint(
                    new Point(
                        getRect().x, 
                        getRect().y + getRect().height
                    )
                );
            } else if (Util.distanceSquared(
                    p.x, 
                    p.y, 
                    getRect().x, 
                    getRect().y + getRect().height
                ) <= MAX_SQUARED_DISTANCE) {
                // lower left clicked, so set upper right fixed
                setFixedPoint(
                    new Point(
                        getRect().x + getRect().width, 
                        getRect().y
                    )
                );
            } else if (Util.distanceSquared(
                    p.x, 
                    p.y, 
                    getRect().x + getRect().width, 
                    getRect().y + getRect().height
                ) <= MAX_SQUARED_DISTANCE) {
                // lower right clicked, so set upper left fixed
                setFixedPoint(
                    new Point(
                        getRect().x, 
                        getRect().y
                    )
                );
            } else {
                // if no corner was clicked, clear the fixed point
                setFixedPoint(null);
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
                return;
            }
            if (EllipseSelectGlassPane.this.getFixedPoint() == null) {
                return;
            }
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
            
            SimulationCaller.convertPointFromScreenToView(p);
            
            // leftX of a rectangle must be the further left of its sides
            final int leftX = 
                Math.min(
                    EllipseSelectGlassPane.this.getFixedPoint().x, 
                    p.x
                );
            
            // topY of a rectangle must be for the higher of its sides
            final int topY = 
                Math.min(
                    EllipseSelectGlassPane.this.getFixedPoint().y, 
                    p.y
                );
            final int width = 
                Math.abs(
                    EllipseSelectGlassPane.this.getFixedPoint().x - p.x
                );
            final int height = 
                Math.abs(
                    EllipseSelectGlassPane.this.getFixedPoint().y - p.y
                );
            EllipseSelectGlassPane.this.setRectAndEllipse(
                new Rectangle(leftX, topY, width, height)
            );
            
            repaint();
        }
        
        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
                return;
            }
        }
    }
    
    
    // ************************************************************
    // Listener Inner Class for CREATING Rectangle


    /**
     * Listens for clicks, drags, and drops in the panel.
     */
    private class CreateRectMouseListener extends MouseInputAdapter {
        public CreateRectMouseListener() {
            super();
        }

        @Override
        public void mouseMoved(final MouseEvent aEvent) {
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
            
            final Point currentPoint = aEvent.getLocationOnScreen();
            if (compContainsPoint(currentPoint)) {
                // use crosshair cursor if over the netLogo window
                EllipseSelectGlassPane.this.setCursor(
                    Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                );
            } else {
                // if not over the NetLogo window, use the default cursor
                EllipseSelectGlassPane.this.
                    setCursor(Cursor.getDefaultCursor());
            }
        }

        @Override
        public void mousePressed(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
                return;
            }
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
           
            SimulationCaller.convertPointFromScreenToView(p);
            
            // fixed point of rectangle starts wherever user
            // first clicks in the NetLogo window
            EllipseSelectGlassPane.this.setFixedPoint(p);
        }
        
        @Override
        public void mouseDragged(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
                return;
            }
            if (EllipseSelectGlassPane.this.getFixedPoint() == null) {
                return;
            }
            if (!EllipseSelectGlassPane.this.canEdit()) {
                return;
            }
            
            SimulationCaller.convertPointFromScreenToView(p);
            
            // leftX of a rectangle must be the further left of its sides
            final int leftX = 
                Math.min(
                    EllipseSelectGlassPane.this.getFixedPoint().x, 
                    p.x
                );
            
            // topY of a rectangle must be for the higher of its sides
            final int topY = 
                Math.min(
                    EllipseSelectGlassPane.this.getFixedPoint().y, 
                    p.y
                );
            final int width = 
                Math.abs(
                    EllipseSelectGlassPane.this.getFixedPoint().x - p.x
                );
            final int height = 
                Math.abs(
                    EllipseSelectGlassPane.this.getFixedPoint().y - p.y
                );
            EllipseSelectGlassPane.this.setRectAndEllipse(
                new Rectangle(leftX, topY, width, height)
            );
            
            repaint();
        }
                
        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            final Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                dispatchEvent(aEvent);
            }
            
            if (getRect() != null) {
                // the user drew an intial rectangle in the NetLogo window.
                // switch to editable look and rectangle editing listener,
                // so user can update the rectangle without redrawing it.
                setEditableLook(true);
                useEditMouseListener();
                repaint();
            }
        }
    }
}
