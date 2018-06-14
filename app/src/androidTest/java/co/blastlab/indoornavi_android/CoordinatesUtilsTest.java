package co.blastlab.indoornavi_android;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.CoordinatesUtil;

public class CoordinatesUtilsTest {

	@Test
	public void coordToStringTest() {
		Date date = new Date();
		Coordinates coords = new Coordinates(200, 400, (short)10999, date);

		String coordsString = String.format("{x: 200, y: 400, tagId: 10999, date: new Date(%d)}", date.getTime());
		Assert.assertEquals(coordsString, CoordinatesUtil.coordsToString(coords));
	}
}
