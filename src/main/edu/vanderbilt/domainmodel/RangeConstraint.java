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


package edu.vanderbilt.domainmodel;

public final class RangeConstraint implements Constraint {
        
    private final double min;
    private final double max;
    
    public RangeConstraint(
        final double aMin,
        final double aMax
    ) {
        assert aMin <= aMax;
        this.min = aMin;
        this.max = aMax;
    }
    
    public RangeConstraint(
        final int aMin,
        final int aMax
    ) {
        assert aMin <= aMax;
        this.min = aMin;
        this.max = aMax;
    }
    
    public RangeConstraint(
        final float aMin,
        final float aMax
    ) {
        assert aMin <= aMax;
        this.min = aMin;
        this.max = aMax;
    }
    
    @Override
    public boolean verify(final Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(
            o instanceof Integer 
            || o instanceof Double 
            || o instanceof Float
        )) {
            return false;
        }
        
        Double doubleO = Double.parseDouble("" + o);
        
        return doubleO >= this.min && doubleO <= this.max;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RangeConstraint [min=");
        builder.append(this.min);
        builder.append(", max=");
        builder.append(this.max);
        builder.append("]");
        return builder.toString();
    }
}
