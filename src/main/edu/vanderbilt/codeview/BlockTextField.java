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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;


public final class BlockTextField extends JTextField implements BlockComponent {

    private static final long serialVersionUID = -5703023991822539018L;

    public static enum TextFieldType {
        DOUBLE_VALUE, 
        INT_VALUE
    }
    
    private static final int LARGE_STANDARD_WIDTH = 50; 
    private static final int SMALL_STANDARD_WIDTH = 40; 
    private static final int LARGE_LAYOUT_HEIGHT = 25;
    private static final int SMALL_LAYOUT_HEIGHT = 20;
    public static final int LARGE_FONT_SIZE = 12;
    public static final int SMALL_FONT_SIZE = 9;
    
    private final UUID blockId;
    private final String name;
    
    private final TextFieldType fieldType;
    private BufferedImage image;
    private final BlockTextFieldListener listener;
    private final LayoutSize layoutSize;
    
    public BlockTextField(
        final double aInitialValue,
        final UUID aBlockId,
        final String aName,
        final TextFieldType aFieldType,
        final BlockTextFieldListener aListener,
        final LayoutSize aLayoutSize
   ) {
        super();
        assert aListener != null;
        assert SwingUtilities.isEventDispatchThread();
        this.fieldType = aFieldType;
        this.blockId = aBlockId;
        this.name = aName;
        this.layoutSize = aLayoutSize;
        
        setValue(aInitialValue);
        this.listener = aListener;
        switch (layoutSize) {
        case SMALL:
            this.setPreferredSize(
                new Dimension(Short.MAX_VALUE, SMALL_LAYOUT_HEIGHT));
            break;
        case LARGE:
            this.setPreferredSize(
                    new Dimension(Short.MAX_VALUE, SMALL_LAYOUT_HEIGHT));
            break;
            default: throw new IllegalArgumentException();
        }

        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    public BlockTextField(
        final int aInitialValue,
        final UUID aBlockId,
        final String aName,
        final TextFieldType aFieldType,
        final BlockTextFieldListener aListener,
        final LayoutSize aLayoutSize
    ) {
        super();
        assert SwingUtilities.isEventDispatchThread();
        this.fieldType = aFieldType;
        this.blockId = aBlockId;
        this.name = aName;
        this.layoutSize = aLayoutSize;
        
        setValue(aInitialValue);
        this.listener = aListener;
        switch (layoutSize) {
        case SMALL:
            this.setPreferredSize(
                new Dimension(Short.MAX_VALUE, SMALL_LAYOUT_HEIGHT));
            break;
        case LARGE:
            this.setPreferredSize(
                    new Dimension(Short.MAX_VALUE, SMALL_LAYOUT_HEIGHT));
            break;
            default: throw new IllegalArgumentException();
        }
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    @Override
    public void setMaxWidth(final Graphics g) {
        switch (this.layoutSize) {
        case SMALL:
            g.setFont(new Font(
                g.getFont().getFontName(), 
                Font.PLAIN, 
                BlockTextField.SMALL_FONT_SIZE
            ));
            FontMetrics metrics = g.getFontMetrics();
            int maxWidth = metrics.stringWidth("-9999");
            final int smallPadding = 5;
            maxWidth += smallPadding;
            setMaximumSize(
                new Dimension(maxWidth, SMALL_LAYOUT_HEIGHT));
            setMinimumSize(
                new Dimension(maxWidth, SMALL_LAYOUT_HEIGHT));
            setPreferredSize(
                new Dimension(maxWidth, SMALL_LAYOUT_HEIGHT));
            break;
        case LARGE:
            g.setFont(new Font(
                g.getFont().getFontName(), 
                Font.PLAIN, 
                BlockTextField.LARGE_FONT_SIZE
            ));
            metrics = g.getFontMetrics();
            maxWidth = metrics.stringWidth("-9999");
            final int largePadding = 5;
            maxWidth += largePadding;
            setMaximumSize(
                new Dimension(maxWidth, LARGE_LAYOUT_HEIGHT));
            setMinimumSize(
                new Dimension(maxWidth, LARGE_LAYOUT_HEIGHT));
            setPreferredSize(
                new Dimension(maxWidth, LARGE_LAYOUT_HEIGHT));
            break;
            default: throw new IllegalArgumentException();
        }
    }
    
    @Override
    public void activateListener() {
        addFocusListener(this.listener);
    }
    
    public BlockTextFieldListener getListener() {
        return this.listener;
    }
    
    @Override
    public void setImage(final BufferedImage aImage) {
        assert aImage != null;
        this.image = aImage;
    }
    
    
    @Override
    public BufferedImage getImage() {
        assert this.image != null;
        return this.image;
    }
    
    
    public void setValue(final int value) {
        assert SwingUtilities.isEventDispatchThread();
        final String newText = "" + value;
        assert !newText.equals("");
        BlockTextField.this.setText(newText);
    }
    
    
    public void setValue(final double value) {
        assert SwingUtilities.isEventDispatchThread();
        if (
            BlockTextField.this.getFieldType() == TextFieldType.INT_VALUE
        ) {
            final String newText = "" + (int) value;
            assert !(newText).equals("");
            setText(newText);
        } else if (
            BlockTextField.this.getFieldType() 
            == TextFieldType.DOUBLE_VALUE
        ) {
            final String newText = "" + value;
            assert !(newText.equals(""));
            setText(newText);
        } else {
            throw new IllegalStateException();
        }
    }  
    
    
    @Override
    public void setAvailable(final boolean isAvailable) {
        assert SwingUtilities.isEventDispatchThread();
        BlockTextField.this.setEditable(isAvailable);
        BlockTextField.this.setFocusable(isAvailable);              
    }
    
    
    @Override
    public int getStandardWidth() {
        switch (this.layoutSize) {
        case SMALL:
            return SMALL_STANDARD_WIDTH;
        case LARGE:
            return LARGE_STANDARD_WIDTH;
            default: throw new IllegalArgumentException();
        }
    }
    
    
    @Override
    public ComponentType getComponentType() {
        return ComponentType.BLOCK_TEXT_FIELD;
    }


    public TextFieldType getFieldType() {
        return this.fieldType;
    }
    
    
    @Override
    public UUID getBlockId() {
        return this.blockId;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockTextField [blockId=");
        builder.append(this.blockId);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", fieldType=");
        builder.append(this.fieldType);
        builder.append(", getText()=");
        builder.append(this.getText());
        builder.append("]");
        return builder.toString();
    }
}
