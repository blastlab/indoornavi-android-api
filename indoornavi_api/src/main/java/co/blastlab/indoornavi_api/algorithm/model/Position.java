package co.blastlab.indoornavi_api.algorithm.model;

public class Position {

	public Point position;
	public Long timestamp;

	public Position(Point position, Long timestamp) {
		this.position = position;
		this.timestamp = timestamp;
	}
}
