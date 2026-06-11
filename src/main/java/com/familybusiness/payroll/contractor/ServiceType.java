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

    public static String displayNameFor(String value) {
        if (value == null || value.isBlank()) {
            return DEEP_FULL_SERVICE_CLEANUP.displayName;
        }
        for (ServiceType type : values()) {
            if (type.name().equals(value)) {
                return type.displayName;
            }
        }
        return value;
    }

    public static boolean isDeepFullServiceCleanup(String value) {
        return DEEP_FULL_SERVICE_CLEANUP.name().equals(value);
    }
}
