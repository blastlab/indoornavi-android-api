# __Interacting with a Map__

## __Zoom gestures__

The floor map responds to standard gestures:
- Double tap zooming
- Pinching inward to zoom out
- Pinching outward to zoom in

## __Events__

Using the IndoorNavi API for iOS, you can listen to events that occur on the map, such as long click or marker tap events.

### __Marker click events__

You can listen and react to marker tap events.  

```swift
marker.addEventListener {
  print("Marker was tapped.")
}
```

### __Area click events__

You can listen and react to area tap events.  

```swift
area.addEventListener {
  print("Area was tapped.")
}
```

### __Long Click Events__

You can apply listener on the map which reacts on long touch.
After the event occurred you get an `INPoint` given in centimeters, corresponding to touching place.

```swift
map.addLongClickListener { point in
  marker.set(point: point)
  marker.draw()
}
```

### __Area Events__

There are two types of events to be distinguished:
- Area events (entering or leaving area)
- Coordinates events (change of tag's coordinates)

After adding an area in frontend server, you can listen to events on enter or exit the area.
This allows you to control movements of the tag.

```swift
// Adds area event listener
map.addAreaEventListener { areaEvent in
  print("Area event occured!")
}
```

Or you can listen to whether the coordinates have been reached.
This allows to fully control the tag's movements.

```swift
// Adds coordinates event listener
map.addCoordinatesEventListener { coordinates in
  print("Coordinates event occured!")
}
```
