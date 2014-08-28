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


package edu.vanderbilt.mac;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;

import edu.vanderbilt.driverandlayout.Loader;

public final class MacAboutHandler implements AboutHandler {

    private static final String ABOUT_TEXT = 
        "ViMAP"
        + "\nVersion: " + Loader.VERSION_STRING
        + "\n\nPowered by NetLogo. Wilensky, U. 1999."
        + "\nhttp://www.ccl.northwestern.edu/netlogo/";
    
    @Override
    public void handleAbout(final AboutEvent event) { 
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    URL myurl = 
                        this.getClass().getResource(Loader.VIMAP_LOGO_FILE);
                    ImageIcon icon =
                            new ImageIcon(myurl);
                    JOptionPane.showMessageDialog(
                        new JFrame(), 
                        ABOUT_TEXT, 
                        "About ViMAP", 
                        JOptionPane.INFORMATION_MESSAGE,
                        icon
                   );  
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
