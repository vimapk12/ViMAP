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


package edu.vanderbilt.runcontroller;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import edu.vanderbilt.codecontroller.MasterCodeController;
import edu.vanderbilt.codeview.MasterCodeView;
import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.domainmodel.PredefinedBlockType;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.driverandlayout.MyMenuBar;
import edu.vanderbilt.runmodel.GlobalEnvironment;
import edu.vanderbilt.runmodel.LocalEnvironment;
import edu.vanderbilt.runview.RunButtonPanel;
import edu.vanderbilt.sets.SetInstance;
import edu.vanderbilt.simulation.SimulationCaller;
import edu.vanderbilt.usermodel.Block;
import edu.vanderbilt.usermodel.BlockSequence;
import edu.vanderbilt.usermodel.UserModel;
import edu.vanderbilt.util.Util;

public final class DefaultRunController implements RunController {
    
    private static enum State {
        GET_WHO_NUMBERS,
        STEP,
        AFTER_RUN
    }
    
    private static enum SetupState {
        BEFORE,
        DURING,
        AFTER
    }
    
    private AtomicBoolean isRunning;
    
    private State state;
    
    private SetupState setupState;
    
    private final AtomicBoolean continueRun;
    
    private GlobalEnvironment globalEnvironment;
    
    private ExecutorService executor;
    
    private MasterCodeView codeView;

    private RunButtonPanel runButtonPanel;
    
    private MyMenuBar myMenuBar;
    
    private boolean runGo;
    
    private int cyclesLeft;
    
    private boolean forever;
    
    private boolean highlight;
    
    private UserModel userModel;
    
    private DomainModel domainModel;
    
    private MasterCodeController codeController;
    
    private boolean isWaiting;
    
    private Block previousBlockForHighlight;
    
    private final TimerScheduler timerScheduler = new TimerScheduler();
    
