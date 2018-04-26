package co.blastlab.indoornavi_api.model;

import java.util.Date;

/**
 * Class representing a Coordinates.
 */

public class Coordinates {

	public int x, y;
	public short tagId;
	public Date date;

	/**
	 * Coordinates constructor
	 *
	 * @param x - point's x coordinate
	 * @param y - point's y coordinate
	 * @param tagId - short id of the tag
	 * @param date - when tag appeared under the given coordinates
	 */
	public Coordinates(int x, int y, short tagId, Date date)
	{
		this.x = x;
		this.y = y;
		this.tagId = tagId;
		this.date = date;
	}
}
