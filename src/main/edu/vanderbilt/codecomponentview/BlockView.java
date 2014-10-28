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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codeview.BlockComponent;
import edu.vanderbilt.codeview.BlockTextField;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.PredefinedBlockType;
import edu.vanderbilt.util.Util;

public final class BlockView extends Box {
    private static final long serialVersionUID = -7949572349113356480L;
    
    private final List<BlockListView> blockLists;
    
    private final List<BlockComponent> blockComponents;
    
    private final BlockTemplate template;
    
    private final UUID id;
    
    private Point clickOffsetPoint;
    
    private final SwingUserCodeView myUserCodeView;
    private final SwingPaletteView myPaletteView;
    
    private static final int BORDER_THICKNESS = 1;
    private static final int SMALL_CONTAINER_CORNER_RADIUS = 2;
    private static final int SMALL_SIMPLE_CORNER_RADIUS = 1;
    private static final int LARGE_CONTAINER_CORNER_RADIUS = 3;
    private static final int LARGE_SIMPLE_CORNER_RADIUS = 2;    
    
    public static final int SMALL_TOP_INSET = 2;
    private static final int SMALL_HORIZ_INSET = 2;
    public static final int LARGE_TOP_INSET = 4;
    private static final int LARGE_HORIZ_INSET = 4;  
    
    private static final float TRANSPARENCY = 0.4f;
    public static final int MAX_NESTING_DEPTH = 4;
    private boolean isTransparent;
    private final Set<JLabel> labels;
    
    private static final int SMALL_FONT_SIZE = 10;
    private static final int LARGE_FONT_SIZE = 12;
    
    private static final int SMALL_SPACER_HEIGHT = 3;
    private static final int LARGE_SPACER_HEIGHT = 10;
    
    private final Color defaultColor;
    
    private final DraggingGlassPane myGlassPane;
    
    private final LayoutSize layoutSize;
            
    public enum LayoutSize {
        LARGE, SMALL
    }
        
