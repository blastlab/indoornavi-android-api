# __IndoorNavi API 1.0__

IndoorNavi is a Java library used to management IndoorNavi maps for Android devices.
Library provides simple control for tags movements and visibility, anchor location and occurred events.
You can also use API to add your own areas, polylines, markers and info windows.

## __Installation__

Declare the Gradle dependency in your app module's build.gradle.
```Gradle
dependencies {
        compile project(':indoornavi_api')
}
```

## __Usage__

### __Permissions__
```INTERNET``` permission is required to connect with frontend server.
```WRITE_EXTERNAL_STORAGE``` permission is required only if you want create csv reports.
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
Create an instance of OnViewReadyCallback class and implement callback in your Activity.
```java
public class MainActivity extends Activity implements OnViewReadyCallback {

    INMap inMap;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inMap = (INMap) findViewById(R.id.inMap);
    }
   
    public void onWebViewReady(INMap mapView) {
        inMap.createMap("frontend server address", "apiKey", hight, wight);
        inMap.load(2, new OnObjectReadyCallback() {
            @Override
            public void onReady(Object o) {
                // Map is loaded and ready to use
            }
        });
    }

}
```