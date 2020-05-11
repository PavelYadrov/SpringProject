package com.netcracker.models;

public enum Status {
    ACTIVE("active"),FROZEN("frozen"),BANNED("banned");
    String stat;
    private Status(String stat){
        this.stat=stat;
    }
}
