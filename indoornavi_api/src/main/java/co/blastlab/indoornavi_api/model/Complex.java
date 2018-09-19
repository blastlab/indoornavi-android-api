package co.blastlab.indoornavi_api.model;

import java.util.List;

/**
 * Class representing a Complex.
 */
public class Complex {

	public int id;
	public String name;
	public List<Building> buildings;

	/**
	 * Complex object.
	 *
	 * @param id id of the complex
	 * @param name name of the complex
	 * @param buildings list of all buildings in the complex
	 */
	public Complex(int id, String name, List<Building> buildings) {
		this.id = id;
		this.name = name;
		this.buildings = buildings;
	}
}
