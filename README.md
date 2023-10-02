# DiffTool
## A simple tool to compare two objects and return the differences
### Requirements
    1.- Handle Null values on both the previous and current objects.
    2.- If a property is updated, the system should track the name of the property, 
        the previous value, and the current value.
    3.- If the property is nested, the property name should use dot notation to 
        indicate the full path.
    4.- If items are added or removed from a list, the system should track the name 
        of the property, the list of items that were added, and the list of items 
        that were removed.
    5.- If the property was updated on an object within a list, the property name 
        should include square brackets with the id of the list item.
        - The id of a list item must be a field annotated with @AuditKey or have 
            the name 'id'. If no field meets this requirement, throw an exception 
            that indicates that the audit system lacks the information it needs to 
            determine what has changed.


