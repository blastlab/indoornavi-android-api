# __BLE Area Events__

Area Events allows you to determinate when someone entered or leave given area.
All areas should be added in frontend server (global solution).
When an event occurs you receive an *AreaEvent* containing id and name of the area, but also date when the event has occurred and *Mode*, which can be either *.onLeave* or *.onEnter*.

Initialization requires entering the backend address and floorID (It's possible to listen on the different floor than it is loaded).

```swift
let ble = INBle(map: self.map, targetHost: self.BackendTargetHost, floorID: 2, apiKey: self.ApiKey, bleLocationManager: self.bleLocationManager!)
ble.addAreaEventListener() { event in
  print("A \(event.mode.rawValue) event has occurred in area \(event.areaName) on \(event.date)"")
}
```

Additionally, you can set the flag so that the point checked was previously pulled to the path.

```swift
ble.usePullToPath = true
```
