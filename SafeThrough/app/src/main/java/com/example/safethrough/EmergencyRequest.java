package com.example.safethrough;

import com.google.gson.annotations.SerializedName;

public class EmergencyRequest {
    @SerializedName("emergency_type")
    private String emergencyType;

    @SerializedName("location")
    private String location;

    @SerializedName("details")
    private String details;

    @SerializedName("timestamp")
    private long timestamp;

    public EmergencyRequest(String emergencyType, String location, String details) {
        this.emergencyType = emergencyType;
        this.location = location;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getEmergencyType() {
        return emergencyType;
    }

    public String getLocation() {
        return location;
    }

    public String getDetails() {
        return details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
