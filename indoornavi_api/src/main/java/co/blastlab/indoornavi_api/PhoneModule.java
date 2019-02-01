package co.blastlab.indoornavi_api;

import org.json.JSONObject;

import co.blastlab.indoornavi_api.connection.CoordinatesConnection;
import co.blastlab.indoornavi_api.connection.PhoneConnection;
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
		PhoneConnection phoneConnection = new PhoneConnection(apiKey, this.backendServer);
		String id = phoneConnection.execute(userData).get();
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
		CoordinatesConnection dataConnection = new CoordinatesConnection(this.apiKey, this.floorId, backendServer, coordinates);
		String data = dataConnection.execute().get();
		return data != null;
	}
}
