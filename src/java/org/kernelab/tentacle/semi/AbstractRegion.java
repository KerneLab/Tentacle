package org.kernelab.tentacle.semi;

import java.util.HashMap;
import java.util.Map;

import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;
import org.kernelab.tentacle.core.Region;

public abstract class AbstractRegion implements Region
{
	private Map<Coordinate, Boolean> cache;

	public AbstractRegion()
	{
		this.setCache(new HashMap<Coordinate, Boolean>());
	}

	public boolean contains(Coordinate point, Coordinates system)
	{
		Coordinate raster = this.rasterize(point);

		if (!this.getCache().containsKey(raster))
		{
			this.getCache().put(raster, this.containsNoCache(raster, system));
		}

		return this.getCache().get(raster);
	}

	protected abstract boolean containsNoCache(Coordinate raster, Coordinates system);

	@Override
	public abstract boolean equals(Object o);

	protected Map<Coordinate, Boolean> getCache()
	{
		return cache;
	}

	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	protected Coordinate rasterize(Coordinate point)
	{
		return point.rasterize();
	}

	protected void setCache(Map<Coordinate, Boolean> cache)
	{
		this.cache = cache;
	}

	@Override
	public abstract String toString();
}
