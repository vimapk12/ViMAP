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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import edu.vanderbilt.codecomponentview.CategoryViewStyle;
import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codecontroller.MyMasterCodeController;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;

public final class CategorySelectPanel extends CategorySelector 
    implements ActionListener {
    
    public static final String ALL_CATEGORY_STRING = "Everything";
    public static final String BASIC_CATEGORY_STRING = "Basic";

    private static final long serialVersionUID = 5621591698003477445L;
    private final MasterCodeController codeController;
    private final AtomicBoolean acceptInput;
    private final Set<JRadioButton> buttons;
    private Map<String, Color> categoryColors;
    private static final int COLUMNS = 3;
    
    public CategorySelectPanel() {
        super(new GridLayout(0, COLUMNS));
        assert SwingUtilities.isEventDispatchThread();
        this.codeController = DependencyManager.getDependencyManager().
            getObject(MyMasterCodeController.class, "controller");
        
        this.acceptInput = new AtomicBoolean(true);
        this.buttons = new HashSet<JRadioButton>();
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.categoryColors = new HashMap<String, Color>();
    }
    
    @Override
    public void setControlsEnabled(final boolean isEnabled) { 
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               for (JRadioButton button: getButtons()) {
                   button.setEnabled(isEnabled);
               }
           }
        });
    }
    
    private JRadioButton addButton(
        final String name,
        final Color aColor,
        final ButtonGroup aGroup
    ) {
        JRadioButton button = new JRadioButton(name);
        button.setOpaque(true);
        
        Font font = button.getFont();
        // same font but bold
        Font boldFont = 
            new Font(font.getFontName(), Font.BOLD, font.getSize());
        button.setFont(boldFont);
        button.setBackground(aColor);
        
        button.setForeground(Color.WHITE);

        this.buttons.add(button);
        aGroup.add(button);
        add(button);
        button.addActionListener(this);
        return button;
    }
    
    @Override
    public void setCategories(
        final List<String> aCategoryList, 
        final Map<String, Color> aCategoryColors
    ) {
        assert SwingUtilities.isEventDispatchThread();
        this.categoryColors = aCategoryColors;

        acceptInput(false);
        this.buttons.clear();
        CategorySelectPanel.this.removeAll();
        ButtonGroup group = new ButtonGroup();
        String firstCategory = BASIC_CATEGORY_STRING;
        JRadioButton button = addButton(
            firstCategory, 
            this.categoryColors.get(firstCategory), 
            group
        );
        button.setSelected(true);
        firstCategory = ALL_CATEGORY_STRING;
        addButton(
            firstCategory, 
            this.categoryColors.get(firstCategory), 
            group
        );
        for (String category: aCategoryList) {
            if (
                category.equals(BASIC_CATEGORY_STRING)
                || category.equals(ALL_CATEGORY_STRING)
            ) {
                continue;
            }
            addButton(
                category, 
                this.categoryColors.get(category), 
                group
            );
        }
        
        setSelectedView(BASIC_CATEGORY_STRING);
        acceptInput(true);
        revalidate();
        repaint();
        
        Dimension dimension = new Dimension(
            GraphicalInterface.LEFT_COLUMN_WIDTH, 
            this.getPreferredSize().height
        );
        this.setMaximumSize(dimension);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
        
        revalidate();
        repaint();
    }
    
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (!this.acceptInput.get()) {
            return;
        }
        
        handleCategoryEvent(event);
    }
    
    Set<JRadioButton> getButtons() {
        return this.buttons;
    }
    
    void acceptInput(final boolean accept) {
        this.acceptInput.set(accept);
    }
    
    void setSelectedView(final String toSelect) {
        for (JRadioButton button: buttons) {
            if (button.getText().equals(toSelect)) {
                button.setForeground(Color.WHITE);
                button.setBackground(this.categoryColors.get(button.getText()));
            } else {
                // button.setForeground(
                //    this.categoryColors.get(button.getText()
                // ));
                button.setForeground(Color.DARK_GRAY);
                button.setBackground(Color.WHITE);
            }
        }
    }

    private void handleCategoryEvent(final ActionEvent event) {
        final JRadioButton button = (JRadioButton) event.getSource();
        final String newCategory = button.getText();
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
        setSelectedView(newCategory);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CategorySelectPanel [categoryColors=");
        builder.append(categoryColors);
        builder.append("]");
        return builder.toString();
    }
}
