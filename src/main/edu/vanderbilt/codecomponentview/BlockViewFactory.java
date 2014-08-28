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


package edu.vanderbilt.codecomponentview;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;
import edu.vanderbilt.codeview.BlockComboBox;
import edu.vanderbilt.codeview.BlockComboBoxListener;
import edu.vanderbilt.codeview.BlockComponent;
import edu.vanderbilt.codeview.BlockTextField;
import edu.vanderbilt.codeview.BlockTextField.TextFieldType;
import edu.vanderbilt.codeview.BlockTextFieldListener;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.domainmodel.ArgumentType;
import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.domainmodel.PredefinedBlockType;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.usermodel.Block;
import edu.vanderbilt.usermodel.BlockSequence;
import edu.vanderbilt.util.Util;

public final class BlockViewFactory {

    private DomainModel domainModel;
    private SwingUserCodeView userCodeView;
    private SwingPaletteView paletteView;
    private DraggingGlassPane glassPane;
    private BlockComboBoxListener comboBoxListener;
    private BlockTextFieldListener textFieldListener;
    
    public BlockViewFactory(
        final String aDomainModel, 
        final String aUserCodeView
    ) {
        this.domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, aDomainModel);
        this.userCodeView = DependencyManager.getDependencyManager().
            getObject(SwingUserCodeView.class, aUserCodeView);
        
        if (this.userCodeView == null) {
            throw new IllegalStateException();
        }
        
