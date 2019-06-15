package org.kernelab.tentacle.semi;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;

public abstract class AbstractCoordinates implements Coordinates
{
	public double getDistance(Coordinate vector)
	{
		return getDistance(getCoordinate(0, 0), vector);
	}

	public double product(Coordinate va, Coordinate vb)
	{
		return va.getX() * vb.getX() + va.getY() * vb.getY();
	}

	public Coordinate subtract(Coordinate b, Coordinate a)
	{
		return this.getCoordinate(b.getX() - a.getX(), b.getY() - a.getY());
	}
}
