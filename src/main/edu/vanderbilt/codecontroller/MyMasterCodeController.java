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


package edu.vanderbilt.codecontroller;

import java.awt.Color;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import edu.vanderbilt.codecomponentview.BlockView;
import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;
import edu.vanderbilt.codecomponentview.CategoryViewStyle;
import edu.vanderbilt.codeview.DraggingGlassPane;
import edu.vanderbilt.codeview.MasterCodeView;
import edu.vanderbilt.domainmodel.ArgumentType;
import edu.vanderbilt.domainmodel.ArgumentType.ArgumentValueType;
import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.domainmodel.PredefinedBlockType;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.driverandlayout.Loader;
import edu.vanderbilt.driverandlayout.MyMenuBar;
import edu.vanderbilt.runview.RunButtonPanel;
import edu.vanderbilt.runview.SpeedSliderPanel;
import edu.vanderbilt.saving.FileExtensionUtility;
import edu.vanderbilt.sets.SelectGlassPane;
import edu.vanderbilt.sets.SetInstance;
import edu.vanderbilt.sets.SetPanel;
import edu.vanderbilt.sets.SetTemplate;
import edu.vanderbilt.sets.ShapedSet;
import edu.vanderbilt.simulation.SimulationCaller;
import edu.vanderbilt.usermodel.Block;
import edu.vanderbilt.usermodel.BlockSequence;
import edu.vanderbilt.usermodel.UserModel;
import edu.vanderbilt.userprocedures.ProceduresMenu;

public final class MyMasterCodeController implements MasterCodeController {

    private final DomainModel domainModel;
    private final UserModel userModel;
    private final MasterCodeView codeView;
    private final RunButtonPanel runButtonPanel;
    private final SpeedSliderPanel speedSliderPanel;
    private final MyMenuBar myMenuBar;
    private final SetPanel setPanel;
    private String agentName;
    private String procedureName;
    private String categoryName;
    private CategoryViewStyle categoryViewStyle;
    private edu.vanderbilt.codecomponentview.BlockViewFactory viewFactory;
    private ProceduresMenu proceduresMenu;
    private String setTypeName;
    private String setName;
    private LayoutSize layoutSize;
    
    private static final boolean PRINT_BLOCKS = false;
    
    public MyMasterCodeController(
        final String aDomainModel,
        final String aUserModel,
        final String aCodeView
    ) {
        DependencyManager deps = DependencyManager.getDependencyManager();
        this.domainModel = deps.getObject(DomainModel.class, aDomainModel);
        this.userModel = deps.getObject(UserModel.class, aUserModel);
        this.codeView = deps.getObject(MasterCodeView.class, aCodeView);
        this.viewFactory = 
            deps.getObject(
                edu.vanderbilt.codecomponentview.BlockViewFactory.class, 
                "factory"
            );
        this.proceduresMenu = 
            deps.getObject(ProceduresMenu.class, "proceduresMenu");
        this.runButtonPanel = 
            deps.getObject(RunButtonPanel.class, "runControls");
        this.speedSliderPanel = 
            deps.getObject(SpeedSliderPanel.class, "speedSlider");
        this.myMenuBar = deps.getObject(MyMenuBar.class, "menuBar");
        this.setPanel = deps.getObject(SetPanel.class, "setPanel");
        this.layoutSize = LayoutSize.LARGE;
    }
    
    @Override
    public void init() {
        this.userModel.setDomainModel(this.domainModel);
        this.userModel.init();
    }
    
    @Override
    public void startUp() {
        assert SwingUtilities.isEventDispatchThread();
        final List<String> agentNames = this.domainModel.getAgentTypeNames();
        this.codeView.setAvailableAgentNames(agentNames);
        List<String> categoryNames = this.domainModel.getCategories();
        Map<String, Color> categoryColors = 
            this.domainModel.getCategoryColors();
        this.codeView.setCategories(
            categoryNames, 
            categoryColors
        );
        this.categoryViewStyle = CategoryViewStyle.BASIC;
        this.categoryName = null;
        setAgent(agentNames.get(0), true);
        updateCodeView();
        
        addSetsToGrapher();
        
        setEditable(true);
        
        if (PRINT_BLOCKS) {
            printAllBlocks();
        }
    }
    
