package co.blastlab.indoornavi_api.model;

import java.util.Date;

public class AreaEvent {

	int tagId, areaId;
	Date date;
	String areaName, mode;

	public AreaEvent(int tagId, Date date, int areaId, String areaName, String mode)
	{
		this.tagId = tagId;
		this.date = date;
		this.areaId = areaId;
		this.areaName = areaName;
		this.mode = mode;
	}
}
