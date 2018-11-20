package co.blastlab.indoornavi_api.utils;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;

public class EventsUtil {

	/**
	 * Converts String in Json format to {@link AreaEvent} object.
	 *
	 * @param jsonString String in Json format containing AreaEvent data
	 * @return AreaEvent object, null if unsuccessful
	 */
	public static AreaEvent jsonEventToAreaEvent(String jsonString) {
		AreaEvent event;
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject areaJsonObject = new JSONObject(jsonObject.getString("area"));

			if (areaJsonObject.has("tagId")) {
				date = areaJsonObject.getString("date");
				event = new AreaEvent(areaJsonObject.getInt("tagId"), date.equals("null") ? null : dt.parse(date), areaJsonObject.getInt("areaId"), areaJsonObject.getString("areaName"), areaJsonObject.getString("mode"));
			} else {
				event = new AreaEvent(dt.parse(jsonObject.getString("date")), areaJsonObject.getInt("id"), areaJsonObject.getString("name"), jsonObject.getString("mode"));
			}
			return event;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	/**
	 * Converts String in Json format to  {@link Coordinates} object.
	 *
	 * @param jsonString String in Json format containing Coordinates data
	 * @return Coordinates object, null if unsuccessful
	 */
	public static Coordinates jsonEventToCoordinates(String jsonString) {
		Coordinates coordinates;
		try {
			String string = convertStandardJSONString(jsonString);
			JSONObject jo = new JSONObject(string);

			JSONObject jsonObject = new JSONObject(jo.getString("coordinates"));

			Date date = new Date(jsonObject.getInt("date"));
			Point point = new Point(PointsUtil.stringToPoint(jsonObject.getString("point")));
			coordinates = new Coordinates(point.x, point.y, 0, (short) jsonObject.getInt("tagShortId"), date);
			return coordinates;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	private static String convertStandardJSONString(String data_json) {
		data_json = data_json.replace("\"{", "{");
		data_json = data_json.replace("}\"", "}");
		return data_json;
	}
}
