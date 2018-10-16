package co.blastlab.indoornavi_api.web_view;

import android.os.AsyncTask;
import android.util.Log;

public class ResourcesModule extends AsyncTask<String, String, String> {

	HttpDownloadResource httpDownloadResource;

	public ResourcesModule(HttpDownloadResource httpDownloadResource) {
		this.httpDownloadResource = httpDownloadResource;
	}

	@Override
	protected String doInBackground(String... param) {
		try {
			this.httpDownloadResource.downloadFile();

		} catch (Exception e) {
			Log.e("ResourcesModule", httpDownloadResource.fileURL + " " + e.toString());
		}
		return null;
	}
}
