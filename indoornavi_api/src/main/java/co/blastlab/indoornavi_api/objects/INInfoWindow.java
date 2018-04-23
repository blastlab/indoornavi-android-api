package co.blastlab.indoornavi_api.objects;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.AndroidExecutionScope;

import java.util.Locale;

public class INInfoWindow extends INObject{

	private INMap inMap;

	public enum Position{
		TOP, RIGHT, BOTTOM, LEFT, TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT;
	}


	public INInfoWindow(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format(Locale.US, "infoWindow%d", this.hashCode());

		String javaScriptString = String.format("var %s = new INInfoWindow(navi);", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void height(int height) {
		String javaScriptString = String.format(Locale.US, "%s.height(%d);", objectInstance, height);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void width(int width) {
		String javaScriptString = String.format(Locale.US, "%s.width(%d);", objectInstance, width);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void open(INObject inObject) {
		String javaScriptString = String.format(Locale.US, "%s.open(%s);", objectInstance, inObject.objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Call inherit method from {@link INObject}.
	 * Method wait till infoWindow object is create.
	 * Use of this method is indispensable to operate on infoWindow object.
	 *
	 * @param doneCallback DoneCallback interface - trigger when infoWindow is create (Promise is resolved).
	 */
	public void ready(DoneCallback<String> doneCallback)
	{
		AndroidDeferredManager dm = new AndroidDeferredManager();
		dm.when(checkReady(), AndroidExecutionScope.UI).done(doneCallback);
	}

	public void setInnerHTML(String html)
	{
		String javaScriptString = String.format("%s.setInnerHTML('%s');", objectInstance, html);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void setPosition(Position position) {
		String javaScriptString = String.format(Locale.US, "%s.setPosition(%d);", objectInstance, position.ordinal());
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
