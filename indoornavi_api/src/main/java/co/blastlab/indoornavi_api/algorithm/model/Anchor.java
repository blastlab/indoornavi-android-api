package co.blastlab.indoornavi_api.algorithm.model;

import java.util.ArrayList;
import java.util.List;

public class Anchor {

	public int id;
	public Point position;
	public double distance = 100.0;
	public double distanceXY = 100.0;
	public List<Integer> rssi_array = new ArrayList<>();
	public double rssiAvg = -100.0;
	public int configNumber;
	public int rssiRef;

	public Anchor(int id, Point position, int rssiRef, int configNumber) {
		this.id = id;
		this.position = position;
		this.configNumber = configNumber;
		this.rssiRef = rssiRef;
	}
}
