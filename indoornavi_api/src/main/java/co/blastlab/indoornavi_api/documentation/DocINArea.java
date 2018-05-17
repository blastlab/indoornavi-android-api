package co.blastlab.indoornavi_api.documentation;

import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

import java.util.List;

/**
 * Class representing an area, creates the INArea object in iframe that communicates with frontend server and draws area.
 */
public interface DocINArea {

	/**
	 * Place area on the map with all given settings. There is necessary to use points() method before draw() method to indicate where area should to be located.
	 * Use of this method is indispensable to draw area with set configuration in the IndoorNavi Map.
	 */
	void draw();

	/**
	 * Locates area at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param points List of {@link Point} To be able to draw area, at least 3 points must be provided.
	 */
	void points(List<Point> points);

	/**
	 * Fills Area whit given color. To apply this method it's necessary to call draw() after.
	 *
	 * @param color string that specifies the color. Supports color in hex format '#AABBCC' and rgb format 'rgb(255,255,255)';
	 */
	void setFillColor(@ColorInt int color);

	/**
	 * Sets Area opacity. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	void setOpacity(@FloatRange(from=0.0, to=1.0)double opacity);
}
