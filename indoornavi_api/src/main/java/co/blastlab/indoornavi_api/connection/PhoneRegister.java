package co.blastlab.indoornavi_api.connection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import co.blastlab.indoornavi_api.objects.INMap;

public class PhoneRegister extends AsyncTask<String, String, Integer> {

	private INMap inMap;

	public PhoneRegister(INMap inMap) {
		this.inMap = inMap;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		String urlString = params[0] + "/rest/v1/phones";
		String userData = "{ \"userData\": \"" + params[1] + "\"}";

		try {
			URL mUrl = new URL(urlString);
			HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Authorization", "Token " + inMap.apiKey);
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Accept", "application/json");

			httpConnection.setUseCaches(false);
			httpConnection.setAllowUserInteraction(false);
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(10000);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);

			OutputStream os = httpConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(os, "UTF-8"));
			writer.write(userData);
			writer.flush();
			writer.close();
			os.close();

			httpConnection.connect();

			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				JSONObject jsonObject = new JSONObject(sb.toString());

				return jsonObject.getInt("id");
			}
			else {
				throw new Exception("Http Connection Error: " + responseCode);

			}
		} catch (Exception e) {
			Log.e("Response exception", e.getMessage());
			System.out.println(e.getMessage());
		}
		return null;
	}
}
