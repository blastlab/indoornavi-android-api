package co.blastlab.indoornavi_android;

import android.graphics.Point;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.utils.PointsUtil;

public class PointsUtilsTest {

	@Test
	public void StringToPointsTest() throws Exception {

		List<Point> points  = new ArrayList<>();
		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));

		String stringPoints = "[{x: 480, y: 480},{x: 1220, y: 480},{x: 1220, y: 1220},{x: 480, y: 1220},{x: 750, y: 750}]";

		Assert.assertEquals( points, PointsUtil.stringToPoints(stringPoints));
	}

	@Test
	public void PointsToStringTest() throws Exception {

		List<Point> points  = new ArrayList<>();
		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));

		String stringPoints = "[{x: 480, y: 480},{x: 1220, y: 480},{x: 1220, y: 1220},{x: 480, y: 1220},{x: 750, y: 750}]";

		Assert.assertEquals( stringPoints, PointsUtil.pointsToString(points));
	}

	@Test
	public void PointToStringTest() throws Exception {

		Point point = new Point(480, 480);
		String stringPoint = "{x: 480, y: 480}";

		Assert.assertEquals( stringPoint, PointsUtil.pointToString(point));
	}

	@Test
	public void StringToPointTest() throws Exception {

		Point point = new Point(480, 480);
		String stringPoint = "{x: 480, y: 480}";

		Assert.assertEquals( point, PointsUtil.stringToPoint(stringPoint));
	}
}
