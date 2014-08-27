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


package edu.vanderbilt.edit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public final class EditMenu extends JMenu {

    private static final long serialVersionUID = 5054120969391693065L;
    
    // display as title of the menu
    private static final String EDIT_MENU_STRING = "Edit"; 
    
    // display in menu item for Create procedure
    public static final String CUT_ALL_STRING = "Cut All"; 
    
    // display in menu item for Delete procedure
    public static final String COPY_ALL_STRING = "Copy All"; 
    
    // display in menu item for Delete procedure
    public static final String PASTE_TO_END_STRING = "Paste to End"; 
    
    // menu item for cutting all code
    private JMenuItem cutAllItem; 
    
    // menu item for copying all code
    private JMenuItem copyAllItem; 
    
    // menu item for pasting code to end of current procedure
    private JMenuItem pasteToEndItem; 

    // listens for menu events
    private final EditMenuListener listener;
    
 
    /**
     * Constructor.
     * 
     * @param editMenuListener listens for menu events
     */
    public EditMenu(final EditMenuListener editMenuListener) {
        super(EDIT_MENU_STRING);
        assert SwingUtilities.isEventDispatchThread();
        this.listener = editMenuListener;
        setupMenuItems();
    }
    
    public void init() {
        this.listener.init();
    }
    
    /**
     * Enables or disables all items in this menu.
     * 
     * @param isEnabled if true, enable all; else, disable all
     */
    public void setAllMenuItemsEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // for each index in the menu item list, 
                // enable/disable the item stored there
                for (int i = 0; i < getItemCount(); i++) {
                    JMenuItem currentItem = getItem(i);
                    currentItem.setEnabled(isEnabled);
                }
            }
        });
    }
    
    /**
     * Load the menu with its menu items.
     */
    private void setupMenuItems() {
        this.cutAllItem = new JMenuItem(CUT_ALL_STRING);
        this.cutAllItem.addActionListener(this.listener);
        this.add(this.cutAllItem);
        
        this.copyAllItem = new JMenuItem(COPY_ALL_STRING);
        this.copyAllItem.addActionListener(this.listener);
        this.add(this.copyAllItem);
        
        this.pasteToEndItem = new JMenuItem(PASTE_TO_END_STRING);
        this.pasteToEndItem.addActionListener(this.listener);
        this.add(this.pasteToEndItem);
    }
}
