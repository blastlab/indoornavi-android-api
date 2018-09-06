package co.blastlab.indoornavi_api.algorithm.model;

public class PairOfPoints {

	public Point pointA;
	public Point pointB;
	public double weight;

	public PairOfPoints(Point pointA, Point pointB, double weight) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.weight = weight;
	}
}
