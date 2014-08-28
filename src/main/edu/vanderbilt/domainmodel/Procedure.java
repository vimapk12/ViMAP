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


package edu.vanderbilt.domainmodel;

public final class Procedure {

    private final String name;
    private final String agentTypeName;
    private final boolean isUserDefined;
    
    public Procedure(
        final String aName,
        final String aAgentTypeName,
        final boolean aIsUserDefined
    ) {
        assert aName != null && !aName.equals("");
        assert aAgentTypeName != null && !aAgentTypeName.equals("");
        
        this.name = aName;
        this.agentTypeName = aAgentTypeName;
        this.isUserDefined = aIsUserDefined;
    }

    public String getName() {
        return this.name;
    }

    public String getAgentTypeName() {
        return this.agentTypeName;
    }

    public boolean isUserDefined() {
        return this.isUserDefined;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Procedure [name=");
        builder.append(this.name);
        builder.append(", agentTypeName=");
        builder.append(this.agentTypeName);
        builder.append(", isUserDefined=");
        builder.append(this.isUserDefined);
        builder.append("]");
        return builder.toString();
    }
}
