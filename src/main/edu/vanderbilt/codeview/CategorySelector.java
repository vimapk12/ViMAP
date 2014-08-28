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
import java.awt.LayoutManager;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public abstract class CategorySelector extends JPanel {
    
    private static final long serialVersionUID = -5416507037274786211L;

    public CategorySelector(final LayoutManager aLayout) {
        super(aLayout);
    }

    public abstract void setControlsEnabled(final boolean isEnabled);

    public abstract void setCategories(
        final List<String> categoryList, 
        final Map<String, Color> categoryColors
    );
}
