package com.team33.FDMGamification.Model;

public enum Stream {
    ST("Software Testing"), BI("Business Intelligence"), TO("Testing Operation"), NONE("NONE");

    private final String fullName;

    Stream(String fullName){
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
    
}
