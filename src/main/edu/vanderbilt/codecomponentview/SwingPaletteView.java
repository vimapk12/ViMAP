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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.driverandlayout.DependencyManager;

public final class SwingPaletteView extends JPanel implements PaletteView {
        
    private static final long serialVersionUID = 9044619136482338239L;

    // does not include the contents of blocks on the "base list level"
    private List<BlockView> blockViews;
    
    private AtomicBoolean isEditable;

    private DraggingGlassPane glassPane;
    
    private MyMouseListener mouseListener;
    
    private BlockView clickedBlockView;
    
    private boolean isGlassPaneSet;
    
    private JPanel context;
        
    private edu.vanderbilt.codecomponentview.BlockViewFactory viewFactory;
    
    public SwingPaletteView() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        assert SwingUtilities.isEventDispatchThread();
        
        setBackground(Color.WHITE);
        
        this.blockViews = new ArrayList<BlockView>();
        this.isEditable = new AtomicBoolean(false);
        
        this.mouseListener = new MyMouseListener();
        addMouseListener(this.mouseListener);
        addMouseMotionListener(this.mouseListener);
        
        isGlassPaneSet = false;
        
        DependencyManager deps = DependencyManager.getDependencyManager();
        this.viewFactory = 
            deps.getObject(
                edu.vanderbilt.codecomponentview.BlockViewFactory.class, 
                "factory"
            );
    }
    
    @Override
    public void init() {
        this.glassPane = DependencyManager.getDependencyManager().
            getObject(DraggingGlassPane.class, "pane");
    }
    
    public JPanel getContext() {
        return this.context;
    }
    
    public void setContext(final JPanel aContext) {
        this.context = aContext;
    }
    
    @Override
    public void setEditable(final boolean aIsEditable) {
        this.isEditable.set(aIsEditable);
    }
    
    
    boolean isEditable() {
        return this.isEditable.get();
    }

    
    @Override
    public BlockView getBlockView(final UUID id) {
        assert id != null;
        
        for (BlockView blockView: this.blockViews) {
            if (blockView.getId() == id) {
                return blockView;
            }
        }
        
        return null;
    }

    @Override
    public void setBlockViews(final List<BlockView> aBlockViews) {
        removeAll();
        for (BlockView blockView: aBlockViews) {
            blockView.setIsActive(false);
            add(blockView);
        }
        revalidate();
        repaint();
    }
    
    public void handleClick(final Point absoluteLocation) {
        setupGlassPane();
        
        BlockView clicked = getDeepestBlockViewAt(absoluteLocation);
        if (clicked != null) {
            clicked.handleClick(absoluteLocation);
            setClickedBlockView(clicked);
        }
    }
    
    public void setClickedBlockView(final BlockView aClickedBlockView) {
        this.clickedBlockView = aClickedBlockView;
    }
    
    public void handleDrag(final Point absoluteLocation) { 
        if (this.clickedBlockView == null) {
            return;
        }
                
        if (!glassPane.hasDraggedBlockView()) {
            moveToGlassPane(absoluteLocation);
        }
        
        glassPane.handleDrag(absoluteLocation);
        
        repaint();  
    }
    
    private void moveToGlassPane(final Point absoluteLocation) {
        assert (clickedBlockView != null);
        
        clickedBlockView.handleDrop();
        
        BlockView oldBlockView = clickedBlockView;
        setClickedBlockView(
            getBlockViewWithFieldsSet(clickedBlockView)
        );
        clickedBlockView.setClickOffset(oldBlockView.getClickOffset());
        clickedBlockView.setBounds(oldBlockView.getBounds());
        
        glassPane.setDraggedBlockView(clickedBlockView);
        clickedBlockView.setIsTransparent(true);
        clickedBlockView.setClickedView(true);
        clickedBlockView.handleDrag(absoluteLocation);
        
        glassPane.setVisible(true);
        glassPane.handleDrag(absoluteLocation);
    }
    
    public BlockView getBlockViewWithFieldsSet(
        final edu.vanderbilt.codecomponentview.BlockView oldBlockView
    ) {
        BlockView result = 
            viewFactory.getBlockViewFromTemplate(
                oldBlockView.getBlockTemplate(),
                oldBlockView.getLayoutSize()
            );
        return result;
    }

    public void handleDrop(final Point absoluteLocation) {
        if (this.clickedBlockView == null) {
            return;
        }
                
        glassPane.handleDrop(absoluteLocation);
        
        glassPane.clearGlassPane();
        clickedBlockView.handleDrop();
        setClickedBlockView(null);
    }
    
    private void setupGlassPane() {
        if (isGlassPaneSet) {
            return;
        }
        
        JFrame frame = getFrame();
        if (frame == null) {
            throw new IllegalStateException();
        }
        
        frame.setGlassPane(this.glassPane);
        isGlassPaneSet = true;
    }
    
    public JFrame getFrame() {
        return (JFrame) SwingUtilities.getAncestorOfClass(
                JFrame.class, 
                this
            );
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
         * When a click is made in the panel, set clickedBlock to be 
         * the innermost block under the click, if any. Set that block's 
         * state to show that it is clicked,
         * and redraw the panel after making adjustments for the click.
         */
        @Override
        public void mousePressed(final MouseEvent aEvent) {
            if (!isEditable()) {
                return;
            }
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                SwingPaletteView.this
            );
            
            handleClick(pointToConvert);
        }

        
        /*
         * When a drag occurs, don't let anything happen unless the 
         * pointer is over the panel.
         * Otherwise, let the clicked block (if any) handle the 
         * drag event itself.
         */
        @Override
        public void mouseDragged(final MouseEvent aEvent) {
            if (!isEditable()) {
                return;
            }
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                SwingPaletteView.this
            );
            
            handleDrag(pointToConvert);
        }
        
        
        /*
         * When a drop occurs, move the clicked block and contents 
         * back to the default pane,
         * and let the clicked block handle its own drop behavior.
         */
        @Override
        public void mouseReleased(final MouseEvent aEvent) {
            if (!isEditable()) {
                return;
            }
            Point pointToConvert = aEvent.getPoint();
            SwingUtilities.convertPointToScreen(
                pointToConvert, 
                SwingPaletteView.this
            );
            
            handleDrop(pointToConvert);
        }
    }
}
