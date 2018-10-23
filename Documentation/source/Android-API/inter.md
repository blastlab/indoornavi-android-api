# __Interacting with a Map__

## __Zoom gestures__

The floor map responds to standard zoom management gestures:
- Double tap zooming
- Two fingers stretch to zoom out
- Two fingers join to zoom in

## __Events__

API can handle events occurred on the map.

### __Marker click events__

You can listen and react to marker click events.  

```java
inMarker.addEventListener(new OnMarkerClickListener() {
    @Override
        public void onClick() {
            Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
        }
    });
```

### __Area click events__

You can listen and react to area click events.  

```java
inArea.addEventListener(new OnINObjectClickListener() {
	@Override
	public void onClick() {
		Toast.makeText(this, "Event!", Toast.LENGTH_LONG).show();
	}
});
```

### __Long Click Events__

You can apply listener on the map which reacts on long touch.
After the event occurred you get Point given in pixels, corresponding to touching place.

```java
inMap.addLongClickListener(new OnEventListener<Point>() {
            @Override
            public void onEvent(Point point) {
                new INMarker.INMarkerBuilder(inMap)
                    .point(MapUtil.pixelsToRealDimensions(inMap.scale, point))
                    .build();
            }
        });
```

### __Area Events__

Two types of events are distinguished:
- Area Events (```inMap.AREA```)
- Coordinates Events (```inMap.Coordinates```)

After adding an area in frontend server, you can listen to events on entering or exit from the area.
This will allow you to control movements of the tag,

```java
        inMap.addEventListener(INMap.AREA, new OnEventListener<List<AreaEvent>>() {
            @Override
            public void onEvent(List<AreaEvent> events) {
                Toast.makeText(context, "Area event occur!", Toast.LENGTH_LONG).show();
            }
        });
```

Or you can listen to whether the coordinates have been reached.
This will allow full control of the tag's movements.


```java
        inMap.addEventListener(INMap.COORDINATES, new OnEventListener<List<Coordinates>>() {
            @Override
            public void onEvent(List<Coordinates> coordinates) {
                Toast.makeText(context, "Coordinates event occur!", Toast.LENGTH_LONG).show();
            }
        });
```


