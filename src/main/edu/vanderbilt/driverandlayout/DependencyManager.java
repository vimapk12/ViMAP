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

import java.util.HashMap;
import java.util.Map;

public final class DependencyManager {

    private final Map<ObjectId, Object> objects;
    private static volatile DependencyManager dependencyManager;
    
    private DependencyManager() {
        this.objects = new HashMap<ObjectId, Object>();
    }

    public static DependencyManager getDependencyManager() {
        if (dependencyManager == null) {
            dependencyManager = new DependencyManager();
        }
        
        return dependencyManager;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getObject(final Class<T> aClass, final String aName) {
        final ObjectId inputId = new ObjectId(aClass, aName);
        if (!this.objects.containsKey(inputId)) {
            throw new IllegalArgumentException(aClass + " " + aName);
        }
        
        return (T) this.objects.get(inputId);
    }
    
    public void addObject(final Object o, final String name) {
        final ObjectId inputId = new ObjectId(o, name);
        
        if (this.objects.containsKey(inputId)) {
            throw new IllegalArgumentException("Object already present.");
        }
        
        this.objects.put(inputId, o);
    }

    private final class ObjectId {
        private final Class<?> myClass;
        private final String name;
        
        public ObjectId(final Class<?> aClass, final String aName) {
            this.myClass = aClass;
            this.name = aName;
        }
        
        public ObjectId(final Object aObject, final String aName) {
            this.myClass = aObject.getClass();
            this.name = aName;
        }

        private DependencyManager getOuterType() {
            return DependencyManager.this;
        }
        
        // ignore myClass, because we want ObjectId's of superclasses
        // to be evaluated by the full equals() method for equality
        // when stored in a hash table.
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result;
            if (this.name != null) {
                result += name.hashCode();
            }
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            
            final ObjectId other = (ObjectId) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.myClass == null) {
                if (other.myClass != null) {
                    return false;
                }
            } else if (!this.myClass.isAssignableFrom(other.myClass)) {
                // return false if this.myClass is not the same class
                // or a superclass or interface of other.myClass
                return false;
            }
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ObjectId [myClass=");
            builder.append(this.myClass);
            builder.append(", name=");
            builder.append(this.name);
            builder.append("]");
            return builder.toString();
        }  
    }
}
