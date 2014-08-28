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

public final class EllipseShapeChecker implements ShapeChecker {

    /**
     * Expects an ellipse with bounding rectangle
     *  of the form lower-left-x, lower-left-y,
     * width, height.
     * 
     * Assumes that lower-left corner of NetLogo window has
     * coordinates (0, 0).
     * 
     * Rejects if a coordinate is negative, 
     * width or height is not positive, or if there are
     * not 4 values.
     */
    @Override
    public String checkShape(final List<Double> values) {
        final int expectedNumValues = 4;
        if (values.size() != expectedNumValues) {
            return "You did not draw an ellipse.";
        }
        
        int index = 0;
        final double leftX = values.get(index++);
        final double topY = values.get(index++);
        final double rightX = values.get(index++);
        final double bottomY = values.get(index++);
        if (leftX < 0) {
            return "Left edge is out of the window.";
        }
        
        if (topY < 0) {
            return "Top edge is out of the window.";
        }
        if (rightX <= leftX) {
            return "The ellipse must have width.";
        }        
        if (bottomY <= topY) {
            return "The ellipse must have height.";
        }
        
        return ""; // valid ellipse
    }

}
