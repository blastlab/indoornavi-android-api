# __Phone module__

Phone module allows saving data in the database.  
You can register a user with custom data or save coordinates.

### Phone Register

Register phone to get an assigned id.

```java
PhoneModule phoneModule = new PhoneModule("http://localhost:90", inMap);
this.phoneID = phoneModule.registerPhone("user data");
```

### Save Coordinates

After obtaining the assigned id, you can save the coordinates.

```java
phoneModule.saveCoordinates(new Coordinates(position.x, position.y, position.z, this.phoneID, new Date().getTime()));
```