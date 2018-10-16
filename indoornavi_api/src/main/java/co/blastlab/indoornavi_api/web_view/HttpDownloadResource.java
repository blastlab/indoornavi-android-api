package co.blastlab.indoornavi_api.web_view;

import android.util.Log;
import android.webkit.WebResourceRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpDownloadResource {

	private WebResourceRequest request;
	private String requestHashCode;
	private String saveDir;
	public String fileURL;

	private static final int MAX_BUFFER_SIZE = 524288; //2^19

	public HttpDownloadResource(String fileURL, String saveDir, WebResourceRequest request, String requestHashCode) {
		this.fileURL = fileURL;
		this.saveDir = saveDir;
		this.request = request;
		this.requestHashCode = requestHashCode;
	}

	public void downloadFile() throws IOException {

		String extension = "";
		HttpURLConnection httpConn = (HttpURLConnection) new URL(fileURL).openConnection();

		for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
			httpConn.setRequestProperty(entry.getKey(), entry.getValue());
		}

		httpConn.setRequestMethod(request.getMethod());

		if (URI.create(fileURL).getPort() == 4200 && httpConn != null) {
			extension = getExtensionBaseOnMimeType(httpConn.getContentType());
		}

		int responseCode = httpConn.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {

			String saveFilePath = saveDir + File.separator + getFileName(fileURL, httpConn) + extension + requestHashCode;

			byte[] buffer;
			if (httpConn.getContentLength() > 0) {
				buffer = new byte[httpConn.getContentLength()];
			} else {
				buffer = new byte[MAX_BUFFER_SIZE];
			}

			InputStream inputStream = httpConn.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			inputStream.close();

			Log.e("HttpDownloadUtility", "File  " + fileURL + " downloaded");

			setHeaderFile(saveDir, fileURL + requestHashCode, httpConn.getHeaderFields());
			updateConfigFile(saveDir, fileURL + requestHashCode);
		} else {
			Log.e("HttpDownloadUtility", "No " + fileURL + " file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
	}

	private String getFileName(String fileURL, HttpURLConnection httpURLConnection) {
		String fileName = "";
		String disposition = httpURLConnection.getHeaderField("Content-Disposition");

		if (disposition != null) {
			int index = disposition.indexOf("filename=");
			if (index > 0) {
				fileName = disposition.substring(index + 10, disposition.length() - 1);
			}
		} else {
			fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
		}
		return fileName;
	}

	private void updateConfigFile(String saveDir, String fileURL) {
		try {
			File file = new File(saveDir + "/urlConfig.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(fileURL + "\n");
			out.close();
		} catch (IOException e) {
			Log.e("updateConfigFile:", e.toString());
		}
	}

	private boolean createFolder(String saveDir) {
		File file = new File(saveDir + File.separator + "HeaderFile");
		if (!file.exists()) {
			return file.mkdir();
		} else {
			return true;
		}
	}

	private void setHeaderFile(String saveDir, String fileURL, Map<String, List<String>> responseHeaderMap) {
		try {
			if (!createFolder(saveDir)) return;

			File file = new File(saveDir + "/HeaderFile/" + fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length()) + ".txt");
			if (!file.exists()) {
				file.createNewFile();

				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				for (Map.Entry<String, List<String>> entry : responseHeaderMap.entrySet()) {
					out.write(entry.getKey() + ":");

					Iterator<String> headerPropertyIterator = entry.getValue().iterator();
					while (headerPropertyIterator.hasNext()) {
						out.write(headerPropertyIterator.next());
						if (headerPropertyIterator.hasNext()) out.write(",");
					}
					out.write("\n");
				}
				out.close();
			}
		} catch (IOException e) {
			Log.e("setHeaderFile:", e.toString());
		}
	}

	public Map<String, String> getHeaderFile(String saveDir, String fileURL) {
		try {
			File file = new File(saveDir + "/HeaderFile" + File.separator + fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length()) + ".txt");
			if (!file.exists()) {
				return null;
			}

			InputStream inputStream = new FileInputStream(file);
			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;

			Map<String, String> responseHeaderMap = new HashMap<>();

			while ((line = rd.readLine()) != null) {
				String[] arrayline = line.split(":");
				responseHeaderMap.put(arrayline[0], arrayline[1]);
			}
			return responseHeaderMap;

		} catch (Exception e) {
			Log.e("InputStream exception :", e.toString());
		}
		return null;
	}

	private String getFilExtension(String fileName) {
		String[] splitedUrl = fileName.split("\\.");
		return splitedUrl[splitedUrl.length - 2];
	}

	public String getMimeType(String fileName, String url) {
		if (url.contains("images")) return "application/octet-stream";

		switch (getFilExtension(fileName)) {
			case "css":
				return "text/css";
			case "js":
				return "text/javascript";
			case "png":
				return "image/png";
			case "jpg":
				return "image/jpeg";
			case "ico":
				return "image/x-icon";
			case "octet-stream":
				return "application/octet-stream";
			case "html":
				return "text/html";
			case "svg":
				return "image/svg+xml";
			case "woff":
			case "ttf":
			case "eot":
				return "application/x-font-opentype";
		}
		return "application/json";
	}

	private String getExtensionBaseOnMimeType(String mimeType) {
		if (mimeType == null) return "";
		switch (mimeType) {
			case "text/html":
			case "text/html; charset=UTF-8":
				return ".html";
			case "application/json":
				return ".json";
		}
		return "";
	}

	public static String headerMapToString(Map<String, String> map) {
		StringBuilder headerMap = new StringBuilder();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			headerMap.append(entry.getKey() + ":" + entry.getValue());
			headerMap.append("\n");
		}
		return headerMap.toString();
	}
}

