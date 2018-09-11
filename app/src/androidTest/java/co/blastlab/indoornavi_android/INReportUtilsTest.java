package co.blastlab.indoornavi_android;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class INReportUtilsTest {

	@Test
	public void jsonToAreaEventTest() {

		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;

		List<AreaEvent> events = new ArrayList<>();
		try{
			events.add(new AreaEvent(10999,  dt.parse("2017-04-03T22:00:00.000"), 1, "test", AreaEvent.ON_ENTER));
			events.add(new AreaEvent(10999,  dt.parse("2017-04-03T22:00:00.000"), 1, "test", AreaEvent.ON_LEAVE));
		}
		catch (Exception e) {
			throw new AssertionError("Parse error, cloud not parse 2017-04-03T22:00:00.000 to Date() format");
		}

		String eventString = "[{\"tagId\":10999,\"date\":\"2017-04-03T22:00:00.000Z\",\"areaId\":1,\"areaName\":\"test\",\"mode\":\"ON_ENTER\"},{\"tagId\":10999,\"date\":\"2017-04-03T22:00:00.000Z\",\"areaId\":1,\"areaName\":\"test\",\"mode\":\"ON_LEAVE\"}]";

		List<AreaEvent> events2 = ReportUtil.jsonToAreaEventArray(eventString);

		for(AreaEvent event : events){
			AreaEvent e = events2.get(events.indexOf(event));

			Assert.assertEquals(event.tagId, e.tagId);
			Assert.assertEquals(event.date, e.date);
			Assert.assertEquals(event.areaId, e.areaId);
			Assert.assertEquals(event.areaName, e.areaName);
			Assert.assertEquals(event.mode, e.mode);

		}
	}

	@Test
	public void jsonToCoordinatesTest() {
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
		String date;

		List<Coordinates> coords = new ArrayList<>();
		try{
			coords.add(new Coordinates(200,800,(short)10999, dt.parse("2017-04-03T22:00:00.000")));
			coords.add(new Coordinates(100,700,(short)10999, dt.parse("2017-04-03T22:00:00.000")));
			coords.add(new Coordinates(500,500,(short)10999, dt.parse("2017-04-03T22:00:00.000")));
		}
		catch (Exception e) {
			throw new AssertionError("Parse error, cloud not parse 2017-04-03T22:00:00.000 to Date() format");
		}

		String coordsString = "[{\"x\":200,\"y\":800,\"tagId\":10999,\"date\":\"2017-04-03T22:00:00.000Z\"},{\"x\":100,\"y\":700,\"tagId\":10999,\"date\":\"2017-04-03T22:00:00.000Z\"},{\"x\":500,\"y\":500,\"tagId\":10999,\"date\":\"2017-04-03T22:00:00.000Z\"}]";
		List<Coordinates> coords2 = ReportUtil.jsonToCoordinatesArray(coordsString);

		for(Coordinates coord : coords){
			Coordinates c = coords2.get(coords.indexOf(coord));

			Assert.assertEquals(coord.x, c.x);
			Assert.assertEquals(coord.y, c.y);
			Assert.assertEquals(coord.deviceId, c.deviceId);
			Assert.assertEquals(coord.date, c.date);

		}
	}
}
