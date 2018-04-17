package co.blastlab.indoornavi_api;

import org.jdeferred.Deferred;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

	private Constants() {
	}

	public static Map<Integer, Deferred> promiseMap = new HashMap<>();
	public static final String LOG = "indoorNavi";
	public static final String indoorNaviInitialization = "var navi = new INMap(\"%s\",\"%s\",\"map\",{width:%d,height:%d});";
}
