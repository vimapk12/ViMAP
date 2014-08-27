/////////////
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
/////////////  


package edu.vanderbilt.usermodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.domainmodel.PredefinedBlockType;
import edu.vanderbilt.domainmodel.Procedure;

public final class UserModel {

    private final Map<String, Map<String, BlockSequence>> 
        agentToProcedureToCode;
    private DomainModel domainModel;
    
    /**
     * Used to store a reference to a block that has been removed
     * from the user's code, but still might be added back to the code.
     */
    private Block removedBlock;
    private final Clipboard clipboard;
    
    public UserModel() {
        this.agentToProcedureToCode = 
            new LinkedHashMap<String, Map<String, BlockSequence>>();
        this.removedBlock = null;
        this.clipboard = new Clipboard();
    }
    
    public void setDomainModel(final DomainModel aDomainModel) {
        this.domainModel = aDomainModel;
    }
    
    public void init() {
        assert this.domainModel != null;
        
        for (String agentTypeName: this.domainModel.getAgentTypeNames()) {
            Map<String, BlockSequence> procedureToCode 
                = new HashMap<String, BlockSequence>();
            for (
                Procedure procedure
                : this.domainModel.getProceduresForAgent(agentTypeName)
            ) {
                procedureToCode.put(procedure.getName(), new BlockSequence());
            }
            
            this.agentToProcedureToCode.put(agentTypeName, procedureToCode);
        }
    }
    
    public void resetAll(final UserModel aUserModel) {
        resetAll(
            aUserModel.agentToProcedureToCode,
            aUserModel.removedBlock
        );
    }
    
    public BlockSequence getBlockSequenceById(final UUID id) {
        for (BlockSequence blockSequence: getAllBlockSequences()) {
            if (blockSequence.getId() != null && blockSequence.getId() == id) {
                return blockSequence;
            }
        }
        
        return null;
    }
    
    public Block getBlockById(final UUID id) {
        for (BlockSequence blocks: getAllBlockSequences()) {
            for (Block block: blocks.getBlocks()) {
                if (block.getId() == id) {
                    return block;
                }
            }
        }
        
        if (this.removedBlock != null) {
            for (Block block: this.removedBlock.getSelfAndDescendantBlocks()) {
                if (block.getId() == id) {
                    return block;
                }
            }
        }
        
        return null;
    }
    
    public void clearBlockSequences() {
        for (
            Map<String, BlockSequence> innerMap
            : this.agentToProcedureToCode.values()
        ) {
            for (Entry<String, BlockSequence> entry: innerMap.entrySet()) {
                entry.getValue().clear();
            }
        }
    }
    
    public BlockSequence getGoBlockSequence(final String agent) {
        return getBlockSequence(agent, DomainModel.GO_STRING);
    }
    
    public BlockSequence getSetupBlockSequece(final String agent) {
        return getBlockSequence(agent, DomainModel.SETUP_STRING);
    }
    
    public BlockSequence getBlockSequence(
        final String agent, 
        final String procedure
    ) {
        assert this.agentToProcedureToCode.containsKey(agent);
        Map<String, BlockSequence> map = this.agentToProcedureToCode.get(agent);
        assert map.containsKey(procedure);
        return map.get(procedure);
    }
    
    public BlockSequence getBlockSequenceByBlockId(final UUID aBlockId) {
        for (BlockSequence blockSequence: getAllBlockSequences()) {
            for (Block block: blockSequence.getBlocks()) {
                if (block.getId().equals(aBlockId)) {
                    return blockSequence;
                }
            }
        }
        
        return null;
    }
    
    public void addUserProcedure(
        final String procedureName, 
        final List<String> agentTypes
    ) {
        for (String agentType: agentTypes) {
            assert this.agentToProcedureToCode.containsKey(agentType);
            Map<String, BlockSequence> procedureToCode = 
                this.agentToProcedureToCode.get(agentType);
            assert !procedureToCode.containsKey(procedureName);
            procedureToCode.put(procedureName, new BlockSequence());
        }
    }
    
    public void setRemovedBlock(final Block block) {
        this.removedBlock = block;
    }

    
    public void removeUserProcedure(final String procedureName) {
        for (
            Map<String, BlockSequence> procedureToCode
            : this.agentToProcedureToCode.values()
        ) {
            if (procedureToCode.containsKey(procedureName)) {
                procedureToCode.remove(procedureName);
            }
        }
        
        for (BlockSequence blocks: getAllBlockSequences()) {
            blocks.removeAll(procedureName);
        }
    }
    
    public void removeAllUserProcedures() {
        for (
            Map<String, BlockSequence> procedureToCode
            : this.agentToProcedureToCode.values()
        ) {
            Set<String> procedureNamesToRemove = new HashSet<String>();
            for (String procedureName: procedureToCode.keySet()) {
                if (this.domainModel.isUserProcedure(procedureName)) {
                    procedureNamesToRemove.add(procedureName);
                    
                    for (BlockSequence blocks: getAllBlockSequences()) {
                        blocks.removeAll(procedureName);
                    }  
                }
            }
            for (String procedureNameToRemove: procedureNamesToRemove) {
                procedureToCode.remove(procedureNameToRemove);
            }
        }
    }

