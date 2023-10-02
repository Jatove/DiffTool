package com.difftool.util;

import java.util.List;

public final class ListUpdate implements ChangeType {
    private String property;
    private final List<Object> added;
    private final List<Object> removed;

    public ListUpdate(String property, List<Object> added, List<Object> removed) {
        this.property = property;
        this.added = added;
        this.removed = removed;
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
                ", \"added\":" + "\"" + added + "\"" +
                ", \"removed\":" + "\"" + removed + "\"" +
                "}";
    }
}
