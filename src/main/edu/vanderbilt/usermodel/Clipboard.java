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


package edu.vanderbilt.usermodel;

public final class Clipboard {

    private String agentNameSource;
    
    private String procedureNameSource;
    
    private BlockSequence code;
    
    public Clipboard() {
        this.agentNameSource = null;
        this.procedureNameSource = null;
        this.code = null;
    }
    
    public void setContents(
        final String aAgentNameSource,
        final String aProcedureNameSource,
        final BlockSequence aCode
    ) {
        this.agentNameSource = aAgentNameSource;
        this.procedureNameSource = aProcedureNameSource;
        this.code = BlockSequence.copy(aCode);
    }
    
    public BlockSequence getCode() {
        return BlockSequence.copy(this.code);
    }
    
    public String getAgentNameSource() {
        return this.agentNameSource;
    }
    
    public String getProcedureNameSource() {
        return this.procedureNameSource;
    }
}
