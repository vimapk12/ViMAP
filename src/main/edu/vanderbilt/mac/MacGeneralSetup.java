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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.apple.eawt.Application;

import edu.vanderbilt.driverandlayout.Loader;

public abstract class MacGeneralSetup {

    public static void setupFirstPartForMac() {
        try {
            // moving menus to Mac menu bar
            // may not work with Nimbus look and feel
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                "com.apple.mrj.application.apple.menu.about.name", 
                "ViMAP"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }
    
    public static void setupForMac() {
        try {
            Application.getApplication().setAboutHandler(new MacAboutHandler());
            Application.getApplication().setQuitHandler(new MacQuitHandler());
            
            BufferedImage image = null;
            try {
                if (Loader.isRunningJavaWebStart()) {
                    URL myurl = 
                        MacGeneralSetup.class.getClassLoader().getResource(
                            Loader.VIMAP_LOGO_FILE.substring(1)
                        );
                    image = 
                        ImageIO.read(myurl);
                } else {
                    URL myurl = Thread.currentThread().getClass().
                            getResource(Loader.VIMAP_LOGO_FILE);
                    image = 
                        ImageIO.read(myurl);
                }

                Application.getApplication().setDockIconImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
