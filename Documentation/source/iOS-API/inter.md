# __Interacting with a Map__

## __Zoom gestures__

The floor map responds to standard gestures:
- Double tap zooming
- Pinching inward to zoom out
- Pinching outward to zoom in

## __Events__

Using the IndoorNavi API for iOS, you can listen to events that occur on the map, such as long click or marker tap events.

### __Marker click events__

You can listen and react to marker click events.  

___Swift___
```swift
marker.addEventListener {
  print("Marker was touched.")
}
```

___Objective-C___
```objc
[marker addEventListenerOnClickCallback:^{
  NSLog(@"Marker was touched.");
}];
```

### __Long Click Events__

You can apply listener on the map which reacts on long touch.
After the event occurred you get `INPoint` given in pixels, corresponding to touching place.

___Swift___
```swift
map.addLongClickListener { point in
  let marker = INMarker(withMap: self.map)
  let pointWithRealCoordinates = MapHelper.realCoordinates(fromPixel: point, scale: self.map.scale!)
  marker.set(point: pointWithRealCoordinates)
  marker.draw()
}
```

___Objective-C___
```objc
[map addLongClickListener:^(INPoint point){
  INMarker* marker = [[INMarker alloc] initWithMap:self->map];
  INPoint pointWithRealCoordinates = [MapHelper realCoordinatesFromPixel:point scale:self->map.scale];
  [marker setPoint:pointWithRealCoordinates];
  [marker draw];
}];
```

### __Area Events__

There are two types of events to be distinguished:
- Area events (entering or leaving area)
- Coordinates events (change of tag's coordinates)

After adding an area in frontend server, you can listen to events on enter or exit the area.
This allows you to control movements of the tag,

___Swift___
```swift
// Adds area event listener
map.addAreaEventListener { areaEvent in
  print("Area event occured!")
}
```

___Objective-C___
```objc
// Adds area event listener
[map addAreaEventListener:^(AreaEvent* areaEvent) {
  NSLog(@"Area event occured!");
}];
```

Or you can listen to whether the coordinates have been reached.
This allows to fully control the tag's movements.

___Swift___
```swift
// Adds coordinates event listener
map.addCoordinatesEventListener { coordinates in
  print("Coordinates event occured!")
}
```

___Objective-C___
```objc
// Adds coordinates event listener
[map addCoordinatesEventListener:^(Coordinates* coordinates) {
  NSLog(@"Coordinates event occured!");
}];
```
