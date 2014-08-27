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


package edu.vanderbilt.codeview;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.vanderbilt.codecomponentview.BlockView;
import edu.vanderbilt.codecontroller.MasterCodeController;

public interface MasterCodeView {

    void setAgentMethodSelectPanel(
        AgentProcedureSelectView agentMethodSelectView
    );
    void setCategorySelector(
        CategorySelector panel
    );
    
    void setAvailableAgentNames(List<String> agentNames);
    void setAvailableProcedureNames(List<String> procedureNames);
    void setCategories(
        List<String> categoryNames,
        Map<String, Color> categoryColors
    );
    void setCurrentAgentName(String agentName);
    void setCurrentProcedureName(String procedureName);     
    void setPaletteBlockViews(List<BlockView> blockViews);
    void setUserCodeId(UUID id);
    void setUserBlockViews(List<BlockView> blockViews);
    void setEditable(boolean isEditable);
    edu.vanderbilt.codecomponentview.BlockView getBlockView(UUID id);
    void highlightBlockView(UUID id);
    void updateLayoutSize();
    
    void setMasterCodeController(MasterCodeController controller);
}
