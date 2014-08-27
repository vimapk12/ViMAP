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

import java.util.HashMap;
import java.util.Vector;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.ExtensionManager;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.Syntax;

public final class ArduinoExtension extends DefaultClassManager {

    public static final int BAUD_RATE = 9600;
    static SerialPort serialPort;
    static PortListener portListener;
    
    /*
     * holds current value of each feature as set by sensors, in a hash table
     */
    private static HashMap<String, Double> values = 
        new HashMap<String, Double>();
    
    /*
     * assign default value BAUD_RATE to key "BaudRate"
     */
    static {
        values.put("BaudRate", (double) BAUD_RATE);
    }
    
    /*
     * called by NetLogo when extension is imported
     * @see org.nlogo.api.DefaultClassManager#load(org.nlogo.api.PrimitiveManager)
     */
    @Override
    public void load(final PrimitiveManager pm) throws ExtensionException {
        
        // register primitive names and a DefaultReporter or DefaultCommand
        // implementation for each primitive name
        pm.addPrimitive("primitives", new Primitives());
        pm.addPrimitive("ports", new Ports());
        pm.addPrimitive("open", new Open());
        pm.addPrimitive("close", new Close());
        pm.addPrimitive("get", new Get());
        pm.addPrimitive("write-string", new WriteString());
        pm.addPrimitive("write-int", new WriteInt());
        pm.addPrimitive("write-byte", new WriteByte());
        pm.addPrimitive("is-open?", new IsOpen());
    }
    
