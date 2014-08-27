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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.simulation.SimulationCaller;

/**
 * Each subclass of SelectGlassPane allows the user to
 * draw or redraw the shape of a particular type of shaped set,
 * such as a rectangle or a polygon.
 */
public abstract class SelectGlassPane extends JPanel {

    private static final long serialVersionUID = -318661850947993514L;
    
    // current shape as shown in the view
    private final List<Double> currentShape; 
    
    // last valid shape before "Redraw Shape" was clicked
    private final List<Double> shapeBeforeRedraw; 

    // whether the NetLogo area is clickable. true in CREATE or EDIT mode.
    private boolean canEdit;

    // whether the shape looks "draggable." 
    // true only in EDIT mode (not CREATE mode)
    private boolean editableLook; 

    // radius of each circular node for dragging in an editable shape
    static final int NODE_RADIUS = 5;
    
    public static enum MODE {
        CREATE, // draw a new shape from scratch
        VIEW, // look at a non-editable shape
        EDIT // drag to edit a pre-existing shape
    }
    
    public SelectGlassPane() {
        super();
        
        this.currentShape = new ArrayList<Double>();
        this.shapeBeforeRedraw = new ArrayList<Double>();
        this.canEdit = true;
        this.editableLook = false;
    }
    
    /**
     * Setting the mode allows a glass pane to show a shape,
     * let the user draw a new shape, or let the user edit an
     * existing shape.
     * 
     * @param aMode create, view, or edit a shape
     */
    public final void setMode(final MODE aMode) {
        switch (aMode) {
        case CREATE:
            this.canEdit = true;
            this.editableLook = false; // not an error
            useCreateMouseListener();
            break;
        case VIEW: 
            this.canEdit = false;
            this.editableLook = false;
            break;
        case EDIT:
            this.canEdit = true;
            this.editableLook = true;
            useEditMouseListener();
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        repaint();
    }
    
    public final void startUp() {
        assert SwingUtilities.isEventDispatchThread();
        this.setVisible(true);
        this.setOpaque(false);
        this.currentShape.clear();
        displayCurrentShape();
        repaint();
    }
    
    /**
     * Sets the current shape and displays it.
     * 
     * @param shape A list of points, x then y: x1, y1, x2, y2, ...
     */
    public final void initializeShape(final List<Double> shape) {
        this.currentShape.clear();
        this.currentShape.addAll(shape);
        displayCurrentShape();
        repaint();
    }
    
    /**
     * Tells a subclass of SelectGlassPane to display "currentShape"
     */
    abstract void displayCurrentShape();
    
    /**
     * Gets the shape currently displayed in the glass pane,
     * which might not be the same as "currentShape"
     * 
     * @return a list of points of the shape as actually displayed
     */
    abstract List<Double> pullDisplayedShape();
    
    /**
     * Use the mouse listener for creating a new shape.
     */
    abstract void useCreateMouseListener();
    
    /**
     * Use the mouse listener for editing a preexisting shape.
     * Never used for non-editable shapes, such as paths.
     */
    abstract void useEditMouseListener();
    
    /**
     * Saves old shape in shapeBeforeRedraw, then clears currentShape
     * and redraws.
     */
    public final void saveAndClearCurrentShape() {
        this.shapeBeforeRedraw.clear();
        this.shapeBeforeRedraw.addAll(this.currentShape);
        
        this.currentShape.clear();
        displayCurrentShape();
        repaint();
    }    
    
    // returns the current shape as it appears in the view, 
    // as a list of double coordinates
    public final List<Double> getShapeFromView() {
        this.currentShape.clear();
        this.currentShape.addAll(pullDisplayedShape());
        
        List<Double> result = new ArrayList<Double>();
        result.addAll(this.currentShape);
        return result;
    }
    
    // reverts to the last shape before "Redraw Shape" was clicked
    public final void undoRedraw() {
        this.currentShape.clear();
        this.currentShape.addAll(this.shapeBeforeRedraw);
        displayCurrentShape();
        repaint();
    }
    
    /**
     * Sets whether the appearance of the view should be editable
     * or non-editbale.
     * 
     * @param isEditableLook true if the shape should appear editable.
     */
    final void setEditableLook(final boolean isEditableLook) {
        this.editableLook = isEditableLook;
    }
    
    /**
     * Whether the view should listen for edits to the shape.
     * 
     * @return if true, the view may listen for edits to the shape.
     */
    final boolean canEdit() {
        return this.canEdit;
    }
    
    final boolean isEditableLook() {
        return this.editableLook;
    }
    
    final List<Double> getCurrentShape() {
        return this.currentShape;
    }
    
    /**
     * May be called by subclasses to dispatch an event from the glass
     * pane through to underlying components, such as buttons that lie
     * outside of the NetLogo window.
     * 
     * @param aEvent the mouse event to pass through
     */
    final void dispatchEvent(final MouseEvent aEvent) {
        final Point glassPanePoint = aEvent.getPoint();
        
        final Container container = 
            GraphicalInterface.getFrame().getContentPane();
        // convert the point into the content pane's coordinates
        final Point containerPoint = SwingUtilities.convertPoint(
            SelectGlassPane.this,
            glassPanePoint, 
            container
        );

        // find the component in the content pane that would have
        // received the mouse event
        final Component component = SwingUtilities.getDeepestComponentAt(
            container, 
            containerPoint.x, 
            containerPoint.y
        );

        if (component != null) {
            // convert to the component's coordinates
            final Point componentPoint = SwingUtilities.convertPoint(
                SelectGlassPane.this, 
                glassPanePoint, 
                component
            );
            
            // dispatch a new, matching event to the component
            component.dispatchEvent(new MouseEvent(
                component, 
                aEvent.getID(), 
                aEvent.getWhen(), 
                aEvent.getModifiers(),
                componentPoint.x, 
                componentPoint.y, 
                aEvent.getClickCount(), 
                aEvent.isPopupTrigger()
            ));
        }
    }
    
    /**
     * Returns true if the NetLogo view contains the given point.
     * @param p the point to test
     * @return whether the NetLogo view contains the point
     */
    final boolean compContainsPoint(final Point p) {
        final Point compPoint = SimulationCaller.getViewLocationOnScreen();
        final Dimension compDimension = SimulationCaller.getViewDimension();
        
        return (
            p.x >= compPoint.x 
            && p.y >= compPoint.y 
            && p.x <= compPoint.x + compDimension.width 
            && p.y <= compPoint.y + compDimension.height
       );
    }
}
