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

import java.util.ArrayList;
import java.util.List;

public final class GraphSettings {

    private boolean usingColor;
    private boolean usingAllTimeAverage;
    private final List<String> agentNames;
    private String agent;
    private String windowSize;
    private boolean showAllMeasurePoints;
    
    public GraphSettings(final String[] names) {
        if (names.length == 0) {
            throw new IllegalArgumentException();
        }
        this.agentNames = new ArrayList<String>();
        for (String name: names) {
            this.agentNames.add(name);
        }
        this.agent = names[0];
        this.usingColor = false;
        this.usingAllTimeAverage = false;
        this.windowSize = "3";
        this.showAllMeasurePoints = false;
    }
    
    public boolean isShowAllMeasurePoints() {
        return this.showAllMeasurePoints;
    }
    
    public void setShowAllMeasurePoints(final boolean showAll) {
        this.showAllMeasurePoints = showAll;
    }
    
    public boolean isAllTimeAverage() {
        return this.usingAllTimeAverage;
    }
    
    public void setUsingAllTimeAverage(final boolean useAvg) {
        this.usingAllTimeAverage = useAvg;
    }
    
    public boolean isUsingColor() {
        return this.usingColor;
    }
    
    public void setUsingColor(final boolean useCol) {
        this.usingColor = useCol;
    }
    
    public String[] getAgentNames() {
        return this.agentNames.toArray(new String[this.agentNames.size()]);
    }
    
    public void addAgentName(final String name) {
        this.agentNames.add(name);
    }
    
    /*
     * returns true if removed current agent name, else false.
     */
    public boolean removeAgentName(final String name) {
        this.agentNames.remove(name);
        if (this.agent.equals(name)) {
            this.agent = this.agentNames.get(0);
            return true;
        }
        
        return false;
    }
    
    public String getWindowSize() { 
        return this.windowSize; 
    }
    
    public void setWindowSize(final String aWindowSize) { 
        this.windowSize = aWindowSize; 
    }
    
    public String getAgent() { 
        return this.agent; 
    }
    
    public void setAgent(final String aAgent) { 
        this.agent = aAgent; 
    }
}