    private void addSetsToGrapher() {
        final List<String> setNamesToAdd = new ArrayList<String>();
        for (
            SetInstance currentSetInstance
            : this.domainModel.getSetInstances()
        ) {
            if (!currentSetInstance.getSetTemplate().isDefault()) {
                setNamesToAdd.add(currentSetInstance.getSetName());
            }
        }
        
        for (String currentSetName: setNamesToAdd) {
            SimulationCaller.addSetToMeasureWorlds(currentSetName);
        }
    }
    
    @Override
    public void showAndHighlightBlock(final UUID blockId) {
        assert SwingUtilities.isEventDispatchThread();
        
        final String localAgentName = 
            this.userModel.getAgentNameByBlockId(blockId);
        assert localAgentName != null;
        setAgent(localAgentName, false);
        final String localProcedureName = 
            this.userModel.getProcedureNameByBlockId(blockId);
        assert localProcedureName != null;
        setProcedure(localProcedureName);
        this.codeView.highlightBlockView(blockId);
    }
    
    @Override
    public void clearHighlights() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCodeView();
            }
        });
    }
    
    private void updateWorldSize() {
        setEditable(false);
        UpdateWorldWorker worker = new UpdateWorldWorker(this.domainModel);
        worker.execute();
        // can't call get() or EDT may be blocked, causing freeze
    }
    
    class UpdateWorldWorker extends SwingWorker<Void, Void> {
        private DomainModel workerDomainModel;
        
        public UpdateWorldWorker(final DomainModel aModel) {
            this.workerDomainModel = aModel;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            final int yMin = SimulationCaller.reportInt("min-pycor");
            final int yMax = SimulationCaller.reportInt("max-pycor");
            final int xMin = SimulationCaller.reportInt("min-pxcor");
            final int xMax = SimulationCaller.reportInt("max-pxcor");
            final int patchSize = SimulationCaller.reportInt("patch-size");
            workerDomainModel.updateWorldSize(
                yMin, 
                yMax, 
                xMin, 
                xMax, 
                patchSize
            );
            return null;
        }

        @Override
        protected void done() {
            setEditable(true);
        }
    }
    
    @Override
    public void resetImageFileName() {
        this.domainModel.resetImageFileName();
        SimulationCaller.setImageFileName(this.domainModel.getImageFileName());
        updateWorldSize();
    }
    
    @Override
    public void setImageFileName(final String fileName) {
        if (
            fileName == null
            || !FileExtensionUtility.isImageExtension(
                FileExtensionUtility.getExtension(fileName)
            )) {
            throw new IllegalArgumentException();
        }
        this.domainModel.setImageFileName(fileName);
        SimulationCaller.setImageFileName(fileName);
        updateWorldSize();
    }
    
    @Override
    public void setAgent(final String aAgentName, final boolean mustRun) {
        assert this.domainModel.getAgentTypeNames().
            contains(aAgentName);
        if (
            !mustRun 
            && this.agentName != null 
            && this.agentName.equals(aAgentName)
        ) {
            return;
        }
        
        this.agentName = aAgentName;
        final String formerProcedureName = this.procedureName;
        this.codeView.setCurrentAgentName(aAgentName);
        
        List<String> procedureNames = 
            this.domainModel.getProcedureNamesForAgent(aAgentName);
        this.codeView.setAvailableProcedureNames(procedureNames);
        
        // if the new agent type has the former procedure, display the
        // former procedure for the new agent type. otherwise, display
        // the first procedure in the list for the new agent type.
        if (
            formerProcedureName != null 
            && procedureNames.contains(formerProcedureName)
        ) {
            setProcedure(formerProcedureName);
        } else {
            setProcedure(procedureNames.get(0));
        }
        
        // procedure name might not have changed,
        // but current sequence id will still be different
        UUID sequenceId = 
            this.userModel.getBlockSequence(
                this.agentName, 
                this.procedureName
            ).getId();
        this.codeView.setUserCodeId(sequenceId);
        updatePaletteBlocks();
        updateCodeView();
    }
    
    @Override
    public void setCategory(
        final CategoryViewStyle style, 
        final String name
    ) {
        switch (style) {
        case ALL:
        case BASIC:
            if (this.categoryViewStyle == style) {
                return;
            }
            break;
        case SELECTION:
            if (
                this.categoryViewStyle == style 
                && this.categoryName.equals(name)
            ) {
                return;
            }
            break;
        default:
            throw new IllegalStateException();
        }
       
        this.categoryViewStyle = style;
        this.categoryName = name;
        updatePaletteBlocks();
    }
    
    // This is a special utility method that is used 
    // to create an individual .png of 
    // all the blocks found in the particular unit.
    // We used this when we created the Net Logo Dictionary.  
    
    private void printAllBlocks() {
        assert PRINT_BLOCKS;
        List<String> agentNames = this.domainModel.getAgentTypeNames();
        Set<BlockTemplate> templates = new HashSet<BlockTemplate>();
        for (final String curAgentName: agentNames) {
            List<String> procedureNames = 
                this.domainModel.getProcedureNamesForAgent(curAgentName);
            for (final String curProcedureName: procedureNames) {
                templates.addAll(
                    this.domainModel.getBlockTemplates(
                        curAgentName, 
                        curProcedureName
                    )
                );
            }
        }
        
        for (final BlockTemplate template: templates) {
            final edu.vanderbilt.codecomponentview.BlockView blockView = 
                this.viewFactory.getBlockViewFromTemplate(template, layoutSize);
            final JFrame frame = new JFrame();
            final Container container = frame.getContentPane();
            container.add(blockView);
            frame.setVisible(true);
            frame.pack();
            final BufferedImage image = 
                new BufferedImage(
                    blockView.getWidth(), 
                    blockView.getHeight(), 
                    BufferedImage.TYPE_INT_ARGB
                );
            container.print(image.getGraphics());
            final int cropAmount = BlockView.LARGE_TOP_INSET;
            final BufferedImage croppedImage = image.getSubimage(
                cropAmount, // x
                cropAmount, // y
                image.getWidth() - cropAmount * 2, // width
                image.getHeight() - cropAmount // height
            );
            final String fileName = 
                "blockImages/" + template.getName() + ".png";
            try {
                ImageIO.write(croppedImage, "PNG", new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void setLayoutSize(final LayoutSize aLayoutSize) {
        this.layoutSize = aLayoutSize;
        updatePaletteBlocks();
        updateCodeView();
    }
    
    private void updatePaletteBlocks() {
        List<BlockTemplate> templates = 
            this.domainModel.getBlockTemplates(
                this.agentName, 
                this.procedureName
            );
        List<edu.vanderbilt.codecomponentview.BlockView> blockViews = 
            new ArrayList<edu.vanderbilt.codecomponentview.BlockView>();
        for (BlockTemplate template: templates) {
            if (
                template.getPredefinedBlockType() 
                    == PredefinedBlockType.UPDATE_SET
                && this.domainModel.getSetInstancesOfType(
                    template.getUpdateSetType()
                ).isEmpty()) {
                  continue;
             }
            
            if (this.categoryViewStyle == CategoryViewStyle.BASIC) {
                if (!template.getIsBasic()) {
                    continue;
                }                
            } else if (this.categoryViewStyle == CategoryViewStyle.SELECTION) {
                if (!this.categoryName.equals(template.getCategory())) {
                    continue;
                }
            }
            
            
            blockViews.add(
                this.viewFactory.getBlockViewFromTemplate(template, layoutSize)
            );
        }
        
        this.codeView.setPaletteBlockViews(blockViews);
    }
    
    @Override
    public void setProcedure(final String aProcedureName) {
        assert this.domainModel.getProcedureNamesForAgent(this.agentName).
            contains(aProcedureName);
        
        this.procedureName = aProcedureName;
        this.codeView.setCurrentProcedureName(aProcedureName);
        UUID sequenceId = 
            this.userModel.getBlockSequence(
                this.agentName, 
                this.procedureName
            ).getId();
        this.codeView.setUserCodeId(sequenceId);
        updatePaletteBlocks();
        updateCodeView();
    }
    
    @Override
    public void setEditable(final boolean isEditable) {
        this.codeView.setEditable(isEditable);
    }
    
    @Override
    public void setEnumValue(
        final UUID blockId,
        final String fieldName,
        final String item
    ) {
        Block block = this.userModel.getBlockById(blockId);
        if (block == null) {
            return;
        }
        if (block.getArgument(fieldName).equals(item)) {
            return;
        }
        
        boolean success = block.setArgument(fieldName, item);
        assert success;
        updateCodeView();
        Loader.modelEdited();
    }


    @Override
    public void setNumberValue(
        final UUID blockId, 
        final String fieldName, 
        final String value
    ) {
        Block block = this.userModel.getBlockById(blockId);
        if (block == null) {
            return;
        }
        BlockTemplate template = block.getTemplate();
        ArgumentType argumentType = template.getArgumentType(fieldName);
        assert argumentType != null;
        if (argumentType.getArgumentValueType() == ArgumentValueType.INT) {
            try {
               int intValue = Integer.parseInt(value);
               if (
                   ((Integer) block.getArgument(fieldName)).intValue() 
                   == intValue
               ) {
                   return;
               }
               boolean success = block.setArgument(fieldName, intValue);
               if (!success) {
                   System.out.println("could not update");
               }
            } catch (NumberFormatException e) {
                System.out.println("not an integer");
            }
        } else if (
            argumentType.getArgumentValueType() == ArgumentValueType.REAL
        ) {
            try {
                double doubleValue = Double.parseDouble(value);
                if (
                    ((Double) block.getArgument(fieldName)).doubleValue() 
                    == doubleValue
                ) {
                    return;
                }
                boolean success = block.setArgument(fieldName, doubleValue);
                if (!success) {
                    System.out.println("could not update");
                }
             } catch (NumberFormatException e) {
                 System.out.println("not a double");
             }
         } else {
             throw new IllegalArgumentException();
         }
        
        updateCodeView();
        Loader.modelEdited();
    }
    
    @Override
    public void cancelNewSet() {
        cleanUpSetEditor();
    }
    
    private void cleanUpSetEditor() {
        assert SwingUtilities.isEventDispatchThread();

        // hide buttons in set panel and clear message
        this.setPanel.setAvailableButtons(false, false, false, false, false);
        this.setPanel.setMessage("");
        disableShapeEditorEnableOthers();
        
        DraggingGlassPane pane = DependencyManager.getDependencyManager().
            getObject(DraggingGlassPane.class, "pane");
        // set the frame's glassPane to be the same 
        // glass pane passed in to the user code panel.
        GraphicalInterface.getFrame().setGlassPane(pane);
        pane.setVisible(false);
        this.setName = null;
        this.setTypeName = null;
    }
    
    private void addSetToBlocks() {
        assert SwingUtilities.isEventDispatchThread();
        
        for (
            BlockTemplate blockTemplate
            : this.domainModel.getCallSetBlockTemplates()
        ) {
            blockTemplate.getArgumentType(BlockTemplate.SETS).
                addEnumValue(this.setName);
        }
        
        for (
            BlockTemplate blockTemplate
            : this.domainModel.getUpdateSetBlockTemplates(this.setTypeName)
        ) {
            ArgumentType argType = 
                blockTemplate.getArgumentType(BlockTemplate.SETS);
            argType.addEnumValue(this.setName);
            if (argType.getEnumValues().size() == 1) {
                // the new set is the only option
                argType.setDefaultValue(this.setName);
            }
        }
        
        updateCodeView();
    }
    
    private void removeSetFromBlocks() {
        assert SwingUtilities.isEventDispatchThread();
        for (
            BlockTemplate blockTemplate
            : this.domainModel.getCallSetBlockTemplates()
        ) {
            blockTemplate.getArgumentType(BlockTemplate.SETS).
                removeEnumValue(this.setName);
        }
        
        for (
            BlockTemplate blockTemplate
            : this.domainModel.getUpdateSetBlockTemplates(this.setTypeName)
        ) {
            ArgumentType argType = 
                blockTemplate.getArgumentType(BlockTemplate.SETS);
            argType.removeEnumValue(this.setName);
        }
        
        updateCodeView();
    }
    
    @Override
    public void deleteSet(final String aSetName) {
        assert SwingUtilities.isEventDispatchThread();

        this.setName = aSetName;
        this.setTypeName = this.domainModel.getSetInstance(aSetName).
            getSetTemplate().getName();
        this.domainModel.deleteSetInstance(aSetName);
        SimulationCaller.deleteSet(aSetName);
        SimulationCaller.removeSetFromMeasureWorlds(this.setName);
        
        this.userModel.removeOrphanSetBlocks(this.setName);
        removeSetFromBlocks();
        updatePaletteBlocks(); 
        cleanUpSetEditor();
        this.setPanel.setMessage(
            "<html>Group <b>" + aSetName + "</b> was deleted OK!</html>"
        );
        Loader.modelEdited();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void doneEditingShape() {
        assert SwingUtilities.isEventDispatchThread();
        
        SetTemplate setTemplate = 
            this.domainModel.getSetTemplate(this.setTypeName);
        assert setTemplate.isShape();
        
        ShapedSet shapedSet = setTemplate.getShapedSet();
        SelectGlassPane selectGlassPane = shapedSet.getSelectGlassPane();
        final String checkString = 
            shapedSet.checkShape(
                selectGlassPane.getShapeFromView()
            );
        if ("".equals(checkString)) {
            // shape is valid
            
            List<Double> shape = selectGlassPane.getShapeFromView();
            assert shape != null;
            
            SetInstance toEdit = this.domainModel.getSetInstance(this.setName);
            toEdit.setArgument(BlockTemplate.SHAPE, shape);

            final String editedSetName = this.setName;
            
            cleanUpSetEditor();
            this.setPanel.setMessage(
                "<html>Group <b>" + editedSetName + "</b> was edited OK!</html>"
            );
            Loader.modelEdited();
        } else {
            SetInstance toEdit = this.domainModel.getSetInstance(this.setName);
            selectGlassPane.initializeShape(
                (List<Double>) toEdit.getArgumentValue(BlockTemplate.SHAPE)
            );
            JOptionPane.showMessageDialog(
                GraphicalInterface.getFrame(),
                checkString,
                "Invalid Shape",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    @Override
    public void doneDrawingShape() {
        assert SwingUtilities.isEventDispatchThread();
        
        SetTemplate setTemplate = 
            this.domainModel.getSetTemplate(this.setTypeName);
        assert setTemplate.isShape();
        assert !this.domainModel.isSetNameTaken(this.setName);
        
        ShapedSet shapedSet = setTemplate.getShapedSet();
        SelectGlassPane selectGlassPane = shapedSet.getSelectGlassPane();
        final String checkString = 
            shapedSet.checkShape(
                selectGlassPane.getShapeFromView()
            );
        if ("".equals(checkString)) {
            // shape is valid
            
            List<Double> shape = selectGlassPane.getShapeFromView();
            assert shape != null;
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put(
                BlockTemplate.SHAPE, 
                shape
            );

            this.domainModel.addSetInstance(
                setTemplate, 
                this.setName, 
                arguments
            );
            SimulationCaller.addSet(this.setName);
            SimulationCaller.addSetToMeasureWorlds(this.setName);
            
            addSetToBlocks();
            final String newSetName = this.setName;
            
            // might need to add a new update-set block to palette
            updatePaletteBlocks(); 
            cleanUpSetEditor();
            this.setPanel.setMessage(
                "<html>Group <b>" + newSetName + "</b> was created OK!</html>"
            );
            Loader.modelEdited();
        } else {
            JOptionPane.showMessageDialog(
                GraphicalInterface.getFrame(),
                checkString,
                "Invalid Shape",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void requestEditSetShapeBeforeShapeKnown(final String aSetName) {
        assert SwingUtilities.isEventDispatchThread();
        
        final SetInstance setInstance = 
            this.domainModel.getSetInstance(aSetName);
        final SetTemplate setTemplate = setInstance.getSetTemplate();
        assert setTemplate.isShape();

        enableShapeEditorDisableOthers();
        SelectGlassPane selectGlassPane = 
            setTemplate.getShapedSet().getSelectGlassPane();
        GraphicalInterface.getFrame().setGlassPane(selectGlassPane);
        selectGlassPane.startUp();
        selectGlassPane.initializeShape(
            (List<Double>) setInstance.getArgumentValue(BlockTemplate.SHAPE)
        );
        
        ShapedSet shapedSet = setTemplate.getShapedSet();
        if (shapedSet.canEdit()) {
            // same is editable without redrawing, i.e., is not a path
            
            this.setPanel.setAvailableButtons(
                false, // Done Drawing Shape 
                false, // Cancel New Set
                true, // Done Editing Shape
                shapedSet.canRedraw(), // Redraw Shape
                false // Cancel Redraw Shape
            );        
            
            selectGlassPane.setMode(SelectGlassPane.MODE.EDIT);
        } else {
            // shape is a path: must redraw completely to edit
            
            this.setPanel.setAvailableButtons(
                false, // Done Drawing Shape
                false, // Cancel New Set
                true, // Done Editing Shape
                true, // Redraw Shape
                false // Cancel Redraw Shape
            );
            
            selectGlassPane.setMode(SelectGlassPane.MODE.VIEW);
        }
        
        this.setPanel.setMessage(
            "<html>" + setTemplate.getName() 
            + ": <b>" + aSetName + "</b></html>"
        );
        this.setName = aSetName;
        this.setTypeName = setTemplate.getName();
    }
    
    @Override
    public void requestAddSetBeforeArgsKnown(
        final String aSetTypeName, 
        final String aSetName
    ) {
        assert SwingUtilities.isEventDispatchThread();
        
        SetTemplate setTemplate = this.domainModel.getSetTemplate(aSetTypeName);
        if (setTemplate.isShape()) {
            enableShapeEditorDisableOthers();
            SelectGlassPane selectGlassPane = 
                setTemplate.getShapedSet().getSelectGlassPane();
            GraphicalInterface.getFrame().setGlassPane(selectGlassPane);
            selectGlassPane.startUp();
            selectGlassPane.setMode(SelectGlassPane.MODE.CREATE);
            ShapedSet shapedSet = setTemplate.getShapedSet();
            this.setPanel.setAvailableButtons(
                true, // Done Drawing Shape
                true, // Cancel New Set
                false, // Done Editing Shape
                shapedSet.canRedraw(), // Redraw Shape
                false // Cancel Redraw Shape
            );
            
            this.setPanel.setMessage(
                "<html>" + aSetTypeName + ": <b>" + aSetName + "</b></html>"
            );
            
            JOptionPane.showMessageDialog(
                GraphicalInterface.getFrame(),
                shapedSet.getMessageBody(),
                shapedSet.getMessageTitle(),
                JOptionPane.PLAIN_MESSAGE
            );

            this.setName = aSetName;
            this.setTypeName = aSetTypeName;
        } else {
            assert !this.domainModel.isSetNameTaken(this.setName);
            
            Map<String, Object> arguments = new HashMap<String, Object>();
            for (String argName: setTemplate.getArgumentNames()) {
                Object defaultValue = 
                    setTemplate.getArgumentType(argName).getDefaultValue();
                arguments.put(argName, defaultValue);
            }

            this.domainModel.addSetInstance(
                setTemplate, 
                aSetName, 
                arguments
            );
            SimulationCaller.addSet(aSetName);
            SimulationCaller.addSetToMeasureWorlds(this.setName);

            this.setName = aSetName;
            this.setTypeName = aSetTypeName;
            addSetToBlocks();
            
            // might need to add a new update-set block to palette
            updatePaletteBlocks(); 
            cleanUpSetEditor();
            this.setPanel.setMessage(
                "<html>Group <b>" + aSetName + "</b> was created OK!</html>"
            );
            Loader.modelEdited();
        }
    }
    
    private void enableShapeEditorDisableOthers() {        
        this.codeView.setEditable(false);
        this.runButtonPanel.disbaleAll();
        this.speedSliderPanel.setSliderEnabled(false);
        this.myMenuBar.setAllMenuItemsEnabled(false);
    }
    
    private void disableShapeEditorEnableOthers() {        
        this.codeView.setEditable(true);
        this.runButtonPanel.disableStopEnableOthers();
        this.speedSliderPanel.setSliderEnabled(true);
        this.myMenuBar.setAllMenuItemsEnabled(true);
    }

    @Override
    public void setBlockViewName(final UUID blockViewId, final String name) {
        edu.vanderbilt.codecomponentview.BlockView blockView = 
            this.codeView.getBlockView(blockViewId);
        assert blockView != null;
        blockView.setName(name);
        updateCodeView();
        Loader.modelEdited();
    }


    @Override
    public void insertBlock(
        final UUID blockId,
        final BlockTemplate blockTemplate, 
        final UUID sequenceId, 
        final int index
    ) {
        assert blockId != null && blockTemplate != null && sequenceId != null;
        final BlockSequence targetSequence = 
            this.userModel.getBlockSequenceById(sequenceId);
        assert targetSequence != null;
        
        final Block existingBlock = this.userModel.getBlockById(blockId);
        if (existingBlock == null) {
            final Block newBlock = new Block(blockTemplate);
            targetSequence.insertBlock(newBlock, index);
        } else {
            targetSequence.insertBlock(existingBlock, index);
        }
        Loader.modelEdited();
        updateCodeView();
    }


    @Override
    public void removeBlock(final UUID blockId) {
        assert blockId != null;
        final BlockSequence targetSequence =
            this.userModel.getBlockSequenceByBlockId(blockId);
        
        // keep the removed block temporarily, in case it is added
        // back to the model when dropped. this way, you won't lose
        // its argument values or contained blocks.
        final Block removedBlock = targetSequence.remove(blockId);
        this.userModel.setRemovedBlock(removedBlock);
        Loader.modelEdited();
        updateCodeView();
    }
    
    private void removeNonDefaultSetInstancesFromGrapher() {
        final List<String> setNamesToRemove = new ArrayList<String>();
        for (
            SetInstance currentSetInstance
            : this.domainModel.getSetInstances()
        ) {
            if (!currentSetInstance.getSetTemplate().isDefault()) {
                setNamesToRemove.add(currentSetInstance.getSetName());
            }
        }
        
        for (String currentSetName: setNamesToRemove) {
            SimulationCaller.removeSetFromMeasureWorlds(currentSetName);
        }
    }


    @Override
    public void clear() {
        assert SwingUtilities.isEventDispatchThread();
        
        this.userModel.removeAllUserProcedures();
        this.domainModel.removeUserProcedures();
        
        // must call before removing set instances
        removeNonDefaultSetInstancesFromGrapher(); 
        
        this.domainModel.removeSetInstances();
        
        this.domainModel.resetImageFileName();
        this.userModel.clearBlockSequences();
        setAgent(this.domainModel.getAgentTypeNames().get(0), true);
        setProcedure(
            this.domainModel.getProcedureNamesForAgent(this.agentName).get(0)
        );
        this.setPanel.setMessage("");
        updateCodeView();
    }


    @Override
    public void addUserProcedure(
        final String name, 
        final List<String> agentTypes
    ) {
        this.domainModel.addUserProcedure(name, agentTypes);
        this.userModel.addUserProcedure(name, agentTypes);
        this.codeView.setAvailableAgentNames(
            this.domainModel.getAgentTypeNames()
        );
        this.codeView.setAvailableProcedureNames(
            this.domainModel.getProcedureNamesForAgent(this.agentName)
        );
        this.codeView.setCurrentAgentName(this.agentName);
        this.codeView.setCurrentProcedureName(this.procedureName);
        Loader.modelEdited();
        updatePaletteBlocks();
    }


    @Override
    public void deleteUserProcedure(final String name) {
        this.domainModel.removeUserProcedure(name);
        this.userModel.removeUserProcedure(name);
        this.codeView.setAvailableAgentNames(
            this.domainModel.getAgentTypeNames()
        );
        this.codeView.setAvailableProcedureNames(
            this.domainModel.getProcedureNamesForAgent(this.agentName)
        );
        Loader.modelEdited();
        
        if (this.procedureName.equals(name)) {
            // the deleted procedure is the current procedure,
            // so switch to the first procedure for this agent type
            setProcedure(
                this.domainModel.
                    getProcedureNamesForAgent(this.agentName).get(0)
            );
        }
        
        updatePaletteBlocks();
        
        this.codeView.setCurrentAgentName(this.agentName);
        this.codeView.setCurrentProcedureName(this.procedureName);
        updateCodeView();
    }
    
    void updateCodeView() {
        BlockSequence blocks = this.userModel.getBlockSequence(
            this.agentName, 
            this.procedureName
        );
        final List<edu.vanderbilt.codecomponentview.BlockView> blockViews = 
            new ArrayList<edu.vanderbilt.codecomponentview.BlockView>();
        for (Block block: blocks.getBlocks()) {
            edu.vanderbilt.codecomponentview.BlockView blockView = 
                this.viewFactory.getBlockViewFromBlock(block, layoutSize);
            blockViews.add(blockView);
        }
        this.codeView.setUserBlockViews(blockViews);
        this.proceduresMenu.updateEnabledState();
    }
    
    @Override
    public void cutAll() {
        final BlockSequence toCut = this.userModel.getBlockSequence(
            this.agentName, 
            this.procedureName
        );
        
        this.userModel.setClipboardContents(
            this.agentName, 
            this.procedureName, 
            toCut
        );
        
        toCut.clear();
        Loader.modelEdited();
        updateCodeView();
    }
    
    @Override
    public void copyAll() {
        final BlockSequence toCopy = this.userModel.getBlockSequence(
            this.agentName, 
            this.procedureName
        );
        this.userModel.setClipboardContents(
            this.agentName, 
            this.procedureName, 
            toCopy
        );
    }
    
    @Override
    public void pasteToEnd() {        
        final BlockTemplate illegalBlockType = isPasteLegal();
        if (illegalBlockType == null) {
            final BlockSequence toPaste = this.userModel.getClipboardCode();
            
            BlockSequence targetSequence = this.userModel.getBlockSequence(
                this.agentName, 
                this.procedureName
            );
            final int initialTargetLength = targetSequence.getBlocks().size();
            for (int i = 0; i < toPaste.getBlocks().size(); i++) {
                final Block toAdd = Block.copy(toPaste.getBlocks().get(i));
                targetSequence.insertBlock(toAdd, initialTargetLength + i);
            }
            
            Loader.modelEdited();
            updateCodeView();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   JOptionPane.showMessageDialog(
                       null,
                       "<html>You can't paste a <b>" 
                           + illegalBlockType.getName() 
                           + "</b> block<br>into the <b>" 
                           + getAgentName() + " " + getProcedureName() 
                           + "</b> procedure.</html>",
                       "Paste Error",
                       JOptionPane.ERROR_MESSAGE
                   );
               }
            });
        }
    }
    
    /*
     * Returns an illegal BlockTemplate from the clipboard if a paste would be
     * legal, meaning that all blocks on the clipboard are allowed in
     * the current agent procedure. Returns null if a paste operation is legal.
     */
    private BlockTemplate isPasteLegal() {
        final BlockSequence toPaste = this.userModel.getClipboardCode();
        
        for (Block block: toPaste.getBlocksAndDescendantBlocks()) {
            if (!isLegalBlockTemplate(block.getTemplate())) {
                return block.getTemplate();
            }
        }
        
        return null;
    }
    
    /*
     * Returns true if the given block template is available in the current
     * agent procedure.
     */
    private boolean isLegalBlockTemplate(final BlockTemplate blockTemplate) {
        final List<BlockTemplate> legalTemplates = 
            this.domainModel.getBlockTemplates(
                this.agentName, 
                this.procedureName
            );
        
        return legalTemplates.contains(blockTemplate);
    }
    
    String getAgentName() {
        return this.agentName;
    }
    
    String getProcedureName() {
        return this.procedureName;
    }
}
