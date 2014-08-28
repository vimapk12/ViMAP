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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.vanderbilt.codeview.CategorySelectView;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.sets.SetInstance;
import edu.vanderbilt.sets.SetTemplate;
import edu.vanderbilt.sets.SetTemplateConstructor;
import edu.vanderbilt.simulation.SimulationCaller;

public abstract class DomainModelImporter {

    private static boolean isImageComputation;
    
    /**
     * Used to prompt the NetLogo model for whether it is an image
     * computation model.
     */
    private static final String IMAGE_COMP_STRING = "is-image-computation";
    
    /**
     * Used to prompt the NetLogo model for whether the repeat block
     * should be shown in the palette.
     */
    private static final String USING_REPEAT_STRING = "is-using-repeat";
    
    /**
     * Set template with empty fields, and isDefault == true. Used
     * for predefined "all" and "other" sets.
     */
    private static SetTemplate defaultSetTemplate;
    
    /**
     * Name of the if-else block type.
     */
    private static final String IF = "If";
    
    private static final String SET_IF = "Groups-If";
        
    /**
     * Name of the repeat block type.
     */
    private static final String REPEAT = "Repeat";
    
    /**
     * Name of the call set block type.
     */
    private static final String CALL_SET = "Call-group";
    
    /**
     * Base name of the update set block type. The name
     * of the set to be called will be appended to this name
     * with a dash.
     */
    private static final String UPDATE_SET = "Update-group";
    
    private static final String RECTANGLE = "rectangle";
    private static final String ELLIPSE = "ellipse";
    
    private static final List<BlockTemplate> BLOCK_TEMPLATES_FROM_NETLOGO = 
        new ArrayList<BlockTemplate>();
    
    private static DomainModel getDomainModel(final String modelName) {
        return DependencyManager.getDependencyManager().
            getObject(DomainModel.class, modelName);
    }
    
    /*
     * Gather the NetLogo model's data on what blocks 
     * and sensors to use and what agent types are to have their 
     * own separate code, and set up the VPL environment accordingly.
     */
    public static void importModelData(
        final String serverName,
        final String netLogoFileName
    ) {
        getIsImageComputation();
        
        final int yMin = SimulationCaller.reportInt("min-pycor");
        final int yMax = SimulationCaller.reportInt("max-pycor");
        final int xMin = SimulationCaller.reportInt("min-pxcor");
        final int xMax = SimulationCaller.reportInt("max-pxcor");
        final int patchSize = SimulationCaller.reportInt("patch-size");
        
        final List<String> categories = getCategories();
        
        List<SetTemplate> setTemplates = null;
        List<SetInstance> setInstances = null;
        if (isImageComputation) {
            setTemplates = getSetTemplates();
            setInstances = getSetInstances();
        }
        
        final List<String> agentTypes = getAgentTypeNames();
        final LinkedHashMap<String, List<Procedure>> agentToProcedureList = 
            getAgentToProcedureList(agentTypes);
        final LinkedHashMap<String, List<BlockTemplate>> 
            agentToBlockTemplateList =
            getAgentToBlockTemplateList(agentTypes, setTemplates);
        
        getDomainModel(serverName).
            setIsImageComputation(isImageComputation);
        getDomainModel(serverName).loadData(
            agentToProcedureList, 
            agentToBlockTemplateList,
            categories,
            setTemplates,
            setInstances,
            yMin, 
            yMax, 
            xMin, 
            xMax,
            patchSize,
            netLogoFileName
        );
    }
    
    private static List<SetTemplate> getSetTemplates() {
        List<SetTemplate> result = new ArrayList<SetTemplate>();
        result.addAll(getBuiltInSetTemplates());
        result.addAll(getModelDefinedSetTemplates());
        return result;
    }
    
    private static void initializeDefaultSetTemplateIfNeeded() {
        if (defaultSetTemplate == null) {
            LinkedHashMap<String, ArgumentType> map = 
                new LinkedHashMap<String, ArgumentType>();
            defaultSetTemplate  = 
                new SetTemplate("", false, map, null, true);
        }
    }
    
