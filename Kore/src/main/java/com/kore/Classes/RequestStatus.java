package com.kore.Classes;

public enum RequestStatus {
    FAILED(-1, "FAILED"),
    PENDING(0, "PENDING"),
    PROCESSED(1, "PROCESSED");

    private final int code;
    private final String label;

    RequestStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
    
    public static RequestStatus fromCode(int code) {
        for (RequestStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status inválido: " + code);
    }
    
    public static RequestStatus fromLabel(String label) {
        for (RequestStatus status : values()) {
            if (status.label.equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Label de status inválido: " + label);
    }
}
