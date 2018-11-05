package co.blastlab.indoornavi_api.algorithm;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.blastlab.indoornavi_api.algorithm.model.Anchor;
import co.blastlab.indoornavi_api.algorithm.model.PairOfPoints;
import co.blastlab.indoornavi_api.algorithm.utils.Matrix;
import co.blastlab.indoornavi_api.algorithm.model.Position;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Algorithm {

	public SparseArray<Anchor> anchorMatrix = new SparseArray<>();
	private int distance_reference = 1;
	private double circleRange = 2;
	private double maxDistanceFromAnchor;

	private double[] exp = new double[]{
		2.1,       //free space
		3.1,       // urban area
		4,         //shadowed urban area
		1.7,       //Line-of-Sight in Building
		5,         //Obstruction in Building
		2.5};      //Obstruction in Factories


	public enum LocalizationMethod {TRILATERATION, CROSSING_CIRCLE}

	//public Localization() {}

	public Position getPosition(LocalizationMethod localizationMethod, SparseArray<Anchor> anchorMatrix, double maxDistanceFromAnchor) {
		if (anchorMatrix == null || anchorMatrix.size() == 0) return null;

		this.anchorMatrix = anchorMatrix;
		this.maxDistanceFromAnchor = maxDistanceFromAnchor;

		switch (localizationMethod) {
			case TRILATERATION:
				return trilatationMethod(this.anchorMatrix);
			case CROSSING_CIRCLE:
				return crossingCirclesMethod(this.anchorMatrix);
		}
		return null;
	}

	private PairOfPoints getCrossingPointFromBeaconsCircle(Anchor anchor1, Anchor anchor2) {

		double distance = getDistance(anchor1.position, anchor2.position);
		double delta = getDelta(anchor1.distanceXY, anchor2.distanceXY, distance);
		//check if crossing circle
		if (anchor1.distanceXY + anchor2.distanceXY > distance && distance > abs(anchor1.distanceXY - anchor2.distanceXY) && delta != -1) {
			double x = (anchor1.position.x + anchor2.position.x) / 2.0 + (anchor2.position.x - anchor1.position.x) * (pow(anchor1.distanceXY, 2) - pow(anchor2.distanceXY, 2)) / (2.0 * distance * distance);
			double y = (anchor1.position.y + anchor2.position.y) / 2.0 + (anchor2.position.y - anchor1.position.y) * (pow(anchor1.distanceXY, 2) - pow(anchor2.distanceXY, 2)) / (2.0 * distance * distance);

			Position pointA = new Position(x + 2.0 * (anchor1.position.y - anchor2.position.y) * delta / (distance * distance),
				y - 2.0 * (anchor1.position.x - anchor2.position.x) * delta / (distance * distance), 0);
			Position pointB = new Position(x - 2.0 * (anchor1.position.y - anchor2.position.y) * delta / (distance * distance),
				y + 2.0 * (anchor1.position.x - anchor2.position.x) * delta / (distance * distance), 0);
			return new PairOfPoints(pointA, pointB, 0);

		} else {
			Position point = getBestPointBetweenTwoNotCrossingAnchors(anchor1, anchor2);
			return new PairOfPoints(point, null, 0);
		}
	}

	private double getDelta(double d1, double d2, double distance) {
		double sqrtStatement = (distance + d1 + d2) * (distance + d1 - d2) * (distance - d1 + d2) * (-distance + d1 + d2);
		if (sqrtStatement < 0) {
			return -1;
		}
		return sqrt(sqrtStatement) / 4.0;
	}

	private Position getBestPointBetweenTwoNotCrossingAnchors(Anchor anchor1, Anchor anchor2) {
		double dx = abs(anchor1.position.x - anchor2.position.x);
		double dy = abs(anchor1.position.y - anchor2.position.y);

		double x, y;
		if (anchor1.position.x < anchor2.position.x) {
			x = anchor1.position.x + ((anchor1.distanceXY * dx) / (anchor1.distanceXY + anchor2.distanceXY));
		} else {
			x = anchor2.position.x + ((anchor2.distanceXY * dx) / (anchor1.distanceXY + anchor2.distanceXY));
		}

		if (anchor1.position.y < anchor2.position.y) {
			y = anchor1.position.y + ((anchor1.distanceXY * dy) / (anchor1.distanceXY + anchor2.distanceXY));
		} else {
			y = anchor2.position.y + ((anchor2.distanceXY * dy) / (anchor1.distanceXY + anchor2.distanceXY));
		}
		return new Position(x, y, 0);
	}

	private List<Anchor> getThreeNearestAnchors(SparseArray<Anchor> anchorMatrix) {
		List<Anchor> bestThreeAnchors = new ArrayList<>();
		int numberOfAnchorsToLocate = 3;

		for (int i = 0; i < numberOfAnchorsToLocate; ++i) {

			double minValue = 10000000;
			int indexOfTheBest = -1;

			for (int j = 0; j < anchorMatrix.size(); ++j) {

				if (anchorMatrix.valueAt(j).distanceXY < minValue && !bestThreeAnchors.contains(anchorMatrix.valueAt(j))) {
					indexOfTheBest = j;
					minValue = anchorMatrix.valueAt(j).distanceXY;
				}
			}
			if (indexOfTheBest != -1) bestThreeAnchors.add(anchorMatrix.valueAt(indexOfTheBest));
		}
		return bestThreeAnchors;
	}

	private double getDistance(Position pointA, Position pointB) {
		return sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x) + (pointA.y - pointB.y) * (pointA.y - pointB.y));
	}

	private List<Integer> Kalman_filter(List<Integer> input) {
		if (input.isEmpty()) return null;

		double K, P_prev, x_prev_estimated, x_estimated = calculateAverage(input) == Double.NEGATIVE_INFINITY ? input.get(0) : calculateAverage(input);

		double P = 0;
		double Q = 0.065;
		double R = 1.4;
		int z;

		List<Integer> X_estimated_array = new ArrayList<>();

		for (Integer in : input) {
			x_prev_estimated = x_estimated;
			P_prev = P + Q;

			z = in;
			K = P_prev / (P_prev + R);
			x_estimated = x_prev_estimated + K * (z - x_prev_estimated);
			P = (1 - K) * P_prev;

			X_estimated_array.add((int) Math.floor(x_estimated));
		}
		return X_estimated_array;
	}


	private double[] getWhiteNoise(int vectorSize) {

		double[] white_noise = new double[vectorSize];
		Random random = new Random();

		for (int i = 0; i < vectorSize; i++) {
			white_noise[i] = random.nextGaussian();
		}
		return white_noise;
	}

	private void rssiFilter() {
		for (int j = 0; j < anchorMatrix.size(); j++) {
			anchorMatrix.valueAt(j).rssiAvg = calculateAverage(Kalman_filter(anchorMatrix.valueAt(j).rssi_array));
			anchorMatrix.valueAt(j).rssi_array.clear();
		}
	}

	private double calculateAverage(List<Integer> marks) {
		if (marks == null || marks.isEmpty()) return Double.NEGATIVE_INFINITY;
		Integer sum = 0;
		if (!marks.isEmpty()) {
			for (Integer mark : marks) {
				sum += mark;
			}
			return sum.doubleValue() / marks.size();
		}
		return sum;
	}

	private void getDistanceFromNode() {
		for (int i = 0; i < anchorMatrix.size(); i++) {
			if (anchorMatrix.valueAt(i).rssiAvg == Double.NEGATIVE_INFINITY) {
				anchorMatrix.removeAt(i);
				i -= 1;
				continue;
			}
			anchorMatrix.valueAt(i).distance = pow(distance_reference * 10, ((anchorMatrix.valueAt(i).rssiRef - anchorMatrix.valueAt(i).rssiAvg /*+ getWhiteNoise(anchorMatrix.size())*/) / (10 * exp[0])));
			double distanceXY_square = pow(anchorMatrix.valueAt(i).distance, 2) - (pow((anchorMatrix.valueAt(i).position.z - 1), 2));
			anchorMatrix.valueAt(i).distanceXY = distanceXY_square > 0 ? sqrt(distanceXY_square) : 0;
		}
	}

	private Position trilatationMethod(SparseArray<Anchor> anchorMatrix) {

		int n = anchorMatrix.size();

		double[][] A = new double[2][n - 1];
		double[] b = new double[n - 1];

		rssiFilter();
		getDistanceFromNode();

		for (int i = 0; i < n - 1; i++) {
			A[0][i] = (2 * (anchorMatrix.valueAt(i).position.x - anchorMatrix.valueAt(n - 1).position.x));
			A[1][i] = (2 * (anchorMatrix.valueAt(i).position.y - anchorMatrix.valueAt(n - 1).position.y));
			b[i] = pow(anchorMatrix.valueAt(i).position.x, 2) - pow(anchorMatrix.valueAt(n - 1).position.x, 2) +
				pow(anchorMatrix.valueAt(i).position.y, 2) - pow(anchorMatrix.valueAt(n - 1).position.y, 2) - pow(anchorMatrix.valueAt(i).distanceXY, 2) -
				pow(anchorMatrix.valueAt(n - 1).distanceXY, 2);
		}

		Matrix matrixA = new Matrix(A);
		Matrix matrixb = new Matrix(b);

		Matrix res = (matrixA.transpose().multiply(matrixA)).solve(matrixA.transpose().multiply(matrixb));


		return new Position(res.data[0][0], res.data[1][0], 0.0);
	}

	private Position crossingCirclesMethod(SparseArray<Anchor> anchorMatrix) {

		rssiFilter();
		getDistanceFromNode();
		List<Anchor> bestThreeAnchors = getThreeNearestAnchors(anchorMatrix);

		List<Position> nearestPointArray = new ArrayList<>();
		List<Anchor> closeAnchors = new ArrayList<>();

		for (Anchor anchor : bestThreeAnchors) {
			if (anchor.distanceXY < maxDistanceFromAnchor) closeAnchors.add(anchor);
		}
		if (closeAnchors.size() > 1) {
			for (int i = 0; i < closeAnchors.size(); ++i) {
				for (int j = i + 1; j < closeAnchors.size(); ++j) {
					PairOfPoints nearestPoints = getCrossingPointFromBeaconsCircle(closeAnchors.get(i), closeAnchors.get(j));
					if (nearestPoints.pointA != null) nearestPointArray.add(nearestPoints.pointA);
					if (nearestPoints.pointB != null) nearestPointArray.add(nearestPoints.pointB);
				}
			}
		} else if (closeAnchors.size() == 1) {
			nearestPointArray.add(new Position(closeAnchors.get(0).position.x, closeAnchors.get(0).position.y, 0));
		} else {
			return null;
		}
		return getMeanPointFromPointsList(getThreeClosesPoints(nearestPointArray));
	}

	private List<Position> getThreeClosesPoints(List<Position> points) {
		List<Position> bestThreePoints = new ArrayList<>();

		for (Position point : points) {
			for (int i = 0; i < points.size(); i++) {
				point.z += getDistance(point, points.get(i));
			}
			if (points.size() > 1) point.z = point.z / (points.size() - 1);
		}

		if (points.size() < 3) {
			return points;
		}

		for (int i = 0; i < 3; ++i) {

			double minValue = 10000000;
			int indexOfTheBest = -1;

			for (int j = 0; j < points.size(); ++j) {

				if (points.get(j).z < minValue && !bestThreePoints.contains(points.get(j))) {
					indexOfTheBest = j;
					minValue = points.get(j).z;
				}
			}
			if (indexOfTheBest != -1) bestThreePoints.add(points.get(indexOfTheBest));
		}
		return bestThreePoints;
	}

	private Position getMeanPointFromPointsList(List<Position> nearestPointArray) {
		if (nearestPointArray.isEmpty()) return null;

		double x = 0, y = 0;
		for (int i = 0; i < nearestPointArray.size(); ++i) {
			x += nearestPointArray.get(i).x;
			y += nearestPointArray.get(i).y;
		}
		return new Position(x > 0 ? x / nearestPointArray.size() : 0, y > 0 ? y / nearestPointArray.size() : 0, 0);
	}


	public Position getIntersectionCircleLine(Position circlePosition, Position nextPosition) {
		double distance = getDistance(circlePosition, nextPosition);

		if (distance < circleRange) {
			return nextPosition;
		}

		if ((circlePosition.x - nextPosition.x) == 0) {
			double x_correction = circlePosition.y > nextPosition.y ? (-circleRange) : circleRange;
			return new Position(circlePosition.x, circlePosition.y + x_correction, 0);
		}

		if ((circlePosition.y - nextPosition.y) == 0) {
			double y_correction = circlePosition.x > nextPosition.x ? (-circleRange) : circleRange;
			return new Position(circlePosition.x + y_correction, circlePosition.y, 0);
		}

		double a = (circlePosition.y - nextPosition.y) / (circlePosition.x - nextPosition.x);
		double b = circlePosition.y - a * circlePosition.x;

		double A = 1 + pow(a, 2);
		double B = 2 * ((a * b) - circlePosition.x - a * circlePosition.y);
		double C = pow(circlePosition.x, 2) + pow(b, 2) - 2 * b * circlePosition.y + pow(circlePosition.y, 2) - pow(circleRange, 2);

		double x_1 = (-B - sqrt(pow(B, 2) - 4 * A * C)) / (2 * A);
		double y_1 = a * x_1 + b;
		Position pointA = new Position(x_1, y_1, 0);

		double x_2 = (-B + sqrt(pow(B, 2) - 4 * A * C)) / (2 * A);
		double y_2 = a * x_2 + b;
		Position pointB = new Position(x_2, y_2, 0);

		if (getDistance(pointA, nextPosition) < getDistance(pointB, nextPosition)) {
			return pointA;
		} else {
			return pointB;
		}
	}

}