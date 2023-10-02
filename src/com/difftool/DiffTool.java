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
        Map<String, Object> previousMap = new HashMap<>();
        Map<String, Object> currentMap = new HashMap<>();

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

        for (Field field : previous.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                previousMap.put(field.getName(), field.get(previous));
                currentMap.put(field.getName(), field.get(current));
            } catch (InaccessibleObjectException | IllegalAccessException e) {
                return null;
            }
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

                for (int i = 0; i < previousList.size(); i++) {
                    Object previousListItem;
                    try {
                        previousListItem = previousList.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        changes.add(new ListUpdate(property, new ArrayList<>(), currentList));
                        break;
                    }
                    Object currentListItem;
                    try {
                        currentListItem = currentList.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        changes.add(new ListUpdate(property, previousList, new ArrayList<>()));
                        break;
                    }
                    List<ChangeType> nestedChanges = diff(previousListItem, currentListItem);
                    if (nestedChanges == null) {
                        if (!previousListItem.equals(currentListItem)) {
                            changes.add(new PropertyUpdate(property, previousListItem, currentListItem));
                        }
                    } else {
                        for (ChangeType change : nestedChanges) {
                            if (change instanceof PropertyUpdate propertyUpdate) {
                                for (Field field : previousList.get(i).getClass().getDeclaredFields()) {
                                    try {
                                        if (field.getName().equalsIgnoreCase("id") || field.getAnnotation(AuditKey.class) != null) {
                                            field.setAccessible(true);
                                            propertyUpdate.setProperty(property + "[" + field.get(previousList.get(i)) + "]." + propertyUpdate.getProperty());
                                            break;
                                        } else {
                                            throw new IllegalArgumentException("Objects must have an id field");
                                        }
                                    } catch (IllegalAccessException | InaccessibleObjectException e) {
                                        return null;
                                    }
                                }
                            }
                        }
                        changes.addAll(nestedChanges);
                    }
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

    private Map<String, Object> getFields(Object object) {
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

    private boolean itHasIdField(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                if (field.getName().equalsIgnoreCase("id") || field.getAnnotation(AuditKey.class) != null) {
                    return true;
                }
            } catch (InaccessibleObjectException e) {
                return false;
            }
        }
        return false;
    }

    private boolean itHasProperties(Object object) {
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
