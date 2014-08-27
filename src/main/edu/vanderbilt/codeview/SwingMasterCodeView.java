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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecomponentview.BlockView;
import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;
import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;


public final class SwingMasterCodeView 
    extends JPanel implements MasterCodeView, ComponentListener {

    private static final long serialVersionUID = 4323679796458396118L;
    private JScrollPane paletteScrollPane;
    private JScrollPane userCodeScrollPane;
    private JPanel leftPanel;    
    private final edu.vanderbilt.codecomponentview.UserCodeView userCodeView;
    private final edu.vanderbilt.codecomponentview.PaletteView paletteView;
    private AgentProcedureSelectView agentMethodSelectPanel;
    private CategorySelector categorySelector;
    private LayoutSize layoutSize;
    private MasterCodeController codeController;
    
    public SwingMasterCodeView() {
        super(new GridLayout(1, 2));
            
        assert SwingUtilities.isEventDispatchThread();
        
        this.paletteView = DependencyManager.getDependencyManager().
            getObject(
                edu.vanderbilt.codecomponentview.SwingPaletteView.class, 
                "palette"
            );
        ((edu.vanderbilt.codecomponentview.SwingPaletteView) this.paletteView).
            setContext(this);
        this.paletteScrollPane = new JScrollPane(
            (JPanel) paletteView, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.paletteScrollPane.getViewport().
            setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        this.paletteScrollPane.setPreferredSize(new Dimension(
            GraphicalInterface.LEFT_COLUMN_WIDTH, 
            GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT 
        ));
        this.paletteScrollPane.
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
          
        this.userCodeView = DependencyManager.getDependencyManager().
            getObject(
                edu.vanderbilt.codecomponentview.SwingUserCodeView.class, 
                "userCode"
            );
        this.userCodeScrollPane = new JScrollPane(
            (JPanel) userCodeView, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.userCodeScrollPane.getViewport().
            setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        this.userCodeScrollPane.setPreferredSize(new Dimension(
            GraphicalInterface.MIDDLE_COLUMN_WIDTH, 
            GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT 
        ));
        this.userCodeScrollPane.
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
        DraggingGlassPane pane = DependencyManager.getDependencyManager().
            getObject(DraggingGlassPane.class, "pane");
        // set the frame's glassPane to be the same 
        // glass pane passed in to the user code panel.
        GraphicalInterface.getFrame().setGlassPane(pane);
        pane.setOpaque(false);
            
        this.layoutSize = LayoutSize.LARGE;
        
        final int xPadding = 20;
        final int yPadding = 10;
        this.setPreferredSize(
            new Dimension(
                GraphicalInterface.LEFT_COLUMN_WIDTH 
                    + GraphicalInterface.MIDDLE_COLUMN_WIDTH + xPadding, 
                GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT + yPadding
            ) 
        );
        
        this.leftPanel = new JPanel();
        this.add(this.leftPanel);
        
        JPanel rightPanel = new JPanel();
        this.add(rightPanel);
            
        this.leftPanel.add(this.paletteScrollPane);
        rightPanel.add(this.userCodeScrollPane);
        
        this.addComponentListener(this);
    }
    
    JPanel getLeftPanel() {
        return this.leftPanel;
    }
    
    
    JScrollPane getPaletteScrollPane() {
        return this.paletteScrollPane;
    }
    
    @Override
    public void setCategories(
        final List<String> categoryNames,
        final Map<String, Color> categoryColors
    ) {
        assert this.categorySelector != null;
        this.categorySelector.setCategories(categoryNames, categoryColors);
    }


    @Override
    public void setAvailableAgentNames(final List<String> agentNames) {
        assert this.agentMethodSelectPanel != null;
        this.agentMethodSelectPanel.setAgentMenuItems(agentNames);
    }


    @Override
    public void setAvailableProcedureNames(final List<String> procedureNames) {
        assert this.agentMethodSelectPanel != null;
        this.agentMethodSelectPanel.setProcedureMenuItems(procedureNames);     
    }


    @Override
    public void setCurrentAgentName(final String agentName) {
        this.agentMethodSelectPanel.setSelectedAgent(agentName);
    }


    @Override
    public void setCurrentProcedureName(final String procedureName) {
        this.agentMethodSelectPanel.setSelectedMethod(procedureName);
        
    }


    @Override
    public void setPaletteBlockViews(
        final List<edu.vanderbilt.codecomponentview.BlockView> blockViews
    ) {
        this.paletteView.setBlockViews(blockViews);
    }

    @Override
    public void setUserCodeId(final UUID id) {
        this.userCodeView.setId(id);
    }
    

    @Override
    public void setUserBlockViews(
        final List<edu.vanderbilt.codecomponentview.BlockView> blockViews
    ) {
        if (SwingUtilities.isEventDispatchThread()) {
            getUserCodeView().setBlockViews(blockViews);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        getUserCodeView().setBlockViews(blockViews);
                    }
                 });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }           
        }
    }
    
    edu.vanderbilt.codecomponentview.UserCodeView getUserCodeView() {
        return this.userCodeView;
    }
    
    @Override
    public void setCategorySelector(
        final CategorySelector selector
    ) {
        this.categorySelector = selector;
    }


    @Override
    public void setAgentMethodSelectPanel(
        final AgentProcedureSelectView aAgentMethodSelectPanel
    ) {
        this.agentMethodSelectPanel = aAgentMethodSelectPanel;
    }
    
    @Override
    public void setEditable(final boolean isEditable) {
        this.paletteView.setEditable(isEditable);
        this.userCodeView.setEditable(isEditable);
        this.agentMethodSelectPanel.setMenusEnabled(isEditable);
    }
    
    @Override
    public void highlightBlockView(final UUID id) {
        this.userCodeView.resetPreviousBlockColor();
        scrollToShowBlockView(id);
        this.userCodeView.setExecutingBlock(id, true);
    }
    
    private void scrollToShowBlockView(final UUID id) {
        BlockView blockView = this.userCodeView.getBlockView(id);
        JPanel userCodePanel = (JPanel) this.userCodeView;
        Rectangle rect = 
            SwingUtilities.convertRectangle(
                blockView.getParent(), 
                blockView.getBounds(), 
                userCodePanel
            );
        if (!userCodePanel.getVisibleRect().contains(blockView.getBounds())) {
            final int bonusHeight = 200;
            int newHeight = rect.height + bonusHeight;
            int maxY = userCodePanel.getHeight();
            if (rect.y + newHeight > maxY) {
                newHeight = maxY - rect.y - 1;
            }
            Rectangle newRect = 
                new Rectangle(rect.x, rect.y, rect.width, newHeight);
            userCodePanel.scrollRectToVisible(newRect);
        }
    }


    @Override
    public edu.vanderbilt.codecomponentview.BlockView getBlockView(
        final UUID id
    ) {
        if (this.paletteView.getBlockView(id) != null) {
            return this.paletteView.getBlockView(id);
        }
        
        return this.userCodeView.getBlockView(id);
    }
    
    private LayoutSize deriveLayoutSize() {
        if (this.getWidth() < getPreferredSize().width) {
            return LayoutSize.SMALL;
        }
        
        return LayoutSize.LARGE;
    }
    
    @Override
    public synchronized void updateLayoutSize() {
        updateLayoutSize(deriveLayoutSize());
    }
    
    private synchronized void updateLayoutSize(final LayoutSize aLayoutSize) {
        if (this.layoutSize == aLayoutSize) {
            return;
        }
        
        this.layoutSize = aLayoutSize;
        final int yPadding = 10;
        switch (aLayoutSize) {
        case SMALL:
            final int smallXPadding = 10;
            this.paletteScrollPane.setPreferredSize(new Dimension(
                GraphicalInterface.SMALL_PANEL_WIDTH, 
                GraphicalInterface.SMALL_PANEL_HEIGHT
            ));
            this.userCodeScrollPane.setPreferredSize(new Dimension(
                GraphicalInterface.SMALL_PANEL_WIDTH, 
                GraphicalInterface.SMALL_PANEL_HEIGHT
            ));
            this.setMinimumSize(
                new Dimension(
                    GraphicalInterface.SMALL_PANEL_WIDTH * 2 + smallXPadding, 
                    GraphicalInterface.SMALL_PANEL_HEIGHT + yPadding
                ) 
            );
            break;
        case LARGE:
            this.paletteScrollPane.setPreferredSize(new Dimension(
                GraphicalInterface.LEFT_COLUMN_WIDTH, 
                GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT 
            ));
            this.userCodeScrollPane.setPreferredSize(new Dimension(
                GraphicalInterface.LEFT_COLUMN_WIDTH, 
                GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT 
            ));
            final int largeXPadding = 20;
            this.setPreferredSize(
                new Dimension(
                    GraphicalInterface.LEFT_COLUMN_WIDTH 
                    + GraphicalInterface.MIDDLE_COLUMN_WIDTH + largeXPadding, 
                    GraphicalInterface.CODE_OR_CHART_PANEL_HEIGHT + yPadding
                ) 
            );
            break;
        default:
            throw new IllegalStateException();
        }
        
        this.revalidate();
        this.repaint();
        this.paletteScrollPane.revalidate();
        this.paletteScrollPane.repaint();
        this.userCodeScrollPane.revalidate();
        this.userCodeScrollPane.repaint();
        
        if (this.codeController != null) {
            this.codeController.setLayoutSize(aLayoutSize);
        }
    }
    
    @Override
    public synchronized void setMasterCodeController(
        final MasterCodeController controller
    ) {
        this.codeController = controller;
        this.userCodeView.setMasterCodeController(controller);
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        updateLayoutSize();
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        // do nothing
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        // do nothing        
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        // do nothing        
    }
}