    private final Runnable laterContinueTask = new Runnable() {
        @Override
        public void run() {
            getExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    continueRun();
                }
            });
        }
    };
    
    public DefaultRunController() {
        this.continueRun = new AtomicBoolean(true);
        this.state = State.GET_WHO_NUMBERS;
        this.executor = Executors.newSingleThreadExecutor();
        this.setupState = SetupState.BEFORE;
        this.isRunning = new AtomicBoolean(false);
        this.globalEnvironment = new GlobalEnvironment();
        this.isWaiting = false;
        this.previousBlockForHighlight = null;
    }
    
    @Override
    public void init() {
        DependencyManager dep = DependencyManager.getDependencyManager();
        this.codeView = 
            dep.getObject(MasterCodeView.class, "masterCodeView");
        this.runButtonPanel = 
            dep.getObject(RunButtonPanel.class, "runControls");
        this.userModel = dep.getObject(UserModel.class, "userModel");
        this.domainModel = dep.getObject(DomainModel.class, "domainModel");
        this.codeController = 
            dep.getObject(MasterCodeController.class, "controller");
        this.myMenuBar = dep.getObject(MyMenuBar.class, "menuBar");
    }

    @Override
    public void run(
        final boolean aRunSetup, 
        final boolean aRunGo, 
        final boolean aHighlight,
        final int cycles,
        final boolean isForever
    ) {
        if (this.isRunning.get()) {
            return;
        }
                
        Util.printIfDebugging(
            "setup: " + aRunSetup + " go: " + aRunGo 
            + " highlight: " + aHighlight + " cycles: " + cycles
        );
                
        this.isRunning.set(true);
        this.continueRun.set(true);
        this.codeView.setEditable(false);
        this.runButtonPanel.enableStopDisableOthers();
        this.myMenuBar.setAllMenuItemsEnabled(false);
        this.previousBlockForHighlight = null;
        
        this.cyclesLeft = cycles;
        this.forever = isForever;
        this.highlight = aHighlight;
        
        this.runGo = aRunGo;
        
        if (aRunSetup) {
            this.setupState = SetupState.BEFORE;
        } else {
            this.setupState = SetupState.AFTER;
        }
        
        this.state = State.GET_WHO_NUMBERS;
        
        if (!aRunGo && !aRunSetup) {
            // reset was called, but user code should not run
            SimulationCaller.runResetCommand(
                this.laterContinueTask, 
                this.domainModel.getSetInstanceNames()
            );
        } else {
            this.laterContinueTask.run();
        }
    }
    
    @Override
    public void stop() {
        if (!this.isRunning.get()) {
            return;
        }
        
        this.continueRun.set(false);
    }

    @Override
    public void setDelayPerBlockInMillis(final int delayInMillis) {
        this.globalEnvironment.setDelayInMillis(delayInMillis);
    }
    
    void continueRun() {
        if (this.continueRun.get()) {
            Util.printIfDebugging("continue > do continue");
            doContinueRun();
        } else {
            Util.printIfDebugging("continue > cleanup");
            cleanupRun();
        }
    }
    
    GlobalEnvironment getGlobalEnvironment() {
        return this.globalEnvironment;
    }
    
    ExecutorService getExecutor() {
        return this.executor;
    }
    
    Runnable getLaterContinueTask() {
        return this.laterContinueTask;
    }
    
    private void advanceSetupState() {
        switch (this.setupState) {
        case BEFORE:
            this.setupState = SetupState.DURING;
            Util.printIfDebugging("new setup state: during");
            break;
        case DURING:
            Util.printIfDebugging("new setup state: after");
            this.setupState = SetupState.AFTER;
            this.state = State.GET_WHO_NUMBERS;
            break;
        default:    
            throw new IllegalStateException();
        }
    }
    
    private void advanceRunState() {
        switch (this.state) {
        case GET_WHO_NUMBERS:
            Util.printIfDebugging("new state: step");
            this.state = State.STEP;
            break;
        case STEP:
            Util.printIfDebugging("new state: after");
            
            if (this.highlight) {
                this.codeController.clearHighlights();
            }
            this.previousBlockForHighlight = null;
            this.state = State.AFTER_RUN;
            break;
        default:   
            throw new IllegalStateException();
        }
    }
    
    private void doContinueRun() {
        if (this.setupState == SetupState.BEFORE) {
            Util.printIfDebugging("do continue > start setup");
            advanceSetupState();
            SimulationCaller.runResetCommand(
                this.laterContinueTask, 
                this.domainModel.getSetInstanceNames()
            );
            return;
        }
        
        if (this.state == State.GET_WHO_NUMBERS) {
            if (this.setupState == SetupState.AFTER && !this.runGo) {
                Util.printIfDebugging("don't run go");
                cleanupRun();
                return;
            }
            Util.printIfDebugging("do continue > get who");
            getWhoNumbers();
            
            // check if the "last-cycle" flag has been set in NetLogo
            // during the previous run cycle
            final boolean shouldStop = 
                SimulationCaller.reportBooleanNoPrefix("last-cycle");
            if (shouldStop) {
                this.state = State.AFTER_RUN;
                this.continueRun.set(false);
            } else {
                SimulationCaller.setupCycle();
                advanceRunState();
                return;
            }
        }
        
        if (this.state == State.STEP) {
            Util.printIfDebugging("do continue > step");
            doStep();
            return;
        }
        
        assert this.state == State.AFTER_RUN;
        SimulationCaller.takedownCycle();
        SimulationCaller.doTickInEmbodimentWorld();
        SimulationCaller.updateAllMeasureWorlds();
        if (!this.forever) {
            this.cyclesLeft--;
        }
        if (this.runGo && (this.forever || this.cyclesLeft > 0)) {        
            Util.printIfDebugging("do continue > run again");
            this.state = State.GET_WHO_NUMBERS;
            this.laterContinueTask.run();
            return;
        }
        
        Util.printIfDebugging("do continue > clean up");
        cleanupRun();
    }
    
    private void getWhoNumbers() {
        Util.printIfDebugging("getting who numbers");
        this.globalEnvironment.resetAgentIndex();
        
        SimulationCaller.reportStringWithCallback(
            getWhoMapReporter(),
            new Report() {
                @Override
                public void reportAndContinue(final Object message) {
                    getGlobalEnvironment().
                        setAgents(getAgentMap((String) message));
                    getExecutor().submit(getLaterContinueTask());
                }
        });
    }
    
    private String getWhoMapReporter() {
        Collection<String> agentTypeNames = 
            this.domainModel.getAgentTypeNames();

        StringBuilder builder = new StringBuilder();
        builder.append("(list");
        for (String currentAgentTypeName: agentTypeNames) {
            builder.
                append(" \"").
                append(currentAgentTypeName).
                append(
                    "\" (sort [who] of wabbits with [agent-kind-string = \""
                ).
                append(currentAgentTypeName).
                append("\" ])");
        }
        builder.append(")"); 
        return builder.toString();
    }
    
    /**
     * Called by getWhoNumbers(). Returns a Map from who numbers to AgentTypes, 
     * when passed a return string from NetLogo containing the data.
     * 
     * expected String format:
     * [wolves, [1.0, 2.0, 3.0, 4.0, 5.0], sheep, [6.0, 7.0, 8.0]]
     * AgentType names in quotes, followed by their list of who numbers in 
     * brackets delimited by spaces.
     * 
     * @param agentString the coded data string obtained from NetLogo, in the
     * format specified above.
     * 
     * @return a map from agent "who" number to AgentType, for all active
     * agents in the model.
     */
    Map<Integer, String> getAgentMap(final String agentString) {
        Map<Integer, String> resultMap = new HashMap<Integer, String>();

        // for each AgentType in the model, find its associated list
        for (
            final String agentTypeName
            : this.domainModel.getAgentTypeNames()
        ) {
            
            // nameIndex holds the index of the AgentType's name 
            // in the returned String.
            final int nameIndex = agentString.indexOf(agentTypeName);
            // every AgentType name should be present.
            if (nameIndex < 0) {
                throw new IllegalStateException();
            }
            
            // index of the open bracket at the start of the current 
            // AgentType's list of who numbers
            final int openBracketIndex = agentString.indexOf('[', nameIndex);
            // index of the close bracket at the end of the current AgentType's 
            // list of who numbers
            final int closeBracketIndex = 
                agentString.indexOf(']', openBracketIndex);
            
            // if the list is not empty
            if (openBracketIndex + 1 < closeBracketIndex) {
                // listSubstring contains all characters between the open and 
                // close brackets (excluding the brackets)
                final String listSubstring = 
                    agentString.substring(
                        openBracketIndex + 1, closeBracketIndex
                    );
                final String[] whoNumbers = listSubstring.split(",");
                for (int i = 0; i < whoNumbers.length; i++) {
                    resultMap.put(
                        getTurtleNumber(whoNumbers[i]), 
                        agentTypeName
                    );
                }
            }   
        }
        
        return resultMap;
    }
    
    /**
     * Returns the "who" number of a turtle as in int, trimming off the
     * "turtle " prefix if present, an casting from double to int
     * if required. Useful for gettin the "who" number from a returned
     * value from NetLogo, which could take a variety of forms.
     * 
     * @param input a string in the form "turtle X", "turtle X.0", "X", or "X.0"
     * @return the "who" number as an int
     */
    private int getTurtleNumber(final String input) {
        String trimmedInput = input;
        
        if (input.contains(" ")) {
            final int spaceIndex = input.indexOf(" ") + 1;
            trimmedInput = trimmedInput.substring(spaceIndex);
        }
        
        return (int) Double.parseDouble(trimmedInput);
    }
    
    private void doStep() {
        if (!this.globalEnvironment.hasCurrentAgent()) {
            Util.printIfDebugging("all agents ran at doStep");
            if (this.isWaiting) {
                Util.printIfDebugging("waiting already");
                this.isWaiting = false;
            } else {
                Util.printIfDebugging("will schedule timer");
                highlightIfNeeded();
                this.isWaiting = true;
                // new TimerScheduler();
                this.timerScheduler.scheduleNewContinue();
                return;
            }
            
            // all agents have run setup or go method
            if (this.setupState == SetupState.DURING) {
                Util.printIfDebugging("finished setup");
                // finished running setup
                advanceSetupState();
            } else {
                Util.printIfDebugging("finished go");
                // finished running go
                advanceRunState();
            }
            this.laterContinueTask.run();
            return;
        }

        // continue calling agents to run
        Util.printIfDebugging("still more agents 1");
        if (
            this.globalEnvironment.isStackEmpty()
            || this.globalEnvironment.isDead(
               this.globalEnvironment.getCurrentWhoNumber()
            ) 
        ) {
            Util.printIfDebugging("next agent");
            // all code has run for the current agent, 
            // so proceed to the next agent
            this.globalEnvironment.clearStackForNextAgent();
            jumpToCurrentProcedureForNextAgent();
            return;
        }
        
        Util.printIfDebugging("still more code");
        // else there is still a LocalEnvironment on the stack, 
        // so continue with the current agent
        final LocalEnvironment localEnv = this.globalEnvironment.peek();
        
        if (localEnv.isRepeatCounterNegative()) {
            Util.printIfDebugging("enough repeats done");
            // if you've already run the code for this LocalEnvironment 
            // enough times, pop the stack and go on
            this.globalEnvironment.popAndDelete();
            
            String calledSet = this.globalEnvironment.getCalledSetName();
            if (calledSet == null) {
                calledSet = "";
            }
            final String command = "set called-set-name \"" + calledSet + "\"";
            Util.printIfDebugging("return from called set: " + command);
            SimulationCaller.runCommandWithCallback(
                command, 
                this.laterContinueTask
            );
            
            return;
        }
        
        // else you have not run the code list enough times, 
        // so continue with the LocalEnvironment
        

        if (localEnv.isProgramCounterTooHigh()) {          
            Util.printIfDebugging("end of sequence");
            // if you have reached the end of the code list, 
            // go back to the beginning and decrement the 
            // repeat counter
            
            localEnv.zeroProgramCounter();
            localEnv.decrementRepeatCounter();
            this.laterContinueTask.run();
            return;
        }
        
        Util.printIfDebugging("run block");
        final Block currentBlock = localEnv.getCurrentBlock();
        runBlock(currentBlock);
    }
    
    private void runBlock(final Block block) {            
        // evaluate the block's code
        Util.printIfDebugging("running block: " + block);
        if (this.isWaiting) {
            this.isWaiting = false;
        } else {
            highlightIfNeeded();
            this.previousBlockForHighlight = block;
            this.isWaiting = true;
            // new TimerScheduler();
            this.timerScheduler.scheduleNewContinue();
            return;
        }

        BlockTemplate template = block.getTemplate();
        
        switch (template.getPredefinedBlockType()) {
            case CALL_SET:
                runCallSet(block);  
                break;
            case DEFAULT:
                runSimpleBlock(block);
                break;
            case IF_ELSE:
                runIfElseBlock(block);
                break;
            case PROCEDURE_CALL:
                runUserProcedure(block);
                break;
            case REPEAT:
                runRepeatBlock(block);
                break;
            case UPDATE_SET:
                runUpdateSet(block);  
                break;
            case SET_IF_ELSE:
                runSetIfElseBlock(block);
                break;
            case IF_ELSE_COMP_INT:
                runIfElseCompInt(block);
                break;
            case IF_ELSE_COMP_TWO_VARS:
                runIfElseCompTwoVars(block);
                break;
            default:
                throw new IllegalStateException();
        }
    }
    
    private void highlightIfNeeded() {
        if (this.highlight && (this.previousBlockForHighlight != null)) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                   @Override
                   public void run() {
                       getCodeController().showAndHighlightBlock(
                           getPreviousBlockForHighlight().getId()
                       );
                   }
                });
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    MasterCodeController getCodeController() {
        return this.codeController;
    }
    
    Block getPreviousBlockForHighlight() {
        return this.previousBlockForHighlight;
    }

    
    private void runUserProcedure(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.PROCEDURE_CALL;
        
        final String procedureName = block.getTemplate().getName();
        BlockSequence sequence = this.userModel.getBlockSequence(
            this.globalEnvironment.getCurrentAgentTypeName(), 
            procedureName
        );
        advanceProgramCounter();
        
        assert sequence != null;
        jump(
            sequence.getBlocks(), 
            0, 
            this.globalEnvironment.getCurrentWhoNumber()
        );
    }

    private void runUpdateSet(final Block block) {
        assert block.getTemplate().getPredefinedBlockType()
            == PredefinedBlockType.UPDATE_SET;
        final String setName = (String) block.getArgument(BlockTemplate.SETS);
        advanceProgramCounter();
        
        StringBuilder sb = new StringBuilder();
        if (this.globalEnvironment.getShouldUnhighlightSet()) {
            sb.append("ask followers [unhighlight] ");
            this.globalEnvironment.setShouldUnhighlightSet(false);
        }
        
        sb.append("java-Update-set-").
            append(block.getTemplate().getUpdateSetType());
        sb.append(" \"").append(setName).append("\" ");
        
        final SetInstance setInstance = 
            this.domainModel.getSetInstance(setName);
        if (setInstance.getSetTemplate().isShape()) {
            @SuppressWarnings("unchecked")
            List<Double> shape = 
                (List<Double>) setInstance.
                    getArgumentValue(BlockTemplate.SHAPE);
            
            assert shape != null;
            sb.append("[");
            boolean isXCor = true;
            for (Double value: shape) {
                if (isXCor) {
                    sb.append(getNetLogoXcor(value));
                } else {
                    sb.append(getNetLogoYcor(value));
                }
                isXCor = !isXCor;                
                sb.append(" ");
            }
            sb.append("]");
        } else if (setInstance.getSetTemplate().isUserDefined()) {
            for (String argumentName: block.getTemplate().getArgumentNames()) {
                if (argumentName.equals(BlockTemplate.SETS)) {
                    // don't repeat the name of the set
                    continue;
                }
                sb.append(" ");
                sb.append(Util.quoteIfNotANumber(
                    block.getArgument(argumentName).toString()
                ));
            }
            sb.append(" ");
        } else {
            throw new IllegalArgumentException();
        }
        
        if (setName.equals(DomainModel.ALL_SET_STRING)) {
            // call the default set of all followers
            sb.append("ask followers [highlight]");
        } else if (setName.equals(DomainModel.OTHER_SET_STRING)) {
            // call the default set "other": 
            // all followers not in any defined set
            sb.append("ask other-agents [highlight]");
        } else {
            // call a specific agent set
            sb.append("ask [agents] of agent-set \"").
                append(setName).
                append("\" [highlight]");
        }
         
        this.globalEnvironment.setShouldUnhighlightSet(true);
        
        Util.printIfDebugging("command: " + sb.toString());
        SimulationCaller.runCommandWithCallback(
            sb.toString(), 
            this.laterContinueTask
        );
    }
    
    private double getNetLogoXcor(final double viewXCor) {
        return this.domainModel.getXMin() 
            + viewXCor / this.domainModel.getPatchSize();
    }
    
    private double getNetLogoYcor(final double viewYCorFromTop) {
        return this.domainModel.getYMax() 
            - viewYCorFromTop / this.domainModel.getPatchSize();
    }
    
    private void runCallSet(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.CALL_SET;
        
        final String calledSetName = 
            (String) block.getArgument(BlockTemplate.SETS);
        advanceProgramCounter();
        jumpForCalledSet(
            block.getBlockSequences().get(0).getBlocks(),
            0,
            this.globalEnvironment.getCurrentWhoNumber(),
            calledSetName
        );
    }
    
    /*
     * NB: Repeat blocks will always run in the top-level agent's context,
     * never in the context of a called set. This means that a repeat block
     * nested inside a call-set block will be run repeatedly by the controller
     * agent, not the individual followers; the controller agent will iterate
     * normally (and repeatedly) over the nested instructions, but delegate 
     * their actual execution each time to its follower agent set.
     */
    private void runRepeatBlock(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.REPEAT;
        
        final int repeatTimes = 
            (Integer) block.getArgument(BlockTemplate.TIMES);
        advanceProgramCounter();
        jump(
            block.getBlockSequences().get(0).getBlocks(), 
            repeatTimes - 1, 
            this.globalEnvironment.getCurrentWhoNumber()
        );
    }
    
    private void runSetIfElseBlock(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.SET_IF_ELSE;
        
        final String condition = 
            block.getArgument(BlockTemplate.PREDICATES).toString();
    
        if (!this.globalEnvironment.peek().getHasRunSetIf()) {
            this.globalEnvironment.peek().setHasRunSetIf(true);

            // push the "condition" string 
            // onto the GlobalEnvironment's conditionStack
            jump(
                block.getBlockSequences().get(0).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber(),
                condition
            );
        } else {
            this.globalEnvironment.peek().setHasRunSetIf(false);
            advanceProgramCounter();

            // push the "not" of the "condition" string 
            // onto the GlobalEnvironment's conditionStack
            jump(
                block.getBlockSequences().get(1).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber(),
                complement(condition)
            );
        } 
    }
    
    private String getNetLogoComparator(final String comparator) {
        if (
            comparator.equals("<=")
            || comparator.equals(">=")
            || comparator.equals("=")
        ) {
            return comparator;
        }
        
        if (comparator.equals("less than")) {
            return "<";
        }
        if (comparator.equals("greater than")) {
            return ">";
        }
        if (comparator.equals("less than or equal to")) {
            return "<=";
        }
        if (comparator.equals("greater than or equal to")) {
            return ">=";
        }
        if (
            comparator.equals("equals")
            || comparator.equals("equal to")
        ) {
            return "=";
        }
        
        throw new IllegalArgumentException("Unknown comparator: " + comparator);
    }
    
    
    private void runIfElseCompInt(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.IF_ELSE_COMP_INT;
        
        final String javaPrefix = "java-";
        final int who = this.globalEnvironment.getCurrentWhoNumber();
        final String leftVarReporter = 
            javaPrefix + block.getArgument(BlockTemplate.LEFT_VAR).toString() 
                + " " + who;
        final String comparator =
            block.getArgument(BlockTemplate.COMPARATOR).toString();
        final int currentInt = 
            (Integer) block.getArgument(BlockTemplate.RIGHT_INT);
        final String reporter = 
            leftVarReporter + " " 
            + getNetLogoComparator(comparator) + " " + currentInt;
        
        final boolean result = 
            SimulationCaller.reportBooleanNoPrefix(reporter);
        advanceProgramCounter();
        if (result) {
            jump(
                block.getBlockSequences().get(0).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        } else {
            jump(
                block.getBlockSequences().get(1).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        }
    }
    
    
    private void runIfElseCompTwoVars(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.IF_ELSE_COMP_TWO_VARS;
        
        final String javaPrefix = "java-";
        final int who = this.globalEnvironment.getCurrentWhoNumber();
        final String leftVarReporter = 
            javaPrefix + block.getArgument(BlockTemplate.LEFT_VAR).toString() 
                + " " + who;
        final String comparator =
            block.getArgument(BlockTemplate.COMPARATOR).toString();
        final String rightVarReporter =
            javaPrefix + block.getArgument(BlockTemplate.RIGHT_VAR).toString() 
                + " " + who;
        final String reporter = 
            leftVarReporter + " " 
            + getNetLogoComparator(comparator) + " " + rightVarReporter;
        
        final boolean result = 
            SimulationCaller.reportBooleanNoPrefix(reporter);
        advanceProgramCounter();
        if (result) {
            jump(
                block.getBlockSequences().get(0).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        } else {
            jump(
                block.getBlockSequences().get(1).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        }
    }
    
    /*
     * NB: If-else blocks will always run in the top-level agent's
     * context, never in the context of a called set. This means that
     * an if-else block may be nested inside a call-set block; the top-level
     * agent running the code will run the if-else block from its own 
     * perspective, then take one branch, then call its followers to 
     * run the individual statements in that branch.
     */
    private void runIfElseBlock(final Block block) {
        assert block.getTemplate().getPredefinedBlockType() 
            == PredefinedBlockType.IF_ELSE;
        
        final String reporter = 
            block.getArgument(BlockTemplate.PREDICATES).toString();

        final boolean result = SimulationCaller.reportBoolean(
            reporter, 
            this.globalEnvironment.getCurrentWhoNumber()
        );
        advanceProgramCounter();
        if (result) {
            jump(
                block.getBlockSequences().get(0).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        } else {
            jump(
                block.getBlockSequences().get(1).getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );
        }
    }
 
    private void runSimpleBlock(final Block block) {
        StringBuilder sb = new StringBuilder();
        
        if (this.globalEnvironment.getShouldUnhighlightSet()) {
            sb.append("ask followers [unhighlight] ");
            this.globalEnvironment.setShouldUnhighlightSet(false);
        }
        
        if (
            block.getTemplate().getIsObserver()
            || block.getTemplate().getIsSetUpdate()
        ) {
            sb.append("java-").append(block.getTemplate().getName());
            for (String argumentName: block.getTemplate().getArgumentNames()) {
                sb.append(" ");
                sb.append(Util.quoteIfNotANumber(
                    block.getArgument(argumentName).toString()
                ));
            }
        } else {
            // handle the case where this block is within a call-set block.
            // if so, each simple block will be run by the called set.
            final String calledSetName = 
                this.globalEnvironment.getCalledSetName();
            if (calledSetName == null) {
                // no called set
                sb.append("ask turtles with [who = ").
                    append(this.globalEnvironment.getCurrentWhoNumber()).
                    append(" ]");
            } else {
                // call the current called-set
                
                if (calledSetName.equals(DomainModel.ALL_SET_STRING)) {
                    // call the default set of all followers
                    sb.append("ask followers ");
                } else if (calledSetName.equals(DomainModel.OTHER_SET_STRING)) {
                    // call the default set "other": 
                    // all followers not in any defined set
                    sb.append("ask other-agents ");
                } else {
                    // call a specific agent set
                    sb.append(" if agent-set \"").append(calledSetName).
                        append("\" != false [").
                        append(" ask [agents] of agent-set \"").
                        append(calledSetName).
                        append("\" ");
                }
                
                // add condition on agents to respond, if any
                
                if (!this.globalEnvironment.getNonNullConditions().isEmpty()) {
                    sb.append(" with [");
                    sb.append(
                        unionConditions(
                            this.globalEnvironment.getNonNullConditions()
                        )
                    );
                    sb.append("] ");
                }
            }
            
            sb.append("[ java-").append(block.getTemplate().getName());
            for (String argumentName: block.getTemplate().getArgumentNames()) {
                sb.append(" ");
                sb.append(Util.quoteIfNotANumber(
                    block.getArgument(argumentName).toString()
                ));
            }
            sb.append(" ] ");
            
            // for sets that may be uninitialized, add closing brace for if
            if (calledSetName != null 
                && !calledSetName.equals(DomainModel.ALL_SET_STRING)
                && !calledSetName.equals(DomainModel.OTHER_SET_STRING)
            ) {
                sb.append("]");
            }
        }

        advanceProgramCounter();
        Util.printIfDebugging("command: " + sb.toString());
        SimulationCaller.runCommandWithCallback(
            sb.toString(), 
            this.laterContinueTask
        );
    }
    
    
    private void jumpToCurrentProcedureForNextAgent() {
        
        // move on to the next agent, if any
        this.globalEnvironment.incrementAgentIndex();
        
        // if there is another agent left that has not run, 
        // set up the stack for that agent
        if (this.globalEnvironment.hasCurrentAgent()) {
            Util.printIfDebugging("still more agents 2");
            String agentTypeName = 
                this.globalEnvironment.getCurrentAgentTypeName();

            BlockSequence blockSequence;
            if (this.setupState == SetupState.DURING) {
                blockSequence = 
                    this.userModel.getSetupBlockSequece(agentTypeName);
            } else {
                blockSequence = 
                    this.userModel.getGoBlockSequence(agentTypeName);
            }
         
            Util.printIfDebugging(
                "run: " + blockSequence.getBlocks() + " agent: " 
                + this.globalEnvironment.getCurrentWhoNumber()
            );
            // continue run from the new stack
            jump(
                blockSequence.getBlocks(), 
                0, 
                this.globalEnvironment.getCurrentWhoNumber()
            );   
        } else {
            Util.printIfDebugging("all agents ran at jumpToCurrent");
            // else all agents have run their code
            
            // continue, and let the run controller advance 
            // the run to after-step
            this.laterContinueTask.run();
        }
    }
    
    private String complement(final String condition) {
        return "not " + condition;
    }
    
    private String unionConditions(final List<String> conditions) {
        if (conditions.isEmpty()) {
            return "true";
        }
        if (conditions.size() == 1) {
            return conditions.get(0);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i).equals(LocalEnvironment.NULL_CONDITION)) {
                throw new IllegalArgumentException();
            }
            
            if (i == 0) {
                builder.append("(").append(conditions.get(i)).append(")");
            } else {
                builder.append(" and (").append(conditions.get(i)).append(")");
            }
        }
        return builder.toString();
    }
    
    private void advanceProgramCounter() {
        final LocalEnvironment localEnv = this.globalEnvironment.peek();
        localEnv.incrementProgramCounter();
    }
    
    private void jump(
        final List<Block> blocks,
        final int repeatCount,
        final int agentWhoNumber
    ) {
        jump(
            blocks, 
            repeatCount, 
            agentWhoNumber, 
            LocalEnvironment.NULL_CONDITION
        );
    }
    
    private void jumpForCalledSet(
        final List<Block> blocks,
        final int repeatCount,
        final int agentWhoNumber,
        final String calledSetName
    ) {
        jumpForCalledSet(
            blocks, 
            repeatCount, 
            agentWhoNumber, 
            calledSetName, 
            LocalEnvironment.NULL_CONDITION
        );
    }
    
    private void jump(
        final List<Block> blocks,
        final int repeatCount,
        final int agentWhoNumber,
        final String condition
   ) {        
        // push the new LocalEnvironment onto the stack
        this.globalEnvironment.push(
            new LocalEnvironment(
                blocks, 
                repeatCount, 
                agentWhoNumber,
                condition
            ) 
       );
        // when stepping into, there will be never be a maintained 
        // return value in the global "register."
        this.laterContinueTask.run();
    }
    
    private void jumpForCalledSet(
        final List<Block> blocks,
        final int repeatCount,
        final int agentWhoNumber,
        final String calledSetName,
        final String condition
   ) {        
        // push the new LocalEnvironment onto the stack
        this.globalEnvironment.push(
            new LocalEnvironment(
                blocks, 
                repeatCount, 
                agentWhoNumber,
                calledSetName,
                condition
            ) 
       );
        
        String calledSet = this.globalEnvironment.getCalledSetName();
        if (calledSet == null) {
            calledSet = "";
        }
        final String command = "set called-set-name \"" + calledSet + "\"";
        Util.printIfDebugging("jump for called set: " + command);
        SimulationCaller.runCommandWithCallback(
            command, 
            this.laterContinueTask
        );
    
        // when stepping into, there will be never be a maintained 
        // return value in the global "register."
    }
    
    private void cleanupRun() {
        if (this.globalEnvironment.getShouldUnhighlightSet()) {
            SimulationCaller.runCommandWithCallback(
                "ask followers[unhighlight]", 
                null
            );
            this.globalEnvironment.setShouldUnhighlightSet(false);
        }
        
        this.state = State.GET_WHO_NUMBERS;
        this.codeView.setEditable(true);
        this.runButtonPanel.disableStopEnableOthers();
        this.myMenuBar.setAllMenuItemsEnabled(true);
        this.isRunning.set(false);
        this.codeController.clearHighlights();
        this.globalEnvironment.clearStackForNextAgent();
    }
    
    private class TimerScheduler {
    	
    	private final Timer timer;
        /**
         * Constructor.
         */
        public TimerScheduler() {
            this.timer = new Timer();
        }
        
        public void scheduleNewContinue() {
        	timer.schedule(
                new TimerJob(), 
                getGlobalEnvironment().getDelayInMillis()
            );
        }
        
        /**
         * Runs its run() method after timer expires.
         */
        class TimerJob extends TimerTask {
            @Override
            public void run() {
                getLaterContinueTask().run();
            }
        } 
    }
}
