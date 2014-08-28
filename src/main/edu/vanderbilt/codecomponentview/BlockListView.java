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


package edu.vanderbilt.codecomponentview;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;

public final class BlockListView extends Box implements Highlightable {

    private static final long serialVersionUID = -4772384243434543635L;
    private Component vertShim;
    public static final String VERT_SHIM_NAME = "vertShim";
    private final UUID id;
    
    private static final int LARGE_STANDARD_HEIGHT = 25;
    private static final int SMALL_STANDARD_HEIGHT = 10;
    
    public BlockListView(
        final UUID aId,
        final LayoutSize layoutSize
    ) {
        super(BoxLayout.Y_AXIS);
        
        int topInset;
        switch (layoutSize) {
        case LARGE:
            topInset = BlockView.LARGE_TOP_INSET;
            vertShim = Box.createVerticalStrut(LARGE_STANDARD_HEIGHT);
            break;
        case SMALL:
            topInset = BlockView.SMALL_TOP_INSET;
            vertShim = Box.createVerticalStrut(SMALL_STANDARD_HEIGHT);
            break;
        default:
            throw new IllegalArgumentException();
        }
        vertShim.setName(VERT_SHIM_NAME);
        add(vertShim);
        
        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(
                    0, // top
                    0, // left
                    topInset, 
                        // bottom -- make 2 to compensate for lack of
                       // bottom border on blocks
                    0 // right
                )
            )
        );
        
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        
        this.id = aId;
    }
    
    @Override
    public int getParentDepth() {
        BlockView parentView = (BlockView) getParent();
        return parentView.getDepth();
    }
    
    @Override
    public UUID getSequenceId() {
        return this.id;
    }

    @Override
    public void highlight() {     
        if (this.isEmpty()) {
            final int yellow = 0xffff66;
            this.setBackground(new Color(yellow));
        }
    }
    
    public List<BlockView> getBlockViews() {
        List<BlockView> result = new ArrayList<BlockView>();
        for (Component comp: getComponents()) {
            if (comp instanceof RowBox) {
                result.add((BlockView) ((RowBox) comp).getComponent(0));
            }
        }
        
        return result;
    }
    
    public int maxInternalDepth() {
        int result = 0;
        for (BlockView currentBlock: getBlockViews()) {
            int currentBlockDepth = currentBlock.getDepthAddedWhenDropped();
            if (currentBlockDepth > result) {
                result = currentBlockDepth;
            }
        }
        
        return result;
    }
    
    @Override
    public void unhighlight() {
        this.setBackground(Color.WHITE);
        highlightLast(false);
    }
    
    private boolean isEmpty() {
        if (this.getComponentCount() > 2) {
            return false;
        }
        
        Component[] components = this.getComponents();
        
        return components.length == 0 
            || components[ 0 ] == null 
            || components[ 0 ] == this.vertShim;
    }
    
    /*
     * Call the last contained BlockView to highlight 
     * itself, indicating that the block
     * would drop into place after the current last item.
     */
    @Override
    public void highlightLast(final boolean isHighlight) {
        if (this.isEmpty()) {
            return;
        }
        
        RowBox lastRow = 
            (RowBox) this.getComponent(this.getComponentCount() - 1);
        BlockView lastBlock = (BlockView) lastRow.getComponent(0);
        
        if (isHighlight) {
            lastBlock.highlight(true);
        } else {
            lastBlock.unhighlight();
        }
    }
    
    public void setBlockViews(final List<BlockView> blockViews) {
        if (blockViews.isEmpty()) {
            removeAll();
            add(vertShim);
        } else {
            removeAll();
            for (BlockView blockView: blockViews) {
                Box rowBox = new RowBox();
                rowBox.add(blockView);
                if (!blockView.getBlockTemplate().hasSequences()) {
                    rowBox.add(Box.createHorizontalGlue());
                }
                add(rowBox);
            }
        }
    }
    
    public void removeBlockView(final BlockView blockView) {
        remove(blockView.getParent());
        if (getComponentCount() == 0) {
            add(vertShim);
        }
        revalidate();
    }
}
