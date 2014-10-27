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
//   https://ccl.northwestern.edu/netlogo/docs/copyright.html  //
//
//--//--//--//--//--//--// 


package edu.vanderbilt.domainmodel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.vanderbilt.codeview.CategorySelectView;
import edu.vanderbilt.sets.SetInstance;
import edu.vanderbilt.sets.SetTemplate;
import edu.vanderbilt.util.Util;

public final class DomainModel {
    
    /**
     * Category name for user procedure call blocks.
     */
    public static final String USER_CATEGORY_STRING = "User Procedures";
    
    /**
     * Category name for call set and update set blocks.
     */
    public static final String SET_CATEGORY_STRING = "Groups";
    
    /**
     * Category name for if-else and repeat blocks.
     */
    public static final String CONTROL_CATEGORY_STRING = "Control";
    
    public static final String SECRET_NUMBER_CATEGORY_STRING = "Secret Number";
    
    public static final String PEN_CATEGORY_STRING = "Pen";
    
    public static final String MOVEMENT_CATEGORY_STRING = "Movement";
    
    public static final String MEASURE_CATEGORY_STRING = "Measure";
    
    public static final String ALL_PURPOSE_CATEGORY_STRING = "All-Purpose";
    
    /**
     * Name of the "setup" procedure, which is always 
     * present for each agent type.
     */
    public static final String SETUP_STRING = "setup";
    
    /**
     * Name of the "go" procedure, which is always present for each agent type.
     */
    public static final String GO_STRING = "go";
    
    /**
     * Name of the set of all turtles. Used in call-set blocks.
     */
    public static final String ALL_SET_STRING = "all";
    
    /**
     * Name of the set of turtles not in any named set. Used in call-set blocks.
     */
    public static final String OTHER_SET_STRING = "other";
    
    private static final String CALL_SET_STRING = "call-set";
    private static final String UPDATE_SET_STRING = "update-set";
    
    private LinkedHashMap<String, List<Procedure>> agentToProcedureList;
    private LinkedHashMap<String, List<BlockTemplate>> agentToBlockList;
    
    private final List<SetTemplate> setTemplates;
    private final List<SetInstance> setInstances;
    
    // names of the categories of block, in the order in which they
    // should be displayed
    private final List<String> categories;
        
    private final Map<String, Color> categoryToBlockColor;
    
    private boolean isImageComputation;
    
    // the file name of the current NetLogo model
    private String netLogoFileName;
    private int yMin;
    private int yMax;
    private int xMin;
    private int xMax;
    private int patchSize;
    
    public static final Color FALLBACK_BUTTON_COLOR = new Color(153, 153, 153);
    public static final Color FALLBACK_BLOCK_COLOR = new Color(150, 150, 150);
    
    private String imageFileName;
    private static final String DEFAULT_IMAGE_FILE_NAME = "myimage.jpg";
    // private static final String DEFAULT_IMAGE_FILE_NAME = null;

    
    public DomainModel() {
        this.categories = new ArrayList<String>();
        this.setTemplates = new ArrayList<SetTemplate>();
        this.setInstances = new ArrayList<SetInstance>();
        
        this.categoryToBlockColor = new HashMap<String, Color>();
        this.imageFileName = DEFAULT_IMAGE_FILE_NAME;
    }
    
    public void loadData(
        final DomainModel aDomainModel
    ) {
        loadData(
            aDomainModel.agentToProcedureList, 
            aDomainModel.agentToBlockList,
            aDomainModel.categories,
            aDomainModel.setTemplates,
            aDomainModel.setInstances,
            aDomainModel.yMin,
            aDomainModel.yMax,
            aDomainModel.xMin,
            aDomainModel.xMax,
            aDomainModel.patchSize,
            aDomainModel.netLogoFileName
        );
    }
    
