# __Area Events__

Area Events allows you to determinate when someone entered or leave given area. 
All areas should be added in frontend server (global solution). 
When an event occurs you receive AreaEvent object containing id and name of the area, but also date when the event has occurred and mode.

Available modes: <br>
` - ON_LEAVE`<br>
` - ON_ENTER`

Initialization requires entering the backend address and floorId (It's possible to listen on the different floor than it is loaded.

```java
INBle inBle = new INBle(inMap, backendServer, inMap.getFloorId);
inBle.addAreaEventListener(new OnEventListener<AreaEvent>() {
    @Override
    public void onEvent(AreaEvent areaEvent) {
        Log.e("Indoor", areaEvent.areaName + " " + areaEvent.mode);
    }
});
```

Additionaly, you can set the flag so that the point checked was previously pull to the path.

```java
inBle.setPulledPositionFlag(true);
```