package co.blastlab.indoornavi_api.model;

import java.util.Date;

public class INCoordinates {

	public int x, y;
	public short tagId;
	public Date date;

	public INCoordinates(int x, int y, short tagId, Date date)
	{
		this.x = x;
		this.y = y;
		this.tagId = tagId;
		this.date = date;
	}
}
