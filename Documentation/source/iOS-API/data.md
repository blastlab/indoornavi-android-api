# __Data handle__

Library have a possibility to retrieve data directly from the backend server.
It gives the user possibility to download historical events of tag entry to areas or to download all coordinates that have been saved in the database.
Additionally, you can download elements assigned globally to the map, such as a list of areas or paths added to the map.

## __INData__

INData object is used to receive elements set on the map in fronted server editor.
Returns elements for the floor with is currently loaded.

### Paths

Retrieve list of Path objects.

```swift
let data = INData(map: map, targetHost: BackendTargetHost, apiKey: ApiKey)
data.getPaths(fromFloorWithID: floorID) { paths in
  print("Paths: \(paths)")
}
```

### Areas

Retrieve list of INArea objects.

```swift
let data = INData(map: map, targetHost: BackendTargetHost, apiKey: ApiKey)
data.getAreas(fromFloorWithID: floorID) { areas in
  for area in areas {
    area.draw()
  }
}
```

## __INReport__

INReport object is used to retrieve historical data from the database.
It allows to get archived area events or tag coordinates.

### Area events

```swift
let report = INReport(map: map, targetHost: BackendTargetHost, apiKey: ApiKey)
report.getAreaEvents(fromFloorWithID: floorID, from: Date(timeIntervalSince1970: 1428105600), to: Date()) { areaEvents in
  print("Area events: ", areaEvents)
}
```

### Coordinates

```swift
let report = INReport(map: map, targetHost: BackendTargetHost, apiKey: ApiKey)
report.getCoordinates(fromFloorWithID: floorID, from: Date(timeIntervalSince1970: 1428105600), to: Date()) { coordinates in
  print("Coordinates: ", coordinates)
}
```
