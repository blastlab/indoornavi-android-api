package co.blastlab.indoornavi_api.model;

import java.util.Date;

/**
 * Class representing a Coordinates.
 */

public class Coordinates {

	public int x, y, z;
	public short deviceId;
	public Date date;

	/**
	 * Coordinates constructor
	 *
	 * @param x point's x coordinate
	 * @param y point's y coordinate
	 * @param z point's z coordinate
	 * @param deviceId short id of the device
	 * @param date when tag appeared under the given coordinates
	 */
	public Coordinates(int x, int y, int z, short deviceId, Date date)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.deviceId = deviceId;
		this.date = date;
	}
}
