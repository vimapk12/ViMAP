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


package edu.vanderbilt.saving;

import java.awt.Desktop;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.vanderbilt.driverandlayout.DependencyManager;


/**
 * The file menu for a menu bar.
 *
 */
public final class FileMenu extends JMenu {
    
    private static final long serialVersionUID = 6902425695011915373L;

    // display in menu item for New
    public static final String NEW_STRING = "New"; 
    
    // display in menu item for Open
    public static final String OPEN_STRING = "Open File..."; 
    
    // display in menu item for Save
    public static final String SAVE_STRING = "Save"; 
    
    // display in menu item for Save As
    public static final String SAVE_AS_STRING = "Save As..."; 
    
    // display in menu item for Quit
    public static final String QUIT_STRING = "Quit"; 
    
    // display as title of the menu
    private static final String FILE_MENU_STRING = "File"; 
    
    private static final int SET_IMAGE_INDEX = 5;
    
    public static final String SET_IMAGE_STRING = "Set Image...";
    
    public static final String MANUAL_STRING = "View Manual";
    
    public static final String MANUAL_BROWSER_STRING = "Open Manual in Browser";
    
    // listens for menu events
    private final FileMenuAndWindowListener menuBarListener; 
    
    // holds indexes of menu items that aren't separators
    private List<Integer> nonSeparatorIndexes; 
    
    
    /**
     * Constructor.
     * 
     */
    public FileMenu() {
        super(FILE_MENU_STRING);
        assert SwingUtilities.isEventDispatchThread();
        
        DependencyManager dep = DependencyManager.getDependencyManager();
        this.menuBarListener = dep.getObject(
            FileMenuAndWindowListener.class, 
            "menuListener"
        );
        setupMenuItems();
    }
    
    
    public void addSetImageOption() {
        assert SwingUtilities.isEventDispatchThread();
                
        JMenuItem newItem = new JMenuItem(SET_IMAGE_STRING);
        newItem.addActionListener(this.menuBarListener);
        this.add(newItem, SET_IMAGE_INDEX);
        this.insertSeparator(SET_IMAGE_INDEX);
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
                // for each index in the menu item list that does 
                // not point to a separator, 
                // enable/disable the item stored there
                for (Integer currentIndex: getNonSeparatorIndexes()) {
                    JMenuItem currentItem = FileMenu.this.getItem(currentIndex);
                    currentItem.setEnabled(isEnabled);
                } 
            }
        });
    }
    
    
    List<Integer> getNonSeparatorIndexes() {
        return this.nonSeparatorIndexes;
    }
    
    
    private static boolean canLoadManualInBrowser() {
        return Desktop.isDesktopSupported();
    }
    
    
    /**
     * Should be called only from constructor, for thread safety.
     * 
     * Load the menu with it menu items.
     */
    private void setupMenuItems() {
        addMenuItem(NEW_STRING);
        addMenuItem(OPEN_STRING);
        this.addSeparator();
        
        addMenuItem(SAVE_STRING);
        addMenuItem(SAVE_AS_STRING);
        this.addSeparator();
        
        addMenuItem(MANUAL_STRING);
        if (canLoadManualInBrowser()) {
            addMenuItem(MANUAL_BROWSER_STRING);
        }
        this.addSeparator();
        
        addMenuItem(QUIT_STRING); 
        
        int counter = 0;
        // store the indexes of all menu items that are not separators.
        this.nonSeparatorIndexes = new ArrayList<Integer>();
        this.nonSeparatorIndexes.add(counter); // New
        counter++;
        this.nonSeparatorIndexes.add(counter); // Open
        counter += 2; // (SEPARATOR at 2)
        this.nonSeparatorIndexes.add(counter); // Save
        counter++;
        this.nonSeparatorIndexes.add(counter); // Save As...
        counter += 2; // (SEPARATOR at 5)
        this.nonSeparatorIndexes.add(counter); // Quit
    }
    
    
    /**
     * Should be called only from setupMenuItems, for thread safety.
     * 
     * Makes a new menu item with the designated name, adds its action
     * listener, and adds it to this menu.
     * 
     * @param itemName the name to display for the menu item
     */
    private void addMenuItem(final String itemName) {
        JMenuItem newItem = new JMenuItem(itemName);
        newItem.addActionListener(this.menuBarListener);
        this.add(newItem);
    }
}
