package co.blastlab.indoornavi_api;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.blastlab.indoornavi_api.connection.Connection;
import co.blastlab.indoornavi_api.connection.ConnectionHandler;
import co.blastlab.indoornavi_api.model.Border;
import co.blastlab.indoornavi_api.model.Complex;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INMap;

import co.blastlab.indoornavi_api.utils.ComplexUtils;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class responsible for communication with backend and database.
 */
public class INData {

	private String targetHost, apiKey;
	private INMap inMap;
	private Executor threadPoolExecutor;

	/**
	 * Data object constructor.
	 *
	 * @param inMap      INMap instance
	 * @param targetHost address to the INMap backend server
	 * @param apiKey     the API key created on INMap server
	 */
	public INData(INMap inMap, String targetHost, String apiKey) {
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.inMap = inMap;

		setExecutorInAsyncTask();
	}

	private void setExecutorInAsyncTask() {
		int corePoolSize = 60;
		int maximumPoolSize = 80;
		int keepAliveTime = 10;

		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maximumPoolSize);
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	}

	/**
	 * Retrieve list of paths.
	 */
	public List<Path> getPaths(int floorId) {
		try {
			ConnectionHandler pathConnection = new ConnectionHandler(apiKey, this.targetHost, Connection.Method.GET);
			String path = pathConnection.executeOnExecutor(threadPoolExecutor, String.format(Locale.ENGLISH, ConnectionHandler.PATH + "/%d", floorId)).get();
			if (path != null || !path.isEmpty()) {
				return getPathsFromJson(path);
			}
		} catch (Exception e) {
			Log.e("Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): path download error.");
		}
		return null;
	}

	/**
	 * Returns the list of global areas for given floor.
	 */
	public List<INArea> getAreas(int floorId) {
		try {
			if (inMap.getMapScale() == null) {
				throw new Exception("INMap is not ready or undefined. Call load() first and then INMap will be ready.");
			}

			ConnectionHandler complexConnection = new ConnectionHandler(apiKey, this.targetHost, Connection.Method.GET);
			String areas = complexConnection.executeOnExecutor(threadPoolExecutor, ConnectionHandler.AREAS + floorId).get();
			if (areas != null || !areas.isEmpty()) {
				return getAreasFromJSON(areas);
			}
		} catch (Exception e) {
			Log.e("Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): Areas download error. " + e);
		}
		return null;
	}

	/**
	 * Retrieve list of complexes.
	 */
	public List<Complex> getComplexes() {
		try {
			ConnectionHandler complexConnection = new ConnectionHandler(apiKey, this.targetHost, Connection.Method.GET);
			String complexes = complexConnection.executeOnExecutor(threadPoolExecutor, ConnectionHandler.COMPLEXES).get();
			if (complexes != null || !complexes.isEmpty()) {
				return ComplexUtils.getComplexesFromJSON(complexes);
			}
		} catch (Exception e) {
			Log.e("Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): Complex json parse error.");
		}
		return null;
	}

	private List<Path> getPathsFromJson(String paths) {
		if (!paths.equals("[]") && !paths.equals("null")) {
			List<Path> pathList = new ArrayList<>();
			try {
				JSONArray jsonArray = new JSONArray(paths);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					pathList.add(new Path(PointsUtil.stringToPoint(jsonObject.getString("startPoint")), PointsUtil.stringToPoint(jsonObject.getString("endPoint"))));
				}
				return pathList;
			} catch (Exception e) {
				Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
			}
		}
		return null;
	}

	private List<INArea> getAreasFromJSON(String jsonString) {
		if (jsonString == null) return null;

		List<INArea> areas = new ArrayList<>();

		try {
			JSONArray jsonAreasList = new JSONArray(jsonString);

			for (int i = 0; i < jsonAreasList.length(); i++) {
				INArea inArea = INArea.createDefault(this.inMap);

				JSONObject area = jsonAreasList.getJSONObject(i);
				inArea.setName(area.getString("name"));
				inArea.setDatabaseId(area.getInt("id"));

				List<Point> points = PointsUtil.stringToPoints(area.getString("points"));

				inArea.setPoints(points);
				inArea.setOpacity(0.2);
				inArea.setColor(Color.GREEN);
				inArea.setBorder(new Border(4, Color.GREEN));

				try {
					CountDownLatch latch = new CountDownLatch(1);
					inArea.ready(data -> latch.countDown());

					latch.await();
					areas.add(inArea);

				} catch (Exception e) {
					Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
				}
			}
			return areas;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}
}
