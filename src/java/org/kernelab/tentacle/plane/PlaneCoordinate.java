package org.kernelab.tentacle.plane;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.semi.AbstractCoordinate;

public class PlaneCoordinate extends AbstractCoordinate
{
	public PlaneCoordinate(Coordinate p)
	{
		super(p);
	}

	public PlaneCoordinate(double x, double y)
	{
		super(x, y);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof PlaneCoordinate)
		{
			PlaneCoordinate pc = (PlaneCoordinate) o;
			return pc.getX() == this.getX() && pc.getY() == this.getY();
		}
		else
		{
			return false;
		}
	}

	public PlaneCoordinate rasterize()
	{
		if (this.isRasterized())
		{
			return this;
		}
		else
		{
			PlaneCoordinate raster = new PlaneCoordinate((int) this.getX(), (int) this.getY());
			raster.setRasterized(true);
			return raster;
		}
	}
}
