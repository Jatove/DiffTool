# DiffTool
## A simple tool to compare two objects and return the differences
### Usage
```java
Use to compare two objects of the same type and return the differences
List<ChangeType> changes = DiffTool.diff(Object obj1, Object obj2);

it will return a list with changes that can be printed
```
### Example
```java
User user1 = new User("John", "Doe", 25);
User user2 = new User("John", "Doe", 26);

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="age", "previous"="25", "current"="26"}

It can also check lists
User user1 = new User("John", "Doe", 25, Arrays.asList("one", "two", "three"));
User user2 = new User("John", "Doe", 25, Arrays.asList("one", "two", "four"));

List<ChangeType> changes = DiffTool.diff(user1, user2);

for (ChangeType change : changes) {
    System.out.println(change);
}

// Output
// {"property"="friends", "added"="[four]", "removed"="[three]"}
```


