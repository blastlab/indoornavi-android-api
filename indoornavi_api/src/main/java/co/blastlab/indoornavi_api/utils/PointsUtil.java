package co.blastlab.indoornavi_api.utils;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
/**
 * Utility class for parsing data from and to object points coordinates represented as a String.
 *
 * @author Agata Ziółkowska <achmielewska@blastlab.co>
 */
public class PointsUtil {

	/**
	 * Converts list of object points coordinates to String representation. String can be evaluate to JavaScript values.
	 *
	 * @param points List of object points coordinates
	 * @return String of coordinates values, like [{x: 480, y: 450},{x: 1220, y: 150}] in JavaScript representation.
	 */
	public static String pointsToString(List<Point> points) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		for (int i = 0; i < points.size(); i++) {
			stringBuilder.append(String.format("{x: %d, y: %d},", points.get(i).x, points.get(i).y));
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("]");

		return stringBuilder.toString();
	}

	/**
	 * Converts String to List of object points coordinates.
	 *
	 * @param stringPoints String of points in JavaScript representation e.g: [{x: 480, y: 450},{x: 1220, y: 150}]
	 * @return object points coordinates
	 */
	public static List<Point> stringToPoints(String stringPoints) {
		List<Point> points = new ArrayList<>();

		String str = stringPoints.replaceAll("[^-?0-9]+", " ");
		final String[] tokens = str.trim().split(" ");

		for (int i = 0; i < tokens.length; i += 2) {
			points.add(new Point(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1])));
		}
		return points;
	}

	public static Point stringToPoint(String stringPoint) {

		String str = stringPoint.replaceAll("[^-?0-9]+", " ");
		final String[] tokens = str.trim().split(" ");

		return new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
	}

	public static String pointToString(Point point) {
		return String.format("{x: %d, y: %d}", point.x, point.y);
	}
}
