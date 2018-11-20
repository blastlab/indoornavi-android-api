package co.blastlab.indoornavi_api.connection;

import android.util.Log;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INMap;

public class CoordinatesConnection extends Connection {

	private String payload;

	public CoordinatesConnection(INMap inMap, String backendServer, Coordinates coordinates) {
		super(inMap.getApiKey(), backendServer);
		this.payload = String.format(Locale.ENGLISH, "[{\"floorId\": %d, \"point\": {\"x\": %d, \"y\": %d, \"z\": %d}, \"date\": \"%s\", \"phoneId\": %d}]", inMap.getFloorId(), coordinates.x, coordinates.y, coordinates.z, getFormattedDate(coordinates.date), coordinates.deviceId);
	}

	public CoordinatesConnection(INMap inMap, String backendServer, List<Coordinates> coordinatesList) {
		super(inMap.getApiKey(), backendServer);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (Coordinates coordinates : coordinatesList) {
			stringBuilder.append(String.format(Locale.ENGLISH, "{\"floorId\": %d, \"point\": {\"x\": %d, \"y\": %d, \"z\": %d}, \"date\": \"%s\", \"phoneId\": %d}", inMap.getFloorId(), coordinates.x, coordinates.y, coordinates.z, getFormattedDate(coordinates.date), coordinates.deviceId));
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("]");
		this.payload = stringBuilder.toString();
	}

	private String getFormattedDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
		return simpleDateFormat.format(date);
	}

	@Override
	protected String doInBackground(String... param) {

		try {
			HttpURLConnection httpConnection = openConnection(Connection.COORDINATES);

			setConnectionProperties();
			sendData(payload);
			httpConnection.connect();

			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				return getResponse();
			} else {
				throw new Exception("Http Connection Error: " + responseCode);
			}
		} catch (Exception e) {
			Log.e("Response exception", e.toString());
		}
		return null;
	}
}
