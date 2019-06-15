package org.kernelab.tentacle.core;

public interface Coordinate extends Rasterizable
{
	public double getX();

	public double getY();

	public Coordinate rasterize();
}
