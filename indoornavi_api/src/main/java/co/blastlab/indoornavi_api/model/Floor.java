package co.blastlab.indoornavi_api.model;

/**
 * Class representing a Floor.
 */
public class Floor {

	public int id;
	public String name;
	public int level;

	/**
	 * Floor object.
	 *
	 * @param id id of the floor
	 * @param name name of the floor
	 * @param level level of the floor
	 */
	public Floor(int id, String name, int level) {
		this.id = id;
		this.name = name;
		this.level = level;
	}
}
