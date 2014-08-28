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


package edu.vanderbilt.userprocedures;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


public final class ProceduresMenu extends JMenu {

    private static final long serialVersionUID = 2540424964164383431L;
    
    // display in menu item for Create procedure
    public static final String CREATE_PROCEDURE_STRING = "Create Procedure..."; 
    
    // display in menu item for Delete procedure
    public static final String DELETE_PROCEDURE_STRING = "Delete Procedure..."; 
    
    // display as title of the menu
    private static final String PROCEDURES_MENU_STRING = "Procedures"; 
    
    // listens for menu events
    private final ProceduresMenuListener listener;
    
    // menu item for creating procedures
    private JMenuItem createProcedureItem; 
    
    // menu item for deleting procedures
    private JMenuItem deleteProcedureItem; 
    
    private AtomicBoolean canEnable;
    
    
    /**
     * Constructor.
     * 
     * @param proceduresMenuListener listens for menu events
     */
    public ProceduresMenu(final ProceduresMenuListener proceduresMenuListener) {
        super(PROCEDURES_MENU_STRING);
        assert SwingUtilities.isEventDispatchThread();
        this.canEnable = new AtomicBoolean(true);
        this.listener = proceduresMenuListener;
        setupMenuItems();
    }
    
    AtomicBoolean getCanEnable() {
        return this.canEnable;
    }
    
    /**
     * Enables or disables all items in this menu, except any that should not
     * be enabled due to the number of procedures in existence.
     * 
     * @param isEnabled if true, enable all; else, disable all
     */
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
    
    
    /**
     * Updates whether menu items are enabled, 
     * based on whether they can be used.
     */
    public void updateEnabledState() {
        if (!this.canEnable.get()) {
            // don't update enabled state if
            // then menu is disabled during a run
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCreateEnabledState();
                updateDeleteEnabledState();
            }
        });
    }

    
    /**
     * Disables the create procedure button if the limit has been reached,
     * otherwise enabling it.
     */
    void updateCreateEnabledState() {
        // if too many procedure already, disable
        if (this.listener.areTooManyProcedures()) {
            this.createProcedureItem.setEnabled(false);
        } else {
            // otherwise, enable
            this.createProcedureItem.setEnabled(true);
        }
    }
    
    
    /**
     * Disables the delete procedure button if no procedures exist,
     * otherwise enabling it.
     */
    void updateDeleteEnabledState() {
        // if no procedures to delete, disable
        if (this.listener.areNoProcedures()) {
            this.deleteProcedureItem.setEnabled(false);
        } else {
            // otherwise, enable
            this.deleteProcedureItem.setEnabled(true);
        }
    }
    
    
    /**
     * Load the menu with its menu items.
     */
    private void setupMenuItems() {
        this.createProcedureItem = new JMenuItem(CREATE_PROCEDURE_STRING);
        this.createProcedureItem.addActionListener(this.listener);
        this.add(this.createProcedureItem);
        
        this.deleteProcedureItem = new JMenuItem(DELETE_PROCEDURE_STRING);
        this.deleteProcedureItem.addActionListener(this.listener);
        this.add(this.deleteProcedureItem);
    }
}
