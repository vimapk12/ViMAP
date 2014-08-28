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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class BlockSequence {

    private final List<Block> blocks;
    private final UUID id;
    
    public BlockSequence() {
        this.blocks = new ArrayList<Block>();
        this.id = UUID.randomUUID();
    }
    
    public List<Block> getBlocks() {
        List<Block> result = new ArrayList<Block>();
        result.addAll(this.blocks);
        return result;
    }
    
    public static BlockSequence copy(final BlockSequence original) {
        BlockSequence result = new BlockSequence();
        
        final List<Block> originalBlocks = original.getBlocks();
        for (int i = 0; i < originalBlocks.size(); i++) {
            final Block originalBlock = originalBlocks.get(i);
            result.insertBlock(Block.copy(originalBlock), i);
        }
        
        return result;
    }
    
    public static void copyInto(
        final BlockSequence original, 
        final BlockSequence target
    ) {
        target.getBlocks().clear();
        final BlockSequence toAdd = copy(original);
        for (int i = 0; i < toAdd.getBlocks().size(); i++) {
            target.insertBlock(toAdd.getBlocks().get(i), i);
        }
    }
    
    public List<Block> getBlocksAndDescendantBlocks() {
        List<Block> result = new ArrayList<Block>();
        for (Block block: this.blocks) {
            result.add(block);
            for (
                BlockSequence blockSequence
                : block.getOwnAndDescendantBlockSequences()
            ) {
                result.addAll(blockSequence.blocks);
            }
        }
        
        return result;
    }
    
    public void insertBlock(
        final Block block, 
        final int index
    ) {
        assert index >= 0 && index <= this.blocks.size();
        this.blocks.add(index, block);
    }
    
    public Block remove(final UUID blockId) {
        Block toRemove = null;
        for (Block block: this.blocks) {
            if (block.getId().equals(blockId)) {
                toRemove = block;
            }
        }
        
        assert toRemove != null;
        this.blocks.remove(toRemove);
        return toRemove;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public void clear() {
        this.blocks.clear();
    }
    
    public void removeAll(final String blockName) {
        Set<Block> toRemove = new HashSet<Block>();
        for (Block block: this.blocks) {
            if (block.getTemplate().getName().equals(blockName)) {
                toRemove.add(block);
            }
        }
        
        this.blocks.removeAll(toRemove);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockSequence [blocks=");
        builder.append(this.blocks);
        builder.append(", id=");
        builder.append(this.id);
        builder.append("]");
        return builder.toString();
    }
}
