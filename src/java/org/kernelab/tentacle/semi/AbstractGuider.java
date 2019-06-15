package org.kernelab.tentacle.semi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Guider;
import org.kernelab.tentacle.core.Region;

public abstract class AbstractGuider implements Guider
{
	private AbstractCoordinates			system;

	private Map<Coordinate, Boolean>	cache;

	public AbstractGuider(AbstractCoordinates system)
	{
		this.setSystem(system);
		this.setCache(new HashMap<Coordinate, Boolean>());
	}

	protected Map<Coordinate, Boolean> getCache()
	{
		return cache;
	}

	public Coordinate getLastReachableOnLine(double delta, Coordinate a, Coordinate b)
	{
		Coordinate last = null;

		for (Coordinate p : this.getSystem().iteratePointsOnLine(delta, a, b))
		{
			if (!this.isReachable(p))
			{
				return last;
			}
			last = p;
		}

		return last;
	}

	public abstract Coordinate getNearestReachable(Coordinate point, double delta, double radius);

	public AbstractCoordinates getSystem()
	{
		return system;
	}

	public boolean isBanned(Coordinate point, Set<Region> bannedRegions)
	{
		for (Region ban : bannedRegions)
		{
			if (ban.contains(point, this.getSystem()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isReachable(Coordinate point)
	{
		Coordinate raster = this.rasterize(point);

		if (!this.getCache().containsKey(raster))
		{
			this.getCache().put(raster, this.isReachableNoCache(raster));
		}

		return this.getCache().get(raster);
	}

	protected abstract boolean isReachableNoCache(Coordinate point);

	public boolean isReachableOnLine(double delta, Coordinate a, Coordinate b)
	{
		if (!this.isReachable(a) || !this.isReachable(b))
		{
			return false;
		}

		for (Coordinate p : this.getSystem().iteratePointsOnLine(delta, a, b))
		{
			if (!this.isReachable(p))
			{
				return false;
			}
		}

		return true;
	}

	protected Coordinate rasterize(Coordinate point)
	{
		return point.rasterize();
	}

	protected void setCache(Map<Coordinate, Boolean> cache)
	{
		this.cache = cache;
	}

	protected void setSystem(AbstractCoordinates system)
	{
		this.system = system;
	}
}
