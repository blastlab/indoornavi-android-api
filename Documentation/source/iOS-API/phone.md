# __DeviceDataManager__

*DeviceDataManager* object lets you to save data, related to a mobile device.  
You can register a user with custom data or save coordinates.

### Register device

Before using any other functionality, you should register user's device to obtain a *deviceID*.

```swift
let deviceDataManager = DeviceDataManager(targetHost: BackendTargetHost, apiKey: ApiKey)
deviceDataManager.registerDevice(withUserData: "John") { deviceID, error in
  guard error == nil else {
    print("An error occurred: \(error!.localizedDescription)")
  }

  // Save your deviceID
  self.deviceID = deviceID
}
```

### Save Coordinates

After obtaining the deviceID you can save coordinates.

```swift
deviceDataManager.send([position1], date: Date(), floorID: 1, deviceID: deviceID)
```
