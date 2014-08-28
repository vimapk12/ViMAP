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

import javax.swing.filechooser.FileFilter;


/**
 * Allows the user to select only files with NetLogo image extensions, 
 * or any directory.
 */
public final class ImageFileFilter extends FileFilter {
    
    @Override
    public boolean accept(final File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {   
            return true; 
        }
        
        final String extension = 
            FileExtensionUtility.getExtension(file.getName());
        return FileExtensionUtility.isImageExtension(extension);
    }

    @Override
    public String getDescription() {
        return "Image Files: " 
            + FileExtensionUtility.getNetLogoImageTypeString();
    }
}
