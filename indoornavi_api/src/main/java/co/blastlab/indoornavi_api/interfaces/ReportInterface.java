package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import java.util.List;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class ReportInterface {

	@JavascriptInterface
	public void areaEvents(int promiseId, String areaEvents) {
		List<AreaEvent> events = ReportUtil.jsonToAreaEvent(areaEvents);

		Controller.promiseCallbackMap.get(promiseId).onReady(events);
		Controller.promiseCallbackMap.remove(promiseId);
	}

	@JavascriptInterface
	public void coordinates(int promiseId, String coords) {
		List<Coordinates> coordinates = ReportUtil.jsonToCoordinates(coords);

		Controller.promiseCallbackMap.get(promiseId).onReady(coordinates);
		Controller.promiseCallbackMap.remove(promiseId);
	}
}
