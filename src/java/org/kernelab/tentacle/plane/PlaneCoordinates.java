package org.kernelab.tentacle.plane;

import java.util.Iterator;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.semi.AbstractCoordinates;
import org.kernelab.tentacle.util.Log;

public class PlaneCoordinates extends AbstractCoordinates
{
	public class LinePointsIterator implements Iterable<Coordinate>, Iterator<Coordinate>
	{
		private double		delta;

		private Coordinate	a;

		private Coordinate	b;

		private Coordinate	diff;

		private double		dist;

		private double		theta;

		private double		rho;

		public LinePointsIterator(double delta, Coordinate a, Coordinate b)
		{
			this.delta = delta;
			this.a = a;
			this.b = b;
			this.diff = subtract(this.b, this.a);
			this.dist = getDistance(this.diff);
			this.theta = getAzimuth(this.diff, this.dist);
			this.rho = 0;
		}

		protected PlaneCoordinate getCoordinate(Coordinate a, double rho, double theta)
		{
			return PlaneCoordinates.this. //
					getCoordinate(a.getX() + rho * Math.cos(theta), a.getY() + rho * Math.sin(theta));
		}

		public boolean hasNext()
		{
			return this.rho <= this.dist;
		}

		public Iterator<Coordinate> iterator()
		{
			return this;
		}

		public PlaneCoordinate next()
		{
			PlaneCoordinate next = this.getCoordinate(this.a, this.rho, this.theta);

			if (this.rho < this.dist)
			{
				this.rho += this.delta;
				if (this.rho >= this.dist)
				{
					this.rho = this.dist;
				}
			}
			else
			{
				this.rho += this.delta;
			}

			return next;
		}

		public void remove()
		{
		}
	}

	public static final void main(String[] args)
	{
		PlaneCoordinates system = new PlaneCoordinates();

		Coordinate starting = system.getCoordinate(50, 50);

		double rad = 270.0 / 180.0 * Math.PI;

		Coordinate terminal = system.getCoordinate(starting, 20, rad);

		Log.log(terminal);
	}

	public double getAzimuth(Coordinate vector)
	{
		return this.getAzimuth(vector, this.getDistance(vector));
	}

	public double getAzimuth(Coordinate vector, double length)
	{
		if (length == 0)
		{
			return 0;
		}
		double theta = Math.acos(vector.getX() / length);
		if (vector.getY() < 0)
		{
			theta = 2 * Math.PI - theta;
		}
		return theta;
	}

	public PlaneCoordinate getCoordinate(Coordinate origin, double rho, double azimuth)
	{
		return this.getCoordinate(origin.getX() + rho * Math.cos(azimuth), origin.getY() + rho * Math.sin(azimuth));
	}

	public PlaneCoordinate getCoordinate(double x, double y)
	{
		return new PlaneCoordinate(x, y);
	}

	public double getDistance(Coordinate a, Coordinate b)
	{
		double x = b.getX() - a.getX();
		double y = b.getY() - a.getY();
		return Math.sqrt(x * x + y * y);
	}

	public double getIncludedAngle(Coordinate va, Coordinate vb)
	{
		return Math.acos(product(va, vb) / (getDistance(va) * getDistance(vb)));
	}

	public Iterable<Coordinate> iteratePointsOnLine(double delta, Coordinate a, Coordinate b)
	{
		return new LinePointsIterator(delta, a, b);
	}
}
