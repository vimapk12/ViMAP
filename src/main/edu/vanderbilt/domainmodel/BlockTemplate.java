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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Template object that describes a kind of code block that 
 * can be made in this model.
 * 
 * Includes properties for the name of the kind of block and the 
 * arguments it takes, if any.
 *
 */
public final class BlockTemplate {    
    
    /**
     * Argument name for if-else block.
     */
    public static final String PREDICATES = "predicates";
    
    /**
     * Argument name for compareTwoVariables and compareInt blocks.
     */
    public static final String LEFT_VAR = "leftVariable";
    
    /**
     * Argument name for compareTwoVariables and compareInt blocks.
     */
    public static final String COMPARATOR = "comparator";
    
    /**
     * Argument name for compareTwoVariables blocks.
     */
    public static final String RIGHT_VAR = "rightVariable";
    
    /**
     * Argument name for compareInt blocks.
     */
    public static final String RIGHT_INT = "rightInteger";
    
    /**
     * Argument name for call set and update set blocks.
     */
    public static final String SETS = "sets";
    
    /**
     * Argument name for update set blocks with isShape() == true.
     */
    public static final String SHAPE = "shape";
    
    /**
     * Argument name for repeat blocks.
     */
    public static final String TIMES = "times";
    
    /**
     * Maps name of an argument to an object specifying the
     * data type, constraints etc. of the argument.
     */
    private final LinkedHashMap<String, ArgumentType> argumentTypes;
    
    private final String name;
    
    private final String displayName;
    
    private final String labelAfterFirstArg;
    
    /**
     * True if the block must be executed in NetLogo's "observer context."
     */
    private final boolean isObserver;
    
    /**
     * True is the block should be displayed in the set of "basic" blocks.
     */
    private final boolean isBasic;
    
    /**
     * True if the block should be run once if called in a set context,
     * not once by each set member.
     */
    private final boolean isSetUpdate;
    
    /**
     * Heading in view where the block should be placed.
     */
    private final String category;
    
    /**
     * How many code sequences the block contains. For example, a repeat block
     * holds 1 sequence, an if-else block 2.
     */
    private final int sequenceCount;
    
    /**
     * An enumeration that specifies the syntactic meaning of the block.
     * Can be DEFAULT for user-defined blocks.
     */
    private final PredefinedBlockType predefinedBlockType;
    
    /**
     * Null if not an update set block.
     * 
     * Predefined values are BlockTemplate.ELLIPSE and BlockTemplate.RECTANGLE.
     * The NetLogo model can add other String values, with their own methods for
     * determining set membership.
     */
    private final String updateSetType;

    public BlockTemplate(
        final String aName,
        final String aDisplayName,
        final String aLabelAfterFirstArg,
        final LinkedHashMap<String, ArgumentType> aArgumentTypes,
        final boolean aIsObserver,
        final boolean aIsBasic,
        final boolean aIsSetUpdate,
        final String aCategory,
        final int aSequenceCount,
        final PredefinedBlockType aPredefinedBlockType,
        final String aUpdateSetType
   ) {
        assert aName != null && !aName.equals("");
        assert aSequenceCount >= 0 && aSequenceCount <= 2;
        
        this.name = aName;
        if (aDisplayName == null) {
            this.displayName = aName;
        } else {
            this.displayName = aDisplayName;
        }
        this.labelAfterFirstArg = aLabelAfterFirstArg;

        this.argumentTypes = new LinkedHashMap<String, ArgumentType>();
        if (aArgumentTypes != null) {
            for (Entry<String, ArgumentType> entry: aArgumentTypes.entrySet()) {
                this.argumentTypes.put(entry.getKey(), entry.getValue());
            }
        }
        
        this.isObserver = aIsObserver;
        this.isBasic = aIsBasic;
        this.isSetUpdate = aIsSetUpdate;
        this.category = aCategory;
        this.predefinedBlockType = aPredefinedBlockType;
        this.sequenceCount = aSequenceCount;
        this.updateSetType = aUpdateSetType;
    }
    
    public PredefinedBlockType getPredefinedBlockType() {
        return this.predefinedBlockType;
    }
    
    public boolean hasSequences() {
        switch (predefinedBlockType) {
        case DEFAULT:
        case UPDATE_SET:
        case PROCEDURE_CALL:
            return false;
        case IF_ELSE:
        case REPEAT:
        case CALL_SET:
        case SET_IF_ELSE:
        case IF_ELSE_COMP_INT:
        case IF_ELSE_COMP_TWO_VARS:
            return true;
        default:
            throw new IllegalStateException();
        }
    }
    
    public String getUpdateSetType() {
        return this.updateSetType;
    }
    
    public int getSequenceCount() {
        return this.sequenceCount;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public boolean getIsSetUpdate() {
        return this.isSetUpdate;
    }
    
    public boolean getIsObserver() {
        return this.isObserver;
    }
    
    public boolean getIsBasic() {
        return this.isBasic;
    }

    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String getLabelAfterFirstArg() {
        return this.labelAfterFirstArg;
    }
    
    public List<String> getArgumentNames() {
        List<String> result = new ArrayList<String>();
        for (String currentName: this.argumentTypes.keySet()) {
            result.add(currentName);
        }
        
        return result;
    }
    
    public LinkedHashMap<String, ArgumentType> getArgumentTypes() {
        LinkedHashMap<String, ArgumentType> result = 
            new LinkedHashMap<String, ArgumentType>();
        for (Entry<String, ArgumentType> entry: this.argumentTypes.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    
    public ArgumentType getArgumentType(final String argumentName) {
        assert this.argumentTypes.containsKey(argumentName);
        return this.argumentTypes.get(argumentName);
    }

    // equals and hashCode should use only the name field,
    // so duplicate names will not be allowed.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (this.name == null) {
            return prime * result;
        }
        
        return prime * result + this.name.hashCode();
    }

    // equals and hashCode should use only the name field,
    // so duplicate names will not be allowed.
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BlockTemplate other = (BlockTemplate) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlockTemplate [argumentTypes=");
        builder.append(this.argumentTypes);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", displayName=");
        builder.append(this.displayName);
        builder.append(", isObserver=");
        builder.append(this.isObserver);
        builder.append(", isBasic=");
        builder.append(this.isBasic);
        builder.append(", isSetUpdate=");
        builder.append(this.isSetUpdate);
        builder.append(", category=");
        builder.append(this.category);
        builder.append(", sequenceCount=");
        builder.append(this.sequenceCount);
        builder.append(", predefinedBlockType=");
        builder.append(this.predefinedBlockType.toString());
        builder.append(", updateSetType=");
        builder.append(this.updateSetType);
        builder.append("]");
        return builder.toString();
    }
}
