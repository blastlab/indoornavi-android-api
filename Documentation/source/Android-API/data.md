# __Data handle__

Library have a possibility to retrieve data directly from the backend server.
It gives the user possibility to download historical events of tag entry to areas or to download all coordinates that have been saved in the database.
Additionally, you can download elements assigned globally to the map, such as a list of areas or paths added to the map.

## __INData__

INData object is used to receive elements set on the map in fronted server editor.
Returns elements for the floor with is currently loaded.

### Paths

Retrieve list of Path objects.

```java
INData inData = new INData(inMap, backendServer, apiKey);
		inData.getPaths(paths -> {
				Log.i("Indoor", "Received path: " + paths);
			}
		);
```

### Areas

Retrieve list of INArea objects for given floor.

```java
INData inData = new INData(inMap, backendServer, apiKey);
List<INArea> areas = inData.getAreas(floorID);
```

### Complexes

Retrieve list of available complexes with buildings and floors.

```java
INData inData = new INData(inMap, backendServer, apiKey);
List<Complex> complexes = inData.getComplexes();
```

## __INReport__

INReport object is used to retrieve historical data from the database.
It allows to get archived Area events or tag coordinates.
To enable simpler data processing, the utils class was created.
Utils class allows to convert data to CSV format.

### Area events

```java
INReport inReport = new INReport(inMap, backendServer, apiKey);
inReport.getAreaEvents(new Date(1428105600), new Date(), new OnObjectReadyCallback<List<AreaEvent>>() {
			@Override
			public void onReady(List<AreaEvent> areaEvents) {
				if (areaEvents != null) {
					ReportUtil.areaEventToCSV(areaEvents);
				} else {
					Log.i("Indoor", "No area events available");
				}
			}
		});

```

### Coordinates

```java
INReport inReport = new INReport(inMap, backendServer, apiKey);
inReport.getCoordinates(new Date(1428105600), new Date(), new OnObjectReadyCallback<List<Coordinates>>() {
			@Override
			public void onReady(List<Coordinates> coordinates) {
				if (coordinates != null) {
					ReportUtil.coordinatesToCSV(coordinates);
				} else {
					Log.i("Indoor", "No coordinates available");
				}
			}
		});

```
