package co.blastlab.indoornavi_api.algorithm.model;

public class PairOfPoints {

	public Position pointA;
	public Position pointB;
	public double weight;

	public PairOfPoints(Position pointA, Position pointB, double weight) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.weight = weight;
	}
}
