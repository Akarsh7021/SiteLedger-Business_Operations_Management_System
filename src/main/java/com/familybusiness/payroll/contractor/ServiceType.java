package com.familybusiness.payroll.contractor;

public enum ServiceType {
    DEEP_FULL_SERVICE_CLEANUP("Deep/ Full Service cleanup"),
    GENERAL_CLEANUP("General Cleanup"),
    PRESSURE_WASH("Pressure Wash"),
    HANDOVER_CLEANUP("The Handover Cleanup");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
