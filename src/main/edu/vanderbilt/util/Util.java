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


package edu.vanderbilt.util;

import java.util.ArrayList;
import java.util.List;

public abstract class Util {
    
    public static final boolean DEBUGGING = false;
    
    public static String quoteIfNotANumber(final String input) {
        if (input.length() == 0) {
            return input;
        }
        
        if (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"') {
            // already in quotes
            return input;
        }
        
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e) {
            // not a number, so quote the string
            return "\"" + input + "\"";
        }
        
        // input is a number, so don't quote it
        return input;
    }
    
    public static <T> List<T> copyList(final List<T> input) {
        List<T> result = new ArrayList<T>();
        result.addAll(input);
        return result;
    }
    
    public static <T> T[] listToArray(final List<T> list) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[list.size()];
        
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        
        return result;
    }
    
    public static <T> T[] cloneArray(final T[] oldArray) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[oldArray.length];
        for (int i = 0; i < oldArray.length; i++) {
            result[i] = oldArray[i];
        }
        
        return result;
    }
    
    public static List<String> stringListFromObjectList(
        final List<Object> input
    ) {
        List<String> result = new ArrayList<String>();
        for (Object o: input) {
            result.add((String) o);
        }
        
        return result;
    }
    
    public static String[] getStringArray(final List<String> list) {
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        
        return result;
    }
    
    public static void printIfDebugging(final String text) {
        if (DEBUGGING) {
            System.out.println(text);
        }
    }
    
    /**
     * Reports the square of the distance between points a (ax, ay) and
     * b (bx, by).
     * 
     * @param ax x-coordinate of point a
     * @param ay y-coordinate of point a
     * @param bx x-coordinate of point b
     * @param by y-coordinate of point b
     * @return the square of the distance between the points
     */
    public static int distanceSquared(
        final int ax,
        final int ay,
        final int bx,
        final int by
     ) {
        int dx = (ax - bx);
        int dy = (ay - by);
        return dx * dx + dy * dy;
     }
}
