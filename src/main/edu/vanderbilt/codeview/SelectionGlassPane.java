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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.vanderbilt.simulation.SimulationCaller;

public final class SelectionGlassPane extends JPanel {

    private static final long serialVersionUID = -4134180998914092872L;
    private MyMouseListener mouseListener;
    private Point initialPoint;
    private Rectangle rect;
    private final JavaBlockStrategy strategy;
    
    
    public SelectionGlassPane(final JavaBlockStrategy aStrategy) {
        super();
        assert SwingUtilities.isEventDispatchThread();
        
        setupMouseListener();
        this.strategy = aStrategy;
        
        this.setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                SelectionGlassPane.super.setOpaque(false);
            }
        });
    }
    
    
    Point getInitialPoint() {
        return this.initialPoint;
    }
    
    
    void setInitialPoint(final Point aPoint) {
        this.initialPoint = aPoint;
    }
    
    
    Rectangle getRect() {
        return this.rect;
    }
    
    
    void setRect(final Rectangle aRect) {
        this.rect = aRect;
    }
    
    
    JavaBlockStrategy getStrategy() {
        return this.strategy;
    }
    
    
    @Override
    public void paintComponent(final Graphics aGraphics) {        
        Graphics2D g2d = (Graphics2D) aGraphics;
        super.paintComponent(aGraphics);
        
        if (this.rect != null) {
            g2d.draw(this.rect);
        }
    }    
    
    
    private void setupMouseListener() {
        this.mouseListener = new MyMouseListener();
        addMouseListener(this.mouseListener);
        addMouseMotionListener(this.mouseListener);            
    }
    
    
    boolean compContainsPoint(final Point p) {
        Point compPoint = SimulationCaller.getViewLocationOnScreen();
        Dimension compDimension = SimulationCaller.getViewDimension();
        
        return (
            p.x >= compPoint.x 
            && p.y >= compPoint.y 
            && p.x <= compPoint.x + compDimension.width 
            && p.y <= compPoint.y + compDimension.height
       );
    }
    
    
    double[] getResult() {
        assert SwingUtilities.isEventDispatchThread();
        final int rectangleSides = 4;
        double[] result = new double[rectangleSides];
        
        Point screenLocation = this.rect.getLocation();
        SwingUtilities.convertPointToScreen(screenLocation, this);
        
        final int leftOffset = 15;
        final int topOffset = 35;
        int count = 0;
        result[count] = 
            screenLocation.x 
            - SimulationCaller.getViewLocationOnScreen().x - leftOffset;
        count++;
        result[count] =  
            screenLocation.y 
            - SimulationCaller.getViewLocationOnScreen().y - topOffset;
        count++;
        result[count] = this.rect.getWidth();
        count++;
        result[count] = this.rect.getHeight();
        
        return result;
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

        
        @Override
        public void mouseMoved(final MouseEvent aEvent) {
            Point currentPoint = aEvent.getLocationOnScreen();
            if (compContainsPoint(currentPoint)) {
                SelectionGlassPane.this.setCursor(
                    Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                );
            } else {
                SelectionGlassPane.this.setCursor(Cursor.getDefaultCursor());

            }
        }
        

        @Override
        public void mousePressed(final MouseEvent aEvent) {
            Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                return;
            }
            
            SwingUtilities.convertPointFromScreen(p, SelectionGlassPane.this);
            SelectionGlassPane.this.setInitialPoint(p);
        }
        

        @Override
        public void mouseDragged(final MouseEvent aEvent) {
            Point p = aEvent.getLocationOnScreen();
            if (!compContainsPoint(p)) {
                return;
            }
            if (SelectionGlassPane.this.getInitialPoint() == null) {
                return;
            }
            
            SwingUtilities.convertPointFromScreen(p, SelectionGlassPane.this);
            
            int leftX = 
                Math.min(SelectionGlassPane.this.getInitialPoint().x, p.x);
            int topY = 
                Math.min(SelectionGlassPane.this.getInitialPoint().y, p.y);
            int width = 
                Math.abs(SelectionGlassPane.this.getInitialPoint().x - p.x);
            int height = 
                Math.abs(SelectionGlassPane.this.getInitialPoint().y - p.y);
            SelectionGlassPane.this.setRect(
                new Rectangle(leftX, topY, width, height)
            );
            
            repaint();
        }
                

        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            if (SelectionGlassPane.this.getRect() != null) {
                System.out.println("done!");
            }
            
            SelectionGlassPane.this.getStrategy().setSelected(getResult());
        }
    }
}
