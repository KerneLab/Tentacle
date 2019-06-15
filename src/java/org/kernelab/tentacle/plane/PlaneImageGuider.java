package org.kernelab.tentacle.plane;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;
import org.kernelab.tentacle.core.Planner;
import org.kernelab.tentacle.semi.AbstractGuider;

public class PlaneImageGuider extends AbstractGuider
{
	private BufferedImage				image;

	private Map<Coordinate, Boolean>	roundCache;

	private Color						reachableColor;

	public PlaneImageGuider(PlaneCoordinates system, BufferedImage image, Color reachableColor)
	{
		super(system);
		this.setImage(image);
		this.setRoundCache(new HashMap<Coordinate, Boolean>());
		this.setReachableColor(reachableColor);
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public Coordinate getNearestReachable(Coordinate point, double delta, double radius)
	{
		if (this.isReachable(point))
		{
			return point;
		}

		for (double r = delta; r <= radius; r += delta)
		{
			for (double a = 0.0; a <= Math.PI * 2.0; a += PlanePathPlanner.DETECT_RADIAN_DELTA)
			{
				Coordinate p = this.getSystem().getCoordinate(point, r, a);

				if (this.isReachable(p))
				{
					return p;
				}
			}
		}

		return null;
	}

	public Color getReachableColor()
	{
		return reachableColor;
	}

	protected Map<Coordinate, Boolean> getRoundCache()
	{
		return roundCache;
	}

	@Override
	public PlaneCoordinates getSystem()
	{
		return (PlaneCoordinates) super.getSystem();
	}

	protected boolean isReachableByRound(Coordinate point, double radius)
	{
		PlaneCoordinates system = this.getSystem();

		for (double r = 0.0; r <= Math.PI * 2.0; r += PlanePathPlanner.DETECT_RADIAN_DELTA)
		{
			if (!this.isReachableOnLineExactly(system, Planner.DETECT_DELTA, point,
					system.getCoordinate(point, radius, r)))
			{
				return false;
			}
		}

		return true;
	}

	protected boolean isReachableExactly(Coordinate point)
	{
		Coordinate raster = this.rasterize(point);

		if (!this.getRoundCache().containsKey(raster))
		{
			this.getRoundCache().put(raster, this.isReachableExactlyNoCache(raster));
		}

		return this.getRoundCache().get(raster);
	}

	protected boolean isReachableExactlyNoCache(Coordinate raster)
	{
		try
		{
			return this.getImage().getRGB((int) raster.getX(), (int) raster.getY()) == this.getReachableColor()
					.getRGB();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
	}

	protected boolean isReachableNoCache(Coordinate raster)
	{
		return this.isReachableByRound(raster, Planner.GAP);
	}

	protected boolean isReachableOnLineExactly(Coordinates system, double delta, Coordinate a, Coordinate b)
	{
		if (!this.isReachableExactly(a) || !this.isReachableExactly(b))
		{
			return false;
		}

		for (Coordinate p : system.iteratePointsOnLine(delta, a, b))
		{
			if (!this.isReachableExactly(p))
			{
				return false;
			}
		}

		return true;
	}

	protected void setImage(BufferedImage image)
	{
		this.image = image;
	}

	protected void setReachableColor(Color reachableColor)
	{
		this.reachableColor = reachableColor;
	}

	protected void setRoundCache(Map<Coordinate, Boolean> cache)
	{
		this.roundCache = cache;
	}
}
