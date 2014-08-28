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


package edu.vanderbilt.codeview;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecomponentview.CategoryViewStyle;
import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;

public final class CategorySelectView extends CategorySelector 
    implements ItemListener {

    private static final long serialVersionUID = 5621591698003477445L;
    private final MasterCodeController codeController;
    private final JComboBox<String> categoryComboBox;
    private final AtomicBoolean acceptInput;

    public static final String ALL_CATEGORY_STRING = "Everything";
    public static final String BASIC_CATEGORY_STRING = "Basic";
    
    public CategorySelectView() {
        super(new FlowLayout());
        assert SwingUtilities.isEventDispatchThread();
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
        
        this.categoryComboBox = new JComboBox<String>();
        this.categoryComboBox.addItemListener(this);
        add(this.categoryComboBox);
        
        this.acceptInput = new AtomicBoolean(true);
    }
    
    @Override
    public void setControlsEnabled(final boolean isEnabled) { 
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getCategoryComboBox().setEnabled(isEnabled);
           }
        });
    }
    
    @Override
    public void itemStateChanged(final ItemEvent event) {
        if (!this.acceptInput.get()) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        final JComboBox<String> comboBox = 
            (JComboBox<String>) event.getSource();
        if (comboBox.equals(this.categoryComboBox)) {
            handleCategoryEvent(event);
        }       
    }
    
    @Override
    public void setCategories(
        final List<String> categoryList, 
        final Map<String, Color> categoryColors
    ) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                acceptInput(false);
                getCategoryComboBox().removeAllItems();
                for (String category: categoryList) {
                    getCategoryComboBox().addItem(category);
                }
                acceptInput(true);
            }
         });       
    }
    
    JComboBox<String> getCategoryComboBox() {
        return this.categoryComboBox;
    }
    
    void acceptInput(final boolean accept) {
        this.acceptInput.set(accept);
    }

    private void handleCategoryEvent(final ItemEvent event) {
        if (event.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        @SuppressWarnings("unchecked")
        final JComboBox<String> comboBox = 
            (JComboBox<String>) event.getSource();
        final String newCategory = comboBox.getSelectedItem().toString();
        if (newCategory.equals(ALL_CATEGORY_STRING)) {
            this.codeController.setCategory(CategoryViewStyle.ALL, null);
        } else if (newCategory.equals(BASIC_CATEGORY_STRING)) {
            this.codeController.setCategory(CategoryViewStyle.BASIC, null);
        } else {
            this.codeController.setCategory(
                CategoryViewStyle.SELECTION, 
                newCategory
            );
        }
    }
    
}
