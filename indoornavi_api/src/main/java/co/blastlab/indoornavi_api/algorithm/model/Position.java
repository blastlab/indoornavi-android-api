package co.blastlab.indoornavi_api.algorithm.model;

public class Position {

	public double x;
	public double y;
	public double z;

	public Long timestamp;

	public Position(double x, double y, double z, Long timestamp) {
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
