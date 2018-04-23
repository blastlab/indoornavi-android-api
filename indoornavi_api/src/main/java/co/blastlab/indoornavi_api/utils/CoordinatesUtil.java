package co.blastlab.indoornavi_api.utils;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.model.Coordinates;

public class CoordinatesUtil {

	/**
	 * Converts object coordinates to String representation. String can be evaluate to JavaScript values.
	 *
	 * @param coords  object coordinates
	 * @return String of coordinates values, like {x: 480, y: 450} in JavaScript representation.
	 */
	public static String coordsToString(Coordinates coords) {

		String stringCoords = String.format("{x: %d, y: %d}", coords.x, coords.y);

		return stringCoords;
	}

	/**
	 * Converts String to object coordinates.
	 *
	 * @param stringCoords String of coordinates in JavaScript representation e.g: {x: 480, y: 450}
	 * @return object points coordinates
	 */
	public static List<Point> stringToCoords(String stringCoords) {
		List<Point> points = new ArrayList<>();

		String str = stringCoords.replaceAll("[^-?0-9]+", " ");
		final String[] tokens = str.trim().split(" ");

		for (int i = 0; i < tokens.length; i += 2) {
			points.add(new Point(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1])));
		}
		return points;
	}

}
