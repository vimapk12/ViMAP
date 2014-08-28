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


package edu.vanderbilt.sets;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public final class SetsMenu extends JMenu {

    private static final long serialVersionUID = 4523173631006916425L;
    public static final String CREATE_SET_STRING = "Create Group...";
    public static final String DELETE_SET_STRING = "Delete Group...";
    public static final String EDIT_SET_SHAPE_STRING = "Edit Shape of Group...";
    public static final String SETS_MENU_STRING = "Groups";
    
    private JMenuItem createSetItem;
    private JMenuItem deleteSetItem;
    private JMenuItem editSetShapeItem;
    
    private final SetsMenuListener listener;
    
    private AtomicBoolean canEnable;
    
    public SetsMenu(final SetsMenuListener aSetsMenuListener) {
        super(SETS_MENU_STRING);
        assert SwingUtilities.isEventDispatchThread();
        this.canEnable = new AtomicBoolean(true);
        this.listener = aSetsMenuListener;
        setupMenuItems();
    }
    
    AtomicBoolean getCanEnable() {
        return this.canEnable;
    }
    
    public void setAllMenuItemsEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getCanEnable().set(isEnabled);
                // for each index in the menu item list, 
                // enable/disable the item stored there
                for (int i = 0; i < getItemCount(); i++) {
                    JMenuItem currentItem = getItem(i);
                    currentItem.setEnabled(isEnabled);
                }
                
                if (isEnabled) {
                    updateEnabledState();
                }
            }
        });
    }
    
    public void updateEnabledState() {
        if (!this.canEnable.get()) {
            // don't update enabled state if
            // then menu is disabled during a run
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getCreateSetItem().setEnabled(true);
                updateDeleteEnabledState();
                updateEditEnabledState();
            }
        });
    }
    
    JMenuItem getCreateSetItem() {
        return this.createSetItem;
    }
    
    void updateDeleteEnabledState() {
        this.deleteSetItem.setEnabled(this.listener.shouldEnableDelete());
    }
    
    void updateEditEnabledState() {
        this.editSetShapeItem.setEnabled(this.listener.shouldEnableEdit());
    }
    
    private void setupMenuItems() {
        this.createSetItem = new JMenuItem(CREATE_SET_STRING);
        this.createSetItem.addActionListener(this.listener);
        this.add(this.createSetItem);
        
        this.deleteSetItem = new JMenuItem(DELETE_SET_STRING);
        this.deleteSetItem.addActionListener(this.listener);
        this.add(this.deleteSetItem);
        
        this.editSetShapeItem = new JMenuItem(EDIT_SET_SHAPE_STRING);
        this.editSetShapeItem.addActionListener(this.listener);
        this.add(this.editSetShapeItem);
    }
}
