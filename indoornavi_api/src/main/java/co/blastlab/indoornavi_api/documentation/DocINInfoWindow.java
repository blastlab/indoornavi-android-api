package co.blastlab.indoornavi_api.documentation;

import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INObject;

/**
 * Class representing a info window, creates the INInfoWindow object in iframe that communicates with frontend server and adds info window to a given INObject child.
 */
public interface DocINInfoWindow {

	/**
	 * Sets height dimension of info window. Use of this method is optional. Default dimensions for info window height is 250px.
	 *
	 * @param height info window height given in pixels, min available dimension is 50px.
	 */
	void height(int height);

	/**
	 * Sets width dimension of infoWindow. Use of this method is optional. Default dimensions for info window width is 250px.
	 *
	 * @param width infoWindow width given in pixels, min available dimension is 50px.
	 */
	void width(int width);

	/**
	 * Displays info window on {@link INObject} object.
	 *
	 * @param inObject the object on which you want to display info window.
	 */
	void open(INObject inObject);

	/**
	 * Sets info window content.
	 *
	 * @param html String containing text or html template. To reset info window content it is indispensable to call draw() method again.
	 */
	void setInnerHTML(String html);

	/**
	 * Sets info window position relative to the object. Use of this method is optional. Default position for info window is TOP.
	 *
	 * @param position {@link INInfoWindow.Position}
	 */
	void setPosition(@INInfoWindow.Position int position);
}
