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


// MEASURE CODE
    
    public static void openMeasureWorld(final String baseMeasureFileName) {
        assert !SwingUtilities.isEventDispatchThread();
        String[] agentNames = {};
        String[] measureOptionCommands = {};
        String[] varNames = {};
        List<String[]> measureOptionStringLists = new ArrayList<String[]>();
        if (measureWorlds == null) {
            measureWorlds = new Vector<MeasureWorld>();
            try {
                String types = (String) 
                    comp.report("get-agent-kinds-as-csv");
                agentNames = types.split(",");
                
                String measureOptions = (String) 
                    comp.report("get-list-as-csv measure-option-command-list");
                measureOptionCommands = measureOptions.split(",");
         
                int length = Integer.parseInt((String) 
                    comp.report("list-length measure-option-string-lists"));
                for (int i = 0; i < length; i++) {
                    String tempMeasureText = (String) 
                        comp.report(
                            "get-list-as-csv (item " 
                            + i 
                            + " measure-option-string-lists)"
                        );
                    String[] tempArray = tempMeasureText.split(",");
                    measureOptionStringLists.add(tempArray);
                }
                
                String names = 
                    (String) comp.report("get-list-as-csv var-name-list");
                varNames = names.split(",");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
        
        final String[] finalAgentNames = agentNames;  
        final List<MeasureOption> measureOptions = 
            new ArrayList<MeasureOption>();
        for (int i = 0; i < measureOptionCommands.length; i++) {
            measureOptions.add(
                new MeasureOption(
                    measureOptionStringLists.get(i), 
                    measureOptionCommands[i]
                )
            );
        }
        final String[] finalVarNames = varNames;
        final int maxSize = 2;
        if (measureWorlds.size() <= maxSize) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                   @Override
                   public void run() {
                       // must be run on EDT, because it
                       // constructs a Swing component.
                       OneWorldContainer owc = 
                           new OneWorldContainer(
                               "/" + baseMeasureFileName, 
                               finalAgentNames, 
                               getMeasureWorlds().size() + 1,
                               measureOptions,
                               getComp()
                           );
                       getMeasureWorlds().add(owc.getMeasureWorld());
                   }
                });
                measureWorlds.get(measureWorlds.size() - 1).
                    setupUIOffEDT(finalVarNames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void openMeasureWorldSuite(
        final String baseMeasureFileName, 
        final int stackof,
        final boolean extra
    ) {
        assert !SwingUtilities.isEventDispatchThread();
        String[] agentNames = {};
        String[] measureOptionCommands = {};
        List<String[]> measureOptionStringLists = new ArrayList<String[]>();
        String[] varNames = {};
        boolean canHighlightAgents = false;
        final String smallerMeasureModelFile = "/" 
            + "Smaller" + baseMeasureFileName; 
        if (measureWorlds == null) {
            measureWorlds = new Vector<MeasureWorld>();
            try {
                String types = (String) 
                    comp.report("get-agent-kinds-as-csv");
                agentNames = types.split(",");
                
                String measureOptions = (String) 
                    comp.report("get-list-as-csv measure-option-command-list");
                measureOptionCommands = measureOptions.split(",");

                if (
                    measureOptionCommands.length == 1 
                    && measureOptionCommands[0].equals("")
                ) {
                    return;
                }
                
                int length = 
                    (int) (0 + ((Double) comp.report(
                        "list-length measure-option-string-lists"
                    )));
                for (int i = 0; i < length; i++) {
                    String tempMeasureText = (String) 
                        comp.report(
                            "get-list-as-csv (item " 
                            + i 
                            + " measure-option-string-lists)"
                        );
                    String[] tempArray = tempMeasureText.split(",");
                    measureOptionStringLists.add(tempArray);
                }
                
                String names = 
                    (String) comp.report("get-list-as-csv var-name-list");
                varNames = names.split(",");
                
                canHighlightAgents = 
                    (Boolean) comp.report("can-highlight-agents");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
        
        final String[] finalAgentNames = agentNames;  
        final List<MeasureOption> measureOptions = 
            new ArrayList<MeasureOption>();
        for (int i = 0; i < measureOptionCommands.length; i++) {
            measureOptions.add(
                new MeasureOption(
                    measureOptionStringLists.get(i), 
                    measureOptionCommands[i]
                )
            );
        }
        
        final boolean finalCanHighlightAgents = canHighlightAgents;
        
        //set up double-decker measure world
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // must be called on EDT, because 
                    // it constructs a Swing component.
                    TwoWorldContainer twc = 
                        new TwoWorldContainer(
                            smallerMeasureModelFile, 
                            finalAgentNames, 
                            stackof,
                            measureOptions,
                            finalCanHighlightAgents,
                            getComp()
                        );
                    getMeasureWorlds().addAll(twc.getMeasureWorlds());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        measureWorlds.get(measureWorlds.size() - 2).setupUIOffEDT(varNames);
        measureWorlds.get(measureWorlds.size() - 1).setupUIOffEDT(varNames);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getMeasureWorlds().
                    get(getMeasureWorlds().size() - 2).pickFirstGraph();
                getMeasureWorlds().
                    get(getMeasureWorlds().size() - 1).pickFirstGraph();
            }
        });

        if (extra) {
            openMeasureWorld(baseMeasureFileName);
        }
    }
    
    public static void setupCycle() {
        assert !SwingUtilities.isEventDispatchThread();
        
        try {
            comp.commandLater("setup-cycle");
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
    
    public static void takedownCycle() {
        assert !SwingUtilities.isEventDispatchThread();

        try {
            comp.commandLater("takedown-cycle");
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
    
    public static void addSetToMeasureWorlds(final String setName) {
        assert comp != null;
        assert measureWorlds != null;

        for (final MeasureWorld measureWorld: measureWorlds) {            
            executor.submit(
                new Runnable() {
                    @Override
                    public void run() { 
                        measureWorld.addSetAndUpdate(setName);
                    }
                }
            );
        }
    }
    
    public static void removeSetFromMeasureWorlds(final String setName) {
        assert comp != null;
        assert measureWorlds != null;

        for (final MeasureWorld measureWorld: measureWorlds) {
            executor.submit(
                new Runnable() {
                    @Override
                    public void run() { 
                        measureWorld.removeSetAndUpdate(setName);
                    }
                }
            );
        }       
    }    
    
    public static void doTickInEmbodimentWorld() {
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null
        ) {
            try {
                comp.commandLater("tick");
            } catch (CompilerException e) {
                e.printStackTrace();
            }
            for (MeasureWorld mwf: measureWorlds) {
                mwf.repaint();
            }
        }
    }

    public static void updateAllMeasureWorlds() {        
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null
        ) {
            for (MeasureWorld measureWorld: measureWorlds) {
                String agent = measureWorld.getSettings().getAgent();
                try {
                    Object report = null;
                    if (measureWorld.getSettings().isShowAllMeasurePoints()) {
                        report = 
                            comp.report("get-measures-for \"" + agent + "\"");
                    } else {
                        report = 
                            comp.report(
                "get-measures-for-filtered \"" + agent + "\" \"" + agent + "\""
                            );
                    }
                    if (
                        report instanceof LogoList 
                        && ((LogoList) report).size() > 0
                    ) {
                        LogoList measureList = (LogoList) report;
                        String command = 
                            "update-measures " + measureList.toString();
                        command = command.replace(',', ' ');
                        measureWorld.runMeasureCommand(command);
                        measureWorld.repaint();
                    } else {
                        // no measure points placed yet
                        measureWorld.runMeasureCommand("clear-measures");
                        measureWorld.repaint();
                    }
                } catch (CompilerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void updateMeasureWorld(final int index) {
        if (
            !SwingUtilities.isEventDispatchThread() 
            && comp != null 
            && measureWorlds != null 
            && measureWorlds.size() >= index
        ) {
            MeasureWorld world = measureWorlds.get(index - 1);
            String agent = world.getSettings().getAgent();
            try {
                Object report = null;
                if (world.getSettings().isShowAllMeasurePoints()) {
                    report = comp.report("get-measures-for \"" + agent + "\"");
                } else {
                    report = 
                        comp.report(
                            "get-measures-for-filtered \"" 
                            + agent + "\" \"" + agent + "\""
                        );
                }
                if (
                    report instanceof LogoList 
                    && ((LogoList) report).size() > 0
                ) {
                    LogoList logoList = (LogoList) report;
                    final String command = 
                        "update-measures " 
                        + logoList.toString().replace(',', ' '
                    );
                    
                    world.runMeasureCommand(command);
                    world.repaint();
                } else {
                    // no measure points placed yet
                    world.runMeasureCommand("clear-measures");
                    world.repaint();
                }
            } catch (CompilerException e) {
                e.printStackTrace();
            }
        }
    }
    
    static Vector<MeasureWorld> getMeasureWorlds() {
        return measureWorlds;
    }
}
