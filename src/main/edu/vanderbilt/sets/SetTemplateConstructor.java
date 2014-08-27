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

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import edu.vanderbilt.domainmodel.ArgumentType;
import edu.vanderbilt.domainmodel.ArgumentType.ArgumentValueType;
import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.Constraint;

public abstract class SetTemplateConstructor {
    
    private static SelectGlassPane pane;
    
    public static SetTemplate getEllipseSetTemplate(
        final String ellipseString
    ) {
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();

        Set<Constraint> constraints = new HashSet<Constraint>();
        
        // expected: List<Double> of length 4.
        // first 2 values are leftX, lowerY (can't be negative)
        // next 2 values are width, height (must be positive)
        Constraint doubles = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                for (Object item: list) {
                    if (!(item instanceof Double)) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(doubles);
        
        Constraint fourValues = new Constraint() {            
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                final int desiredLength = 4;
                return list.size() == desiredLength;
            }
        };
        constraints.add(fourValues);
        
        Constraint nonNegative = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                for (Object item: list) {
                    if (((Double) item) < 0) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(nonNegative);
        
        Constraint positiveSize = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                final int length = 4;
                for (int i = 2; i < length; i++) {
                    if (((Double) list.get(i)) <= 0) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(positiveSize);
        
        ArgumentType shapeArg = 
            new ArgumentType(ArgumentValueType.LIST, null, constraints);
        argTypes.put(BlockTemplate.SHAPE, shapeArg);
                        
        if (SwingUtilities.isEventDispatchThread()) {
            pane = new EllipseSelectGlassPane();
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        setPane(new EllipseSelectGlassPane());
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
            
        ShapedSet ellipseShape = new ShapedSet(
            "Draw an Ellipse",
            "Drag with the mouse to draw an ellipse around your group.",
            new EllipseShapeChecker(),
            true,
            false,
            pane
        );
        
        SetTemplate result = 
            new SetTemplate(
                ellipseString, 
                true,
                argTypes, 
                ellipseShape,
                false
         );
        
        return result;       
    }

    public static SetTemplate getRectangleSetTemplate(
        final String rectangleString
    ) {
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();

        Set<Constraint> constraints = new HashSet<Constraint>();
        
        // expected: List<Double> of length 4.
        // first 2 values are leftX, lowerY (can't be negative)
        // next 2 values are width, height (must be positive)
        Constraint doubles = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                for (Object item: list) {
                    if (!(item instanceof Double)) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(doubles);
        
        Constraint fourValues = new Constraint() {            
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                final int desiredLength = 4;
                return list.size() == desiredLength;
            }
        };
        constraints.add(fourValues);
        
        Constraint nonNegative = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                for (Object item: list) {
                    if (((Double) item) < 0) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(nonNegative);
        
        Constraint positiveSize = new Constraint() {
            @Override
            public boolean verify(final Object o) {
                @SuppressWarnings("rawtypes")
                List list = (List) o;
                final int length = 4;
                for (int i = 2; i < length; i++) {
                    if (((Double) list.get(i)) <= 0) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        constraints.add(positiveSize);
        
        ArgumentType shapeArg = 
            new ArgumentType(ArgumentValueType.LIST, null, constraints);
        argTypes.put(BlockTemplate.SHAPE, shapeArg);
                        
        if (SwingUtilities.isEventDispatchThread()) {
            pane = new RectangleSelectGlassPane();
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        setPane(new RectangleSelectGlassPane());
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
            
        ShapedSet rectangleShape = new ShapedSet(
            "Draw a Rectangle",
            "Drag with the mouse to draw a rectangle around your group.",
            new RectangleShapeChecker(),
            true,
            false,
            pane
        );
        
        SetTemplate result = 
            new SetTemplate(
                rectangleString, 
                true, 
                argTypes, 
                rectangleShape,
                false
         );
        
        return result;
    }
    
    static void setPane(final SelectGlassPane aPane) {
        pane = aPane;
    }
}
