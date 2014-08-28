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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public final class GlobalEnvironment {

    private Stack<LocalEnvironment> stack;
    
    private Map<Integer, String> whoNumberToAgentType;
    
    private List<Integer> whoNumbersInOrder;
    
    private int currentAgentIndex;
    
    private AtomicInteger delayInMillis;
    
    private final Set<Integer> deadAgents;
    
    private boolean shouldUnhighlightSet;
    
    public GlobalEnvironment() {
        this.stack = new Stack<LocalEnvironment>();
        this.whoNumbersInOrder = new ArrayList<Integer>();
        this.delayInMillis = new AtomicInteger();
        this.whoNumberToAgentType = new HashMap<Integer, String>();
        this.deadAgents = new HashSet<Integer>();
        this.currentAgentIndex = -1;
        this.shouldUnhighlightSet = false;
    }
    
    public List<String> getNonNullConditions() {
        List<String> result = new ArrayList<String>();
        Iterator<LocalEnvironment> iter = stack.iterator();
        while (iter.hasNext()) {
            LocalEnvironment le = iter.next();
            if (!le.getCondition().equals(LocalEnvironment.NULL_CONDITION)) {
                result.add(le.getCondition());
            }
        }
        
        return result;
    }
    
    public String getCalledSetName() {
        for (int i = this.stack.size() - 1; i >= 0; i--) {
            if (this.stack.get(i).getCalledSetName() != null) {
                return this.stack.get(i).getCalledSetName();
            }
        }
        
        return null;
    }
    
    public boolean getShouldUnhighlightSet() {
        return this.shouldUnhighlightSet;
    }
    
    public void setShouldUnhighlightSet(final boolean shouldUnhighlight) {
        this.shouldUnhighlightSet = shouldUnhighlight;
    }
    
    public void push(final LocalEnvironment aLocalEnvironment) {
        this.stack.push(aLocalEnvironment);
    }
    
    public void popAndDelete() {
        assert !isStackEmpty();
        this.stack.pop();
    }
    
    public LocalEnvironment peek() {
        assert !isStackEmpty();
        return this.stack.peek();
    }
    
    public int getDelayInMillis() {
        return this.delayInMillis.get();
    }
    
    public void setDelayInMillis(final int aDelay) {
        this.delayInMillis.set(aDelay);
    }
    
    public boolean isStackEmpty() {
        return this.stack.isEmpty();
    }
    
    public boolean isDead(final int aWho) {
        return this.deadAgents.contains(aWho);
    }
    
    public void clearStackForNextAgent() {
        this.stack.clear();
    }
    
    public void incrementAgentIndex() {
        this.currentAgentIndex++;
    }
    
    public int getCurrentWhoNumber() {
        return this.whoNumbersInOrder.get(this.currentAgentIndex);
    }
    
    public String getCurrentAgentTypeName() {
        return this.whoNumberToAgentType.get(getCurrentWhoNumber());
    }
    
    public void clear() {
        this.stack.clear();
        this.whoNumberToAgentType.clear();
        this.whoNumbersInOrder.clear();
        this.currentAgentIndex = -1;
    }
    
    public void resetAgentIndex() {
        // set to -1, not 0, because the value will be incremented
        // before use.
        this.currentAgentIndex = -1;
    }
    
    public void setAgents(final Map<Integer, String> newMap) {
        this.whoNumbersInOrder.clear();
        this.whoNumberToAgentType.clear();
        for (final Entry<Integer, String> currentEntry: newMap.entrySet()) {
            this.whoNumbersInOrder.add(currentEntry.getKey());
            this.whoNumberToAgentType.put(
                currentEntry.getKey(), currentEntry.getValue()
            );
        }
    }
    
    public boolean hasCurrentAgent() {
        return this.currentAgentIndex < this.whoNumbersInOrder.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GlobalEnvironment [stack=");
        builder.append(this.stack);
        builder.append(", whoNumberToAgentType=");
        builder.append(this.whoNumberToAgentType);
        builder.append(", whoNumbersInOrder=");
        builder.append(this.whoNumbersInOrder);
        builder.append(", currentAgentIndex=");
        builder.append(this.currentAgentIndex);
        builder.append(", delayInMillis=");
        builder.append(this.delayInMillis);
        builder.append("]");
        return builder.toString();
    }
}
