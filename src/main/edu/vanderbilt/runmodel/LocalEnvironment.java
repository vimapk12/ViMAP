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


package edu.vanderbilt.runmodel;

import java.util.ArrayList;
import java.util.List;

import edu.vanderbilt.usermodel.Block;

public final class LocalEnvironment {

    private List<Block> blocks;
    
    private int programCounter;
    
    private int repeatCounter;
    
    private int whoNumber;
    
    private final String calledSetName;
    
    private boolean hasRunSetIf;
    
    private final String condition;
    
    public static final String NULL_CONDITION = "NULL";
    
    public LocalEnvironment(
        final List<Block> expressionList,
        final int aRepeatCounter,
        final int aWhoNumber,
        final String aCalledSetName,
        final String aCondition
    ) {
        this.blocks = new ArrayList<Block>();
        this.blocks.addAll(expressionList);
        this.programCounter = 0;
        this.repeatCounter = aRepeatCounter;
        this.whoNumber = aWhoNumber;
        this.calledSetName = aCalledSetName;
        this.hasRunSetIf = false;
        this.condition = aCondition;
    }
    
    public LocalEnvironment(
        final List<Block> expressionList,
        final int aRepeatCounter,
        final int aWhoNumber,
        final String aCondition
    ) {
        this(expressionList, aRepeatCounter, aWhoNumber, null, aCondition);
    }
    
    public String getCondition() {
        return this.condition;
    }
    
    public boolean getHasRunSetIf() {
        return this.hasRunSetIf;
    }
    
    public void setHasRunSetIf(final boolean hasRun) {
        this.hasRunSetIf = hasRun;
    }
    
    public String getCalledSetName() {
        return this.calledSetName;
    }
    
    public boolean isRepeatCounterNegative() {
        return this.repeatCounter < 0;
    }
    
    public boolean isProgramCounterTooHigh() {
        if (this.programCounter > this.blocks.size()) {
            throw new IllegalStateException();
        }
        
        return this.programCounter >= this.blocks.size();
    }
    
    public void zeroProgramCounter() {
        this.programCounter = 0;
    }
    
    public void incrementProgramCounter() {
        this.programCounter++;
    }
    
    public void decrementRepeatCounter() {
        this.repeatCounter--;
    }
    
    public Block getCurrentBlock() {
        if (isProgramCounterTooHigh()) {
            throw new IllegalStateException();
        }
        
        return this.blocks.get(this.programCounter);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LocalEnvironment [blocks=");
        builder.append(this.blocks);
        builder.append(", programCounter=");
        builder.append(this.programCounter);
        builder.append(", repeatCounter=");
        builder.append(this.repeatCounter);
        builder.append(", whoNumber=");
        builder.append(this.whoNumber);
        builder.append("]");
        return builder.toString();
    }
}
