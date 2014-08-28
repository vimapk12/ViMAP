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

import java.util.List;
import java.util.UUID;

import edu.vanderbilt.codecontroller.MasterCodeController;

public interface UserCodeView {

    void init();
    void setBlockViews(
        List<edu.vanderbilt.codecomponentview.BlockView> blockViews
    );
    void setEditable(boolean isEditable);
    BlockView getBlockView(UUID id);
    void setMasterCodeController(MasterCodeController controller);
    void setId(UUID id);
    void enumValueChanged(UUID blockId, String fieldName, String item);
    void numberValueChanged(UUID blockId, String fieldName, String value);
    void setExecutingBlock(UUID id, boolean isExecuting);
    void resetPreviousBlockColor();
}
