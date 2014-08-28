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


package arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public final class PortListener implements SerialPortEventListener {
    
    private final SerialPort port;
    private String residue = "";
    private static final char DELIM = ';';
    
    /*
     * instantiated when "open" primitive is called in NetLogo
     */
    public PortListener(final SerialPort aPort) {
        this.port = aPort;
    }
    
    /*
     * called automatically when there is an event on the SerialPort
     * 
     * @see jssc.SerialPortEventListener#serialEvent(jssc.SerialPortEvent)
     */
    @Override
    public void serialEvent(final SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                residue += port.readString();
                int delimiterIndex = residue.indexOf(DELIM);
                
                // remove leading delimiters
                while (delimiterIndex == 0) { 
                    // first character is a delimiter
                    residue = residue.substring(1); 
                    delimiterIndex = residue.indexOf(DELIM); 
                }
                
                // harvest all complete messages (ending in delimiter)
                while (delimiterIndex > 0) {
                    final String head = residue.substring(0, delimiterIndex);
                    parse(head);
                    
                    if (delimiterIndex == residue.length() - 1) {
                        // first delimiter is last char
                        residue = "";
                    } else {
                        // first delimiter is before last char
                        residue = residue.substring(delimiterIndex + 1);
                    }
                    
                    delimiterIndex = residue.indexOf(DELIM);
                }
                
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }   
    }
    
    /*
     * reads a string with expected form "<key>,<double value>" as in "foo,42.0".
     * puts the key, value pair into the ArduinoExtension hash table.
     */
    private void parse(final String entry) {
        final String[] splitEntry = entry.split(",");
        if (splitEntry.length == 2) {
            final String lowerCaseKey = splitEntry[0].toLowerCase();
            try {
                final double value = Double.parseDouble(splitEntry[1]);
                ArduinoExtension.putValue(lowerCaseKey, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
