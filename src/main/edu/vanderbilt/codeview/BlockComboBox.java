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
import java.util.Arrays;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;


public final class BlockComboBox extends JComboBox<String> 
    implements BlockComponent {

    private static final long serialVersionUID = -7600962090289881180L;
    private final UUID blockID;
    private final String name;
    private BufferedImage image;
    private final BlockComboBoxListener listener;
    private final LayoutSize layoutSize;

    
    public BlockComboBox(
        final String[] aItems, 
        final UUID aBlockId,
        final String aName,
        final BlockComboBoxListener aListener,
        final LayoutSize aLayoutSize
   ) {
        super(aItems);
        
        assert aListener != null;
        assert SwingUtilities.isEventDispatchThread();
        this.name = aName;
        this.blockID = aBlockId;
        this.listener = aListener;
        this.layoutSize = aLayoutSize;
        this.setMaximumSize(
            new Dimension(Short.MAX_VALUE, this.getMinimumSize().height));
        ((JLabel) this.getRenderer()).
            setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    @Override
    public void setMaxWidth(final Graphics g) {
        final int padding = 33;
        switch (this.layoutSize) {
        case SMALL:
            g.setFont(new Font(
                g.getFont().getFontName(), 
                Font.PLAIN, 
                BlockTextField.SMALL_FONT_SIZE
            ));
            FontMetrics metrics = g.getFontMetrics();
            int maxWidth = 0;
            for (String item: getItems()) {
                int width = metrics.stringWidth(item);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            
            maxWidth += padding;
            setMaximumSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            setMinimumSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            setPreferredSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            break;
        case LARGE:
            g.setFont(new Font(
                g.getFont().getFontName(), 
                Font.PLAIN, 
                BlockTextField.LARGE_FONT_SIZE
            ));
            metrics = g.getFontMetrics();
            maxWidth = 0;
            for (String item: getItems()) {
                int width = metrics.stringWidth(item);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            
            maxWidth += padding;
            setMaximumSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            setMinimumSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            setPreferredSize(
                new Dimension(maxWidth, this.getMinimumSize().height));
            break;
            default: throw new IllegalArgumentException();
        }
    }
    
    public BlockComboBoxListener getListener() {
        return this.listener;
    }
    
    public String[] getItems() {
        String[] result = new String[this.getItemCount()];
        for (int i = 0; i < this.getItemCount(); i++) {
            result[i] = this.getItemAt(i).toString();
        }
        
        return result;
    }
    
    @Override
    public void activateListener() {
        addActionListener(this.listener); 
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
    
    
    @Override
    public void setAvailable(final boolean isAvailable) {
        assert SwingUtilities.isEventDispatchThread();
        BlockComboBox.this.setEnabled(isAvailable);
    }
    

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BLOCK_COMBO_BOX;
    }
    
    
    @Override
    public int getStandardWidth() {
        final int maxWidth = 120;
        return Math.min(getPreferredSize().width, maxWidth);
    }
    
    
    @Override
    public UUID getBlockId() {
        return this.blockID;
    }
    
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockComboBox [blockID=");
        builder.append(this.blockID);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", getItems()=");
        builder.append(Arrays.toString(getItems()));
        builder.append(", getSelectedItem()=");
        builder.append(getSelectedItem());
        builder.append("]");
        return builder.toString();
    }
}
