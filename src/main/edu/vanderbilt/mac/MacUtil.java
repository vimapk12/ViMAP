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


package edu.vanderbilt.mac;

public abstract class MacUtil {

    /**
     * Returns true if the Mac OS version is at least as new as
     * 10.5.8 (for OS 10.5) or 10.6.3 (for 10.6). 10.7 or newer
     * is always supported.
     * 
     * These limitations exist for any use of 
     * com.apple.eawt.AboutHandler or
     * com.apple.eawt.QuitHandler, which were introduced in
     * Mac OS 10.5.8 and 10.6.3.
     * 
     * https://developer.apple.com/library/mac/documentation/Java/
     *  Reference/JavaSE6_AppleExtensionsRef/api/com/apple/eawt/
     *  Application.html#setAboutHandler(com.apple.eawt.AboutHandler)
     * 
     * @param version
     * @return whether the version is supported
     */
    public static boolean isSupportedMacVersion(final String version) {
        if (
            version.startsWith("10.9")
            || version.startsWith("10.8")
            || version.startsWith("10.7")
        ) {
            return true;
        }
        if (version.startsWith("10.6")) {
            return version.endsWith(".3") 
                || version.endsWith(".4")
                || version.endsWith(".5")
                || version.endsWith(".6")
                || version.endsWith(".7")
                || version.endsWith(".8");
        }
        if (version.startsWith("10.5")) {
            return version.endsWith(".8");
        }
        
        return false;
    }
    
    public static boolean isMacOsX(final String osName) {
        return osName.startsWith("mac os x");
    }
}
