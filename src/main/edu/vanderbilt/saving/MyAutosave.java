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


package edu.vanderbilt.saving;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import edu.vanderbilt.driverandlayout.GraphicalInterface;
import edu.vanderbilt.driverandlayout.Loader;

public final class MyAutosave implements Autosave {

    // used to schedule autosave
    private Timer timer;
    
    // does not include ".vimap" extension or "_Autosave_#" suffix
    private String currentUserModelName;
    
    // number to append to "_Autosave_" to avoid overwriting older autosave
    // versions
    private int currentNumber;
    
    // delay is 30 seconds.
    private static final int DELAY_IN_MILLIS = 30 * 1000;
    
    // used to generate autosave file name: <userFileName>_Autosave_<#>.vimap
    public static final String AUTOSAVE_STRING = "_Autosave_";
    
    // directory in which to store autosave files
    public static final String AUTOSAVE_DIRECTORY = "temp";
        
    public MyAutosave() {
        // do nothing
    }
    
    // returns true if the file name contains "_Autosave_".
    // TODO: should include isAutosave boolean in SavableModel for more
    // robust test
    public static boolean isAutosaveName(final String fileName) {
        return fileName.contains(AUTOSAVE_STRING);
    }
    
    // returns "base model name" for an autosave file, which has had
    // _Autosave_# appended to its name
    public static String getBaseName(final String fileName) {
        if (!isAutosaveName(fileName)) {
            // not an autosave file, so return name unchanged
            return fileName;
        }
        
        // splice "Autosave_#" from the file name
        String result = 
            fileName.substring(0, fileName.indexOf(AUTOSAVE_STRING));
        result += "." + FileExtensionUtility.VIMAP_EXTENSION;
        return result;
    }
    
    @Override
    public void init() {
        // if already initialized, cancel the old timer
        if (this.timer != null) {
            this.timer.cancel();
        }
        
        this.timer = new Timer();
        // schedule autosave to occur if needed, every DELAY_IN_MILLIS ms
        this.timer.schedule(
            new AutosaveTask(), 
            DELAY_IN_MILLIS, // start first after this delay
            DELAY_IN_MILLIS // call repeatedly after this delay
        );
        
        // update name for next autosave file
        deleteLastAndChangeName();
    }
    
    // cancel autosave
    @Override
    public void stop() {
        // note: this timer can't be set again; it must be replaced
        // with a new timer first.
        this.timer.cancel();
    }
    
    @Override
    public void deleteLastAndChangeName() {
        // if no /temp directory, create it
        createAutosaveDirectoryIfNeeded();
        
        if (this.currentUserModelName != null) {
            // delete the previous autosave file, if any
            deleteLast();
        }
        
        // update title to autosave under based on current title bar
        this.currentUserModelName = userModelTitle();
        
        // update number for autosave file based on numbers with same title
        // already present in /temp directory (going 1 higher than the highest)
        updateAutosaveNumber();
    }
    
    // find the highest number suffix in the /temp directory for autosave files
    // whose base names match this.currentUserModelName; then return 1 higher
    // (or 1 if none found).
    private void updateAutosaveNumber() {
        File autosaveDirectory = new File(AUTOSAVE_DIRECTORY);
        
        // get an array of all file names in /temp
        String[] fileNames = autosaveDirectory.list();
        
        int result = 1;
        for (int i = 0; i < fileNames.length; i++) {
            final String fileName = fileNames[i];
            if (isVimapFile(fileName)) {
                if (
                    getFileNameBody(fileName) != null 
                    && getFileNameBody(fileName).
                        equals(this.currentUserModelName)
                ) {
                    // the file has the same "body" name
                    // as the current file
                    int fileNumber = getFileNumber(fileName);
                    if (fileNumber >= result) {
                        result = fileNumber + 1;
                    }
                }
            }
        }
        
        this.currentNumber = result;
    }
    
    private boolean isVimapFile(final String fileName) {
        return fileName != null 
            && fileName.contains("." + FileExtensionUtility.VIMAP_EXTENSION);
    }
    
    // get the number suffix on the file name.
    // if it's not an autosave file name, or it's missing a number,
    // return -1.
    private int getFileNumber(final String fileName) {
        assert fileName != null;
        
        int startIndex = fileName.indexOf(AUTOSAVE_STRING);
        int stopIndex = fileName.indexOf('.', startIndex);
        if (startIndex == -1 || stopIndex == -1) {
            return -1;
        }
        
        try {
            // the number should be between _Autosave_ and .vimap.
            // example: foo_Autosave_42.vimap (return 42)
            int result = Integer.parseInt(
                fileName.substring(
                    startIndex + AUTOSAVE_STRING.length(), 
                    stopIndex
                )
            );
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // return part of the file name before _Autosave_
    private String getFileNameBody(final String fileName) {
        assert fileName != null;
        int index = fileName.indexOf(AUTOSAVE_STRING);
        if (index == -1) {
            return null;
        }
        
        return fileName.substring(0, index);
    }
    
    // return the relative path name for a new autosave file.
    // example: temp/foo_Autosave_42.vimap
    private String getCurrentAutosaveName() {
        assert this.currentUserModelName != null;
        
        StringBuilder sb = new StringBuilder();
        sb.append(AUTOSAVE_DIRECTORY).append("/").
            append(this.currentUserModelName).
            append(AUTOSAVE_STRING).append(this.currentNumber).append(".").
            append(FileExtensionUtility.VIMAP_EXTENSION);
        return sb.toString();
    }
    
    // delete the current autosave file.
    private void deleteLast() {
        final String lastFileName = getCurrentAutosaveName();
        final File lastFile = new File(lastFileName);
        if (lastFile.exists()) {
            boolean success = lastFile.delete();
            if (!success) {
                System.err.println("Could not delete file: " + lastFile);
            }
        }
    }
    
    // create the /temp directory if it does not exist
    private void createAutosaveDirectoryIfNeeded() {
        File autosaveDirectory = new File(AUTOSAVE_DIRECTORY);
        if (!autosaveDirectory.exists()) {
            boolean success = autosaveDirectory.mkdir();
            if (!success) {
                System.err.println(
                    "Could not create autosave directory: " + autosaveDirectory
                );
            }
        }
    }
    
    // return whether this.editing indicates the current file needs to be saved
    // (i.e., has unsaved changes)
    boolean getShouldSave() {
        return Loader.shouldSave();
    }
    
    void save() {
        final String textToSave = StringIO.getTextToSave();
        StringIO.printToFile(textToSave, getCurrentAutosaveName());
    }
    
    // gets the title from the title bar, removing boilerplate text 
    // with the program name and version, and removing ".vimap" if present.
    private String userModelTitle() {
        String fullTitle = GraphicalInterface.getFrame().getTitle();
        int suffixIndex = 
            fullTitle.indexOf("." + FileExtensionUtility.VIMAP_EXTENSION);
        String userTitle;
        if (suffixIndex == -1) {
            userTitle = fullTitle.substring(
                GraphicalInterface.TITLE_PREFIX_STRING.length()
            );
        } else {
            // .vimap extension is present, so remove it along with
            // the program name
            userTitle = fullTitle.substring(
                GraphicalInterface.TITLE_PREFIX_STRING.length(), 
                suffixIndex
            );
        }
        
        return userTitle;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
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