    @Override
    public void unload(final ExtensionManager em) {
        //first remove the event listener (if any) and close the port
        if (portListener != null && serialPort != null) {
            try {
                serialPort.removeEventListener();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            try {
                serialPort.closePort();
            } catch (SerialPortException e2) {
                e2.printStackTrace();
            }
        } else {
            if (serialPort != null && serialPort.isOpened()) {
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
        //now unload the native library, so that if we're loading the extension
        //again on the same NetLogo run, it doesn't cause us troubles.
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            java.lang.reflect.Field field = 
                ClassLoader.class.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Vector<Object> libs = (Vector<Object>) (field.get(classLoader));
            for (Object o: libs) {
                java.lang.reflect.Method finalize = 
                    o.getClass().getDeclaredMethod("finalize" , new Class[0]);
                finalize.setAccessible(true);
                finalize.invoke(o, new Object[0]);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    /*
     * called by the PortListener to update the hash table, on SerialEvent
     */
    static void putValue(
        final String key, 
        final double value
    ) {
        values.put(key, value);
    }
    
    /*
     * called by "get" primitive to look up the most recently seen value
     * of a given feature from the sensors, as set in the hash table on SerialEvent
     * by the PortListener
     */
    static double get(final String key) {
        String lcKey = key.toLowerCase();
        if (values.containsKey(lcKey)) {
            return values.get(lcKey);
        }
        
        return Double.NaN;
    }
    
    /*
     * called by "close" primitive.
     * closes the port and removes its event listener.
     */
    static void doClose() throws ExtensionException {
        try {
            serialPort.removeEventListener();
            serialPort.closePort();
        } catch (SerialPortException e) {
            throw new ExtensionException(
                "Error in writing: " + e.getMessage()
            );
        }
    }
    
    public static final class Primitives extends DefaultReporter {
        
        @Override
        public Syntax getSyntax() {
          return Syntax.reporterSyntax(Syntax.ListType());
        }
        
        @Override
        public Object report(final Argument[] arg0, final Context arg1)
            throws ExtensionException, LogoException {
            
            LogoListBuilder resultLogoList = new LogoListBuilder();
            String[] primitives = {
                "reporter:primitives", 
                "reporter:ports", 
                "reporter:get[Name:String(case-insensitive)]", 
                "reporter:is-open?",
                "",
                "command:open[Port:String]", "command:close", 
                "command:write-string[Message:String]",
                "command:write-int[Message:int]", 
                "command:write-byte[Message:byte]",
                "",
                "ALSO NOTE: Baud Rate of 9600 is expected"
            };
            
            for (String primitive: primitives) {
                resultLogoList.add(primitive);
            }
            return resultLogoList.toLogoList();
        }
    }
    
    public static final class Ports extends DefaultReporter {
        
        @Override
        public Syntax getSyntax() {
          return Syntax.reporterSyntax(Syntax.ListType());
        }
        
        @Override
        public Object report(
            final Argument[] arg0, 
            final Context arg1
        ) throws ExtensionException, LogoException {
            final LogoListBuilder result = new LogoListBuilder();
            
            // get list of port names
            final String[] names = SerialPortList.getPortNames();
            for (String name : names) {
                result.add(name);
            }
            return result.toLogoList();
        }
    }
    
    public static final class Open extends DefaultCommand {

        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[] {Syntax.StringType() });
        }
        
        @Override
        public void perform(final Argument[] args, final Context ctxt)
                throws ExtensionException, LogoException {

            if (serialPort != null && serialPort.isOpened()) {
                throw new ExtensionException("Port is already open");
            }
            
            try {
                final SerialPort resultPort = 
                    new SerialPort(args[0].getString());
                resultPort.openPort();
                
                final int dataBits = 8;
                final int stopBits = 1;
                final int parity = 0;
                resultPort.setParams(
                    BAUD_RATE, // baud rate
                    dataBits, // data bits
                    stopBits, // stop bits
                    parity // parity
                );
                final int mask = 
                    SerialPort.MASK_RXCHAR 
                    + SerialPort.MASK_CTS + SerialPort.MASK_DSR; // Prepare mask
                resultPort.setEventsMask(mask); // Set mask
                
                final PortListener resultListener = 
                    new PortListener(resultPort);
                resultPort.addEventListener(resultListener);
                serialPort = resultPort;
                portListener = resultListener;
            } catch (SerialPortException e) {
                throw new ExtensionException(
                    "Error in opening port: " + e.getMessage()
                );
            }
        }
    }
    
    public static final class IsOpen extends DefaultReporter {
        
        @Override
        public Object report(
            final Argument[] arg0, 
            final Context arg1
        ) throws ExtensionException, LogoException {
            return 
                serialPort != null 
                && serialPort.isOpened() 
                && portListener != null;
        }
    }
    
    public static final class Close extends DefaultCommand {
        
        @Override
        public void perform(
            final Argument[] args, 
            final Context ctxt
        ) throws ExtensionException, LogoException {
            if ((serialPort == null) || (!serialPort.isOpened())) {
                throw new ExtensionException("Serial Port not Open");
            }
             
            doClose();
        }
    }
    
    public static final class Get extends DefaultReporter {
        
        @Override
        public Syntax getSyntax() {
          return Syntax.reporterSyntax(
              new int[] {
                  Syntax.StringType()
              }, 
              Syntax.NumberType()
          );
        }
        
        @Override
        public Object report(final Argument[] args, final Context ctxt)
                throws ExtensionException, LogoException {
            return get(args[0].getString());
        }
    }
    
    public static final class WriteString extends DefaultCommand {
        
        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[] {Syntax.StringType() });
        }
        
        @Override
        public void perform(final Argument[] args, final Context ctxt)
                throws ExtensionException, LogoException {
            if ((serialPort == null) || (!serialPort.isOpened())) {
                throw new ExtensionException("Serial Port not Open");
            }
            try {
                serialPort.writeString(args[0].getString());
            } catch (SerialPortException e) {
                throw new ExtensionException(
                    "Error in writing: " + e.getMessage()
                );
            }
        }
    }
    
    public static final class WriteInt extends DefaultCommand {
        
        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[] {Syntax.NumberType() });
        }
        
        @Override
        public void perform(final Argument[] args, final Context ctxt)
                throws ExtensionException, LogoException {
            if ((serialPort == null) || (!serialPort.isOpened())) {
                throw new ExtensionException("Serial Port not Open");
            }
            try {
                serialPort.writeInt(args[0].getIntValue());
            } catch (SerialPortException e) {
                throw new ExtensionException(
                    "Error in writing: " + e.getMessage()
                );
            }
        }
    }
    
    public static final class WriteByte extends DefaultCommand {
        
        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[] {Syntax.NumberType() });
        }
        
        @Override
        public void perform(final Argument[] args, final Context ctxt)
                throws ExtensionException, LogoException {
            if ((serialPort == null) || (!serialPort.isOpened())) {
                throw new ExtensionException("Serial Port not Open");
            }
            try {
                serialPort.writeByte((byte) (args[0].getIntValue()));
            } catch (SerialPortException e) {
                throw new ExtensionException(
                    "Error in writing: " + e.getMessage()
                );
            }
        }
    }
}
