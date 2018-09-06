package co.blastlab.indoornavi_api;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONObject;

import java.util.Date;

import co.blastlab.indoornavi_api.connection.CoordinatesConnection;
import co.blastlab.indoornavi_api.connection.PhoneConnection;
import co.blastlab.indoornavi_api.objects.INMap;

public class DataConnector {

	private String backendServer;
	private INMap inMap;

	public DataConnector(String backendServer, INMap inMap) {
		this.backendServer = backendServer;
		this.inMap = inMap;
	}

	/**
	 *
	 * @param userData User data, is used to identify user in database.
	 * @param backendServer backend server address
	 * @return Integer represent id assigned to specific user data
	 */
	public Integer phoneRegister(String userData, String backendServer) {
		try {
			PhoneConnection phoneConnection = new PhoneConnection(this.inMap.apiKey, backendServer);
			String id = phoneConnection.execute(userData).get();
			if(id != null) {

				return new JSONObject(id).getInt("id");
			}
		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}

	/**
	 * @param backendServer backend server address
	 *
	 * @return Response data.
	 */
	public String sendCoordinates(String backendServer) {
		try {
			CoordinatesConnection dataConnection = new CoordinatesConnection(this.inMap, new Date(), backendServer, new Point(34, 6666));
			String data = dataConnection.execute().get();
			if(data != null) {
				return data;
			}
		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}
}
