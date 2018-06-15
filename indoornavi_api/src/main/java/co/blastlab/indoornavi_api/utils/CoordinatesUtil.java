package co.blastlab.indoornavi_api.utils;

import java.util.Locale;

import co.blastlab.indoornavi_api.model.Coordinates;

/**
 * Utility class for parsing data from and to coordinates object represented as a String.
 */
public class CoordinatesUtil {

	/**
	 * Converts object coordinates to String representation. String can be evaluate to JavaScript values.
	 *
	 * @param coords object coordinates
	 * @return String of coordinates values, like {x: 480, y: 450, tagId: 10999, date: new Date(1527851140649)} in JavaScript representation.
	 */
	public static String coordsToString(Coordinates coords) {

		return String.format(Locale.US, "{x: %d, y: %d, tagId: %d, date: new Date(%d)}", coords.x, coords.y, coords.tagId, coords.date.getTime());
	}
}
