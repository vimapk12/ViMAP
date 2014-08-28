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

import java.util.List;

/**
 * This class holds the definition of a shape type, including
 * a ShapeChecker for checking if a List<Double> is a valid instance
 * of the shape, a message to display to instruct a user how to draw
 * the shape, and a selectGlassPane instance to implement the
 * tools for drawing the shape.
 */
public final class ShapedSet {

    /**
     * Title to show in dialog box of instructions on drawing the shape.
     */
    private final String messageTitle;
    
    /**
     * Body text to show in dialog box of instructions on drawing the shape.
     */
    private final String messageBody;
    
    /**
     * Used to check if a List<Double> represents a valid instance of the shape.
     */
    private final ShapeChecker shapeChecker;
    
    /**
     * True if a pre-existing instance of the shape 
     * can be edited without redrawing.
     */
    private final boolean canEdit;
    
    /**
     * True if the user should be allowed to redraw from scratch 
     * an instance of the shape.
     * This should be false for shapes like the rectangle, 
     * which can be changed freely once created.
     */
    private final boolean canRedraw;
    
    private final SelectGlassPane selectGlassPane;
    
    public ShapedSet(
        final String aMessageTitle,
        final String aMessageBody,
        final ShapeChecker aShapeChecker,
        final boolean aCanEdit,
        final boolean aCanRedraw,
        final SelectGlassPane aSelectGlassPane
    ) {
        if (
            aMessageTitle == null
            || aMessageBody == null
            || aShapeChecker == null
            || aSelectGlassPane == null
        ) {
            throw new IllegalArgumentException();
        }
        
        this.messageTitle = aMessageTitle;
        this.messageBody = aMessageBody;
        this.shapeChecker = aShapeChecker;
        this.canEdit = aCanEdit;
        this.canRedraw = aCanRedraw;
        this.selectGlassPane = aSelectGlassPane;
    }

    public String getMessageTitle() {
        return this.messageTitle;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public String checkShape(final List<Double> shape) {
        return this.shapeChecker.checkShape(shape);
    }

    public boolean canEdit() {
        return this.canEdit;
    }

    public boolean canRedraw() {
        return this.canRedraw;
    }

    public SelectGlassPane getSelectGlassPane() {
        return this.selectGlassPane;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShapedSet [messageTitle=");
        builder.append(this.messageTitle);
        builder.append(", messageBody=");
        builder.append(this.messageBody);
        builder.append(", canEdit=");
        builder.append(this.canEdit);
        builder.append(", canRedraw=");
        builder.append(this.canRedraw);
        builder.append("]");
        return builder.toString();
    }
}