    public String getAgentNameByBlockId(final UUID id) {
        for (String agentName: this.agentToProcedureToCode.keySet()) {
            Map<String, BlockSequence> map = 
                this.agentToProcedureToCode.get(agentName);
            for (BlockSequence blockSequence: map.values()) {
                for (
                    Block block
                    : blockSequence.getBlocksAndDescendantBlocks()
                ) {
                    if (block.getId() == id) {
                        return agentName;
                    }
                }
            }
        }            
        
        return null;
    }

    public String getProcedureNameByBlockId(final UUID id) {
        for (String agentName: this.agentToProcedureToCode.keySet()) {
            Map<String, BlockSequence> map = 
                this.agentToProcedureToCode.get(agentName);
            for (Entry<String, BlockSequence> entry: map.entrySet()) {
                BlockSequence blockSequence = entry.getValue();
                for (
                    Block block
                    : blockSequence.getBlocksAndDescendantBlocks()
                ) {
                    if (block.getId() == id) {
                        return entry.getKey();
                    }
                }
            }
        }            
        
        return null;
    }
    
    public void removeOrphanSetBlocks(final String setName) {
        BlockSequence currentBlockSequence = 
            findBlockSequenceUsingSet(getAllBlockSequences(), setName);
        while (currentBlockSequence != null) {
            UUID toRemoveId = 
                findBlockUsingSet(currentBlockSequence, setName);
            currentBlockSequence.remove(toRemoveId);
            currentBlockSequence = 
                findBlockSequenceUsingSet(getAllBlockSequences(), setName);
        }
    }
    
    public void setClipboardContents(
        final String agentNameSource,
        final String procedureNameSource,
        final BlockSequence code
    ) {
        this.clipboard.setContents(
            agentNameSource,
            procedureNameSource,
            code
        );
    }
    
    public BlockSequence getClipboardCode() {
        return this.clipboard.getCode();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserModel [agentToProcedureToCode=");
        builder.append(this.agentToProcedureToCode);
        builder.append(", domainModel=");
        builder.append(this.domainModel);
        builder.append("]");
        return builder.toString();
    }
    
    private void resetAll(
        final Map<String, Map<String, BlockSequence>> 
            aAgentToProcedureToCode,
        final Block aRemovedBlock
    ) {
        this.agentToProcedureToCode.clear();
        for (
            Entry<String, Map<String, BlockSequence>> outerEntry
            : aAgentToProcedureToCode.entrySet()
        ) {
            Map<String, BlockSequence> procedureToCode = 
                new HashMap<String, BlockSequence>();
            for (
                Entry<String, BlockSequence> innerEntry
                : outerEntry.getValue().entrySet()
            ) {
                procedureToCode.put(
                    innerEntry.getKey(), 
                    innerEntry.getValue()
                );
            }
            this.agentToProcedureToCode.put(
                outerEntry.getKey(), 
                procedureToCode
            );
        }
        
        this.removedBlock = aRemovedBlock;
    }
    
    private UUID findBlockUsingSet(
        final BlockSequence blockSequence,
        final String setName
    ) {
        for (Block block: blockSequence.getBlocks()) {
            if (
                block.getTemplate().getPredefinedBlockType() 
                    == PredefinedBlockType.CALL_SET
                || block.getTemplate().getPredefinedBlockType() 
                    == PredefinedBlockType.UPDATE_SET
            ) {
                if (
                    ((String) block.getArgument(BlockTemplate.SETS)).
                        equals(setName)
                ) {
                    return block.getId();
                }
            }
        }
        
        return null;
    }
    
    private BlockSequence findBlockSequenceUsingSet(
        final Set<BlockSequence> blockSequences, 
        final String setTypeName
    ) {
        for (BlockSequence blockSequence: blockSequences) {
            for (Block block: blockSequence.getBlocks()) {
                if (
                    block.getTemplate().getPredefinedBlockType() 
                        == PredefinedBlockType.CALL_SET
                    || block.getTemplate().getPredefinedBlockType() 
                        == PredefinedBlockType.UPDATE_SET
                ) {
                    if (
                        ((String) block.getArgument(BlockTemplate.SETS)).
                            equals(setTypeName)
                    ) {
                        return blockSequence;
                    }
                }
            }
        }
        
        return null;
    }
    
    private Set<BlockSequence> getAllBlockSequences() {
        Set<BlockSequence> result = new HashSet<BlockSequence>();
        for (
            Map<String, BlockSequence> map
            : this.agentToProcedureToCode.values()
        ) {
            
            for (BlockSequence blockSequence: map.values()) {
                result.add(blockSequence);
                for (Block block: blockSequence.getBlocks()) {
                    result.addAll(
                        block.getOwnAndDescendantBlockSequences()
                    );
                }
            }
        }
        
        return result;
    }
}
