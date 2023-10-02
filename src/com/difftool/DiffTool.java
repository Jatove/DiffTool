package com.difftool;

import com.difftool.util.AuditKey;
import com.difftool.util.ChangeType;
import com.difftool.util.ListUpdate;
import com.difftool.util.PropertyUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.*;

public class DiffTool {
    public static List<ChangeType> diff(Object previous, Object current) {
        List<ChangeType> changes = new ArrayList<>();
        Map<String, Object> previousMap;
        Map<String, Object> currentMap;

        if (previous == null && current == null) {
            return changes;
        } else if (previous == null) {
            changes.add(new PropertyUpdate("root", null, current));
            return changes;
        } else if (current == null) {
            changes.add(new PropertyUpdate("root", previous, null));
            return changes;
        }

        if (previous.getClass() != current.getClass()) {
            throw new IllegalArgumentException("Objects must be of the same type");
        }

        previousMap = getFields(previous);
        currentMap = getFields(current);

        if (previousMap == null || currentMap == null) {
            return null;
        }

        for (Map.Entry<String, Object> entry : previousMap.entrySet()) {
            String property = entry.getKey();
            Object previousValue = entry.getValue();
            Object currentValue = currentMap.get(property);

            if (previousValue == null && currentValue == null) {
                continue;
            } else if (previousValue == null) {
                changes.add(new PropertyUpdate(property, null, currentValue));
                continue;
            } else if (currentValue == null) {
                changes.add(new PropertyUpdate(property, previousValue, null));
                continue;
            }

            if (previousValue.getClass() != currentValue.getClass()) {
                throw new IllegalArgumentException("Objects must be of the same type");
            }

            if (previousValue instanceof List) {
                List<Object> previousList = (List<Object>) previousValue;
                List<Object> currentList = (List<Object>) currentValue;

                if (itHasProperties(previousList.get(0))) {
                    // is a list of objects
                    for (int i = 0; i < previousList.size(); i++) {
                        Object previousListItem = previousList.get(i);
                        Object currentListItem = currentList.get(i);

                        List<ChangeType> nestedChanges = diff(previousListItem, currentListItem);
                        if (nestedChanges == null) {
                            if (!previousListItem.equals(currentListItem)) {
                                changes.add(new PropertyUpdate(property, previousListItem, currentListItem));
                            }
                        } else {
                            for (ChangeType change : nestedChanges) {
                                if (change instanceof PropertyUpdate propertyUpdate) {
                                    String idField = getIdField(previousListItem);
                                    if (idField != null) {
                                        propertyUpdate.setProperty(property + "[" + idField + "]." + propertyUpdate.getProperty());
                                    } else {
                                        throw new IllegalArgumentException("Objects must have an id field");
                                    }
                                }
                            }
                            changes.addAll(nestedChanges);
                        }
                    }
                } else {
                    // is a list of primitives
                    List<Object> added = new ArrayList<>();
                    List<Object> removed = new ArrayList<>();
                    for (Object previousListItem : previousList) {
                        if (!currentList.contains(previousListItem)) {
                            removed.add(previousListItem);
                        }
                    }
                    for (Object currentListItem : currentList) {
                        if (!previousList.contains(currentListItem)) {
                            added.add(currentListItem);
                        }
                    }
                    changes.add(new ListUpdate(property, added, removed));
                }
            } else if (!previousValue.equals(currentValue)) {
                //Determine if is a nested property and add property name to the path
                List<ChangeType> nestedChanges = diff(previousValue, currentValue);
                if (nestedChanges == null) {
                    changes.add(new PropertyUpdate(property, previousValue, currentValue));
                } else {
                    for (ChangeType change : nestedChanges) {
                        if (change instanceof PropertyUpdate propertyUpdate) {
                            propertyUpdate.setProperty(property + "." + propertyUpdate.getProperty());
                        } else if (change instanceof ListUpdate listUpdate) {
                            listUpdate.setProperty(property + "." + listUpdate.getProperty());
                        }
                    }
                    changes.addAll(nestedChanges);
                }
            }
        }

        return changes;
    }

    private static Map<String, Object> getFields(Object object) {
        Map<String, Object> fields = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                fields.put(field.getName(), field.get(object));
            } catch (InaccessibleObjectException | IllegalAccessException e) {
                return null;
            }
        }
        return fields;
    }

    private static String getIdField(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                if (field.getName().equalsIgnoreCase("id") || field.getAnnotation(AuditKey.class) != null) {
                    field.setAccessible(true);
                    return field.get(object).toString();
                }
            } catch (IllegalAccessException | InaccessibleObjectException e) {
                return null;
            }
        }
        return null;
    }

    private static boolean itHasProperties(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(object) != null) {
                    return true;
                }
            } catch (InaccessibleObjectException | IllegalAccessException e) {
                return false;
            }
        }
        return false;
    }
}