    public void loadData(
        final LinkedHashMap<String, List<Procedure>> aAgentToProcedureList,
        final LinkedHashMap<String, List<BlockTemplate>> aAgentToBlockList,
        final List<String> aCategories,
        final List<SetTemplate> aSetTemplates,
        final List<SetInstance> aSetInstances,
        final int aYMin,
        final int aYMax,
        final int aXMin,
        final int aXMax,
        final int aPatchSize,
        final String aNetLogoFileName
    ) {
        this.categories.clear();
        this.categories.addAll(aCategories);
        setupCategoryColors();
        
        // must be updated before checking set instances,
        // or a set instance could have the same name as a block template.
        this.agentToBlockList = aAgentToBlockList;
        this.agentToProcedureList = aAgentToProcedureList;
                
        if (aSetTemplates != null && aSetInstances != null) {
            // loading an image computation model
            
            for (int i = 0; i < aSetTemplates.size(); i++) {
                for (int j = i + 1; j < aSetTemplates.size(); j++) {
                    if (
                        aSetTemplates.get(i).getName().equals(
                            aSetTemplates.get(j).getName()
                    )) {
                        throw new IllegalArgumentException(
                            "No two set templates can share a name."
                        );
                    }
                }
            }
            this.setTemplates.clear();
            this.setTemplates.addAll(aSetTemplates);
            
            for (int i = 0; i < aSetInstances.size(); i++) {
                for (int j = i + 1; j < aSetInstances.size(); j++) {
                    if (
                        aSetInstances.get(i).getSetName().equals(
                            aSetInstances.get(j).getSetName()
                        )) {
                        throw new IllegalArgumentException(
                            "No two set instances can share a name."
                        );
                    }
                }
            }
                        
            this.setInstances.clear();
            for (SetInstance setInstance: aSetInstances) {
                if (!isSetNameLegal(setInstance.getSetName())) {
                    throw new IllegalArgumentException(
                        "Illegal set name: " + setInstance.getSetName()
                    );
                }
            }
            
            this.setInstances.addAll(aSetInstances);
        } else {
            this.setTemplates.clear();
            this.setInstances.clear();
        }
        
        this.yMin = aYMin;
        this.yMax = aYMax;
        this.xMin = aXMin;
        this.xMax = aXMax;
        this.patchSize = aPatchSize;
        this.netLogoFileName = aNetLogoFileName;
    }
    
    // call after updating background image
    public void updateWorldSize(
        final int aYMin,
        final int aYMax,
        final int aXMin,
        final int aXMax,
        final int aPatchSize
    ) {
        this.yMin = aYMin;
        this.yMax = aYMax;
        this.xMin = aXMin;
        this.xMax = aXMax;
        this.patchSize = aPatchSize;
    }
    
    public String getImageFileName() {
        return this.imageFileName;
    }
    
    public void resetImageFileName() {
        this.imageFileName = DEFAULT_IMAGE_FILE_NAME;
    }
    
    public void setImageFileName(final String name) {
        this.imageFileName = name;
    }
    
    public Color getBlockColor(final String category) {
        if (this.categoryToBlockColor.keySet().contains(category)) {
            return this.categoryToBlockColor.get(category);
        }
        
        return FALLBACK_BLOCK_COLOR;
    }
    
    public Map<String, Color> getCategoryColors() {
        return this.categoryToBlockColor;
    }
    