    private static List<SetTemplate> getBuiltInSetTemplates() {
        List<SetTemplate> result = new ArrayList<SetTemplate>();
        
        initializeDefaultSetTemplateIfNeeded();
        result.add(defaultSetTemplate);
                
        result.add(SetTemplateConstructor.getRectangleSetTemplate(RECTANGLE));
        result.add(SetTemplateConstructor.getEllipseSetTemplate(ELLIPSE));
        
        return result;
    }
    
    
    private static List<SetTemplate> getModelDefinedSetTemplates() {
        List<SetTemplate> result = new ArrayList<SetTemplate>();
        
        try {
            final int setTypesListLength = 
                SimulationCaller.reportInt("list-length set-types-list");
            
            for (int i = 0; i < setTypesListLength; i++) {
                final SetTemplate temp = makeSetTemplate(i);
                for (SetTemplate existingTemplate: result) {
                    if (existingTemplate.getName().equals(temp.getName())) {
                        throw new IllegalArgumentException();
                    }
                }
                if (!DomainModel.areCharactersLegal(temp.getName())) {
                    throw new IllegalArgumentException();
                }
                
                result.add(temp);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    private static SetTemplate makeSetTemplate(final int aIndex) {
        try {
            final String setTypeName = 
                SimulationCaller.reportString(
                    "list-item-property set-types-list " 
                    + aIndex + " \"set-type-name\"");
            
            
            final int argListLength = 
                SimulationCaller.reportInt(
                    "set-type-arg-list-length " + aIndex
                );
            
            LinkedHashMap<String, ArgumentType> argumentTypes = 
                new LinkedHashMap<String, ArgumentType>();
            for (
                int currentArgIndex = 0; 
                currentArgIndex < argListLength; 
                currentArgIndex++
            ) {
                ArgumentType argumentType = 
                    ArgumentTypeFactory.makeArgumentType(
                        aIndex, 
                        currentArgIndex,
                        true
                    );
                argumentTypes.put("" + currentArgIndex, argumentType);
            }
            
            return new SetTemplate(
                setTypeName, 
                false, 
                argumentTypes, 
                null, 
                false
            );
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }    
    }
    
    /*
     * Must be called after getSetTemplates()
     */
    private static List<SetInstance> getSetInstances() {
        List<SetInstance> result = new ArrayList<SetInstance>();
        
        initializeDefaultSetTemplateIfNeeded();
        Map<String, Object> args = new HashMap<String, Object>();
        SetInstance all = new SetInstance(
            defaultSetTemplate, 
            DomainModel.ALL_SET_STRING, 
            args
        );
        result.add(all);
        
        SetInstance other = new SetInstance(
            defaultSetTemplate, 
            DomainModel.OTHER_SET_STRING, 
            args
        );
        result.add(other);
        
        return result;
    }
    
    private static LinkedHashMap<String, List<BlockTemplate>> 
        getAgentToBlockTemplateList(
        final List<String> agentTypes,
        final List<SetTemplate> setTemplates
    ) {
        LinkedHashMap<String, List<BlockTemplate>> result = 
            new LinkedHashMap<String, List<BlockTemplate>>();
        
        List<BlockTemplate> defaultTemplates = 
            getDefaultBlockTemplates(setTemplates);
        getBlockTemplatesFromNetLogo();
        for (String agentType: agentTypes) {
            List<BlockTemplate> agentTemplates = new ArrayList<BlockTemplate>();
            agentTemplates.addAll(defaultTemplates);
            for (BlockTemplate template: getBlockTemplates(agentType)) {
                if (!agentTemplates.contains(template)) {
                    agentTemplates.add(template);
                }
            }
            sortBlockTemplates(agentTemplates);
            result.put(agentType, agentTemplates);
        }
        
        return result;        
    }
    
    /*
     * Sort by category, known categories first.
     */
    private static void sortBlockTemplates(final List<BlockTemplate> blocks) {
        List<BlockTemplate> temp = new ArrayList<BlockTemplate>();
        temp.addAll(blocks);
        blocks.clear();
        
        Set<String> takenCategories = new HashSet<String>();
        takenCategories.add(DomainModel.CONTROL_CATEGORY_STRING);
        for (BlockTemplate block: temp) {
            if (block.getCategory() != null && block.getCategory().equals(
                DomainModel.CONTROL_CATEGORY_STRING)
            ) {
                blocks.add(block);
            }
        }
        takenCategories.add(DomainModel.MOVEMENT_CATEGORY_STRING);
        for (BlockTemplate block: temp) {
            if (block.getCategory() != null && block.getCategory().equals(
                DomainModel.MOVEMENT_CATEGORY_STRING)
            ) {
                blocks.add(block);
            }
        }
        takenCategories.add(DomainModel.PEN_CATEGORY_STRING);
        for (BlockTemplate block: temp) {
            if (block.getCategory() != null && block.getCategory().equals(
                DomainModel.PEN_CATEGORY_STRING)
            ) {
                blocks.add(block);
            }
        }
        takenCategories.add(DomainModel.SECRET_NUMBER_CATEGORY_STRING);
        for (BlockTemplate block: temp) {
            if (block.getCategory() != null && block.getCategory().equals(
                DomainModel.SECRET_NUMBER_CATEGORY_STRING)
            ) {
                blocks.add(block);
            }
        }
        
        Set<String> otherCategories = new HashSet<String>();
        for (BlockTemplate block: temp) {
            String category = block.getCategory();
            if (!takenCategories.contains(category) && category != null) {
                otherCategories.add(category);
            }
        }
        // put blocks from each other category together
        for (String category: otherCategories) {
            for (BlockTemplate block: temp) {
                if (
                    block.getCategory() != null 
                    && block.getCategory().equals(category)
                ) {
                    blocks.add(block);
                }
            }
        }
        // add remaining blocks if any were missed
        for (BlockTemplate block: temp) {
            if (!blocks.contains(block)) {
                blocks.add(block);
            }
        }
    }
    
    private static List<BlockTemplate> getBlockTemplates(
        final String agentType
    ) {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        
        for (String blockName: getPrimitivesList(agentType)) {
            boolean found = false;
            for (BlockTemplate template: BLOCK_TEMPLATES_FROM_NETLOGO) {
                if (template.getName().equals(blockName)) {
                    found = true;
                    result.add(template);
                    break;
                }
            }
            
            if (!found) {
                throw new IllegalStateException(
                    "block " + blockName + " not found"
                );
            }
        }
        
        return result;
    }
    
    private static LinkedHashMap<String, List<Procedure>> 
        getAgentToProcedureList(
        final List<String> agentTypes
    ) {
        LinkedHashMap<String, List<Procedure>> result = 
            new LinkedHashMap<String, List<Procedure>>();
        for (String agentType: agentTypes) {
            List<Procedure> procedureList = new ArrayList<Procedure>();
            
            List<String> procedureNames = getProcedureNames(agentType);
            for (String procedureName: procedureNames) {
                procedureList.add(
                    new Procedure(procedureName, agentType, false)
                );
            }
            
            result.put(agentType, procedureList);
        }
        
        return result;
    }

    
    private static void getIsImageComputation() {
        isImageComputation = SimulationCaller.reportBoolean(IMAGE_COMP_STRING);
    }
    
    private static boolean getIsUsingRepeat() {
        return SimulationCaller.reportBoolean(USING_REPEAT_STRING);
    }
    
    private static List<String> getAgentTypeNames() {
        List<String> result = new ArrayList<String>();
        
        try {
            final int agentTypeListLength = 
                SimulationCaller.reportInt("list-length agent-kind-list");
            for (int i = 0; i < agentTypeListLength; i++) {
                result.add(getAgentsetName(i));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    private static String getAgentsetName(final int aIndex) {
        try {
            final String result = 
                SimulationCaller.reportString(
                    "[name] of list-item agent-kind-list " 
                    + aIndex
                ); 
                    
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> getSetPredicateList() {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int predicateListLength = 
                SimulationCaller.reportInt("list-length set-predicate-list");
            for (int i = 0; i < predicateListLength; i++) {
                result.add(
                    SimulationCaller.reportString(
                        "list-item set-predicate-list " + i
                ));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> getCompTwoVarsRightVars() {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int predicateListLength = 
                SimulationCaller.reportInt("list-length comp-vars-right-vars");
            for (int i = 0; i < predicateListLength; i++) {
                result.add(
                    SimulationCaller.reportString(
                        "list-item comp-vars-right-vars " + i
                ));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> getCompTwoVarsLeftVars() {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int predicateListLength = 
                SimulationCaller.reportInt("list-length comp-vars-left-vars");
            for (int i = 0; i < predicateListLength; i++) {
                result.add(
                    SimulationCaller.reportString(
                        "list-item comp-vars-left-vars " + i
                ));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> getCompIntLeftVars() {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int predicateListLength = 
                SimulationCaller.reportInt("list-length comp-int-left-vars");
            for (int i = 0; i < predicateListLength; i++) {
                result.add(
                    SimulationCaller.reportString(
                        "list-item comp-int-left-vars " + i
                ));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<String> getComparatorsList() {
        final List<String> result = new ArrayList<String>();
        result.add("less than");
        result.add("greater than");
        result.add("equals");
        return result;
    }
    
    private static List<String> getPredicateList() {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int predicateListLength = 
                SimulationCaller.reportInt("list-length predicate-list");
            for (int i = 0; i < predicateListLength; i++) {
                result.add(
                    SimulationCaller.reportString(
                        "list-item predicate-list " + i
                ));
            }
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static BlockTemplate getSetIfElseTemplate() {
        List<String> setPredicateList = getSetPredicateList();
        if (setPredicateList.size() == 0) {
            return null;
        }
        
        ArgumentType setList = 
            ArgumentTypeFactory.makeEnumArgumentType(setPredicateList);
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.PREDICATES, setList);
        
        return new BlockTemplate(
            SET_IF, 
            null,
            null,
            argTypes, 
            true, 
            true,
            false,
            DomainModel.CONTROL_CATEGORY_STRING,
            2,
            PredefinedBlockType.SET_IF_ELSE,
            null
        );  
    }
    
    private static BlockTemplate getIfElseCompIntTemplate() {
        List<String> leftVars = getCompIntLeftVars();
        if (leftVars.size() == 0) {
            return null;
        }
        List<String> comparators = getComparatorsList();

        ArgumentType leftVarArg = 
            ArgumentTypeFactory.makeEnumArgumentType(leftVars);
        ArgumentType comparatorsArg =
            ArgumentTypeFactory.makeEnumArgumentType(comparators);
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.LEFT_VAR, leftVarArg);
        argTypes.put(BlockTemplate.COMPARATOR, comparatorsArg);
        
        final int compareDefault = 0;
        final int compareMin = 0;
        final int compareMax = 500;
        ArgumentType compareArg = 
            ArgumentTypeFactory.makeIntArgumentType(
                compareMin, 
                compareMax, 
                compareDefault
            );
        argTypes.put(BlockTemplate.RIGHT_INT, compareArg);

        return new BlockTemplate(
            "if-comp-int",
            IF,
            null,
            argTypes,
            true,
            false,
            false,
            DomainModel.CONTROL_CATEGORY_STRING,
            2,
            PredefinedBlockType.IF_ELSE_COMP_INT,
            null
        );
    }
    
    private static BlockTemplate getIfElseCompTwoVarsTemplate() {
        List<String> leftVars = getCompTwoVarsLeftVars();
        if (leftVars.size() == 0) {
            return null;
        }
        List<String> comparators = getComparatorsList();
        List<String> rightVars = getCompTwoVarsRightVars();
        if (rightVars.size() == 0) {
            return null;
        }
        
        ArgumentType leftVarArg = 
            ArgumentTypeFactory.makeEnumArgumentType(leftVars);
        ArgumentType comparatorsArg =
            ArgumentTypeFactory.makeEnumArgumentType(comparators);
        ArgumentType rightVarArg = 
            ArgumentTypeFactory.makeEnumArgumentType(rightVars);       
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.LEFT_VAR, leftVarArg);
        argTypes.put(BlockTemplate.COMPARATOR, comparatorsArg);
        argTypes.put(BlockTemplate.RIGHT_VAR, rightVarArg);
        return new BlockTemplate(
            "if-comp-two-vars",
            IF,
            null,
            argTypes,
            true,
            false,
            false,
            DomainModel.CONTROL_CATEGORY_STRING,
            2,
            PredefinedBlockType.IF_ELSE_COMP_TWO_VARS,
            null
        );
    }
    
    private static BlockTemplate getIfElseTemplate() {
        List<String> predicateList = getPredicateList();
        if (predicateList.size() == 0) {
            return null;
        }
        
        ArgumentType setList = 
            ArgumentTypeFactory.makeEnumArgumentType(predicateList);
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.PREDICATES, setList);
        
        return new BlockTemplate(
            IF, 
            null,
            null,
            argTypes, 
            true, 
            false,
            false,
            DomainModel.CONTROL_CATEGORY_STRING,
            2,
            PredefinedBlockType.IF_ELSE,
            null
        );        
    }
    
    private static BlockTemplate getRepeatTemplate() {        
        final int repeatDefault = 2;
        final int repeatMin = 1;
        final int repeatMax = 999;
        
        ArgumentType repeatTimes = 
            ArgumentTypeFactory.makeIntArgumentType(
                repeatMin, 
                repeatMax, 
                repeatDefault
            );
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.TIMES, repeatTimes);
        
        return new BlockTemplate(
            REPEAT, 
            null,
            null,
            argTypes, 
            true, 
            true,
            false,
            DomainModel.CONTROL_CATEGORY_STRING,
            1,
            PredefinedBlockType.REPEAT,
            null
        );         
    }
    
    private static List<BlockTemplate> getDefaultBlockTemplates(
        final List<SetTemplate> setTemplates
    ) {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        
        final BlockTemplate ifElseTemplate = getIfElseTemplate();
        if (ifElseTemplate != null) {
            result.add(ifElseTemplate);
        }
        
        final BlockTemplate compIntTemplate = getIfElseCompIntTemplate();
        if (compIntTemplate != null) {
            result.add(compIntTemplate);
        }
        
        final BlockTemplate compTwoVarsTemplate = 
            getIfElseCompTwoVarsTemplate();
        if (compTwoVarsTemplate != null) {
            result.add(compTwoVarsTemplate);
        }
        
        if (getIsUsingRepeat()) {
            result.add(getRepeatTemplate());
        }
        
        if (isImageComputation) {
            result.addAll(getSetBlockTemplates(setTemplates));
        }

        return result;
    }
    
    private static List<BlockTemplate> getSetBlockTemplates(
        final List<SetTemplate> setTemplates
    ) {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        result.add(getCallSetTemplate());
        result.add(getUpdateSetTemplate(RECTANGLE));
        result.add(getUpdateSetTemplate(ELLIPSE));
        result.add(getSetIfElseTemplate());
        
        if (setTemplates != null) {
            for (SetTemplate setTemplate: setTemplates) {
                if (setTemplate.isUserDefined()) {
                    result.add(
                        getModelDefinedUpdateSetBlockTemplate(setTemplate)
                    );
                }
            }
        }
        
        return result;
    }
    
    private static BlockTemplate getUpdateSetTemplate(
        final String setTypeName
    ) {
        List<String> setNames = new ArrayList<String>();
        ArgumentType setList = 
            ArgumentTypeFactory.makeEnumArgumentType(setNames);
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.SETS, setList);

        final String blockName = UPDATE_SET + "-" + setTypeName;
        return new BlockTemplate(
            blockName, 
            null,
            null,
            argTypes, 
            true, 
            false,
            false,
            DomainModel.SET_CATEGORY_STRING,
            0,
            PredefinedBlockType.UPDATE_SET,
            setTypeName
        );
    }
    
    private static BlockTemplate getCallSetTemplate() {
        List<String> setNames = new ArrayList<String>();
        DomainModel.addDefaultSetNames(setNames);
        ArgumentType setList = 
            ArgumentTypeFactory.makeEnumArgumentType(setNames);
        LinkedHashMap<String, ArgumentType> argTypes = 
            new LinkedHashMap<String, ArgumentType>();
        argTypes.put(BlockTemplate.SETS, setList);
        
        return new BlockTemplate(
            CALL_SET, 
            null,
            null,
            argTypes, 
            true, 
            true,
            false,
            DomainModel.SET_CATEGORY_STRING,
            1,
            PredefinedBlockType.CALL_SET,
            null
        );
    }
    
    private static List<String> getCategories() {
        List<String> result = new ArrayList<String>();
        final int categoriesListLength = 
            SimulationCaller.reportInt("list-length categories-list");
        
        result.add(CategorySelectView.ALL_CATEGORY_STRING);
        result.add(CategorySelectView.BASIC_CATEGORY_STRING);
        for (int i = 0; i < categoriesListLength; i++) {
            final String category = 
                SimulationCaller.reportString(
                    "list-item categories-list " + i
                );
            
            if (DomainModel.isCategoryNameReserved(category)) {
                throw new IllegalStateException(
                    "Illegal category name: " + category
                );
            }
            
            if (category.equals(CategorySelectView.ALL_CATEGORY_STRING)
                || category.equals(CategorySelectView.BASIC_CATEGORY_STRING)
            ) {
                throw new IllegalStateException();
            }
            
            result.add(category);
        }
        
        if (!result.contains(DomainModel.CONTROL_CATEGORY_STRING)) {
            result.add(DomainModel.CONTROL_CATEGORY_STRING);
        }
        
        DomainModel.addReservedCategoryNames(result, isImageComputation);
        return result;
    }
    

    private static void getBlockTemplatesFromNetLogo() {
        try {
            final int blockListLength = 
                SimulationCaller.reportInt("list-length blocks-list");
            
            for (int i = 0; i < blockListLength; i++) {
                final BlockTemplate temp = makeBlockTemplate(i);
                if (BLOCK_TEMPLATES_FROM_NETLOGO.contains(temp)) {
                    throw new IllegalStateException(
                        "duplicate block " + temp.getName()
                    );
                }
                BLOCK_TEMPLATES_FROM_NETLOGO.add(temp);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static BlockTemplate getModelDefinedUpdateSetBlockTemplate(
        final SetTemplate setTemplate
    ) {
        final String blockName = "Update-set-" + setTemplate.getName();
        final String category = DomainModel.SET_CATEGORY_STRING;
        
        List<String> setNames = new ArrayList<String>();
        ArgumentType setList = 
            ArgumentTypeFactory.makeEnumArgumentType(setNames);
        final LinkedHashMap<String, ArgumentType> argumentTypes =
            new LinkedHashMap<String, ArgumentType>();
        argumentTypes.put(BlockTemplate.SETS, setList);
        for (
            Entry<String, ArgumentType> argTypeEntry
            : setTemplate.getArgumentTypes().entrySet()
        ) {
            argumentTypes.put(argTypeEntry.getKey(), argTypeEntry.getValue());
        }

        return new BlockTemplate(
            blockName, 
            null,
            null,
            argumentTypes, 
            false, 
            false,
            false,
            category,
            0, 
            PredefinedBlockType.UPDATE_SET,
            setTemplate.getName()
        );
    }
    
    
    /*
     * Helper method that makes a BlockTemplate based on its index 
     * in the blocks-list from NetLogo.
     * 
     * @return returns the BlockTemplate that has been created.
     */
    private static BlockTemplate makeBlockTemplate(final int aIndex) {
        try {
            final String blockName = 
                SimulationCaller.reportString("list-item-property blocks-list " 
                    + aIndex + " \"block-name\"");
            
            String category =
                SimulationCaller.reportString(
                    "list-item-property blocks-list " 
                    + aIndex + " \"category\""
                );
            if (falsy(category)) {
                category = null;
            }
            
            String displayName =
                SimulationCaller.reportString(
                    "list-item-property blocks-list " 
                    + aIndex + " \"display-name\""
                );
            if (falsy(displayName)) {
                displayName = null;
            }
            
            String labelAfterArg =
                SimulationCaller.reportString(
                    "list-item-property blocks-list " 
                    + aIndex + " \"label-after-arg\""
                );
            if (falsy(labelAfterArg)) {
                labelAfterArg = null;
            }
            
            final int argListLength = 
                SimulationCaller.reportInt("arg-list-length " + aIndex);
            
            LinkedHashMap<String, ArgumentType> argumentTypes = 
                new LinkedHashMap<String, ArgumentType>();
            for (
                int currentArgIndex = 0; 
                currentArgIndex < argListLength; 
                currentArgIndex++
            ) {
                ArgumentType argumentType = 
                    ArgumentTypeFactory.makeArgumentType(
                        aIndex, 
                        currentArgIndex,
                        false
                    );
                argumentTypes.put("" + currentArgIndex, argumentType);
            }
            
            final String isObserver = SimulationCaller.reportString(
                "list-item-property blocks-list " + aIndex + " \"is-observer\""
           );
            boolean observerResult;
            if (truthy(isObserver)) {
                observerResult = true;
            } else if (falsy(isObserver)) {
                observerResult = false;
            } else {
                throw new IllegalArgumentException();
            }
            
            final String isBasic = SimulationCaller.reportString(
                "list-item-property blocks-list " + aIndex + " \"is-basic\""
            );
            boolean basicResult;
            if (truthy(isBasic)) {
                basicResult = true;
            } else if (falsy(isBasic)) {
                basicResult = false;
            } else {
                throw new IllegalArgumentException(blockName);
            }
            
            boolean setUpdateResult = false;
            if (isImageComputation) {
                final String isSetUpdate = SimulationCaller.reportString(
                    "list-item-property blocks-list " 
                        + aIndex + " \"is-set-update\""
                );
                if (truthy(isSetUpdate)) {
                    setUpdateResult = true;
                } else if (falsy(isSetUpdate)) {
                    setUpdateResult = false;
                } else {
                    throw new IllegalArgumentException(
                        blockName + " " + isSetUpdate
                    );
                }
            }
            
            return new BlockTemplate(
                blockName, 
                displayName,
                labelAfterArg,
                argumentTypes, 
                observerResult,
                basicResult,
                setUpdateResult,
                category,
                0,
                PredefinedBlockType.DEFAULT,
                null
            );
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean truthy(final String str) {
        return (str != null && str.equals("true"));
    }
    
    private static boolean falsy(final String str) {
        if (str == null) {
            return false;
        }
        
        return (str.equals("false") || str.equals("0.0") || str.equals("0"));
    }
    
    private static List<String> getPrimitivesList(final String agentType) {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int methodListLength = SimulationCaller.reportInt(
                "primitive-list-length \"" 
                + agentType 
                + "\"" 
           );
            
            for (int i = 0; i < methodListLength; i++) {
                final String currentPrimitiveName = 
                    SimulationCaller.reportString(
                        "primitive-list-item \"" 
                        + agentType 
                        + "\" " 
                        + i
                   );
                result.add(currentPrimitiveName);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        
        return result;
    }
    
    private static List<String> getProcedureNames(final String agentType) {
        final List<String> result = new ArrayList<String>();
        
        try {
            final int methodListLength = SimulationCaller.reportInt(
                "method-list-length \"" 
                + agentType 
                + "\"" 
           );
            
            for (int i = 0; i < methodListLength; i++) {
                final String currentMethodName = SimulationCaller.reportString(
                    "method-list-item \"" 
                    + agentType 
                    + "\" " 
                    + i
               );
                result.add(currentMethodName);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        
        return result;
    }
}
