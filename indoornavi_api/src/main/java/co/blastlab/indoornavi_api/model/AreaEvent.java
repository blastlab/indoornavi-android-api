package co.blastlab.indoornavi_api.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Class representing an AreaEvent.
 */
public class AreaEvent {

	public int tagId, areaId;
	public Date date;
	public String areaName, mode;

	public static final String ON_LEAVE = "ON_LEAVE";
	public static final String ON_ENTER  = "ON_ENTER";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({ON_LEAVE,ON_ENTER})

	private @interface AreaEventMode {}

	/**
	 * AreaEvent constructor
	 *
	 * @param tagId short id of the tag that entered/left given area
	 * @param date when tag appeared on given area
	 * @param areaId area id
	 * @param areaName name of the area
	 * @param mode {@link AreaEventMode}
	 */
	public AreaEvent(int tagId, Date date, int areaId, String areaName, @AreaEventMode String mode)
	{
		this.tagId = tagId;
		this.date = date;
		this.areaId = areaId;
		this.areaName = areaName;
		this.mode = mode;
	}

	/**
	 * AreaEvent constructor
	 *
	 * @param date when tag appeared on given area
	 * @param areaId area id
	 * @param areaName name of the area
	 * @param mode {@link AreaEventMode}
	 */
	public AreaEvent(Date date, int areaId, String areaName, @AreaEventMode String mode)
	{
		this.date = date;
		this.areaId = areaId;
		this.areaName = areaName;
		this.mode = mode;
	}
}
