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


package edu.vanderbilt.codecomponentview;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;


public class RoundedBorder extends AbstractBorder {
	private static final long serialVersionUID = -6138861765665382799L;
	
	private final Color color;
	private final int thickness;
	private final int cornerRadius;
	
	/*
	 * Defaults to thickness 1, corner radius 1
	 */
	public RoundedBorder(final Color aColor) {
		this(aColor, 1);
	}
	
	public RoundedBorder(
        final Color aColor, 
        final int aThickness
    ) {
		this(aColor, aThickness, aThickness);
	}
	
	public RoundedBorder(
        final Color aColor, 
        final int aThickness, 
        final int aCornerRadius
    ) {
		this.color = aColor;
		this.thickness = aThickness;
		this.cornerRadius = aCornerRadius;
	}
	
	@Override
	public final void paintBorder(
        final Component comp, 
        final Graphics g, 
        final int x, 
        final int y, 
        final int width, 
        final int height
    ) {
	    // copy the graphics object argument,
	    // because we'll be changing its properties
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(
	        RenderingHints.KEY_ANTIALIASING, 
	        RenderingHints.VALUE_ANTIALIAS_ON
        );
		RoundRectangle2D rect = 
	        new RoundRectangle2D.Float(
                x + thickness - 1, // x
                y + thickness - 1, // y
                width - 2 * thickness + 1, // width
                height - 2 * thickness + 1, // height
                cornerRadius, // arc width
                cornerRadius // arc height
            );
		g2d.setColor(color);
		
		// use stroke of width this.thickness
		g2d.setStroke(new BasicStroke(thickness)); 
		g2d.draw(rect);
		
		// destroy the copy of the graphics object argument
		g2d.dispose();
	}
	
	/*
	 * Delegate to the overloaded method, with new input-output object
	 */
	@Override
	public final Insets getBorderInsets(final Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}
	
	@Override
	public final Insets getBorderInsets(
        final Component c, 
        final Insets insets
    ) {
	    int sum = thickness + cornerRadius;
	    insets.left = sum;
	    insets.right = sum;
	    insets.top = sum;
	    insets.bottom = sum;
		return insets;
	}
}
