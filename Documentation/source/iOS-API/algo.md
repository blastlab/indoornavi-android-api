# __Localization__

One of the functionality of the library is the possibility of user localization via Bluetooth.
This allows to locally display current position of the user's device and allows the recording of definite
coordinates to the database for later analysis of the history coordinates.

## __BLELocationManager__

In order to retrieve updates that report the user's current location use a *BLELocationManager* object. It is the object that you use to retrieve and manage location-related events based on BLE localization using iBeacons.

### __Usage__

To obtain correct position updates it is crucial to configure *BLELocationManager* object correctly. One of the parameters passed to the initializer is a *configuration* parameter, which is an array of *INBeaconConfiguration*'s. Each value describes every specific iBeacon device: its coordinates in centimeters, txPower given in decibels, major and minor numbers, and floorID, where beacon has been placed. A properly set configuration plays a significant role in calculating current position. An example of a set of configurations is shown below:

```swift
let configurations = [INBeaconConfiguration(x: 0, y: 0, z: 300, txPower: -69, major: 00000, minor: 0, floorID: 1),
                      INBeaconConfiguration(x: 1000, y: 0, z: 300, txPower: -69, major: 00001, minor: 0, floorID: 1),
                      INBeaconConfiguration(x: 1000, y: 1000, z: 300, txPower: -69, major: 00002, minor: 0, floorID: 1),
                      INBeaconConfiguration(x: 0, y: 1000, z: 300, txPower: -69, major: 00003, minor: 0, floorID: 1)]
```

Having the set of configurations, you can then create your *BLELocationManager* object.

```swift
let bleLocationManager = BLELocationManager(beaconUUID: BeaconUUID, configurations: configurations, delegate: self)
bleLocationManager.useCLBeaconAccuracy = true
```

### __Info.plist__

To use BLE localization, it is necessary to add a property to your *Info.plist* file for key *NSLocationWhenInUseUsageDescription*, with a proper description.

### __Setup a BLELocationManagerDelegate__

As you have probably seen above, a *delegate* is passed as a parameter to a *BLELocationManager* initializer. A *BLELocationManagerDelegate* is used to handle location-related events, such as retrieving new location data, handling errors etc. For more information about *BLELocationManagerDelegate* see documentation.

```swift
// Tells the delegate that new location data is available.
func bleLocationManager(_ manager: BLELocationManager, didUpdateLocation location: INLocation) {
  let position = INPoint(x: Int32(location.x.rounded()), y: Int32(location.y.rounded()))
  circle.position = position
  circle.draw()
}

// Tells the delegate that the authorization status for the application changed.
func bleLocationManager(_ manager: BLELocationManager, didChangeAuthorization status: INAuthorizationStatus) {
  if status == .notDetermined {
    manager.requestWhenInUseAuthorization()
  } else if status == .authorizedWhenInUse || status == .authorizedAlways {
    manager.startUpdatingLocation()
  } else {
    // The app is not authorized to use localization.
  }
}

// Tells the delegate that the user left the region, where localization was set and is out of range.
func bleLocationManagerLeftRegion(_ manager: BLELocationManager, withLastesKnownLocation location: INLocation) {
  print("User has left the region.")
}

// Tells the delegate that no beacon device was detected. The method is called every time the `BLELocationManager` tries to get new location data.
func bleLocationManagerNoBeaconsDetected(_ manager: BLELocationManager) {
  print("No beacons were detected.")
}

// Tells the delegate that the user moved to other floor.
func bleLocationManager(_ manager: BLELocationManager, didChangeFloor floorID: Int) {
  print("Did change floor for: \(floorID).")
}

// Tells the delegate that an error occurred while getting new location data.
func bleLocationManager(_ manager: BLELocationManager, didFailWithError error: Error) {
  print("BLE manager did fail with error: \(error.localizedDescription)")
}

// Tells the delegate that the bluetooth state changed.
func bleLocationManager(_ manager: BLELocationManager, didUpdateBluetoothState state: INBluetoothState) {
  // Handle the bluetooth state.
}
```