    public void setupCategoryColors() {
        List<String> categoriesToMatch = new ArrayList<String>();
        categoriesToMatch.addAll(this.categories);
        
        List<Color> blockColorOptions = new ArrayList<Color>();
        
        final Color controlBlockColor = new Color(0xe1a91a);
        blockColorOptions.add(controlBlockColor);
        
        final Color penBlockColor = new Color(0x0e9a6c);
        blockColorOptions.add(penBlockColor);
        
        final Color movementBlockColor = new Color(0x4a6cd4);
        blockColorOptions.add(movementBlockColor);
        
        final int darkGray = 0x444444;
        final Color secretNumberBlockColor = new Color(darkGray);
        blockColorOptions.add(secretNumberBlockColor);
        
        final int tan = 0xc88330;
        final Color userprocedurecolor = new Color(tan);
        blockColorOptions.add(userprocedurecolor);
        
        final int sky = 0x2ca5e2;
        final Color allpurposeColor = new Color(sky);
        blockColorOptions.add(allpurposeColor);
        
        final int pink = 0xbb42c3;
        final Color measureColor = new Color(pink);
        blockColorOptions.add(measureColor);
        
        final int darkred = 0xad3333;
        final Color setscolor = new Color(darkred);
        blockColorOptions.add(setscolor);
        
        
        this.categoryToBlockColor.put(
            CategorySelectView.ALL_CATEGORY_STRING, 
            Color.RED
        );
        categoriesToMatch.remove(CategorySelectView.ALL_CATEGORY_STRING);
        this.categoryToBlockColor.put(
            CategorySelectView.BASIC_CATEGORY_STRING, 
            Color.BLACK
        );
        categoriesToMatch.remove(CategorySelectView.BASIC_CATEGORY_STRING);
        
        if (this.categories.contains(CONTROL_CATEGORY_STRING)) {
            this.categoryToBlockColor.put(
                CONTROL_CATEGORY_STRING, controlBlockColor
            );
            categoriesToMatch.remove(CONTROL_CATEGORY_STRING);
            blockColorOptions.remove(controlBlockColor);
        }
        if (this.categories.contains(PEN_CATEGORY_STRING)) {
            this.categoryToBlockColor.put(PEN_CATEGORY_STRING, penBlockColor);
            categoriesToMatch.remove(PEN_CATEGORY_STRING);
            blockColorOptions.remove(penBlockColor);
        }
        if (this.categories.contains(MOVEMENT_CATEGORY_STRING)) {
            this.categoryToBlockColor.put(
                MOVEMENT_CATEGORY_STRING, 
                movementBlockColor
            );
            categoriesToMatch.remove(MOVEMENT_CATEGORY_STRING);
            blockColorOptions.remove(movementBlockColor);
        }
        if ( this.categories.contains(MEASURE_CATEGORY_STRING) )
        {
          this.categoryToBlockColor.put(
            MEASURE_CATEGORY_STRING, 
            measureColor);
          categoriesToMatch.remove(MEASURE_CATEGORY_STRING);
          blockColorOptions.remove(measureColor);
        }
        if ( this.categories.contains(ALL_PURPOSE_CATEGORY_STRING) ) {
          this.categoryToBlockColor.put(
               ALL_PURPOSE_CATEGORY_STRING, 
        	   allpurposeColor);
          categoriesToMatch.remove(ALL_PURPOSE_CATEGORY_STRING);
          blockColorOptions.remove(allpurposeColor);
        }
        if ( this.categories.contains(SET_CATEGORY_STRING) )
        {
        	this.categoryToBlockColor.put(
        	  SET_CATEGORY_STRING, 
        	  setscolor);
        	categoriesToMatch.remove(SET_CATEGORY_STRING);
        	blockColorOptions.remove(setscolor);
        }
        if ( this.categories.contains(USER_CATEGORY_STRING) )
        {
        	this.categoryToBlockColor.put(
              USER_CATEGORY_STRING,
              userprocedurecolor);
        	categoriesToMatch.remove(USER_CATEGORY_STRING);
        	blockColorOptions.remove(userprocedurecolor);
        }
        if (this.categories.contains(SECRET_NUMBER_CATEGORY_STRING)) {
            this.categoryToBlockColor.put(
                SECRET_NUMBER_CATEGORY_STRING, 
                secretNumberBlockColor
            );
            categoriesToMatch.remove(SECRET_NUMBER_CATEGORY_STRING);
            blockColorOptions.remove(secretNumberBlockColor);
        }
        
        for (String category: categoriesToMatch) {
            if (blockColorOptions.isEmpty()) {
                this.categoryToBlockColor.put(category, FALLBACK_BLOCK_COLOR);
            } else {
                this.categoryToBlockColor.put(
                    category, 
                    blockColorOptions.get(0)
                );
                
                blockColorOptions.remove(0);
            }
        }
    }
    
    public boolean isImageComputation() {
        return this.isImageComputation;
    }
    
    public void setIsImageComputation(final boolean aIsImageComputation) {
        this.isImageComputation = aIsImageComputation;
    }
        
    public static boolean isCategoryNameReserved(final String categoryName) {
        return USER_CATEGORY_STRING.equals(categoryName)
            || SET_CATEGORY_STRING.equals(categoryName);
    }
    
    public static void addReservedCategoryNames(
        final List<String> categories,
        final boolean isImageComputation
    ) {
        if (isImageComputation) {
            categories.add(SET_CATEGORY_STRING);
        }
        categories.add(USER_CATEGORY_STRING);
    }
    
    public static void addDefaultSetNames(final List<String> setNames) {
        setNames.add(ALL_SET_STRING);
        setNames.add(OTHER_SET_STRING);
    }
    
