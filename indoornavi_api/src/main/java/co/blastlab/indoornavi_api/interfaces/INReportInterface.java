package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import java.util.List;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class INReportInterface {

	@JavascriptInterface
	public void areaEvents(int promiseId, String areaEvents) {

		if(!areaEvents.equals("[]") && !areaEvents.equals("null")) {
			List<AreaEvent> events = ReportUtil.jsonToAreaEventArray(areaEvents);
			Controller.promiseCallbackMap.get(promiseId).onReady(events);
		}
		else {
			Controller.promiseCallbackMap.get(promiseId).onReady(null);
		}
		Controller.promiseCallbackMap.remove(promiseId);
	}

	@JavascriptInterface
	public void coordinates(int promiseId, String coords) {

		if(!coords.equals("[]") && !coords.equals("null")) {
			List<Coordinates> coordinates = ReportUtil.jsonToCoordinatesArray(coords);
			Controller.promiseCallbackMap.get(promiseId).onReady(coordinates);
		}
		else {
			Controller.promiseCallbackMap.get(promiseId).onReady(null);
		}
		Controller.promiseCallbackMap.remove(promiseId);
	}
}
