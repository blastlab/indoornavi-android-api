package co.blastlab.indoornavi_api.documentation;

import android.graphics.Point;
import android.webkit.ValueCallback;

import java.util.List;

import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;

/**
 * Class INObject is the root of the IndoorNavi objects hierarchy. Every IN object has INObject as a superclass (except INMap).
 */
public interface DocINObject {

	/**
	 * Method wait till object is create.
	 * Use of this method is indispensable to operate on the object.
	 *
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	void ready(OnObjectReadyCallback onObjectReadyCallback);

	/**
	 * Return the id of the object.
	 *
	 * @param onReceiveValueCallback interface - invoked when object id is available.
	 */
	void getID(final OnReceiveValueCallback<Long> onReceiveValueCallback);

	/**
	 * Receives coordinates of the given object.
	 *
	 * @param onReceiveValueCallback interface - invoked when list of points is available.
	 */
	void getPoints(final OnReceiveValueCallback<List<Point>> onReceiveValueCallback);

	/**
	 * Removes object and destroys it instance in the frontend server, but do not destroys object class instance in your app.
	 */
	void remove();

	/**
	 * Checks if point of given coordinates is inside of the object.
	 *
	 * @param coordinates checking coordinates
	 * @param valueCallback interface - invoked when boolean value is available.
	 */
	void isWithin(Coordinates coordinates, final ValueCallback<Boolean> valueCallback);
}
