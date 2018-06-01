# __IndoorNavi API 1.0__

IndoorNavi is a Java library used to manage IndoorNavi maps for Android devices.
Library provides simple control for tags movements and visibility, anchor location and occurred events.
You can also use API to add your own areas, polylines, markers and info windows.

## __Installation__

Declare the Gradle dependency in your app module's build.gradle.
```gradle
dependencies {
        implementation 'com.blastlab:indoornavi:1.0.0'
}
```

## __Provided functionalities__
The API allows you to add these graphics to a map:
- __INPolyline__ - place a polyline on the map.
- __INArea__ -  place enclosed area on the map.
- __INMarker__ - place proper icon on specific place on the map.
- __INInfoWindow__ - displays text or images in a popup window place on marker.

## __Usage__

### __Permissions__
```INTERNET``` permission is required to connect with frontend server.<br>
```WRITE_EXTERNAL_STORAGE``` permission is required only if you want to create csv reports.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### __Layout (XML file)__
It's necessary to add INMap object in XML file.
```xml
<co.blastlab.indoornavi_api.objects.INMap
    android:id="@+id/inMap"
    android:layout_width="fill_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android"/>
```

### __Activity (Java)__
Create an instance of INMap class and implement OnViewReadyCallback callback in your Activity. <br>
When IN Map object is ready OnViewReadyCallback will be invoked, only then you can load the target map.
```java
public class MainActivity extends Activity implements OnINMapReadyCallback {

    INMap inMap;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inMap = (INMap) findViewById(R.id.inMap);
    }
   
    public void onINMapReady(INMap mapView) {
        inMap.createMap("frontend server address", "apiKey", hight, wight);
        inMap.load(floorId)
    }

}
```

### __How to create object__
To create any object you have to use object builder pattern.

#### __INArea__ object:
```java
INArea inArea = new INArea.INAreaBuilder(inMap)
	.points(points)
	.setFillColor(Color.GREEN)
	.setOpacity(0.3)
	.build();
```
#### __INPolyline__ object:
```java
INPolyline inPolyline = new INPolyline.INPolylineBuilder(inMap)
	.points(points)
	.setLineColor(Color.LTGRAY)
	.build();
```
#### __INMarker__ object:
```java
INMarker inMarker = new INMarker.INMarkerBuilder(inMap)
	.point(new Point(600, 600))
	.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
	.setLabel("This is label")
	.build();
```
It's possible to add EventListener on the Marker object
```java
inMarker.addEventListener(new OnMarkerClickListener() {
	@Override
		public void onClick() {
			Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
		}
	});
```

#### __INInfoWindow__ object:
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

### __Objects properties__ 
You can retrive some information about an object, like:

#### ID
return id of the given object
```java
inPolyline.getID(id -> { Log.i("MyTag", "ID: " + id); });
```
#### Points
return points od the given object
```java
inPolyline.getPoints(points -> Log.i("MyTag", "Points: " +  PointsUtil.pointsToString(points)));
```

#### isWithin 
Checks if given Coordinates are inside of the object. 
```java
inArea.isWithin(new Coordinates(200, 800, (short)109999, new Date()), bool -> Log.i("Indoor", "Received value: " + bool));
```