    public List<BlockTemplate> getUpdateSetBlockTemplates(
        final String setTypeName
    ) {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        for (BlockTemplate blockTemplate: getAllBlockTemplates()) {
            if (
                blockTemplate.getPredefinedBlockType() 
                    == PredefinedBlockType.UPDATE_SET
                && blockTemplate.getUpdateSetType().equals(setTypeName)
            ) {
                result.add(blockTemplate);
            }
        }
        
        return result;
    }
    
    public List<BlockTemplate> getCallSetBlockTemplates() {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        for (BlockTemplate blockTemplate: getAllBlockTemplates()) {
            if (
                blockTemplate.getPredefinedBlockType() 
                 == PredefinedBlockType.CALL_SET
             ) {
                result.add(blockTemplate);
            }
        }
        
        return result;
    }
        
    public List<BlockTemplate> getBlockTemplates(
        final String agentName,
        final String procedureName
    ) {
        assert this.agentToBlockList.containsKey(agentName);
        
        if (isUserProcedure(procedureName)) {
            // don't return blocks that call user procedures if
            // the procedure is itself a user procedure
            
            List<BlockTemplate> result = new ArrayList<BlockTemplate>();
            for (
                BlockTemplate blockTemplate
                : this.agentToBlockList.get(agentName)
            ) {
                if (
                    blockTemplate.getPredefinedBlockType() 
                        != PredefinedBlockType.PROCEDURE_CALL
                ) {
                    result.add(blockTemplate);
                }
            }
            
            return result;
        }
        
        return this.agentToBlockList.get(agentName);
    }
    
    
    public List<String> getAgentTypeNames() {
        List<String> result = new ArrayList<String>();
        result.addAll(this.agentToProcedureList.keySet());
        return result;
    }
    
    
    public List<Procedure> getProceduresForAgent(final String agentName) {
        return this.agentToProcedureList.get(agentName);
    }
    
    public List<String> getProcedureNamesForAgent(final String agentName) {
        List<String> result = new ArrayList<String>();
        for (Procedure procedure: getProceduresForAgent(agentName)) {
            result.add(procedure.getName());
        }
        
        return result;
    }
    
