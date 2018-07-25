# __INMap object__

INMap is the main class of the API, responsible for communication with frontend server. It inherits from UIView, so it can be added to application's interface. It corresponds to the floor you want to display.
All other objects are created corresponding to the given INMap object and they require an INMap instance.

___Swift___
```swift
// Create an INMap
var map = INMap(frame: mapFrame, targetHost: "http://mybuilding.com", apiKey: "apiKey")
// Load map with floor specified by floorID
map.load(floorID)
```

___Objective-C___
```objc
// Create an INMap
INMap* map = [[INMap alloc] initWithFrame:mapFrame targetHost:@"http://mybuilding.com" apiKey:@"apiKey"];
// Load map with floor specified by floorID
[map load:floorID];
```
