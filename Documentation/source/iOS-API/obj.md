# __Map Objects__

## __INObject__

INObject is a base class of all classes representing objects on map. Every instance has its own `objectID`, which is nil until object is fully initialized. You can also get points of the given object or check if given coordinates are inside of it. Examples are shown below:

___Swift___
```swift
// Function is asynchronous, it takes callback with array of points as an input argument
object.getPoints() { points in
  // Your functionality...
}

// Function is asynchronous, it takes callback with boolean value as an input argument
object.isWithin(coordinates: yourGivenCoordinates) { isWithin in
  // Your functionality...
}
```

___Objective-C___
```objc
// Function is asynchronous, it takes callback with array of points and its size as input arguments
[object getPointsWithCallbackHandler:^(const INPoint points[], NSInteger size) {
  // Your functionality...
}];

// Function is asynchronous, it takes callback with boolean value as an input argument
[object isWithinCoordinates:yourGivenCoordinates withSize:sizeOfGivenCoordinates callbackHandler:^(BOOL isWithin) {
  // Your functionality...
}];
```

## __INPolyline__

An INPolyline is a series of connected points, which represents a path. Points should be given in real-world dimensions. Additionally, you can specify the color of the created path.

___Swift___
```swift
let polyline = INPolyline(withMap: map)
polyline.set(points: pointsArray)
polyline.setColorWith(red: 0.3, green: 0.8, blue: 0.3)
polyline.draw()
```

___Objective-C___
```objc
INPolyline* polyline = [[INPolyline alloc] initWithMap:blueComponent];
[polyline setPointsArray:pointsArray withArraySize:arraySize];
[polyline setColorWithRed:0.3 green:0.8 blue:0.3];
[polyline draw];
```

## __INArea__

An INArea is a closed area, defined with a series of points which starts and ends at the same point. Points should be given in real-world dimensions. Additionally, you can change color and opacity of the created area.

___Swift___
```swift
let area = INArea(withMap: yourMap)
area.set(points: yourPointsArray)
area.setFillColor(red: 0.3, green: 0.8, blue: 0.3)
area.setOpacity(0.5)
area.draw()
```

___Objective-C___
```objc
INArea* area = [[INArea alloc] initWithMap:yourMap];
[area setPointsArray:pointsArray withArraySize:arraySize];
[area setFillColorWithRed:0.3 green:0.8 blue:0.3];
[area setOpacity:0.5];
[area draw];
```

## __INMarker__

An INMarker represents a single location on the map. You can customize your marker by changing the default icon and adding your own label.
Additionally, you can add a click event on the marker and proper actions.

___Swift___
```swift
// Create INMarker
let marker = INMarker(withMap: yourMap)
marker.set(point: INPoint(x: 600, y: 600))
marker.setIcon(withPath: "https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
marker.setLabel(withText: "This is label")
marker.draw()
```

___Objective-C___
```objc
INMarker* marker = [[INMarker alloc] initWithMap:yourMap];
[marker setPoint:INPointMake(600, 600)];
[marker setIconWithPath:@"https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png"];
[marker setLabelWithText:@"This is label"];
[marker draw];
```

It's possible to add an EventListener to the Marker object. An event is invoked after clicking on the marker.

___Swift___
```swift
marker.addEventListener {
  print("On click event occured.")
}
```

___Objective-C___
```objc
[marker addEventListenerOnClickCallback:^{
  NSLog(@"On click event occured.");
}];
```

## __INInfoWindow__

For INMarker object, you can add INInfoWindow. An info window allows you to display necessary information.
Additionally, you can set the height and width of the window, custom content and position according to the marker you display on.
Content can be simple HTML code, it gives you a possibility to design your own unique message.

___Swift___
```swift
let infoWindow = INInfoWindow(withMap: yourMap)
infoWindow.setInnerHTML(string: "<h2>Lorem ipsum dolor sit amet</h2>"
infoWindow.position = .top
infoWindow.height = 100
infoWindow.width = 100
```

___Objective-C___
```objc
INInfoWindow* infoWindow = [[INInfoWindow alloc] initWithMap:yourMap];
[infoWindow setInnerHTMLWithString:@"<h2>Lorem ipsum dolor sit amet</h2>"];
[infoWindow setPosition:PositionTop];
infoWindow.height = 100;
infoWindow.width = 100;
```

You can attach an info window to a marker:

___Swift___
```swift
marker.add(infoWindow: infoWindow)
```

___Objective-C___
```objc
[marker addInfoWindow:infoWindow];
```