    public SetTemplate getSetTemplate(final String name) {
        for (SetTemplate setTemplate: this.setTemplates) {
            if (setTemplate.getName().equals(name)) {
                return setTemplate;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public List<SetTemplate> getSetTemplates() {
        List<SetTemplate> result = new ArrayList<SetTemplate>();
        result.addAll(this.setTemplates);
        return result;
    }
    
    public void removeSetInstances() {
        Set<SetInstance> toRemove = new HashSet<SetInstance>();
        for (SetInstance setInstance: this.setInstances) {
            if (
                !setInstance.getSetTemplate().isDefault()
            ) {
                toRemove.add(setInstance);
            }
        }
        
        for (SetInstance target: toRemove) {
            this.setInstances.remove(target);
        }
    }
    
    public void addSetInstance(
        final SetTemplate setTemplate, 
        final String setName, 
        final Map<String, Object> arguments
    ) {
        if (
            setTemplate == null
            || setName == null
            || arguments == null
        ) {
            throw new IllegalArgumentException();
        }
        
        if (!isSetNameLegal(setName)) {
            throw new IllegalArgumentException();
        }
        
        SetInstance newInstance = 
            new SetInstance(
                setTemplate, 
                setName, 
                arguments
            );
        this.setInstances.add(newInstance);
    }
    
    public void deleteSetInstance(final String setName) {
        if (setName == null) {
            throw new IllegalArgumentException();
        }
        
        SetInstance toDelete = getSetInstance(setName);
        if (toDelete.getSetTemplate().isDefault()) {
            throw new IllegalArgumentException("Can't delete default sets.");
        }
        
        // not a default set
        this.setInstances.remove(toDelete);
    }
    
    public SetInstance getSetInstance(final String setName) {
        for (SetInstance setInstance: this.setInstances) {
            if (setInstance.getSetName().equals(setName)) {
                return setInstance;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public List<SetInstance> getSetInstances() {
        List<SetInstance> result = new ArrayList<SetInstance>();
        result.addAll(this.setInstances);
        return result;
    }
    
    public List<SetInstance> getSetInstancesOfType(final String setTypeName) {
        List<SetInstance> result = new ArrayList<SetInstance>();
        for (SetInstance setInstance: getSetInstances()) {
            if (setInstance.getSetTemplate().getName().equals(setTypeName)) {
                result.add(setInstance);
            }
        }
        
        return result;
    }
    
    public List<String> getSetInstanceNames() {
        List<String> result = new ArrayList<String>();
        for (SetInstance setInstance: this.setInstances) {
            result.add(setInstance.getSetName());
        }
        
        return result;
    }
    
    /*
     * A set name is not allowed to match any other set instance's name,
     * any set template's name, or any block template's name.
     */
    public boolean isSetNameTaken(final String setName) {
        for (SetInstance setInstance: this.setInstances) {
            if (setInstance.getSetName().equals(setName)) {
                return true;
            }
        }
        
        for (SetTemplate setTemplate: this.setTemplates) {
            if (setTemplate.getName().equals(setName)) {
                return true;
            }
        }
        
        for (
            List<BlockTemplate> blockTemplateList
            : this.agentToBlockList.values()
        ) {
            for (BlockTemplate blockTemplate: blockTemplateList) {
                if (blockTemplate.getName().equals(setName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean areCharactersLegal(final String setName) {
        if (setName == null) {
            return false;
        }
        
        char[] charArray = setName.toCharArray();
        for (char c: charArray) {
            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    public int getYMin() {
        return this.yMin;
    }

    
    public int getYMax() {
        return this.yMax;
    }

    
    public int getXMin() {
        return this.xMin;
    }

    
    public int getXMax() {
        return this.xMax;
    }
    
    public int getPatchSize() {
        return this.patchSize;
    }  
    
    public List<String> getCategories() {
        return Util.copyList(this.categories);
    }
    
    public void addUserProcedure(
        final String procedureName, 
        final List<String> agentTypes
    ) {
        assert !isBlockNameTaken(procedureName);
        
        BlockTemplate newTemplate = new BlockTemplate(
            procedureName, 
            null,
            null,
            null,
            true, 
            false,
            false,
            USER_CATEGORY_STRING, 
            0,
            PredefinedBlockType.PROCEDURE_CALL,
            null
        );
                
        for (String agentType: agentTypes) {
            Procedure newProcedure = 
                new Procedure(procedureName, agentType, true);
            List<Procedure> procedureList = 
                this.agentToProcedureList.get(agentType);
            procedureList.add(newProcedure);
            
            List<BlockTemplate> templateList = 
                this.agentToBlockList.get(agentType);
            templateList.add(newTemplate);
        }
    }
    
    public boolean isUserProcedure(final String name) {
        assert name != null;
        for (List<BlockTemplate> templateSet: this.agentToBlockList.values()) {
            for (BlockTemplate template: templateSet) {
                if (template.getName().equals(name)) {
                    return template.getPredefinedBlockType() 
                        == PredefinedBlockType.PROCEDURE_CALL;
                }
            }
        }
        
        if (name.equals(GO_STRING) || name.equals(SETUP_STRING)) {
            return false;
        }
        
        throw new IllegalArgumentException("not found: " + name);        
    }
    
    public boolean isBlockNameTaken(final String name) {
        for (List<BlockTemplate> templateSet: this.agentToBlockList.values()) {
            for (BlockTemplate template: templateSet) {
                if (template.getName().equals(name)) {
                    return true;
                }
            }
        }
        
        if (
            name.equals(GO_STRING) 
            || name.equals(SETUP_STRING)
            || name.contains(CALL_SET_STRING)
            || name.contains(UPDATE_SET_STRING)
        ) {
            return true;
        }
        
        return false;
    }
    
    public void removeUserProcedure(final String target) {
        assert doesProcedureExist(target);
            
        for (List<Procedure> procedures: this.agentToProcedureList.values()) {
            Set<Procedure> toRemove = new HashSet<Procedure>();
            for (Procedure procedure: procedures) {
                if (procedure.getName().equals(target)) {
                    toRemove.add(procedure);
                }
            }
            procedures.removeAll(toRemove);
        }
        
        for (List<BlockTemplate> blocks: this.agentToBlockList.values()) {
            Set<BlockTemplate> toRemove = new HashSet<BlockTemplate>();
            for (BlockTemplate template: blocks) {
                if (template.getName().equals(target)) {
                    toRemove.add(template);
                }
            }
            blocks.removeAll(toRemove);
        }
    }
    
    public void removeUserProcedures() {
        for (
            List<Procedure> procedureList
            : this.agentToProcedureList.values()
        ) {
            Set<Procedure> toRemove = new HashSet<Procedure>();
            for (Procedure procedure: procedureList) {
                if (procedure.isUserDefined()) {
                    toRemove.add(procedure);
                }
            }
            
            procedureList.removeAll(toRemove);
        }    
        
        for (List<BlockTemplate> blocks: this.agentToBlockList.values()) {
            Set<BlockTemplate> toRemove = new HashSet<BlockTemplate>();
            for (BlockTemplate template: blocks) {
                if (
                    template.getPredefinedBlockType() 
                    == PredefinedBlockType.PROCEDURE_CALL
                ) {
                    toRemove.add(template);
                }
            }
            blocks.removeAll(toRemove);
        }
    }
    
    
    public boolean hasUserProcedure() {
        for (Procedure procedure: getAllProcedures()) {
            if (procedure.isUserDefined()) {
                return true;
            }
        }
        
        return false;
    }
    
    
    public Set<String> getUniqueUserProcedureNames() {
        Set<String> result = new HashSet<String>();
        for (Procedure procedure: getUserProcedures()) {
            // Set.add will not add equal strings to the set
            result.add(procedure.getName());
        }
        return result;
    }
    
    
    public int maxUserProceduresForOneAgent() {
        int max = 0;
        for (String agentType: this.getAgentTypeNames()) {
            int agentCount = 0;
            for (Procedure procedure: getUserProcedures()) {
                if (procedure.getAgentTypeName().equals(agentType)) {
                    agentCount++;
                }
            }
            
            if (agentCount > max) {
                max = agentCount;
            }
        }
            
        return max;
    }
    
    
    public String getNetLogoFileName() {
        return this.netLogoFileName;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DomainModel [isImageComputation=");
        builder.append(this.isImageComputation);
        builder.append(", agentToProcedureList=");
        builder.append(this.agentToProcedureList);
        builder.append(", agentToBlockList=");
        builder.append(this.agentToBlockList);
        builder.append(", setTemplates=");
        builder.append(this.setTemplates);
        builder.append(", setInstances=");
        builder.append(this.setInstances);
        builder.append(", categories=");
        builder.append(this.categories);
        builder.append(", netLogoFileName=");
        builder.append(this.netLogoFileName);
        builder.append(", yMin=");
        builder.append(this.yMin);
        builder.append(", yMax=");
        builder.append(this.yMax);
        builder.append(", xMin=");
        builder.append(this.xMin);
        builder.append(", xMax=");
        builder.append(this.xMax);
        builder.append(", patchSize=");
        builder.append(this.patchSize);
        builder.append("]");
        return builder.toString();
    }
    
    private List<Procedure> getUserProcedures() {
        List<Procedure> result = new ArrayList<Procedure>();
        for (Procedure procedure: getAllProcedures()) {
            if (procedure.isUserDefined()) {
                result.add(procedure);
            }
        }
        
        return result;
    }
    
    private boolean isSetNameLegal(final String setName) {
        return !isSetNameTaken(setName) && areCharactersLegal(setName);
    }
    
    private List<BlockTemplate> getAllBlockTemplates() {
        List<BlockTemplate> result = new ArrayList<BlockTemplate>();
        for (List<BlockTemplate> innerList: this.agentToBlockList.values()) {
            result.addAll(innerList);
        }
        
        return result;
    }
    
    private boolean doesProcedureExist(final String name) {
        List<Procedure> allProcedures = getAllProcedures();
        for (Procedure procedure: allProcedures) {
            if (procedure.getName().equals(name)) {
                return true;
            }
        }
        
        return false;
    }
    
    private List<Procedure> getAllProcedures() {
        List<Procedure> result = new ArrayList<Procedure>();
        for (
            List<Procedure> procedureList
            : this.agentToProcedureList.values()
        ) {
            result.addAll(procedureList);
        }
        
        return result;
    }
}
