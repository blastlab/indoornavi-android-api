package co.blastlab.indoornavi_api.documentation;

import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;

public interface DocReport {

	/**
	 * Retrieve list of archived Area events.
	 *
	 * @param floorId id of the floor you want to get area events from
	 * @param from start date of the period
	 * @param to end date of the period
	 * @param onObjectReadyCallback callback interface invoke when {@link AreaEvent} list is ready
	 */
	void getAreaEvents(int floorId, Date from, Date to, OnObjectReadyCallback<List<AreaEvent>> onObjectReadyCallback);

	/**
	 * Retrieve list of archived coordinates.
	 *
	 * @param floorId id of the floor you want to get coordinates from
	 * @param from start date of the period
	 * @param to end date of the period
	 * @param onObjectReadyCallback callback interface invoke when {@link Coordinates} list is ready
	 */
	void getCoordinates(int floorId, Date from, Date to, OnObjectReadyCallback<List<Coordinates>> onObjectReadyCallback);
}