    public BlockView(
        final BlockTemplate aTemplate,
        final BlockLayout aBlockLayout,
        final UUID aId,
        final List<UUID> sequenceIds,
        final Color aDefaultColor,
        final SwingUserCodeView aMyUserCodeView,
        final SwingPaletteView aMyPaletteView,
        final DraggingGlassPane aGlassPane,
        final List<BlockComponent> aBlockComponents,
        final LayoutSize aLayoutSize
    ) {
        super(BoxLayout.Y_AXIS);
        
        assert aMyPaletteView != null;
        assert aMyUserCodeView != null;
        
        this.layoutSize = aLayoutSize;
        this.id = aId;
        this.blockLists = new ArrayList<BlockListView>();
        this.blockComponents = aBlockComponents;
        
        this.template = aTemplate;
        this.defaultColor = aDefaultColor;
        this.myUserCodeView = aMyUserCodeView;
        this.myPaletteView = aMyPaletteView;
        this.myGlassPane = aGlassPane;
        
        Box rowBox = Box.createHorizontalBox();
        rowBox.setAlignmentX(LEFT_ALIGNMENT);
        this.labels = new HashSet<JLabel>();
        
        JLabel nameLabel = new JLabel(aTemplate.getDisplayName());
        String a_name = nameLabel.getText();
        nameLabel.setText(prep_block_display_name(a_name));
        
       // old code
       // if ( s.contains("step size") )
       // {
       //   s = s.replace("step size", "step-size");
       //   nameLabel.setText(s);
       // }
        	
        // this.nameLabel = new JLabel(aTemplate.getDisplayName() + " ");
        Font font = nameLabel.getFont();
        // same font but bold
        Font boldFont = null;
        
        switch (layoutSize) {
        case LARGE:
            boldFont = new Font(font.getFontName(), Font.BOLD, LARGE_FONT_SIZE);
            break;
        case SMALL:
            boldFont = new Font(font.getFontName(), Font.BOLD, SMALL_FONT_SIZE);
            break;
        default:
            throw new IllegalStateException();
        }
        
        nameLabel.setFont(boldFont);
        nameLabel.setForeground(Color.WHITE);

        this.labels.add(nameLabel);
        rowBox.add(nameLabel);
        for (int i = 0; i < aBlockComponents.size() && i < 2; i++) {
            JComponent newComponent = (JComponent) aBlockComponents.get(i);
            font = newComponent.getFont();

            switch (layoutSize) {
            case LARGE:
                newComponent.setFont(
                    new Font(
                        font.getFontName(), 
                        Font.PLAIN, BlockTextField.LARGE_FONT_SIZE
                    ));
                break;
            case SMALL:
                newComponent.setFont(
                    new Font(
                        font.getFontName(), Font.PLAIN, 
                        BlockTextField.SMALL_FONT_SIZE
                    ));
                break;
            default:
                throw new IllegalStateException();
            }

            rowBox.add(newComponent);

            if (i == 0 && aTemplate.getLabelAfterFirstArg() != null) {
                JLabel extraLabel = 
                    new JLabel(aTemplate.getLabelAfterFirstArg());
                extraLabel.setFont(boldFont);
                extraLabel.setForeground(Color.WHITE);
                this.labels.add(extraLabel);
                rowBox.add(extraLabel);
            }            
        }
        
        // add "then" label after condition, for "If-else" block
      //  if (aTemplate.getPredefinedBlockType() == PredefinedBlockType.IF_ELSE) {
      //      JLabel extraLabel = new JLabel(" then ");
      //      extraLabel.setFont(boldFont);
      //      extraLabel.setForeground(Color.WHITE);
      //      this.labels.add(extraLabel);
      //      rowBox.add(extraLabel);
      //  }
        add(rowBox);
        
        if (aBlockComponents.size() > 2) {
            rowBox = Box.createHorizontalBox();
            rowBox.setAlignmentX(LEFT_ALIGNMENT);
            for (int i = 2; i < aBlockComponents.size(); i++) {
                JComponent newComponent = (JComponent) aBlockComponents.get(i);
                font = newComponent.getFont();

                switch (layoutSize) {
                case LARGE:
                    newComponent.setFont(
                        new Font(
                            font.getFontName(), 
                            Font.PLAIN, BlockTextField.LARGE_FONT_SIZE
                        ));
                    break;
                case SMALL:
                    newComponent.setFont(
                        new Font(
                            font.getFontName(), Font.PLAIN, 
                            BlockTextField.SMALL_FONT_SIZE
                        ));
                    break;
                default:
                    throw new IllegalStateException();
                }
                rowBox.add(newComponent);
            }
            add(rowBox);
        }
        
        int myCornerRadius;
        switch (aBlockLayout) {
        case SIMPLE:
            switch (aLayoutSize) {
            case SMALL:
                myCornerRadius = SMALL_SIMPLE_CORNER_RADIUS;
                break;
            case LARGE:
                myCornerRadius = LARGE_SIMPLE_CORNER_RADIUS;
                break;
            default:
                throw new IllegalArgumentException();
            }
            
            break;
        case ONE_SEQUENCE:
            switch (aLayoutSize) {
            case SMALL:
                myCornerRadius = SMALL_CONTAINER_CORNER_RADIUS;
                break;
            case LARGE:
                myCornerRadius = LARGE_CONTAINER_CORNER_RADIUS;
                break;
            default:
                throw new IllegalArgumentException();
            }
            BlockListView view = 
                new BlockListView(sequenceIds.get(0), layoutSize);
            view.setAlignmentX(LEFT_ALIGNMENT);
            this.blockLists.add(view);
            add(view);
            break;
        case TWO_SEQUENCE:
            switch (aLayoutSize) {
            case SMALL:
                myCornerRadius = SMALL_CONTAINER_CORNER_RADIUS;
                break;
            case LARGE:
                myCornerRadius = LARGE_CONTAINER_CORNER_RADIUS;
                break;
            default:
                throw new IllegalArgumentException();
            }
            BlockListView view1 = 
                new BlockListView(sequenceIds.get(0), layoutSize);
            view1.setAlignmentX(LEFT_ALIGNMENT);
            this.blockLists.add(view1);
            add(view1);
            
            Box spacer = Box.createHorizontalBox();
             
            switch (layoutSize) {
            case SMALL:
                spacer.add(Box.createRigidArea(
                    new Dimension(0, SMALL_SPACER_HEIGHT)
                ));
                break;
            case LARGE:
                spacer.add(Box.createRigidArea(
                    new Dimension(0, LARGE_SPACER_HEIGHT)
                ));
                break;
            default:
                throw new IllegalArgumentException();
            }

            spacer.setAlignmentX(LEFT_ALIGNMENT);
            add(spacer);
            
            // add "otherwise" label between block fields, for "If-else" block
            if (
                aTemplate.getPredefinedBlockType() 
                == PredefinedBlockType.IF_ELSE
                || aTemplate.getPredefinedBlockType()
                == PredefinedBlockType.IF_ELSE_COMP_INT
                || aTemplate.getPredefinedBlockType()
                == PredefinedBlockType.IF_ELSE_COMP_TWO_VARS
            ) {
                Box extraRowBox = Box.createHorizontalBox();
                extraRowBox.setAlignmentX(LEFT_ALIGNMENT);
                JLabel extraLabel = new JLabel("otherwise");
                extraLabel.setFont(boldFont);
                extraLabel.setForeground(Color.WHITE);
                extraRowBox.add(extraLabel);
                this.labels.add(extraLabel);
                add(extraRowBox);
            }
            
            BlockListView view2 = 
                new BlockListView(sequenceIds.get(1), layoutSize);
            this.blockLists.add(view2);
            view2.setAlignmentX(LEFT_ALIGNMENT);
            add(view2);
            break;
        default:
            throw new IllegalStateException();
        }
        
        setOpaque(false);
        setBackground(defaultColor);
        final int darkGray = 0x555555;
        
        switch (layoutSize) {
        case SMALL:
            setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(
                        SMALL_TOP_INSET, // top
                        SMALL_HORIZ_INSET, // left
                        0, // bottom -- make 0 to prevent doubling with next top
                        SMALL_HORIZ_INSET // right
                    ),
                    new RoundedBorder(
                        new Color(darkGray), 
                        BORDER_THICKNESS, 
                        myCornerRadius
                    )
                )
            );
            break;
        case LARGE:
            setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(
                        LARGE_TOP_INSET, // top
                        LARGE_HORIZ_INSET, // left
                        0, // bottom -- make 0 to prevent doubling with next top
                        LARGE_HORIZ_INSET // right
                    ),
                    new RoundedBorder(
                        new Color(darkGray), 
                        BORDER_THICKNESS, 
                        myCornerRadius
                    )
                )
            );
            break;
        default: 
            throw new IllegalArgumentException();
        }

        setAlignmentX(LEFT_ALIGNMENT);
        
        this.clickOffsetPoint = new Point();
        this.isTransparent = false;
    }
    
    public LayoutSize getLayoutSize() {
        return this.layoutSize;
    }
    
    /**
     * 
     * @return 1 if this block is directly in the dragging panel, 2 if it is
     * nested in a container that is directly in the panel, etc. 
     * Used to determine whether a block should be allowed to go inside 
     * another, or if the nesting would be too deep.
     */
    public int getDepth() {
        int result = 1;
        Component currentContainer = 
            SwingUtilities.getAncestorOfClass(BlockListView.class, this);
        while (currentContainer != null) {
            result++;
            currentContainer = 
                SwingUtilities.getAncestorOfClass(
                    BlockListView.class, 
                    currentContainer
                );
        }
        
        return result;
    }
    
    
    /**
     * This method is overridden by the types of block.
     * 
     * @return how much potential depth is added to a nest of 
     * blocks if this block is dropped inside. 0 for simple blocks 
     * that don't have contents, 1 for a container block with no 
     * containers inside (adds 1 to overall depth because 
     * more blocks are meant to go within), 2 for a container 
     * with a container in it, etc.
     */
    public int getDepthAddedWhenDropped() {
        if (this.blockLists.isEmpty()) {
            return 0;
        }
        
        int result = 1;
        int maxInternalDepth = 0;
        for (BlockListView blockListView: this.blockLists) {
            int tempMax = blockListView.maxInternalDepth();
            if (tempMax > maxInternalDepth) {
                maxInternalDepth = tempMax;
            }
        }
        
        return result + maxInternalDepth;
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); 
        
        
        if (isTransparent) {
            g2.setComposite(
                AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 
                    TRANSPARENCY
                )
            ); 
        }
        
        super.paintComponent(g2);
        g2.setColor(getBackground());
        switch (layoutSize) {
        case SMALL:
            g2.fillRect(
                SMALL_HORIZ_INSET, 
                SMALL_TOP_INSET, 
                getWidth() - SMALL_HORIZ_INSET * 2, 
                getHeight() - SMALL_TOP_INSET
            );
            break;
        case LARGE:
            g2.fillRect(
                LARGE_HORIZ_INSET, 
                LARGE_TOP_INSET, 
                getWidth() - LARGE_HORIZ_INSET * 2, 
                getHeight() - LARGE_TOP_INSET
            );
            break;
        default:
            throw new IllegalStateException();    
        }
        g2.dispose();
    }
    
    public void setExecuting(final boolean isExecuting) {
        if (isExecuting) {
            final int redColor = 0xff4444;
            setBackground(new Color(redColor));
        } else {
            setBackground(defaultColor);
        }
    }
    
    public Set<BlockView> getSelfAndDescendants() {
        Set<BlockView> result = new HashSet<BlockView>();
        result.add(this);
        for (BlockListView blockList: this.blockLists) {
            for (BlockView blockView: blockList.getBlockViews()) {
                result.addAll(blockView.getSelfAndDescendants());
            }
        }
        return result;
    }
    
    public void setIsTransparent(final boolean aIsTransparent) {
        this.isTransparent = aIsTransparent;
        if (aIsTransparent) {
            for (JLabel label: this.labels) {
                label.setForeground(Color.BLACK);
            }
        } else {
            for (JLabel label: this.labels) {
                label.setForeground(Color.WHITE);
            }
        }
    }
    
    public void setContents(
        final List<BlockView> blockViews, 
        final int listIndex
    ) {
        assert listIndex < this.blockLists.size();
        
        this.blockLists.get(listIndex).setBlockViews(blockViews);
    }
    
    public List<BlockComponent> getBlockComponents() {
        return Util.copyList(this.blockComponents);
    } 
    
    public void setIsActive(final boolean isActive) {
        for (BlockComponent blockComponent: blockComponents) {
            blockComponent.setAvailable(isActive);
        }
    }
    
    public BlockTemplate getBlockTemplate() {
        return this.template;
    }
    
    public void handleClick(final Point absoluteLocation) {
        setClickedView(true);
        setClickOffset(absoluteLocation);
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public void setClickedView(final boolean isClicked) {
        if (isClicked) {
            setBackground(Color.GREEN);
        } else {
            setBackground(defaultColor);
        }
    }
    
    public void setClickOffset(final Point absoluteLocation) {
        Point myOffset = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(myOffset, this);
    
        this.clickOffsetPoint.setLocation(myOffset.x, myOffset.y);
    }
    
    public void unhighlight() {
        myGlassPane.highlight(this, false, true);
    }
    
    public void highlight(final boolean isBelow) {
        myGlassPane.highlight(this, true, isBelow);
    }
    
    public void handleDrop() {
        setClickedView(false);
    }
    
    public void handleDrag(final Point localLocation) {
        final double leftX = localLocation.x - this.clickOffsetPoint.x;
        final double topY = localLocation.y - this.clickOffsetPoint.y;
        Point2D.Double tempUpperLeftPoint = new Point2D.Double(leftX, topY);
        
        tempUpperLeftPoint = adjustForBlockOutOfBounds(tempUpperLeftPoint);
        
        setBounds((int) tempUpperLeftPoint.x, (int) tempUpperLeftPoint.y, 
            this.getWidth(), this.getHeight());  
    }

    private Point2D.Double adjustForBlockOutOfBounds(
        final Point2D.Double aUpperLeftPoint
    ) {
        Point offset = getUpperLeftOffset();
        
        final double padding = 5.0;
        final int minXEdit = -4;
        final double minX = minXEdit + offset.x + padding;
        final int largeMinYEdit = -4;
        int minYEdit = largeMinYEdit;
        if (layoutSize == LayoutSize.SMALL) {
            final int smallMinYEdit = -2;
            minYEdit = smallMinYEdit;
        }
        final double minY = minYEdit + offset.y 
            + DraggingGlassPane.getMenuHeight() + padding;
        
        double rightSpaceX = 
            myUserCodeView.getLocationOnScreen().x + myUserCodeView.getWidth();
        double leftSpaceX = myPaletteView.getLocationOnScreen().x;
        double spaceWidth = rightSpaceX - leftSpaceX;
        
        final int largeMaxXEdit = 6;
        int maxXEdit = largeMaxXEdit;
        if (layoutSize == LayoutSize.SMALL) {
            maxXEdit = 2;
        }
        final double maxX = 
            maxXEdit + offset.x + spaceWidth - getWidth() + padding;
        final int maxYEdit = 12;
        final double maxY = 
            maxYEdit + offset.y + myUserCodeView.getHeight() 
                - getHeight() + DraggingGlassPane.getMenuHeight() - padding;
        
        // check if out of bounds at top or left
        if (aUpperLeftPoint.x < minX) {
            aUpperLeftPoint.x = minX;
        }
        if (aUpperLeftPoint.y < minY) {
            aUpperLeftPoint.y = minY;
        }
        
        // check if out of bounds at bottom or right
        if (aUpperLeftPoint.x > maxX) {
            aUpperLeftPoint.x = maxX;
        }
        if (aUpperLeftPoint.y > maxY) {
            aUpperLeftPoint.y = maxY;
        }
        
        return aUpperLeftPoint;
    }
    
    public BlockTemplate getTemplate() {
        return this.template;
    }
    
    private Point getUpperLeftOffset() {
        Container contentPane = myPaletteView.getFrame().getContentPane();
        Container codeContext = myPaletteView.getContext();
        int x = 
            codeContext.getLocationOnScreen().x 
            - contentPane.getLocationOnScreen().x;
        int y = 
            codeContext.getLocationOnScreen().y 
            - contentPane.getLocationOnScreen().y;
        
        return new Point(x, y);
    }
    
    public Point getClickOffset() {
        return this.clickOffsetPoint;
    }
    
    // This is a utility method that displays the proper name for each command block.
    // This class is called BlockView, so I want to keep the "view" logic in this class.
    // Each commmand block must have a unique string name, but the display names can be 
    // the same.  
    private static String prep_block_display_name(String s)
    {
      switch(s) {
      case "set-textbox":
      case "set-op-textbox":
    	  s = "set";
    	  break;
      }
      s = s.replace("-", " ") + " ";
      if ( s.contains("step size") ) {
        return s.replace("step size", "step-size");
      }
      return s;
    }
}