        this.comboBoxListener = new BlockComboBoxListener(this.userCodeView);
        this.textFieldListener = new BlockTextFieldListener(this.userCodeView);
    }
    
    public void init() {
        this.glassPane = DependencyManager.getDependencyManager().
            getObject(DraggingGlassPane.class, "pane");
        this.paletteView = DependencyManager.getDependencyManager().
            getObject(SwingPaletteView.class, "palette");
    }
    
    // pass in an enum
    // returns an enum 
    public static BlockLayout getBlockLayout(
        final PredefinedBlockType blockType
    ) {
        switch (blockType) {
        case CALL_SET:
        case REPEAT:
            return BlockLayout.ONE_SEQUENCE;
        case UPDATE_SET:
        case DEFAULT:
        case PROCEDURE_CALL:
            return BlockLayout.SIMPLE;
        case IF_ELSE:
        case SET_IF_ELSE:
        case IF_ELSE_COMP_INT:
        case IF_ELSE_COMP_TWO_VARS:
            return BlockLayout.TWO_SEQUENCE;
        default:
            throw new IllegalArgumentException();
        }
    }
 
    public BlockView getBlockViewFromTemplate(
        final BlockTemplate template,
        final LayoutSize layoutSize
    ) {         
        UUID blockId = UUID.randomUUID();
        List<UUID> ids = new ArrayList<UUID>();
        ids.add(UUID.randomUUID());
        ids.add(UUID.randomUUID());
        BlockView result = new BlockView(
            template, 
            getBlockLayout(template.getPredefinedBlockType()), 
            blockId,
            ids,
            domainModel.getBlockColor(template.getCategory()),
            userCodeView,
            paletteView,
            glassPane,
            getBlockComponents(template, blockId, layoutSize),
            layoutSize
        );
        initializeFieldsAsDefaults(template, result);
        return result;
    }
    
    void initializeContainedSequences(
        final Block block, 
        final BlockView view
    ) {
        if (block.getTemplate().getSequenceCount() == 1) {
            // the block has one block sequence
            BlockSequence contents = block.getBlockSequences().get(0);
            List<BlockView> blockViews = new ArrayList<BlockView>();
            for (Block currentBlock: contents.getBlocks()) {
                blockViews.add(
                    getBlockViewFromBlock(currentBlock, view.getLayoutSize())
                );
            }
            view.setContents(blockViews, 0);
        } else if (block.getTemplate().getSequenceCount() == 2) {
            // the block has two block sequences
            BlockSequence firstSequence = block.getBlockSequences().get(0);
            List<BlockView> firstBlockViews = new ArrayList<BlockView>();
            for (Block currentBlock: firstSequence.getBlocks()) {
                firstBlockViews.add(
                    getBlockViewFromBlock(currentBlock, view.getLayoutSize())
                );
            }
           view.setContents(firstBlockViews, 0);
            
            BlockSequence secondSequence = block.getBlockSequences().get(1);
            List<BlockView> secondBlockViews = new ArrayList<BlockView>();
            for (Block currentBlock: secondSequence.getBlocks()) {
                secondBlockViews.add(
                    getBlockViewFromBlock(currentBlock, view.getLayoutSize())
                );
            }
            view.setContents(secondBlockViews, 1);
        }   
    }
    
    void initializeFieldsAsDefaults(
        final BlockTemplate template, 
        final BlockView view
    ) {
        int counter = 0;
        for (
            Entry<String, ArgumentType> entry
            : template.getArgumentTypes().entrySet()
        ) {
            BlockComponent component = 
                view.getBlockComponents().get(counter);            
            final String argumentName = entry.getKey();
            final ArgumentType argumentType = entry.getValue();
            component.setMaxWidth(paletteView.getGraphics());
            counter++;
            if (argumentType.isEnum()) {
                // combo box
                String argumentValue = (String) 
                    template.getArgumentType(argumentName).getDefaultValue();
                ((BlockComboBox) component).setSelectedItem(argumentValue);
                continue;
            }
            
            switch (argumentType.getArgumentValueType()) {
            case BOOLEAN_BLOCK:
                break;
            case INT:
                int intArgumentValue = 
                (Integer) template.getArgumentType(argumentName).
                    getDefaultValue();
                ((BlockTextField) component).setValue(intArgumentValue);
                break;
            case INT_BLOCK:
                break;
            case LIST:
                break;
            case NUMBER_BLOCK:
                break;
            case REAL:
                double argumentValue = 
                (Double) template.getArgumentType(argumentName).
                    getDefaultValue();
                ((BlockTextField) component).setValue(argumentValue);
                break;
            case REAL_BLOCK:
                break;
            case STRING:
                break;
            default:
                throw new IllegalStateException();      
            }
        }       
    }
    
    void initializeFields(final Block block, final BlockView view) {
        int counter = 0;
        for (
            Entry<String, ArgumentType> entry
            : block.getTemplate().getArgumentTypes().entrySet()
        ) {
            BlockComponent component = 
                view.getBlockComponents().get(counter);         
            final String argumentName = entry.getKey();
            final ArgumentType argumentType = entry.getValue();
            component.setMaxWidth(paletteView.getGraphics());
            counter++;
            component.activateListener();
            if (argumentType.isEnum()) {
                // combo box
                String argumentValue = (String) block.getArgument(argumentName);
                ((BlockComboBox) component).setSelectedItem(argumentValue);
                continue;
            }
            
            switch (argumentType.getArgumentValueType()) {
            case BOOLEAN_BLOCK:
                break;
            case INT:
                int intArgumentValue = 
                    (Integer) block.getArgument(argumentName);
                ((BlockTextField) component).setValue(intArgumentValue);
                break;
            case INT_BLOCK:
                break;
            case LIST:
                break;
            case NUMBER_BLOCK:
                break;
            case REAL:
                double argumentValue = (Double) block.getArgument(argumentName);
                ((BlockTextField) component).setValue(argumentValue);
                break;
            case REAL_BLOCK:
                break;
            case STRING:
                break;
            default:
                throw new IllegalStateException();      
            }
        }
    }

    
    public BlockView getBlockViewFromBlock(
        final Block block,
        final LayoutSize layoutSize
    ) {
        List<UUID> ids = new ArrayList<UUID>();
        for (BlockSequence blockSequence: block.getBlockSequences()) {
            ids.add(blockSequence.getId());
        }
        
        BlockTemplate template = block.getTemplate();
        BlockLayout layout = 
            getBlockLayout(block.getTemplate().getPredefinedBlockType());
        UUID id = block.getId();
        Color defaultColor = 
            domainModel.getBlockColor(block.getTemplate().getCategory());
        List<BlockComponent> components =  
            getBlockComponents(block.getTemplate(), block.getId(), layoutSize);
        assert template != null;
        assert layout != null;
        assert id != null;
        assert defaultColor != null;
        assert components != null;

        BlockView result = new BlockView(
            template, 
            layout, 
            id,
            ids,
            defaultColor,
            userCodeView,
            paletteView,
            glassPane,
            components,
            layoutSize
        );
        initializeContainedSequences(block, result);
        initializeFields(block, result);
        
        return result;
    }
    
    private List<BlockComponent> getBlockComponents(
        final BlockTemplate template, 
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        switch (template.getPredefinedBlockType()) {
        case CALL_SET:
            return getCallSetComponents(blockId, layoutSize);
        case DEFAULT:
        case UPDATE_SET:
            return getModelDefinedBlockComponents(
                template, 
                blockId, 
                layoutSize
            );
        case PROCEDURE_CALL:
            return new ArrayList<BlockComponent>();
        case IF_ELSE:
        case SET_IF_ELSE:
            return getIfElseComponents(template, blockId, layoutSize);
        case REPEAT:
            return getRepeatComponents(template, blockId, layoutSize);
        case IF_ELSE_COMP_INT:
            return getIfElseCompIntComponents(template, blockId, layoutSize);
        case IF_ELSE_COMP_TWO_VARS:
            return getIfElseCompTwoVarsComponents(
                template, blockId, layoutSize
            );
        default:
            throw new IllegalStateException();
        }
    }
  
    private List<BlockComponent> getModelDefinedBlockComponents(
        final BlockTemplate template,
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        assert template != null;
        assert blockId != null;
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        for (
            Entry<String, ArgumentType> entry
            : template.getArgumentTypes().entrySet()
        ) {
            BlockComponent newComponent = 
                getBlockComponent(
                    entry.getKey(),
                    entry.getValue(), 
                    blockId, 
                    layoutSize
                );
            result.add(newComponent);
        }
        return result;    
    }
    
    private BlockComponent getBlockComponent(
        final String name, 
        final ArgumentType argumentType, 
        final UUID id,
        final LayoutSize layoutSize
    ) {
        assert name != null && name.length() > 0;
        assert argumentType != null;
        assert id != null;
        
        if (argumentType.isEnum()) {
            // combo box
            List<Object> valueList = argumentType.getEnumValues();
            String[] temp = new String[valueList.size()];
            String[] asStrings = valueList.toArray(temp);
            return new BlockComboBox(
                asStrings, 
                id, 
                name, 
                this.comboBoxListener,
                layoutSize
            );
        }
        
        switch (argumentType.getArgumentValueType()) {
        case BOOLEAN_BLOCK:
            throw new UnsupportedOperationException();
        case INT:
            return new BlockTextField(
                (Integer) argumentType.getDefaultValue(), 
                id, 
                name,
                TextFieldType.INT_VALUE,
                this.textFieldListener,
                layoutSize
            );
        case INT_BLOCK:
            throw new UnsupportedOperationException();
        case LIST:
            throw new UnsupportedOperationException();
        case NUMBER_BLOCK:
            throw new UnsupportedOperationException();
        case REAL:
            return new BlockTextField(
                (Double) argumentType.getDefaultValue(), 
                id, 
                name,
                TextFieldType.DOUBLE_VALUE,
                this.textFieldListener,
                layoutSize
            ); 
        case REAL_BLOCK:
            throw new UnsupportedOperationException();
        case STRING:
            throw new UnsupportedOperationException();
        default:
            throw new IllegalStateException();      
        }
    }
    
    private List<BlockComponent> getRepeatComponents(
        final BlockTemplate template,
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        ArgumentType times = template.getArgumentType(BlockTemplate.TIMES);
        int defaultTimes = (Integer) times.getDefaultValue();
        
        BlockTextField textField = new BlockTextField(
            defaultTimes,
            blockId,
            BlockTemplate.TIMES,
            TextFieldType.INT_VALUE,
            this.textFieldListener,
            layoutSize
       );
        result.add(textField);
        return result;    
    }
    
    private List<BlockComponent> getCallSetComponents(
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        final BlockComboBox jCombo = 
            new BlockComboBox(
                Util.getStringArray(this.domainModel.getSetInstanceNames()),
                blockId,
                BlockTemplate.SETS,
                this.comboBoxListener,
                layoutSize
            );
        result.add(jCombo);
        return result;    
    }
    
    private List<BlockComponent> getIfElseCompIntComponents(
        final BlockTemplate template,
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        ArgumentType leftVar = 
            template.getArgumentType(BlockTemplate.LEFT_VAR);
        List<String> leftVarValues = 
            Util.stringListFromObjectList(leftVar.getEnumValues());
        final String[] leftVars = 
            leftVarValues.toArray(new String[leftVarValues.size()]);
        final BlockComboBox leftVarCombo = 
            new BlockComboBox(
                leftVars, 
                blockId, 
                BlockTemplate.LEFT_VAR,
                this.comboBoxListener,
                layoutSize
            );
        result.add(leftVarCombo);
        
        ArgumentType comparator = 
            template.getArgumentType(BlockTemplate.COMPARATOR);
        List<String> comparatorValues = 
            Util.stringListFromObjectList(comparator.getEnumValues());
        final String[] comparators = 
            comparatorValues.toArray(new String[comparatorValues.size()]);
        final BlockComboBox comparatorCombo = 
            new BlockComboBox(
                comparators, 
                blockId, 
                BlockTemplate.COMPARATOR,
                this.comboBoxListener,
                layoutSize
            );
        result.add(comparatorCombo);
        
        ArgumentType rightIntVar = 
            template.getArgumentType(BlockTemplate.RIGHT_INT);
        int defaultRightIntVar = (Integer) rightIntVar.getDefaultValue();
        
        BlockTextField textField = new BlockTextField(
            defaultRightIntVar,
            blockId,
            BlockTemplate.RIGHT_INT,
            TextFieldType.INT_VALUE,
            this.textFieldListener,
            layoutSize
       );
        result.add(textField);
    
        return result;
    }
    
    private List<BlockComponent> getIfElseCompTwoVarsComponents(
        final BlockTemplate template,
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        ArgumentType leftVar = 
            template.getArgumentType(BlockTemplate.LEFT_VAR);
        List<String> leftVarValues = 
            Util.stringListFromObjectList(leftVar.getEnumValues());
        final String[] leftVars = 
            leftVarValues.toArray(new String[leftVarValues.size()]);
        final BlockComboBox leftVarCombo = 
            new BlockComboBox(
                leftVars, 
                blockId, 
                BlockTemplate.LEFT_VAR,
                this.comboBoxListener,
                layoutSize
            );
        result.add(leftVarCombo);
        
        ArgumentType comparator = 
            template.getArgumentType(BlockTemplate.COMPARATOR);
        List<String> comparatorValues = 
            Util.stringListFromObjectList(comparator.getEnumValues());
        final String[] comparators = 
            comparatorValues.toArray(new String[comparatorValues.size()]);
        final BlockComboBox comparatorCombo = 
            new BlockComboBox(
                comparators, 
                blockId, 
                BlockTemplate.COMPARATOR,
                this.comboBoxListener,
                layoutSize
            );
        result.add(comparatorCombo);
        
        ArgumentType rightVar = 
            template.getArgumentType(BlockTemplate.RIGHT_VAR);
        List<String> rightVarValues = 
            Util.stringListFromObjectList(rightVar.getEnumValues());
        final String[] rightVars = 
            leftVarValues.toArray(new String[rightVarValues.size()]);
        final BlockComboBox rightVarCombo = 
            new BlockComboBox(
                rightVars, 
                blockId, 
                BlockTemplate.RIGHT_VAR,
                this.comboBoxListener,
                layoutSize
            );
        result.add(rightVarCombo);
        return result;   
    }
    
    private List<BlockComponent> getIfElseComponents(
        final BlockTemplate template,
        final UUID blockId,
        final LayoutSize layoutSize
    ) {
        List<BlockComponent> result = new ArrayList<BlockComponent>();
        
        ArgumentType predicates = 
            template.getArgumentType(BlockTemplate.PREDICATES);
        List<String> values = 
            Util.stringListFromObjectList(predicates.getEnumValues());
        final String[] predicateList = 
            values.toArray(new String[values.size()]);
        final BlockComboBox jCombo = 
            new BlockComboBox(
                predicateList, 
                blockId, 
                BlockTemplate.PREDICATES,
                this.comboBoxListener,
                layoutSize
            );
        result.add(jCombo);
        return result;    
    }
}
