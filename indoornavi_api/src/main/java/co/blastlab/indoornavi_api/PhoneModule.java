package co.blastlab.indoornavi_api;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.connection.Connection;
import co.blastlab.indoornavi_api.connection.ConnectionHandler;
import co.blastlab.indoornavi_api.model.Coordinates;

public class PhoneModule {

	private String backendServer;
	private String apiKey;
	private int floorId;

	/**
	 * @param backendServer backend server address
	 * @param apiKey        apiKey to IndoorNavi
	 */
	public PhoneModule(String backendServer, String apiKey, int floorId) {
		this.backendServer = backendServer;
		this.apiKey = apiKey;
		this.floorId = floorId;
	}

	/**
	 * Register phone with user data and return the endowed id.
	 *
	 * @param userData User data, is used to identify user in database.
	 * @return Integer represent id assigned to specific user data
	 */
	public short registerPhone(String userData) throws Exception {
		ConnectionHandler phoneConnection = new ConnectionHandler(apiKey, this.backendServer, Connection.Method.POST);
		phoneConnection.setData(String.format("{ \"userData\": \"%s\"}", userData));
		String id = phoneConnection.execute(ConnectionHandler.AUTH).get();
		if (id != null) {
			return (short) new JSONObject(id).getInt("id");
		}
		return -1;
	}

	/**
	 * @param coordinates Coordinates saved in database.
	 * @return Response data.
	 */
	public Boolean saveCoordinates(Coordinates coordinates) throws Exception {
		ConnectionHandler dataConnection = new ConnectionHandler(apiKey, this.backendServer, Connection.Method.POST);
		dataConnection.setData(getPayloadForSingleCoordinates(this.floorId, coordinates));
		String data = dataConnection.execute(ConnectionHandler.COORDINATES).get();
		return data != null;
	}

	/**
	 * @param coordinates Coordinates saved in database.
	 * @return Response data.
	 */
	public Boolean saveCoordinates(List<Coordinates> coordinates) throws Exception {
		ConnectionHandler dataConnection = new ConnectionHandler(apiKey, this.backendServer, Connection.Method.POST);
		dataConnection.setData(getPayloadForList(this.floorId, coordinates));
		String data = dataConnection.execute(ConnectionHandler.COORDINATES).get();
		return data != null;
	}

	private String getPayloadForSingleCoordinates(int floorId, Coordinates coordinates) {
		return String.format(Locale.ENGLISH, "[{\"floorId\": %d, \"point\": {\"x\": %d, \"y\": %d, \"z\": %d}, \"date\": \"%s\", \"phoneId\": %d}]", floorId, coordinates.x, coordinates.y, coordinates.z, getFormattedDate(coordinates.date), coordinates.deviceId);
	}

	private String getPayloadForList(int floorId, List<Coordinates> coordinatesList) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (Coordinates coordinates : coordinatesList) {
			stringBuilder.append(String.format(Locale.ENGLISH, "{\"floorId\": %d, \"point\": {\"x\": %d, \"y\": %d, \"z\": %d}, \"date\": \"%s\", \"phoneId\": %d}", floorId, coordinates.x, coordinates.y, coordinates.z, getFormattedDate(coordinates.date), coordinates.deviceId));
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("]");
		return  stringBuilder.toString();
	}

	private String getFormattedDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return simpleDateFormat.format(date);
	}


}
