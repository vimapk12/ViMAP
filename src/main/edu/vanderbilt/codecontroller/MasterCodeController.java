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

import java.util.List;
import java.util.UUID;

import edu.vanderbilt.codecomponentview.BlockView.LayoutSize;
import edu.vanderbilt.codecomponentview.CategoryViewStyle;
import edu.vanderbilt.domainmodel.BlockTemplate;

public interface MasterCodeController {

    void startUp();
    
    // if mustRun is false, setAgent will do nothing
    // if the new agentName matches the current agentName.
    // mustRun should be true when opening a new model.
    void setAgent(String agentName, boolean mustRun);
    void setProcedure(String procedureName);
    void setEditable(boolean isEditable);
    void setEnumValue(UUID blockId, String fieldName, String item);
    void setNumberValue(UUID blockId, String fieldName, String value);
    void setCategory(CategoryViewStyle style, String name);
    void requestAddSetBeforeArgsKnown(String setTypeName, String setName);
    void cancelNewSet();
    void doneDrawingShape();
    void doneEditingShape();
    void setBlockViewName(UUID blockViewId, String name);
    void deleteSet(String setName);
    void requestEditSetShapeBeforeShapeKnown(String setName);
    void setLayoutSize(LayoutSize layoutSize);
    
    void resetImageFileName();
    void setImageFileName(String fileName);
    
    // blockTemplate is ignored if there is a block with 
    // UUID blockId in the userModel. if there is no such block, 
    // the block view must be new from the palette.
    void insertBlock(
        UUID blockId, 
        BlockTemplate blockTemplate, 
        UUID sequenceId, 
        int index
    );
    void removeBlock(UUID blockId);
    void clear();
    void addUserProcedure(String name, List<String> agentTypes);
    void deleteUserProcedure(String name);
    void init();
    void showAndHighlightBlock(UUID blockId);
    void clearHighlights();
    
    /**
     * Remove all code from the current agent procedure to the "clipboard".
     * Make note of the agent name and procedure name 
     * of the original code source.
     */
    void cutAll();
    
    /**
     * Place a copy of all code from the current agent procedure 
     * on the "clipboard". Make note of the agent name and procedure 
     * name of the original code source.
     */
    void copyAll();
    
    /**
     * If every block on the "clipboard" is legal in the current agent procedure
     * (i.e., present in the palette of that procedure), then place a copy of 
     * any code from the clipboard at the end of the current agent procedure.
     * 
     * Otherwise, show the user an error message explaning the problem. 
     * The message should mention one illegal block by name, as well as the 
     * agent name and procedure name of the original source of the 
     * clipboard code.
     */
    void pasteToEnd();
}
