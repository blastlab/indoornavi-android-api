package co.blastlab.indoornavi_api.model;

import java.util.List;

/**
 * Class representing a Building.
 */
public class Building {

	public int id;
	public String name;
	public List<Floor> floors;

	/**
	 * Building object.
	 *
	 * @param id id of the building
	 * @param name name of the building
	 * @param floors list of all floors in the building
	 */
	public Building(int id, String name, List<Floor> floors) {
		this.id = id;
		this.name = name;
		this.floors = floors;
	}
}
