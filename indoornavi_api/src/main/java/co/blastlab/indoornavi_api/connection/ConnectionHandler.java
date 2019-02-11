package co.blastlab.indoornavi_api.connection;

import android.util.Log;

import java.net.HttpURLConnection;

public class ConnectionHandler extends Connection {

	public final static String AUTH = "/phones/auth";
	public final static String COORDINATES = "/phones/coordinates";
	public final static String COMPLEXES = "/complexes";
	public final static String AREAS = "/areas";
	public final static String PATH = "/paths";

	private String data = "";
	private  Method method;

	public ConnectionHandler(String apiKey, String backendServer, Method method) {
		super(apiKey, backendServer);
		this.method = method;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			HttpURLConnection httpConnection = openConnection(params[0]);

			setConnectionProperties(method);
			if(data != null && !data.equals("")) {
				sendData(data);
			}
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
