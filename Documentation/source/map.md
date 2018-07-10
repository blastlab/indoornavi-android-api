# __INMap object__

The INMap object is the main object to communicate with the frontend server. INMap object corresponds to the floor you want to display.
It inherits WebView object and provides his basic functionality. All other objects are created corresponding to given INMap object and they require a INMap instance.

INMap can be called only if onINMapReady invoked, after that, you can create map instance and load floor plans.

```java
inMap = (INMap) findViewById(R.id.inMap);
   
public void onINMapReady(INMap mapView) {
   inMap.createMap("http://mybuilding.com", "apiKey", hight, wight);
    inMap.load(floorId)
    }
```