package co.blastlab.indoornavi_api.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
	 * @return String of coordinates values, like {x: 480, y: 450} in JavaScript representation.
	 */
	public static String coordsToString(Coordinates coords) {

		return String.format(Locale.US, "{x: %d, y: %d, tagId: %d, date: new Date(%d)}", coords.x, coords.y, coords.tagId, coords.date.getTime());
	}

	/**
	 * Converts String to object coordinates.
	 *
	 * @param stringCoords String of coordinates in JavaScript representation e.g: {x: 480, y: 450}
	 * @return object points coordinates or null if unsuccessful
	 */
	public static List<Coordinates> stringToCoords(String stringCoords) {
		List<Coordinates> coordinates = new ArrayList<>();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;
		try {

			String str = stringCoords.replaceAll("[^-?0-9]+", " ");
			final String[] tokens = str.trim().split(" ");

			for (int i = 0; i < tokens.length; i += 4) {
				date = tokens[i + 3];
				date = date.substring(0, date.length() - 1);
				coordinates.add(new Coordinates(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1]), (short) Integer.parseInt(tokens[i + 2]), dt.parse(date)));
			}
			return coordinates;
		}
		catch (Exception e) {
			Log.e("Coord parse exception: ", e.toString());
		}
		return null;
	}

}
