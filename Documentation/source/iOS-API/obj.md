# __Map Objects__

## __INObject__

INObject is a base class of all classes representing objects on map. Every instance has its own `objectID`, which is *nil* until object is fully initialized. You can also get points of the given object or check if given coordinates are inside of it. Examples are shown below:

```swift
// Function is asynchronous, it takes callback with an array of points as an input argument
object.getPoints() { points in
  // Your functionality...
}

// Function is asynchronous, it takes callback with boolean value as an input argument
object.isWithin(coordinates: yourGivenCoordinates) { isWithin in
  // Your functionality...
}
```

<div class="note">      
   Note: For every object that inherits from INObject, to apply changes it's necessary to call draw() method after setting properties. Object is then synchronized and displayed on the map.
</div>

## __INPolyline__

An INPolyline is a series of connected points, which represents a path. Points should be given in real-world dimensions. Additionally, you can specify the color of the created path.

```swift
// Create a polyline object.
let polyline = INPolyline(withMap: map)
// Set polyline's properties.
polyline.points = yourPointsArray
polyline.color = .green
// Draw polyline.
polyline.draw()
```

## __INArea__

An INArea is a closed area, defined with a series of points which starts and ends at the same point. Points should be given in real-world dimensions. Additionally, you can change color and opacity of the created area.

```swift
// Create an area object.
let area = INArea(withMap: yourMap)
// Set area's properties.
area.points = yourPointsArray
area.color = .green
area.border = Border(width: 2, color: .green)
// Draw area.
area.draw()
```

It’s possible to add an event listener to an INArea object. The block is invoked when the area is tapped.

```swift
area.addEventListener {
  print("Area was tapped!")
}
area.draw()
```

Additionaly you can check if given coordinates are located inside area.

```swift
area.isWithin(coordinates: yourPoint) { isWithin in
  print("Is within: \(isWithin)")
}
```

You can also access area's middle point.

```swift
print("Area's center: \(area.center)")
```

## __INMarker__

An INMarker represents a single location on the map. You can customize your marker by changing the default icon and adding your own label.

```swift
// Create a marker object.
let marker = INMarker(withMap: yourMap)
// Set marker's properties.
marker.position = INPoint(x: 600, y: 600)
marker.label = "This is a label"
// Draw marker.
marker.draw()
```

Changing default icon can be done either by setting path to an icon to an *iconPath* property or by assigning custom *UIImage* object to an *icon* property. Both values are optional and *nil* by default, with *icon* property having higher priority. So when both set, *icon* is being used.

```swift
// Set an icon as a path.
marker.iconPath = "https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png"
// Set an icon as a UIImage.
marker.icon = UIImage(named: "icon")
```

It's possible to add an event listener to the marker object. The block is invoked when the marker is tapped.

```swift
marker.addEventListener {
  print("Marker was tapped!")
}
```

## __INInfoWindow__

For INMarker object, you can add INInfoWindow. An info window allows you to display necessary information.
Additionally, you can set the height and width of the window, custom content and position according to the marker you display on.
Content can be simple HTML code, it gives you a possibility to design your own unique message.

```swift
// Create an info window object.
let infoWindow = INInfoWindow(withMap: yourMap)
// Set info window's properties.
infoWindow.width = 100
infoWindow.height = 100
infoWindow.position = .top
infoWindow.content = "<h2>Lorem ipsum dolor sit amet</h2>"
```

You can attach an info window to a marker:

```swift
marker.add(infoWindow: infoWindow)
```

## __INCircle__

An INCircle object represents a circle which can map a specific location. You can customize it's color, radius and border.

```swift
// Create circle object.
let circle = INCircle(withMap: self.map)
// Set circle's properties.
circle1.position = INPoint(x: 600, y: 600)
circle.color = .red
circle.radius = 10
circle.border = Border(width: 5, color: .blue)
// Draw circle.
circle.draw()
```
