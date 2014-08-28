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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.driverandlayout.DependencyManager;

public final class SwingUserCodeView extends JPanel 
    implements UserCodeView, Highlightable {

    private static final long serialVersionUID = 647845268233967055L;
    private MyMouseListener mouseListener;
    private BlockView clickedBlockView;
    private DraggingGlassPane myGlassPane;
    private boolean isDraggedFromUserCode;
    private Highlightable highlightableTarget;
    private BlockView blockTarget;
    private boolean afterBlockTarget;
    public static final String VERT_SHIM_NAME = "vertShim";
    public static final String HORIZ_SHIM_NAME = "horizShim";
    private List<BlockView> blockViews;
    private MasterCodeController codeController;
    private UUID id;
    private final AtomicBoolean isEditable;
    private BlockView highlightedBlockView;
    
    public SwingUserCodeView(
    ) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        setBackground(Color.WHITE);
        this.blockViews = new ArrayList<BlockView>();
        this.isEditable = new AtomicBoolean(false);
        setupMouseListener();
    }
    
    public void setupMouseListener() {
        this.mouseListener = new MyMouseListener();
        addMouseListener(this.mouseListener);
        addMouseMotionListener(this.mouseListener);
    }
    
    public void setMyGlassPane(final DraggingGlassPane glassPane) {
        this.myGlassPane = glassPane;
    }
    
    @Override
    public void init() {
        DependencyManager dep = DependencyManager.getDependencyManager();
        this.codeController = 
            dep.getObject(MasterCodeController.class, "controller");
    }

    @Override
    public void setBlockViews(final List<BlockView> aBlockViews) {
        this.blockViews.clear();
        removeAll();
        for (BlockView blockView: aBlockViews) {
            this.blockViews.add(blockView);
            add(blockView);
        }
        allowChangesToBlockComponents(isEditable());
        revalidate();
        repaint();
    }
    
    public void removeBlockView(
        final BlockView blockView
    ) {
        this.blockViews.remove(blockView);
        remove(blockView.getParent());
        revalidate();
        repaint();
    }
    
    boolean isEditable() {
        return this.isEditable.get();
    }

    @Override
    public void setEditable(final boolean aIsEditable) {
        this.isEditable.set(aIsEditable);
        allowChangesToBlockComponents(aIsEditable);        
    }
    
    void allowChangesToBlockComponents(final boolean shouldAllow) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               for (
                   final BlockView blockView
                   : getBlockViewsAndDescendants()
               ) {
                    blockView.setIsActive(shouldAllow);
               }
           }
        });
    }
    
    List<BlockView> getBlockViews() {
        return this.blockViews;
    }
    
    Set<BlockView> getBlockViewsAndDescendants() {
        Set<BlockView> result = new HashSet<BlockView>();
        for (BlockView view: this.blockViews) {
            result.addAll(view.getSelfAndDescendants());
        }
        return result;
    }

    @Override
    public BlockView getBlockView(final UUID aId) {
        for (BlockView blockView: blockViews) {
            for (BlockView innerBlockView: blockView.getSelfAndDescendants()) {
                if (innerBlockView.getId() == aId) {
                    return innerBlockView;
                }
            }
        }
        
        return null;
    }

    @Override
    public void setMasterCodeController(final MasterCodeController controller) {
        this.codeController = controller;
    }

    @Override
    public void setId(final UUID aId) {
        this.id = aId;
    }

    @Override
    public void enumValueChanged(
        final UUID blockId, 
        final String fieldName, 
        final String item
    ) {
        this.codeController.setEnumValue(blockId, fieldName, item);        
    }

    @Override
    public void numberValueChanged(
        final UUID blockId, 
        final String fieldName, 
        final String value
    ) {
        this.codeController.setNumberValue(blockId, fieldName, value);
    }

    @Override
    public void setExecutingBlock(
        final UUID aId,
        final boolean isExecuting
    ) {
        BlockView view = getBlockView(aId);
        assert view != null;
        this.highlightedBlockView = view;
        view.setExecuting(true);
        repaint();
    }

    @Override
    public void resetPreviousBlockColor() {
        if (this.highlightedBlockView == null) {
            return;
        }
        
        this.highlightedBlockView.setExecuting(false);
        this.highlightedBlockView = null;
        repaint();        
    }
    
    public void handleClick(final Point absoluteLocation) {        
        BlockView clicked = getDeepestBlockViewAt(absoluteLocation);
        if (clicked != null) {
            clicked.handleClick(absoluteLocation);
            setClickedBlockView(clicked);
        }
    }
    
    BlockView getDeepestBlockViewAt(final Point absoluteLocation) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this);
        
        Component result = 
            SwingUtilities.getDeepestComponentAt(
                this, 
                localLocation.x, 
                localLocation.y
            );
        if (result == null) {
            return null;
        }
        
        while ((!(result instanceof BlockView)) && result.getParent() != null) {
            result = result.getParent();
        }
        
        if (result instanceof BlockView) {
            return (BlockView) result;
        }
        
        return null;
    }
    
    public void setClickedBlockView(final BlockView aClickedBlockView) {
        this.clickedBlockView = aClickedBlockView;
    }
    
    @Override
    public int getParentDepth() {
        return 0;
    }
    
    public void handleDrag(
        final Point absoluteLocation
    ) {
        if (clickedBlockView == null) {
            return;
        }
        
        if (!myGlassPane.hasDraggedBlockView()) {
            moveToGlassPane(absoluteLocation);
            codeController.removeBlock(clickedBlockView.getId());
            
            // make sure the blocks reposition 
            // before highlighting on glass pane
            validate(); 
            handleDragFromGlassPane(
                absoluteLocation,
                this.clickedBlockView.getDepthAddedWhenDropped()
            );
        }
                
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this); 
        
        if (this.contains(localLocation)) {
            updateDropTarget(
                absoluteLocation, 
                this.clickedBlockView.getDepthAddedWhenDropped()
            );
            updateBlockTarget(
                absoluteLocation,
                this.clickedBlockView.getDepthAddedWhenDropped()
            );
        }
        
        updateHighlight(
            absoluteLocation, 
            this.clickedBlockView.getDepthAddedWhenDropped()
        );
        
        myGlassPane.handleDrag(absoluteLocation);
    }
    
    public void handleDrop() {
        if (this.clickedBlockView == null) {
            if (myGlassPane.hasDraggedBlockView()) {
                cleanUpAfterDrop();
            }
            return;
        }
        
        this.clickedBlockView.setIsTransparent(false);
        
        if (!myGlassPane.hasDraggedBlockView()) {
            cleanUpAfterDrop();
            return;
        }
                
        if (isDraggedFromUserCode) {
            if (highlightableTarget != null) { 
                // move to the target list or base view
                if (highlightableTarget != null) {
                    codeController.insertBlock(
                        clickedBlockView.getId(), 
                        clickedBlockView.getBlockTemplate(), 
                        highlightableTarget.getSequenceId(), 
                        getTargetIndex()
                    );
                } else { // must be dragged to a list or base view
                    throw new IllegalStateException();
                }
            }
        } else { // dragged from palette
            if (highlightableTarget != null) {
                codeController.insertBlock(
                    clickedBlockView.getId(), 
                    clickedBlockView.getBlockTemplate(), 
                    highlightableTarget.getSequenceId(), 
                    getTargetIndex()
                );
            } else { // must be dragged to a list or base view
                throw new IllegalStateException();
            }
        }
        
        cleanUpAfterDrop();
    }
    
    @Override
    public UUID getSequenceId() {
        return this.id;
    }
    
    private void cleanUpAfterDrop() {
        getClickedBlockView().handleDrop();
        removeBlockTarget();
        removeDropHighlight();
        setClickedBlockView(null);
        isDraggedFromUserCode = false;
        myGlassPane.clearGlassPane();
        highlightableTarget = null;
    }
    
    public BlockView getClickedBlockView() {
        return this.clickedBlockView;
    }
    
    private void updateHighlight(
        final Point absoluteLocation,
        final int depthAdded
    ) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this); 
        
        // if the mouse pointer is over the user code panel
        if (this.contains(localLocation)) {
            updateDropTarget(absoluteLocation, depthAdded);
            updateBlockTarget(absoluteLocation, depthAdded);
        } else {
            // else, the pointer is not over the user code panel            
            removeDropHighlight();
            highlightableTarget = null;
        }
    }
    
    private void removeDropHighlight() {
        if (highlightableTarget != null) {
            highlightableTarget.unhighlight();
        }
        if (blockTarget != null) {
            blockTarget.unhighlight();
        }
    }
    
    public void removeDropTargets() {
        removeBlockTarget();
        removeDropHighlight();
        highlightableTarget = null;
    }
    
    private void moveToGlassPane(final Point absoluteLocation) {
        Component parent = clickedBlockView.getParent();
        clickedBlockView.setIsTransparent(true);
        
        if (parent instanceof RowBox) {
            parent = parent.getParent();
        }
        
        if (parent instanceof BlockListView) {
            BlockListView parentList = (BlockListView) parent;
            parentList.removeBlockView(clickedBlockView);
        } else if (parent instanceof SwingUserCodeView) {
            SwingUserCodeView parentView = (SwingUserCodeView) parent;
            parentView.removeBlockView(clickedBlockView);          
        } else {
            throw new IllegalStateException(parent.toString());
        }
        
        isDraggedFromUserCode = true;
        myGlassPane.setDraggedBlockView(clickedBlockView);
        clickedBlockView.handleDrag(absoluteLocation);
        
        myGlassPane.setVisible(true);
        myGlassPane.handleDrag(absoluteLocation);
    }
    
    public void handleDragFromGlassPane(
        final Point absoluteLocation,
        final int depthAdded
    ) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this); 
        
        if (this.contains(localLocation)) {
            updateDropTarget(absoluteLocation, depthAdded);
            updateBlockTarget(absoluteLocation, depthAdded);
        }
        updateHighlight(
            absoluteLocation,
            depthAdded
        );
    }
    
    private void updateDropTarget(
        final Point absoluteLocation,
        final int depthAdded
    ) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this);
        
        // find the deepest component you're dragging over
        Component deepestComponent = 
            SwingUtilities.getDeepestComponentAt(
                this, 
                localLocation.x, 
                localLocation.y
            );
        
        // if the deepest component was not a Highlightable, 
        // find the first enclosing Highlightable
        Component highlightable = null;
        if (
            deepestComponent instanceof Highlightable
        ) {
            highlightable = deepestComponent;
        } else {            
            highlightable = 
                SwingUtilities.getAncestorOfClass(
                    BlockListView.class, 
                    deepestComponent
                );
            
            if (highlightable == null) {
                highlightable = 
                    SwingUtilities.getAncestorOfClass(
                        SwingUserCodeView.class, 
                        deepestComponent
                    );
            }
        }
        
        // if you now have a Highlightable...
        if (highlightable != null) {
            // if this is different from the previous drop target...
            if (!highlightable.equals(highlightableTarget)) {
                Highlightable asHighlightable = (Highlightable) highlightable;
                while (
                    asHighlightable.getParentDepth() 
                        + depthAdded 
                    >= BlockView.MAX_NESTING_DEPTH
                ) {
                    Component outerHighlightable = 
                        SwingUtilities.getAncestorOfClass(
                            BlockListView.class, 
                            (BlockListView) asHighlightable
                        );
                    
                    if (outerHighlightable == null) {
                        outerHighlightable = 
                            SwingUtilities.getAncestorOfClass(
                                SwingUserCodeView.class, 
                                (BlockListView) asHighlightable
                            );
                    }
                    
                    asHighlightable = (Highlightable) outerHighlightable;
                }
                
                // set the old target's color back to white if there was one
                removeDropHighlight();
                
                // update the value of the dropTarget, 
                // and highlight the new target
                this.highlightableTarget = asHighlightable;
                addDropHighlight();
            }
        } else {
            throw new IllegalStateException(deepestComponent.toString());
        }
    }
    
    private void addDropHighlight() {
        if (highlightableTarget == null) {
            return;
        }

        highlightableTarget.highlight();
        
        if (blockTarget == null) {
            highlightableTarget.highlightLast(true);
        } else {
            blockTarget.highlight(afterBlockTarget);
        }
    }
    
    private void updateBlockTarget(
        final Point absoluteLocation,
        final int depthAdded
    ) {
        Point localLocation = new Point(absoluteLocation.x, absoluteLocation.y);
        SwingUtilities.convertPointFromScreen(localLocation, this);
        
        // find the deepest component into which the 
        // dragged block could be dropped
        Component container = 
            SwingUtilities.getDeepestComponentAt(
                this, 
                localLocation.x, 
                localLocation.y
            );
        
        // if the component is a list or shim, there is no blockTarget
        if (
            container instanceof Highlightable
            || (
                container != null 
                && container.getName() != null 
                && container.getName().equals(VERT_SHIM_NAME)
            )
        ) { 
            Highlightable asHighlightable;
            if (container instanceof Highlightable) {
                asHighlightable = (Highlightable) container;
            } else {
                if (container == null) {
                    throw new IllegalStateException();
                }
                asHighlightable = (Highlightable) container.getParent();
            }
            
            if (
                asHighlightable.getParentDepth() + depthAdded 
                < BlockView.MAX_NESTING_DEPTH
            ) {
                removeBlockTarget();
                addDropHighlight();
                
                final int leftInset = 10;
                Component innerContainer = 
                    SwingUtilities.getDeepestComponentAt(
                        this, 
                        container.getX() + leftInset, 
                        localLocation.y
                    );
                
                if (innerContainer == this) {
                    if (!this.blockViews.isEmpty()) {
                        highlightLast(true);
                    }
                } else {
                    // set the old target's color back to white if there was one
                    removeDropHighlight();
                    
                    if (!(innerContainer instanceof BlockView)) {
                        innerContainer = SwingUtilities.getAncestorOfClass(
                            edu.vanderbilt.codecomponentview.BlockView.class, 
                            innerContainer
                        );
                    }
                    
                    if (
                        innerContainer 
                            == ((Component) asHighlightable).getParent()
                        || innerContainer == null
                    ) {
                        blockTarget = null;
                        addDropHighlight();
                        return;
                    }
                    
                    // update the value of the dropTarget, 
                    // and highlight the new target
                    blockTarget = (BlockView) innerContainer;
                    addDropHighlight();
                            
                    Point localLocation2 = 
                        new Point(absoluteLocation.x, absoluteLocation.y);
                    SwingUtilities.convertPointFromScreen(
                        localLocation2, 
                        blockTarget.getParent()
                    );
                    
                    boolean tempAfter = 
                        isBelowCenterOfBlockTarget(localLocation2.y);
                    if (tempAfter != afterBlockTarget) {
                        afterBlockTarget = tempAfter;
                        removeDropHighlight();
                        addDropHighlight();
                    }
                    // no inner BlockView "to the left" of pointer was found
                }
            }
                        
            return;
        }
        
        if (container instanceof RowBox) {
            container = ((RowBox) container).getComponent(0);
        }
                
        // if the deepest component was not a BlockView, 
        // find the first enclosing BlockView
        if (!(container instanceof BlockView)) {
            container = SwingUtilities.getAncestorOfClass(
                BlockView.class, 
                container
            );
        }
        
        // if you now have a BlockView...
        if (container instanceof BlockView) {
            // if this is different from the previous drop target...
            if (!container.equals(blockTarget)) {
                BlockView asBlockView = (BlockView) container;
                while (
                    asBlockView.getDepth() + depthAdded 
                    > BlockView.MAX_NESTING_DEPTH
                ) {
                    container = SwingUtilities.getAncestorOfClass(
                        BlockView.class, 
                        asBlockView
                    );
                    if (container == null) {
                        return;
                    }
                    asBlockView = (BlockView) container;
                }
                
                // set the old target's color back to white if there was one
                removeDropHighlight();
                
                // update the value of the dropTarget, 
                // and highlight the new target
                blockTarget = asBlockView;
                addDropHighlight();
            }
                        
            Point localLocation2 = 
                new Point(absoluteLocation.x, absoluteLocation.y);
            SwingUtilities.convertPointFromScreen(
                localLocation2, 
                blockTarget.getParent()
            );
            
            boolean tempAfter = isBelowCenterOfBlockTarget(localLocation2.y);
            if (tempAfter != afterBlockTarget) {
                afterBlockTarget = tempAfter;
                removeDropHighlight();
                addDropHighlight();
            }
        }       
    }
    
    private boolean isBelowCenterOfBlockTarget(final int y) {
        int minY = blockTarget.getY();
        int height = blockTarget.getHeight();
        
        return y > minY + height / 2;
    }
    
    private void removeBlockTarget() {
        if (blockTarget != null) {
            blockTarget.unhighlight();
            blockTarget = null;
        }
    }
    
    private int getTargetIndex() {
        Component[] parentComponents = 
            ((java.awt.Container) highlightableTarget).getComponents();
        for (int i = 0; i < parentComponents.length; i++) {
            if (parentComponents[i] instanceof RowBox) {
                if (
                    ((RowBox) parentComponents[i]).getComponent(0) 
                    == blockTarget
                ) {
                    if (afterBlockTarget) {
                        return i + 1;
                    }
                    
                    return i;
                }
            } else {
                if (parentComponents[i] == blockTarget) {
                    if (afterBlockTarget) {
                        return i + 1;
                    }
                    
                    return i;
                }
            }
        }
        
        if (parentComponents.length == 0) {
            return 0;
        }

        if (
            parentComponents[0] != null 
            && VERT_SHIM_NAME.equals(parentComponents[ 0 ].getName())
        ) {
            return 0;
        }
                
        return parentComponents.length;
    }
    
    @Override
    public void highlight() {
        if (this.blockViews.isEmpty()) {
            final int yellow = 0xffff66;
            this.setBackground(new Color(yellow));
        }
    }

    @Override
    public void unhighlight() {
        this.setBackground(Color.WHITE);
        highlightLast(false);
    }

    @Override
    public void highlightLast(final boolean shouldHighlight) {
        if (this.blockViews.isEmpty()) {
            return;
        }
         
        BlockView lastBlock = 
            (BlockView) this.getComponent(this.getComponentCount() - 1);
        
        if (shouldHighlight) {
            lastBlock.highlight(true);
        } else {
            lastBlock.unhighlight();
        }
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

        /*
         * When a click is made in the panel, set clickedBlock 
         * to be the innermost block under the click, if any. 
         * Set that block's state to show that it is clicked,
         * and redraw the panel after making adjustments for the click.
         */
        @Override
        public void mousePressed(final MouseEvent aEvent) {
            if (isEditable()) {
                Point pointToConvert = aEvent.getPoint();
                SwingUtilities.convertPointToScreen(
                    pointToConvert, 
                    SwingUserCodeView.this
                );
                
                handleClick(pointToConvert);
            }
        }

        /*
         * When a drag occurs, don't let anything happen 
         * unless the pointer is over the panel.
         * Otherwise, let the clicked block (if any) 
         * handle the drag event itself.
         */
        @Override
        public void mouseDragged(final MouseEvent aEvent) {
            if (isEditable()) {
                Point pointToConvert = aEvent.getPoint();
                SwingUtilities.convertPointToScreen(
                    pointToConvert, 
                    SwingUserCodeView.this
                );
                
                handleDrag(pointToConvert);
            }
        }
        
        /*
         * When a drop occurs, move the clicked block and 
         * contents back to the default pane,
         * and let the clicked block handle its own drop behavior.
         */
        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            if (isEditable()) {
                handleDrop();
            }
        }
    }
}
