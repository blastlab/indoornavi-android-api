# __Phone module__

Phone module allows to register user and allows to save coordinates in the database.

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