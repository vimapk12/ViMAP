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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public final class ProgressDialog {
    
    private JDialog dialog;
    private JProgressBar progressBar;
    private JLabel messageLabel;
    
    public ProgressDialog(
        final String title,
        final String messageText,
        final int min, 
        final int max
    ) {
        assert SwingUtilities.isEventDispatchThread();

        this.dialog = new JDialog(
            new JFrame(), 
            title,
            true // modal
        );
        
        final int width = 400;
        final int height = 150;
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        final int borderMargin = 10;
        contentPane.setBorder(BorderFactory.createEmptyBorder(
            borderMargin, 
            borderMargin, 
            borderMargin, 
            borderMargin
        ));
        JLabel titleLabel = new JLabel(title);
        final int titleFontSize = 14;
        Font font = new Font("Verdana", Font.BOLD, titleFontSize);
        titleLabel.setFont(font);
        contentPane.add(titleLabel);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.messageLabel = new JLabel(messageText);
        final int topPadding = 25;
        contentPane.add(Box.createRigidArea(
            new Dimension(width, topPadding)
        ));
        contentPane.add(this.messageLabel);
        this.messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.progressBar = new JProgressBar(min, max);
        final int padding = 30;
        contentPane.add(Box.createRigidArea(
                new Dimension(width, padding)
            ));
        contentPane.add(this.progressBar);
        this.progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        this.dialog.setContentPane(contentPane);
        
        final int centerX = 400;
        final int centerY = 300;
        this.dialog.setLocation(centerX, centerY);
        this.dialog.setDefaultCloseOperation(
            WindowConstants.DO_NOTHING_ON_CLOSE
        );
        this.dialog.setUndecorated(true);

        this.dialog.setPreferredSize(new Dimension(width, height));
        this.dialog.pack();
    }
    
    public void show() {
        assert SwingUtilities.isEventDispatchThread();
        this.dialog.setVisible(true);
    }
    
    public void dispose() {
        assert SwingUtilities.isEventDispatchThread();
        this.dialog.setVisible(false);
        this.dialog.dispose();
    }
    
    public void incrementValue() {
        assert SwingUtilities.isEventDispatchThread();
        if (this.progressBar.getValue() < this.progressBar.getMaximum()) {
            this.progressBar.setValue(this.progressBar.getValue() + 1);
        }
    }
    
    public void setMessage(final String text) {
        assert SwingUtilities.isEventDispatchThread();
        this.messageLabel.setText(text);
    }
}
