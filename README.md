# DiffTool
## A simple tool to compare two objects and return the differences
### Usage
```java
Use to compare two objects of the same type and return the differences
List<ChangeType> changes = DiffTool.diff(Object obj1, Object obj2);

it will return a list with changes that can be printed
```
## Examples
### Simple property difference
```java
User user1 = new User("John", "Doe", 25);
User user2 = new User("John", "Doe", 26);

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="age", "previous"="25", "current"="26"}
```

### Nested property difference
```java
User user1 = new User("John", "Doe", 25, new Car("Ford", "Fiesta", 2010));
User user2 = new User("John", "Doe", 25, new Car("Ford", "Fiesta", 2011));

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="car.year", "previous"="2010", "current"="2011"}
```

### List property difference
```java 
User user1 = new User("John", "Doe", 25, Arrays.asList("Jane", "Jason"));
User user2 = new User("John", "Doe", 25, Arrays.asList("Jane", "Jackson", "Barack"));

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="friends", "added"="[Jackson, Barack]", "removed"="[Jason]"}
```

### List property difference with nested objects
```java
User user1 = new User("John", "Doe", 25, Arrays.asList(new Car("c_1", "Ford", "Fiesta", 2010), new Car("c_2", "Ford", "Focus", 2011)));
User user2 = new User("John", "Doe", 25, Arrays.asList(new Car("c_1", "Ford", "Fiesta", 2010), new Car("c_2", "Ford", "Focus", 2012)));

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="cars[c_2].year", "previous"="2011", "current"="2012"}
```

### Throws an exception if the nested object in list has no id or AuditKey annotation
```java
User user1 = new User("John", "Doe", 25, Arrays.asList(new Car("Ford", "Fiesta", 2010), new Car("Ford", "Focus", 2011)));
User user2 = new User("John", "Doe", 25, Arrays.asList(new Car("Ford", "Fiesta", 2010), new Car("Ford", "Focus", 2012)));

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// Exception in thread "main" java.lang.IllegalArgumentException: Object in the list has no id or AuditKey annotation
```

### Throws an exception if the objects are not of the same type
```java
User user1 = new User("John", "Doe", 25);
User user2 = new Employee("John", "Doe", 25);

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// Exception in thread "main" java.lang.IllegalArgumentException: Objects must be of the same type
```


