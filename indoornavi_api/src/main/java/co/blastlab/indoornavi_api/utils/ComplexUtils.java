package co.blastlab.indoornavi_api.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.model.Building;
import co.blastlab.indoornavi_api.model.Complex;
import co.blastlab.indoornavi_api.model.Floor;

/**
 * Utility class for parsing data to list of Complexes represented as a String.
 */
public class ComplexUtils {

	/**
	 * Converts String in Json format to list of  {@link Complex} object.
	 *
	 * @param jsonString String in Json format containing Complexes data
	 * @return list of Complexes, null if unsuccessful
	 */
	public static List<Complex> getComplexesFromJSON(String jsonString) {
		List<Complex> complexes = new ArrayList<>();

		try {
			JSONObject jo = new JSONObject(jsonString);
			JSONArray jsonComplexList = new JSONArray(jo.getString("complexes"));

			if(jsonComplexList.length() == 0) {
				return null;
			}

			for (int i = 0; i < jsonComplexList.length(); i++) {
				JSONObject complex = jsonComplexList.getJSONObject(i);
				complexes.add(new Complex(complex.getInt("id"), complex.getString("name"), getBuildingsFromJSON(complex.getJSONArray("buildings"))));
			}
			return complexes;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	private static List<Floor> getFloorsFromJSON(JSONArray jsonFloorsList) {
		List<Floor> floors = new ArrayList<>();

		try{
			for (int i = 0; i < jsonFloorsList.length(); i++) {
				JSONObject floor = jsonFloorsList.getJSONObject(i);
				floors.add(new Floor(floor.getInt("id"), floor.getString("name"), floor.getInt("level")));
			}
			return floors;

		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	private static List<Building> getBuildingsFromJSON(JSONArray jsonBuildingList) {
		List<Building> buildings = new ArrayList<>();

		try{
			for (int i = 0; i < jsonBuildingList.length(); i++) {
				JSONObject building = jsonBuildingList.getJSONObject(i);
				buildings.add(new Building(building.getInt("id"), building.getString("name"), getFloorsFromJSON(building.getJSONArray("floors"))));
			}
			return buildings;

		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}
}
