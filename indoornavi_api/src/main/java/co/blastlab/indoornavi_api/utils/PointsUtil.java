package co.blastlab.indoornavi_api.utils;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing data from and to object points coordinates represented as a String.
 */
public class PointsUtil {

	/**
	 * Converts list of {@link Point} object coordinates to String representation. String can be evaluate to JavaScript values.
	 *
	 * @param points List of object points coordinates
	 * @return String of coordinates values, like [{x: 480, y: 450},{x: 1220, y: 150}] in JavaScript representation.
	 */
	public static String pointsToString(List<Point> points) {
		try {
			if(points == null) throw new Exception("points are not defined!");

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[");

			for (int i = 0; i < points.size(); i++) {
				stringBuilder.append(String.format("{x: %d, y: %d},", points.get(i).x, points.get(i).y));
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			stringBuilder.append("]");

			return stringBuilder.toString();
		}
		catch (Exception e) {
			Log.e("Points parse Exception", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e);
		}
		return null;
	}

	/**
	 * Converts String to List of {@link Point} object coordinates.
	 *
	 * @param stringPoints String of points in JavaScript representation e.g: [{x: 480, y: 450},{x: 1220, y: 150}]
	 * @return Point object list
	 */
	public static List<Point> stringToPoints(String stringPoints) {
		List<Point> points = new ArrayList<>();

		try {
			if(stringPoints.equals("null")) throw new Exception("String must be provided!");

			String str = stringPoints.replaceAll("[^-?0-9]+", " ");
			final String[] tokens = str.trim().split(" ");

			for (int i = 0; i < tokens.length; i += 2) {
				points.add(new Point(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1])));
			}
			return points;
		}
		catch (Exception e) {
			Log.e("Points parse Exception", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e);
		}
		return null;
	}

	/**
	 * Converts String to {@link Point} object coordinates.
	 *
	 * @param stringPoint String contains point in JavaScript representation e.g: [{x: 480, y: 450}]
	 * @return Point object
	 */
	public static Point stringToPoint(String stringPoint) {
		try {
			if(stringPoint.equals("null")) throw new Exception("String must be provided!");

			String str = stringPoint.replaceAll("[^-?0-9]+", " ");
			final String[] tokens = str.trim().split(" ");

			return new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
		}
		catch (Exception e) {
			Log.e("Point parse exception", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e);
		}
		return null;
	}

	public static String pointToString(Point point) {
		return String.format("{x: %d, y: %d}", point.x, point.y);
	}
}
