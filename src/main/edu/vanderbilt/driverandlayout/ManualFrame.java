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


package edu.vanderbilt.driverandlayout;

import java.awt.Dimension;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.xhtmlrenderer.simple.XHTMLPanel;

public final class ManualFrame extends JFrame {

    private static final long serialVersionUID = -6007972422418498742L;

    public ManualFrame() {
        super("User Manual");
        
        assert SwingUtilities.isEventDispatchThread();
        
        final int width = 800;
        final int height = 600;
        loadContent(width, height);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        pack();
        setSize(width, height);
        setResizable(true);
        setVisible(false);
    }
    
    private void loadContent(
        final int width,
        final int height
    ) {
        final XHTMLPanel panel = new XHTMLPanel();
        panel.setPreferredSize(new Dimension(width, height));
        
        final JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        );
        scroll.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setPreferredSize(new Dimension(width, height));
        this.getContentPane().add(scroll, "Center");
        
        loadPage("/manual.html", panel);
    }
    
    private void loadPage(
        final String urlText, 
        final XHTMLPanel panel
    ) {
        String myUrlText = urlText;
        if (!urlText.startsWith("/")) {
            myUrlText = "/" + myUrlText;
        }
        
        final URL ref = this.getClass().getResource(myUrlText);
        panel.setDocument(ref.toExternalForm());
    }

    public void goVisible() {
        assert SwingUtilities.isEventDispatchThread();
        setVisible(true);
        toFront();
        repaint();
    }
}
