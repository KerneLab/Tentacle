package org.kernelab.tentacle.semi;

import org.kernelab.tentacle.core.Coordinate;

public abstract class AbstractCoordinate implements Coordinate
{
	private double	x;

	private double	y;

	private boolean	rasterized	= false;

	public AbstractCoordinate(Coordinate point)
	{
		this.x = point.getX();
		this.y = point.getY();
	}

	public AbstractCoordinate(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public abstract boolean equals(Object o);

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	public boolean isRasterized()
	{
		return rasterized;
	}

	protected void setRasterized(boolean rasterized)
	{
		this.rasterized = rasterized;
	}

	protected void setX(double x)
	{
		this.x = x;
	}

	protected void setY(double y)
	{
		this.y = y;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "(" + this.getX() + "," + this.getY() + ")";
	}
}
