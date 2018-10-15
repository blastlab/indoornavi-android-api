package co.blastlab.indoornavi_api.web_view;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class ResourcesModule extends AsyncTask<String, String, String> {

	private Context context;
	private Map<String,String> headers;
	String requestHashCode;

	private HttpURLConnection httpConnection;

	public ResourcesModule(Context context, Map<String,String> headers, String requestHashCode) {
		this.context = context;
		this.headers = headers;
		this.requestHashCode = requestHashCode;
	}

	@Override
	protected String doInBackground(String... param) {

		String fileURL = param[0];
		String saveDir = this.context.getFilesDir().getAbsolutePath();

		try {
			HttpDownloadUtility.downloadFile(fileURL, headers, saveDir, requestHashCode);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
