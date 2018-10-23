# __INNavigation__

## __Navigation object__

Initializes the navigation from point A to B with the given accuracy.

```java
INNavigation inNavigation = new INNavigation(this, this.inMap);
inNavigation.startNavigation(new Point(3395, 123), new Point(2592, 170), 0, new OnNavigationMessageReceive<String>() {
	@Override
	public void onMessageReceive(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
});
```


### Update position

The position update is fully automatic at the next calculated position is available.

### Stop 

It stops the navigation immediately

```java
inNavigation.stopNavigation();
```

### Restart

Allows to recalculate the shortest path.

```java
inNavigation.restartNavigation();
```