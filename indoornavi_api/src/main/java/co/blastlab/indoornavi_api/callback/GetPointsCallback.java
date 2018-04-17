package co.blastlab.indoornavi_api.callback;

import android.graphics.Point;

import java.util.List;

import co.blastlab.indoornavi_api.objects.INPolyline;

/**
 * GetPointsCallback -  interface to handle Coordinates as getPoints response {@link INPolyline} .
 */
public interface GetPointsCallback {

	void onReceivePoints(List<Point> points);
}
