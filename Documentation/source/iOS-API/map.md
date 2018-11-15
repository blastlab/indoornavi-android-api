# __INMap object__

INMap is the main class of the API, responsible for communication with frontend server. It inherits from UIView, so it can be added to application's interface. It corresponds to the floor you want to display.
All other objects are created corresponding to the given INMap object and they require an INMap instance.

```swift
// Create an INMap
var map = INMap(frame: mapFrame, targetHost: "http://mybuilding.com", apiKey: "apiKey")
// Load map with floor specified by floorID
map.load(floorID) {
  // This block is called when map has fully loaded.
  print("Map loaded.")
}
```

## __Usage__

As a developer you can download the list of available complexes with buildings and floors.

```swift
map.getComplexes { complexes in
  // Your functionality...
}
```

If map created on the frontend server have published paths, you can pull given point to the nearest corresponding place on path. A possible use might be to place a circle in place of pulled position.

```swift
map.pullToPath(point: yourPosition, accuracy: 0) { pulledPosition in
  circle.position = pulledPosition
  circle.draw()
}
```

Another important feature is possibility to add a long click listener, invoked when map register a long tap. A possible use is to place a marker in this position.

```swift
map.addLongClickListener { pulledPosition in
  marker.position = pulledPosition
  marker.draw()
}
```
