package co.blastlab.indoornavi_api.connection;

import android.util.Log;

import java.net.HttpURLConnection;

public class ComplexConnection extends Connection {

	public ComplexConnection(String apiKey, String backendServer) {
		super(apiKey, backendServer);
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			HttpURLConnection httpConnection = openConnection(Connection.COMPLEXES);

			setConnectionProperties(Method.GET);
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
