# __INNavigation__

Indoor navigation is used to guide people to specific points on the map.
The technology uses Beacon devices spread over the map and estimate localization based on the Bluetooth signal.
Based on the drawn paths and starting and ending points, the shortest path to the destination is determined.

## __Navigation object__

Initializes the navigation from point A to B with the given accuracy.
INNavigation object has to be created after the floor is loaded.
The accuracy of the navigation means in what radius the position should be pulled to the path. 

- `0` indicates that no accuracy shouldn't be taken and all calculated position will be pulled to the path. 

```java
INNavigation inNavigation = new INNavigation(this, this.inMap);
inNavigation.startNavigation(new Point(3395, 123), new Point(2592, 170), 0, new OnNavigationMessageReceive<String>() {
	@Override
	public void onMessageReceive(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
});
```
After initiation navigation, you can handle a message, containing info about navigation status. <br>

- `created` - send when navigation algorithm is started<br>
- `finished` - send when reached destination point<br>
- `error` - send when the error occurred, for example, it's not possible to specify a path, or the end and start point are not connected by any path <br>
- `working` - send when you call `startNavigation` again when navigation is already running<br>


### Update position  

The position update is fully automatic at the next calculated position is available.

### Stop 

It stops the navigation immediately.

```java
inNavigation.stopNavigation();
```

### Restart

Allows recalculating the shortest path.

```java
inNavigation.restartNavigation();
```
