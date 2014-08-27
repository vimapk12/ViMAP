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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import com.thoughtworks.xstream.XStream;

import edu.vanderbilt.domainmodel.BlockTemplate;
import edu.vanderbilt.driverandlayout.Loader;

public abstract class StringIO {
    
    // encoding for output writer when saving
    private static final String CHARACTER_ENCODING = "UTF-8"; 
    private static XStream xmlTranslator;
    
    
    public static void init() {
        xmlTranslator = new XStream();
        setupXMLTranslatorAliases();
    }
    
    
    /**
     * Prints the given string to the current file in which to save.
     * The old contents are overwritten.
     * 
     * @param aText the string to print
     * @param aFileName the name of the file to print to
     */
    public static void printToFile(final String aText, final String aFileName) {
        
        OutputStreamWriter writer = null;
        
        try {
            final FileOutputStream outputStream = 
                new FileOutputStream(aFileName); 
            writer = new OutputStreamWriter(outputStream, CHARACTER_ENCODING);
            writer.write(aText);
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    
    
    /**
     * Get the user's current model in string form.
     * 
     * @return an XML string representing the user's current model
     */
    public static String getTextToSave() {
        final String result = new XStream().toXML(new SavableModel());
        return result;
    }
    
    
    /**
     * Try to generate a SavableModel from the XML in the given file.
     * If that works, then restore the saved model.
     * 
     * @param file the file containing an XML description of a SavableModel
     */
    public static void loadObjectFromFile(final File file) {
        final String fileName = file.getName();
        final String fileText = getFileText(file);
        SavableModel inflatedModel = null;
        try {
            inflatedModel = (SavableModel) new XStream().fromXML(fileText);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        
        Loader.loadSavedModel(inflatedModel, fileName);
    }
    
    
    
    /**
     * Get the text of a file by file name.
     * 
     * @param file the file to load
     * @return the text stored in the file
     */
    private static String getFileText(final File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(
                new FileInputStream(file)
           );
            final StringBuilder builder = new StringBuilder();
            
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            
            return builder.toString();
        } catch (final Exception exception) {
            exception.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        
        return null;
    }
    
    
    /**
     * Provide aliases for the XML translator, 
     * so it will print more readable class
     * names in its XML output.
     */
    private static void setupXMLTranslatorAliases() {
        xmlTranslator.alias("savableModel", SavableModel.class);
        xmlTranslator.alias("blockTemplate", BlockTemplate.class);
    }
}
