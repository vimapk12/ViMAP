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


package edu.vanderbilt.runview;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.vanderbilt.runcontroller.RunController;


/**
 * This panel has a slider intended for controlling
 * the speed of model execution.
 * It handles events on the slider and calls 
 * the executor to update the time delay
 * between blocks.
 */
public final class SpeedSliderPanel extends JPanel implements ChangeListener {
    
    private static final long serialVersionUID = -8629834507448245335L;

    // lowest output value of slider
    private static final int MIN_NUMBER = 0; 
    
    // output value at center of slider
    private static final int MIDDLE_NUMBER = 500; 
    
    // highest output value of slider
    private static final int MAX_NUMBER = 1000; 
    
    // min delay so NetLogo won't hang
    private static final int MIN_DELAY_FOR_SAFETY = 0; 
    
    // the speed control slider
    private final JSlider slider;
    
    // called with updates to the time delay between blocks
    private final RunController runController; 
    
    
    /**
     * Constructor
     * 
     * @param aRunController called with 
     * updates to the time delay between blocks
     */
    public SpeedSliderPanel(final RunController aRunController) {
        assert SwingUtilities.isEventDispatchThread();
        assert aRunController != null;
        this.runController = aRunController;
        
        this.slider = new JSlider(
            SwingConstants.HORIZONTAL,
            MIN_NUMBER, 
            MAX_NUMBER, 
            MIDDLE_NUMBER
       );
        this.slider.setToolTipText("Adjust speed of model");
        
        setupSliderLabels();
        
        this.slider.addChangeListener(this);
        this.add(this.slider);
        updateValue(this.slider.getValue());
    }
    
    JSlider getSlider() {
       return this.slider; 
    }

    public void setSliderEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getSlider().setEnabled(isEnabled);
           }
        });
    }
    
    /**
     * Handles changes to the position of the slider
     */
    @Override
    public void stateChanged(final ChangeEvent event) {
        final  JSlider source = (JSlider) event.getSource();
        updateValue(source.getValue());
    }
    
    
    private void updateValue(final int sliderValue) {
        // delay is calculated as MAX_NUMBER 
        // (1000 millis, or 1 sec) minus the value of the slider.
        // if slider is all the way to right, the delay is 0 (fast speed)
        int delayInMillis = MAX_NUMBER - sliderValue;
        if (delayInMillis < MIN_DELAY_FOR_SAFETY) {
            delayInMillis = MIN_DELAY_FOR_SAFETY;
        }
            
        this.runController.setDelayPerBlockInMillis(delayInMillis);    
    }
    
    
    /**
     * Should be called only from constructor.
     * 
     * Adds text labels to the slider at the left, right, and center
     */
    private void setupSliderLabels() {
        final Hashtable<Integer, JLabel> labelTable = 
            new Hashtable<Integer, JLabel>();
        labelTable.put(MIN_NUMBER, new JLabel("Slow"));
        labelTable.put(MIDDLE_NUMBER, new JLabel("Medium"));
        labelTable.put(MAX_NUMBER, new JLabel("Fast"));
        this.slider.setLabelTable(labelTable);
        this.slider.setPaintLabels(true);
    }
}
