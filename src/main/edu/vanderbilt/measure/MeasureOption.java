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

import java.util.Arrays;

public final class MeasureOption {

    private final String[] buttonTextLines;
    private final String command;
    
    public MeasureOption(
        final String[] aButtonTextLines, 
        final String aCommand
    ) {
        this.buttonTextLines = 
            Arrays.copyOf(aButtonTextLines, aButtonTextLines.length);
        this.command = aCommand;
    }
    
    public String[] getButtonTextLines() {
        return Arrays.copyOf(this.buttonTextLines, this.buttonTextLines.length);
    }
    
    public String getCommand() {
        return this.command;
    }
}
