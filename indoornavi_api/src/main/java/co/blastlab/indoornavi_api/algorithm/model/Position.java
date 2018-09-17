package co.blastlab.indoornavi_api.algorithm.model;

import java.util.Date;

public class Position {

	public double x;
	public double y;
	public double z;

	public Date timestamp;

	public Position(double x, double y, double z, Date timestamp) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.timestamp = timestamp;
	}

	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
