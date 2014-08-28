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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import edu.vanderbilt.domainmodel.ArgumentType;
import edu.vanderbilt.domainmodel.BlockTemplate;

public final class Block {

    private final BlockTemplate blockTemplate;
    private final Map<String, Object> arguments;
    private final UUID id;
    private final List<BlockSequence> blockSequences;
    
    public static enum ReturnType {
        INT_RETURN, REAL_RETURN, BOOLEAN_RETURN, NONE_RETURN
    }
    
    public Block(final BlockTemplate ablockTemplate) {
        this.blockTemplate = ablockTemplate;
        this.arguments = new HashMap<String, Object>();
        this.id = UUID.randomUUID();
        this.blockSequences = new ArrayList<BlockSequence>();
        for (int i = 0; i < ablockTemplate.getSequenceCount(); i++) {
            this.blockSequences.add(new BlockSequence());
        }
        initializeArguments();
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public static Block copy(final Block original) {
        Block result = new Block(original.getTemplate());
        
        for (String argumentKey: original.getTemplate().getArgumentNames()) {
            result.setArgument(argumentKey, original.getArgument(argumentKey));
        }
        
        for (int i = 0; i < result.getBlockSequences().size(); i++) {
            BlockSequence.copyInto(
                original.getBlockSequences().get(i), 
                result.getBlockSequences().get(i)
            );
        }
        return result;
    }
    
    public Object getArgument(final String key) {
        return this.arguments.get(key);
    }
    
    public BlockTemplate getTemplate() {
        return this.blockTemplate;
    }
    
    public boolean setArgument(final String key, final Object value) {
        Map<String, ArgumentType> argumentTypes = 
            this.blockTemplate.getArgumentTypes();
        
        if (!argumentTypes.containsKey(key)) {
            return false;
        }
        ArgumentType type = argumentTypes.get(key);
        if (!type.verify(value)) {
            return false;
        }
        this.arguments.put(key, value);
        return true;
    }
    
    public List<BlockSequence> getBlockSequences() {
        return this.blockSequences;
    }
    
    public Set<Block> getSelfAndDescendantBlocks() {
        Set<Block> result = new HashSet<Block>();
        result.add(this);
        
        for (
            BlockSequence blockSequence
            : getOwnAndDescendantBlockSequences()
        ) {
            result.addAll(blockSequence.getBlocks());
        }
        
        return result;
    }
    
    public Set<BlockSequence> getOwnAndDescendantBlockSequences() {
        Set<BlockSequence> result = new HashSet<BlockSequence>();
        for (BlockSequence blockSequence: this.blockSequences) {
            result.add(blockSequence);
            for (Block block: blockSequence.getBlocks()) {
                result.addAll(block.getOwnAndDescendantBlockSequences());
            }
        }
        
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Block [blockTemplate=");
        builder.append(this.blockTemplate);
        builder.append(", arguments=");
        builder.append(this.arguments);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", blockSequences=");
        builder.append(this.blockSequences);
        builder.append("]");
        return builder.toString();
    }
    
    private void initializeArguments() {
        for (
            Entry<String, ArgumentType> entry
            : this.blockTemplate.getArgumentTypes().entrySet()
        ) {
            final String argumentName = entry.getKey();
            final ArgumentType argumentType = entry.getValue();
            this.arguments.put(argumentName, argumentType.getDefaultValue());
        }
    }
}
