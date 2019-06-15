package org.kernelab.tentacle.plane;

import java.awt.Graphics2D;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;
import org.kernelab.tentacle.core.Planner;
import org.kernelab.tentacle.semi.AbstractRegion;

public class PlaneCircleRegion extends AbstractRegion
{
	private Coordinate	center;

	private double		radius;

	public PlaneCircleRegion(Coordinate center, double radius)
	{
		super();
		this.setCenter(this.rasterize(center));
		this.setRadius(radius);
	}

	protected boolean containsNoCache(Coordinate raster, Coordinates system)
	{
		double dist = system.getDistance(this.getCenter(), raster);

		return dist <= this.getRadius() || Math.abs(dist - this.getRadius()) < Planner.DETECT_ERROR;
	}

	public void draw(Graphics2D g)
	{
		g.fillOval((int) (this.getCenter().getX() - this.getRadius()),
				(int) (this.getCenter().getY() - this.getRadius()), (int) (this.getRadius() * 2),
				(int) (this.getRadius() * 2));
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof PlaneCircleRegion)
		{
			PlaneCircleRegion r = (PlaneCircleRegion) o;
			return r.getCenter().equals(this.getCenter()) && r.getRadius() == this.getRadius();
		}
		else
		{
			return false;
		}
	}

	public Coordinate getCenter()
	{
		return center;
	}

	public double getRadius()
	{
		return radius;
	}

	protected void setCenter(Coordinate center)
	{
		this.center = center;
	}

	protected void setRadius(double radius)
	{
		this.radius = radius;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "[" + this.getCenter() + "," + this.getRadius() + "]";
	}
}
