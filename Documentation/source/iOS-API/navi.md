# __INNavigation__

Indoor navigation is used to guide people to specific points on the map.
The technology uses Beacon devices spread over the map and estimate localization based on the Bluetooth signal.
Based on the drawn paths and starting and ending points, the shortest path to the destination is determined.

## __Navigation object__

Initializes the navigation from point A to B with the given accuracy.
INNavigation object has to be created after the floor is loaded.
The accuracy of the navigation means in what radius the position should be pulled to the path.
If you want immediate updates of current path, pass a correctly configured *BLELocationManager* object with a running location update.

- `0` indicates that no accuracy shouldn't be taken and all calculated position will be pulled to the path.

```swift
let navigation = INNavigation(map: map, bleLocationManager: bleLocationManager, delegate: self)
navigation.startNavigation(from: INPoint(x: 3395, y: 123), to: INPoint(x: 2592, y: 170), withAccuracy: 0)
```

### Update position  

The position update is fully automatic if the next calculated position is available.

### Stop

Stops the navigation immediately.

```swift
navigation.stopNavigation();
```

### Restart

Allows recalculating the shortest path.

```swift
navigation.restartNavigation();
```

## __Setup a INNavigationDelegate__

A *delegate* is passed as a parameter to a *INNavigationDelegate* initializer. A *INNavigationDelegate* is used to handle navigation's current state.

```swift
func navigationCreated(_ navigation: INNavigation) {
  print("Navigation created.")
}

func navigationFinished(_ navigation: INNavigation) {
  print("Navigation finished.")
}

func errorOccured(in navigation: INNavigation) {
  print("An error occured in navigation.")
}

func navigationIsWorking(_ navigation: INNavigation) {
  print("Navigation is working")
}
```

## __Customizing appearance__

There are some options letting you to customize appearance of the navigation, to fit to your application. Examples are shown below:

### Hiding and showing endpoints

You can set the start and destination points to be hidden or visible on map.

```swift
navigation.startPointHidden = true
navigation.endPointHidden = false
```

### Setting path color

```swift
navigation.pathColor = .red
```

### Setting endpoints settings

You can set color, radius and opacity of the navigation's start and end points.
Additionally, it's possible to change border settings.

```swift
let navigationPointProperties = INNavigation.NavigationPointProperties(radius: 5, border: Border(width: 4, color: .cyan), color: .brown)
navigation.startPointProperties = navigationPointProperties
```
