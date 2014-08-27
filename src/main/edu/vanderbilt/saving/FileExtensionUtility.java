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


package edu.vanderbilt.saving;

import java.util.Arrays;


/**
 * This class provides data on relevant file extensions.
 * It can get the file extension of a file, and provide
 * useful file extension names.
 */
public abstract class FileExtensionUtility {
   
    // the file extension for XML files
    public static final String XML_EXTENSION = "xml"; 
    
    // the file extension for NetLogo files
    public static final String NETLOGO_EXTENSION = "nlogo"; 
    
    // the file extension to use for saved models of this program
    public static final String VIMAP_EXTENSION = "vimap";
    
    // NetLogo image types: BMP, JPG, GIF, PNG
    // BMP extensions: bmp, dib
    // JPG extensions: jpeg, jpg, jpe, jfif, jif, jfi
    private static final String[] NETLOGO_IMAGE_EXTENSIONS = {
            "bmp", "dib", "jpeg", "jpg", "jpe", 
            "jfif", "jif", "jfi", "gif", "png"
        };
    
    public static String getNetLogoImageTypeString() {
        return Arrays.toString(NETLOGO_IMAGE_EXTENSIONS);
    }
    
    public static boolean isImageExtension(final String extension) {
        if (extension == null) {
            return false;
        }
        
        for (int i = 0; i < NETLOGO_IMAGE_EXTENSIONS.length; i++) {
            if (NETLOGO_IMAGE_EXTENSIONS[i].equals(extension)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the file extension of a file, by taking any characters after
     * the last period in the file's name.
     * 
     * @param file the file whose extension will be returned
     * @return any characters after the last period in the file's name
     */
    public static String getExtension(final String fileName) {
        final int lastPeriodIndex = fileName.lastIndexOf('.');

        if (lastPeriodIndex < 0 || fileName.length() <= lastPeriodIndex) {
            return null;
        }
        
        return fileName.substring(lastPeriodIndex + 1).toLowerCase();
    }
}
