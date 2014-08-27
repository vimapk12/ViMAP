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


package edu.vanderbilt.driverandlayout;

import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import edu.vanderbilt.edit.EditMenu;
import edu.vanderbilt.saving.FileMenu;
import edu.vanderbilt.sets.SetsMenu;
import edu.vanderbilt.userprocedures.ProceduresMenu;


/**
 * A menu bar containing any needed menus as members. 
 * The menu can enable or disable
 * its parts on command.
 */
public final class MyMenuBar extends JMenuBar {

    private static final long serialVersionUID = -6074514553621870632L;

    private final FileMenu fileMenu;
    
    // the menu for creating or deleting user-defined procedures
    private final ProceduresMenu proceduresMenu; 
    
    private final SetsMenu setsMenu;
    
    private final EditMenu editMenu;
    
    /**
     * Constructor.
     * 
     */
    public MyMenuBar() {
        super();
        
        assert SwingUtilities.isEventDispatchThread();
        
        DependencyManager dep = DependencyManager.getDependencyManager();
        this.fileMenu = dep.getObject(FileMenu.class, "fileMenu");
        this.add(this.fileMenu);
        this.editMenu = dep.getObject(EditMenu.class, "editMenu");
        this.add(this.editMenu);
        this.proceduresMenu = dep.getObject(
            ProceduresMenu.class, 
            "proceduresMenu"
        );
        this.add(this.proceduresMenu);
        
        this.setsMenu = dep.getObject(SetsMenu.class, "setsMenu");
    }
    
    public void addSetImageMenuItem() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MyMenuBar.this.getFileMenu().addSetImageOption();
            }
        });
    }
    
    public void addSetsMenu() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MyMenuBar.this.add(getSetsMenu());
            }
        });
    }
    
    
    /**
     * Enables or disables all items in all menus.
     * 
     * @param isEnabled if true, enables all items; 
     * otherwise, disables all items
     */
    public void setAllMenuItemsEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getFileMenu().setAllMenuItemsEnabled(isEnabled);
               getEditMenu().setAllMenuItemsEnabled(isEnabled);
               getProceduresMenu().setAllMenuItemsEnabled(isEnabled);
               getSetsMenu().setAllMenuItemsEnabled(isEnabled);
           }
        });
    }
    
    FileMenu getFileMenu() {
        return this.fileMenu;
    }
    
    EditMenu getEditMenu() {
        return this.editMenu;
    }
    
    
    public ProceduresMenu getProceduresMenu() {
        return this.proceduresMenu;
    }
    
    SetsMenu getSetsMenu() {
        return this.setsMenu;
    }
}
