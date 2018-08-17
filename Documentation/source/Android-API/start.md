# __Get Started__

This guide contains a quick start to add and manage indoor navigation.

## __Prepare project__

If you want to access map in your Android project it's necessary to create a proper building configuration on the frontend server.
Ones you create `Complex --> Building --> Floor` and upload the appropriate map, it's necessary to add a proper scale and publish map on the server.
You have to check the ID of the floor you want to show in the app and address of the frontend server.

<div class="note">      
   Note: The entire process is presented in the javascript-api documentation.
</div>

## __Installation__

Declare the Gradle dependency in your app module's build.gradle.
```
dependencies {
        implementation 'com.blastlab:indoornavi:1.0.0'
}
```

## __Usage__

### __Permissions__
```INTERNET``` permission is required to connect with frontend server.<br>
```WRITE_EXTERNAL_STORAGE``` permission is required only if you want to create csv reports.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### __SafeBrowsingResponse__
Used to indicate an action to take when hitting a malicious URL.
<div class="note">      
   Functionality is added in API level 27.
</div>


```xml
<application>
	<meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
			   android:value="true" />
</application>
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
Create an instance of INMap class and implement OnINMapReadyCallback interface and use onINMapReady callback method in your Activity. <br>
When INMap object is ready onINMapReady will be invoked, only then you can load the target map.
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
        inMap.createMap("frontend server address", "apiKey", height, wight);
        inMap.load(floorId, new OnObjectReadyCallback() {
			@Override
			public void onReady(Object o) {
			}
		});
    }
}
```
