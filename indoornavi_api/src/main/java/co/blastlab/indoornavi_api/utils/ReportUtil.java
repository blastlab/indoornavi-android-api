package co.blastlab.indoornavi_api.utils;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;

/**
 * Utility class for parsing {@link AreaEvent} and {@link Coordinates} from String representation.
 * It allows to generate CSV files.
 */
public class ReportUtil {

	/**
	 * Converts String in Json format to List of {@link AreaEvent} objects.
	 *
	 * @param jsonString String in Json format containing AreaEvent data
	 * @return AreaEvent list or null if unsuccessful
	 */
	public static List<AreaEvent> jsonToAreaEventArray(String jsonString) {
		List<AreaEvent> events = new ArrayList<>();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;

		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				date = jsonObject.getString("date");
				date = date.substring(0, date.length() - 1);
				events.add(new AreaEvent(jsonObject.getInt("tagId"), dt.parse(date), jsonObject.getInt("areaId"), jsonObject.getString("areaName"), jsonObject.getString("mode")));
			}
			return events;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	/**
	 * Converts String in Json format to List of {@link Coordinates} objects.
	 *
	 * @param jsonString String in Json format containing Coordinates data
	 * @return Coordinates list or null if unsuccessful
	 */
	public static List<Coordinates> jsonToCoordinatesArray(String jsonString) {
		List<Coordinates> coordinates = new ArrayList<>();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;

		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				date = jsonObject.getString("date");
				date = date.substring(0, date.length() - 1);
				coordinates.add(new Coordinates(jsonObject.getInt("x"), jsonObject.getInt("y"), jsonObject.getInt("z"), (short) jsonObject.getInt("tagId"), dt.parse(date)));
			}
			return coordinates;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	/**
	 * Create CSV file for given {@link AreaEvent} list
	 *
	 * @param events AreaEvent list
	 */
	public static void areaEventToCSV(List<AreaEvent> events) {

		try {
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/IndoorNavi");
			if (!dir.exists()) {
				dir.mkdir();
			}
			FileWriter file = new FileWriter(dir + "/AreaEvents-" + new Date().toString() + ".csv");
			BufferedWriter CSV_Output = new BufferedWriter(file);

			String header = "tagId" + "," + "date" + "," + "areaId" + "," + "areaName" + "," + "mode" + "\n";
			CSV_Output.write(header);

			String line;
			for (AreaEvent event : events) {
				line = event.tagId + "," + event.date.toString() + "," + event.areaId + "," + event.areaName + "," + event.mode + "\n";
				CSV_Output.write(line);
			}
			CSV_Output.close();
		} catch (Exception e) {
			Log.e("CSV create exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
		}
	}

	/**
	 * Create CSV file for given {@link Coordinates} list
	 *
	 * @param coordinates Coordinates list
	 */
	public static void coordinatesToCSV(List<Coordinates> coordinates) {

		try {
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/IndoorNavi");
			if (!dir.exists()) {
				dir.mkdir();
			}
			FileWriter file = new FileWriter(dir + "/Coordinates-" + new Date().toString() + ".csv");
			BufferedWriter CSV_Output = new BufferedWriter(file);

			String header = "x" + "," + "y" + "," + "tagId" + "," + "date" + "\n";
			CSV_Output.write(header);

			String line;
			for (Coordinates coordinate : coordinates) {
				line = coordinate.x + "," + coordinate.y + "," + coordinate.deviceId + "," + coordinate.date.toString() + "\n";
				CSV_Output.write(line);
			}
			CSV_Output.close();
		} catch (Exception e) {
			Log.e("CSV create exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
		}
	}
}
