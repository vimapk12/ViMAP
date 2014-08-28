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


package edu.vanderbilt.measure;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.nlogo.lite.InterfaceComponent;

public final class OneWorldContainer extends JFrame {

    private static final long serialVersionUID = 1609477573291213497L;
    private MeasureWorld myWorld;
    
    public OneWorldContainer(
        final String modFile, 
        final String[] names, 
        final int index,
        final List<MeasureOption> measureOptions,
        final InterfaceComponent enactmentWorld
    ) {
        super("Measure World #" + index);
        
        assert SwingUtilities.isEventDispatchThread();
        this.myWorld = new MeasureWorld(
            this,  
            modFile,  
            names, 
            index,
            measureOptions,
            false,
            enactmentWorld
        );
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(this.myWorld);
        
        pack();
        setResizable(false);
        setVisible(true);
    }
    
    public MeasureWorld getMeasureWorld() { 
        return this.myWorld; 
    }
}
