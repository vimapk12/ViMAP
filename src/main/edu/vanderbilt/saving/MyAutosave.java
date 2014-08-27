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


// INNER CLASS
    
    // task to be executed regularly by the timer
    private class AutosaveTask extends TimerTask {
        AutosaveTask() {
            // do nothing
        }
        
        @Override
        public void run() {
            if (getShouldSave()) {
                // save only if there are unsaved changes
                save();
            }
        }        
    }
}
