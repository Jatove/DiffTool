package com.difftool.util;

public final class PropertyUpdate implements ChangeType {
    private String property;
    private final Object previous;
    private final Object current;

    public PropertyUpdate(String property, Object previous, Object current) {
        this.property = property;
        this.previous = previous;
        this.current = current;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "{" +
                "\"property\":" + "\"" + property + '\"' +
                ", \"previous\":" + "\"" + previous + "\"" +
                ", \"current\":" + "\"" + current + "\"" +
                "}";
    }
}
