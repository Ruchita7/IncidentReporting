package com.example.incidentreport.model;

/**
 * Enum for various incident status - New, Assigned and Closed
 */
public enum IncidentStatus {
    NEW(1, "New"), ASSIGNED(2, "Assigned"), CLOSED(3, "Closed");
    private int value;
    private String name;

    private IncidentStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static IncidentStatus getIncidentStatusById(int value) {
        for (IncidentStatus incidentStatus : IncidentStatus.values()) {
            if (incidentStatus.value == value) {
                return incidentStatus;
            }
        }
        return null;
    }

    public static IncidentStatus getIncidentStatusByName(String status) {
        for (IncidentStatus incidentStatus : IncidentStatus.values()) {
            if (incidentStatus.getName().equalsIgnoreCase(status)) {
                return incidentStatus;
            }
        }
        return null;
    }
}
