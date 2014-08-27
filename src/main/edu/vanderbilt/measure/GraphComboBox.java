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


package edu.vanderbilt.measure;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;

import org.nlogo.api.CompilerException;
import org.nlogo.lite.InterfaceComponent;

public final class GraphComboBox extends JComboBox<String> 
    implements ItemListener {

    private static final long serialVersionUID = -3580699701036922494L;
    private final Set<Option> options;
    private final InterfaceComponent measureNetLogo;
    
    public GraphComboBox(
        final String[] titles,
        final String[] commands,
        final String[] fullTitles,
        final InterfaceComponent aMeasureNetLogo
    ) {
        super(fullTitles);
        
        if (aMeasureNetLogo == null) {
            throw new IllegalArgumentException();
        }
        
        if (
            titles.length != commands.length 
            || commands.length != fullTitles.length
        ) {
            throw new IllegalArgumentException();
        }
        
        this.measureNetLogo = aMeasureNetLogo;
        
        this.options = new HashSet<Option>();
        for (int i = 0; i < titles.length; i++) {
            this.options.add(new Option(titles[i], commands[i], fullTitles[i]));
        }
        
        addItemListener(this);
    }
    
    
    private static class Option {
        private final String title;
        private final String command;
        private final String fullTitle;
        
        public Option(
            final String aTitle,
            final String aCommand,
            final String aFullTitle
        ) {
            this.title = aTitle;
            this.command = aCommand;
            this.fullTitle = aFullTitle;
        }

        @SuppressWarnings("unused")
        String getTitle() {
            return title;
        }

        String getCommand() {
            return command;
        }

        String getFullTitle() {
            return fullTitle;
        }
    }
    
    public void reactToSelectedItem() {
        final String fullTitle = this.getSelectedItem().toString();
        
        for (Option option: this.options) {
            if (
                option.getFullTitle() != null 
                && option.getFullTitle().equals(fullTitle)
            ) {
                try {
                    this.measureNetLogo.commandLater(option.getCommand());
                } catch (CompilerException e) {
                    e.printStackTrace();
                }
                
                return;
            }
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent event) {
        if (event.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        
        reactToSelectedItem();
    }
}
