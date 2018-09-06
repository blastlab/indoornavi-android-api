package co.blastlab.indoornavi_api.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection extends AsyncTask<String, String, String> {

	protected final static String AUTH = "/auth";
	protected final static String COORDINATES = "/coordinates";

	private String apiKey;
	private String baseURL;
	private HttpURLConnection httpConnection;

	protected Connection(String apiKey, String backendServer) {
		this.apiKey = apiKey;
		this.baseURL = backendServer + "/rest/v1/phones";
	}

	@Override
	protected String doInBackground(String... params){
		return null;
	}

	protected HttpURLConnection openConnection(String urlAddress) {
		try {
			URL mUrl = new URL(baseURL + urlAddress);
			httpConnection = (HttpURLConnection) mUrl.openConnection();
			return httpConnection;
		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}

	protected void setConnectionProperties() {
		try {
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Authorization", "Token " + apiKey);
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Accept", "application/json");

			httpConnection.setUseCaches(false);
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(10000);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): Http Connection error.");

		}
	}

	protected String getResponse() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			bufferedReader.close();
			return stringBuilder.toString();

		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);

		}
		return null;
	}

	protected void sendData(String data) {
		try {
			OutputStream os = httpConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

			writer.write(data);
			writer.flush();
			writer.close();
			os.close();

		} catch (Exception e) {
			Log.e("Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);

		}
	}
}
