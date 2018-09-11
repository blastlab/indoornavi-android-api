package co.blastlab.indoornavi_api;

import android.util.Log;

import org.json.JSONObject;

import co.blastlab.indoornavi_api.connection.CoordinatesConnection;
import co.blastlab.indoornavi_api.connection.PhoneConnection;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INMap;

public class PhoneModule {

	private String backendServer;
	private INMap inMap;

	/**
	 * @param backendServer backend server address
	 * @param inMap         INMap object instance
	 */
	public PhoneModule(String backendServer, INMap inMap) {
		this.backendServer = backendServer;
		this.inMap = inMap;
	}

	/**
	 * Register phone with user data and return the endowed id.
	 *
	 * @param userData User data, is used to identify user in database.
	 * @return Integer represent id assigned to specific user data
	 */
	public short registerPhone(String userData) throws Exception {
		PhoneConnection phoneConnection = new PhoneConnection(this.inMap.apiKey, this.backendServer);
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
		CoordinatesConnection dataConnection = new CoordinatesConnection(this.inMap, backendServer, coordinates);
		String data = dataConnection.execute().get();
		if (data != null) {
			return true;
		}
		return false;
	}
}
