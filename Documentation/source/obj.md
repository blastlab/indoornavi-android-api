# __Map Objects__

To create an object you have to use object builder pattern.

## __INPolyline__

An INPolyline is a series of connected points, which represents a path.
Points should be given in real-world dimensions. Additionally, you can specify the color of the created path.

```java
INPolyline inPolyline = new INPolyline.INPolylineBuilder(inMap)
    .points(points)
    .setLineColor(Color.LTGRAY)
    .build();
```

## __INArea__

An INArea is a closed area, defined with a series of points which starts and ends at the same point.
Points should be given in real-world dimensions. Additionally, you can change color and opacity of the created area.

```java
INArea inArea = new INArea.INAreaBuilder(inMap)
    .points(points)
    .setFillColor(Color.GREEN)
    .setOpacity(0.3)
    .build();
```

## __INMarker__

An INMarker represents a single location on the map. You can customize your marker by changing the default icon and adding your own label.
Additionally, you can a add click event on the marker and proper actions.

```java
INMarker inMarker = new INMarker.INMarkerBuilder(inMap)
    .point(new Point(600, 600))
    .setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
    .setLabel("This is label")
    .build();
```

It's possible to add an EventListener to the Marker object. An event is invoked after clicking on the marker.

```java
inMarker.addEventListener(new OnMarkerClickListener() {
    @Override
        public void onClick() {
            Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
        }
    });
```

## __INInfoWindow__

For INMarker object, you can add INInfoWindow. An info window allows you to display necessary information.
Additionally, you can set the height and width of the window, custom content and position according to the marker you display on.
Content can be simple HTML code, it gives you a possibility to design your own unique message.

```java
INInfoWindow inInfoWindow = new INInfoWindow.INInfoWindowBuilder(inMap)
    .height(100)
    .width(100)
    .setInnerHTML("<h2>Lorem ipsum dolor sit amet</h2>")
    .setPosition(INInfoWindow.TOP)
    .build();
```

You can attach an info window to a marker:
```java
inMarker.addInfoWindow(inInfoWindow);
```

## __Objects properties__
You can retrieve some information about an object, like:

#### ID
Returns ID of the given object. ID identify object on the backend server.
```java
inPolyline.getID(id -> { Log.i("MyTag", "ID: " + id); });
```
#### Points
Returns points or point of the given object.
```java
inPolyline.getPoints(points -> Log.i("MyTag", "Points: " +  PointsUtil.pointsToString(points)));
```

#### isWithin
Checks if given Coordinates are inside of the object (functionality refers to the area).  
```java
inArea.isWithin(new Coordinates(200, 800, (short)109999, new Date()), bool -> Log.i("Indoor", "Received value: " + bool));
```
