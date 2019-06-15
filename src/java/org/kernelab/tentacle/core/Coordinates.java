package org.kernelab.tentacle.core;

public interface Coordinates
{
	public Coordinate getCoordinate(double x, double y);

	public double getDistance(Coordinate a, Coordinate b);

	public double getIncludedAngle(Coordinate va, Coordinate vb);

	public Iterable<Coordinate> iteratePointsOnLine(double delta, Coordinate a, Coordinate b);

	public double product(Coordinate va, Coordinate vb);

	public Coordinate subtract(Coordinate b, Coordinate a);
}
