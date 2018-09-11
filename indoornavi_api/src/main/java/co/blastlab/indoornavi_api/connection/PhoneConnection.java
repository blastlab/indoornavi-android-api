package co.blastlab.indoornavi_api.connection;

import android.util.Log;

import java.net.HttpURLConnection;

public class PhoneConnection extends Connection {

	public PhoneConnection(String apiKey, String backendServer) {
		super(apiKey, backendServer);
	}

	@Override
	protected String doInBackground(String... params) {

		String userData = String.format("{ \"userData\": \"%s\"}", params[0]);

		try {
			HttpURLConnection httpConnection = openConnection(Connection.AUTH);

			setConnectionProperties();
			sendData(userData);
			httpConnection.connect();

			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				return getResponse();
			} else {
				throw new Exception("Http Connection Error: " + responseCode);
			}
		} catch (Exception e) {
			Log.e("Response exception", e.getMessage());
		}
		return null;
	}
}
