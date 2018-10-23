# __Area Events__

API can handle events occurred when BLE position entered or leave given area.

Available modes: <br>
` - ON_LEAVE`<br>
` - ON_ENTER`


```java
INBle inBle = new INBle(inMap, backendServer, inMap.getFloorId);
inBle.addAreaEventListener(new OnEventListener<AreaEvent>() {
	@Override
	public void onEvent(AreaEvent areaEvent) {
		Log.e("Indoor", areaEvent.areaName + " " + areaEvent.mode);
	}
});
```