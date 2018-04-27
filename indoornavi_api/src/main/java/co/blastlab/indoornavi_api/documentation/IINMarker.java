package co.blastlab.indoornavi_api.documentation;

import android.graphics.Point;

import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;

/**
 * Class representing a marker, creates the INMarker object in iframe that communicates with frontend server and places a marker on the map.
 */
public interface IINMarker {

	/**
	 * Register a callback to be invoked when marker is clicked.
	 *
	 * @param onMarkerClickListener interface - invoked when event occurs.
	 */
	void addEventListener(OnMarkerClickListener onMarkerClickListener);

	/**
	 * Removes listener if exists.
	 */
	void removeEventListener();

	/**
	 * Place marker on the map with all given settings. There is necessary to use point() method before draw() method to indicate where marker should to be located.
	 * Use of this method is indispensable to draw marker with set configuration on the map.
	 */
	void draw();

	/**
	 * Locates marker at given coordinates. Use of this method is indispensable.
	 *
	 * @param point {@link Point} Position will be clipped to the point in the bottom center of marker icon.
	 */
	void point(Point point);

	/**
	 * Sets marker label.
	 *
	 * @param label string that will be used as a marker label. If label method isn't used than no label is going to be displayed.
	 * To reset label to a new string call this method again passing new label as a string and call draw() method again.
	 */
	void setLabel(String label);

	/**
	 * Remove marker label. To remove label it is indispensable to call draw() method again.
	 */
	void removeLabel();
	/**
	 * Set marker icon. To apply this method it's necessary to call draw() after.
	 *
	 * @param path String url path to your icon;
	 */
	void setIcon(String path);
}
