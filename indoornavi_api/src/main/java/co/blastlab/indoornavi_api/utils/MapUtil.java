package co.blastlab.indoornavi_api.utils;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONObject;

import co.blastlab.indoornavi_api.model.Scale;

/**
 * Utility class for parsing data from pixels to real dimensions unit, and vice versa.
 * Additionally parsing {@link Scale} from String.
 */
public class MapUtil {

	/**
	 * Convert point given in pixels, to the point corresponding to the actual location in proper unit.
	 *
	 * @param scale {@link Scale} object contains information about the parameters of the map
	 * @param point {@link Point} object contains location given in pixels
	 * @return Point object containing location of the point given in real dimension unit
	 */
	public static Point pixelsToRealDimensions(Scale scale, Point point) {

		try {
			int xDifferenceInPix = scale.start.x - scale.stop.x;
			int yDifferenceInPix = scale.start.y - scale.stop.y;

			double scaleLengthInPixels = Math.sqrt(xDifferenceInPix * xDifferenceInPix + yDifferenceInPix * yDifferenceInPix);
			double centimetersPerPixel = scale.realDistance / scaleLengthInPixels;
			return new Point((int) (Math.round((centimetersPerPixel * point.x))), (int) Math.floor(centimetersPerPixel * point.y));
		} catch (Exception e) {
			Log.e("Parse unit Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}

	/**
	 * Convert point given in real dimension unit to the point given in pixels
	 *
	 * @param scale {@link Scale} object contains information about the parameters of the map
	 * @param point {@link Point} object contains location given in real dimension unit
	 * @return Point object containing location of the point given in pixels
	 */
	public static Point realDimensionsToPixels(Scale scale, Point point) {

		try {
			int xDifferenceInPix = scale.start.x - scale.stop.x;
			int yDifferenceInPix = scale.start.y - scale.stop.y;

			double scaleLengthInPixels = Math.sqrt(xDifferenceInPix * xDifferenceInPix + yDifferenceInPix * yDifferenceInPix);
			double pixelsPerCentimeter = scaleLengthInPixels / scale.realDistance;
			return new Point((int) Math.round(pixelsPerCentimeter * point.x), (int) Math.round(pixelsPerCentimeter * point.y));
		} catch (Exception e) {
			Log.e("Parse unit Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}

	/**
	 * Converts String in Json format to {@link Scale} object.
	 *
	 * @param scaleString String in Json format containing information about scale e.g.: {measure: "CENTIMETERS", realDistance: 325, start: {x: 100, y: 25}, stop: {x: 150, y: 80}}
	 * @return {@link Scale} object containing information about map parameters
	 */
	public static Scale stringToScale(String scaleString) {
		try {
			JSONObject jsonObject = new JSONObject(scaleString);
			if (!jsonObject.isNull("error")) {
				JSONObject jsonError = jsonObject.getJSONObject("error");
				Log.e("MapLoadException", jsonError.getString("error"));
				return null;
			}
			if (jsonObject.has("scale")) {
				jsonObject = jsonObject.getJSONObject("scale");
			}
			Scale.Measure measure = jsonObject.getString("measure").equals("METERS") ? Scale.Measure.METERS : Scale.Measure.CENTIMETERS;
			int realDistance = Integer.parseInt(jsonObject.getString("realDistance"));
			Point stop = PointsUtil.stringToPoint(jsonObject.getString("stop"));
			Point start = PointsUtil.stringToPoint(jsonObject.getString("start"));
			return new Scale(measure, realDistance, start, stop);
		} catch (Exception e) {
			Log.e("Scale parse Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
		}
		return null;
	}
}
