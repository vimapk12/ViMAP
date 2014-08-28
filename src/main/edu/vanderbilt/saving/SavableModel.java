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


package edu.vanderbilt.saving;

import edu.vanderbilt.domainmodel.DomainModel;
import edu.vanderbilt.driverandlayout.DependencyManager;
import edu.vanderbilt.usermodel.UserModel;


public final class SavableModel {
    
    // the name of the NetLogo file to save to or load
    private String netLogoFileName; 
    private DomainModel domainModel;
    private UserModel userModel;
    
    /**
     * Constructor.
     */
    public SavableModel() {
        this.domainModel = DependencyManager.getDependencyManager().
            getObject(DomainModel.class, "domainModel");
        this.userModel = DependencyManager.getDependencyManager().
            getObject(UserModel.class, "userModel");
        this.netLogoFileName = this.domainModel.getNetLogoFileName();
    }
    
    public String getNetLogoFileName() {
        return this.netLogoFileName;
    }
    
    public DomainModel getDomainModel() {
        return this.domainModel;
    }
    
    public UserModel getUserModel() {
        return this.userModel;
    }
    
    public void setDomainModel(final DomainModel aDomainModel) {
        this.domainModel = aDomainModel;
    }
    
    public void setUserModel(final UserModel aUserModel) {
        this.userModel = aUserModel;
    }
    
    public void setNetLogoFileName(final String name) {
        this.netLogoFileName = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SavableModel [netLogoFileName=");
        builder.append(this.netLogoFileName);
        builder.append(", domainModel=");
        builder.append(this.domainModel);
        builder.append(", userModel=");
        builder.append(this.userModel);
        builder.append("]");
        return builder.toString();
    }
}
