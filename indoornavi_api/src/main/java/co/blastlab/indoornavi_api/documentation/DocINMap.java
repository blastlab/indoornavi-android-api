package co.blastlab.indoornavi_api.documentation;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.objects.INMap;

/**
 * Class representing a map, creates the INMap object to communicate with frontend server.
 */
public interface DocINMap {

	/**
	 * Load map of the floor with specific id.
	 *
	 * @param floorId Id of specific floor.
	 */
	 void load(int floorId);

	/**
	 * Create INMap object.
	 *
	 * @param targetHost address to the frontend server
	 * @param apiKey the API key created on server
	 * @param height height of the iframe in pixels
	 * @param weight weight of the iframe in pixels
	 */
	void createMap(String targetHost, String apiKey, int height, int weight);

	/**
	 * Register a callback to be invoked when event occurs.
	 *
	 * @param event type of event listener
	 * @param onEventListener interface - invoked when event occurs.
	 */
	void addEventListener(@INMap.EventListener String event, OnEventListener onEventListener);

	/**
	 * Toggle the tag visibility.
	 *
	 * @param tagId Id of specific tag.
	 */
	void toggleTagVisibility(short tagId);
}
