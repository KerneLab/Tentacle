package org.kernelab.tentacle.plane;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.kernelab.tentacle.AppTentacle;
import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;
import org.kernelab.tentacle.core.Guider;
import org.kernelab.tentacle.core.Region;
import org.kernelab.tentacle.semi.AbstractGuider;
import org.kernelab.tentacle.semi.AbstractPlanner;
import org.kernelab.tentacle.util.Log;

public class PlanePathPlanner extends AbstractPlanner
{
	public static int			MAX_ITER			= 500;

	public static final double	TRY_RADIAN_HALF		= Math.PI * 4.0 / 5.0;

	public static final double	DETECT_RADIAN_DELTA	= Math.PI / 180.0;

	public Set<Region>			bannedRegions		= null;

	private int					iter				= 0;

	public PlanePathPlanner(AppTentacle app)
	{
		super(app);
	}

	protected Coordinate getTryPoint(AbstractGuider guider, PlaneCoordinates system, Coordinate current, double azimuth,
			double step, double delta)
	{
		return guider.getLastReachableOnLine(delta, current, system.getCoordinate(current, step, azimuth));
	}

	protected List<Coordinate> getTryPoints(AbstractGuider guider, PlaneCoordinates system, Set<Region> bannedRegions,
			Coordinate previous, Coordinate current, Coordinate terminal)
	{
		LinkedList<Coordinate> points = new LinkedList<Coordinate>();

		double minError = DETECT_ERROR; // DETECT_DELTA / 10.0;
		double len = 0, bestLen = 0, baseLen = 0, az = 0, bestAz = 0, direct = -1, normal = -1;
		Coordinate point = null, bestPoint;

		direct = system.getAzimuth(system.subtract(terminal, current));

		if (previous != null)
		{
			normal = system.getAzimuth(system.subtract(current, previous));
		}
		else
		{
			normal = direct;
		}

		Log.log("direct: " + (direct / Math.PI * 180));
		Log.log("normal: " + (normal / Math.PI * 180));

		bestLen = baseLen;
		bestPoint = null;
		bestAz = 0;
		for (double r = 0; r <= TRY_RADIAN_HALF; r += DETECT_RADIAN_DELTA)
		{
			az = direct + r;

			// Log.log("find az1:" + az / Math.PI * 180);

			point = this.getTryPoint(guider, system, current, az, TRY_STEP, DETECT_DELTA);

			if (point != null && !guider.isBanned(point, bannedRegions))
			{
				len = system.getDistance(current, point);

				if (len > DETECT_DELTA && len > bestLen && Math.abs(len - bestLen) > minError)
				{
					// Log.log(point + " az1:" +
					// system.getAzimuth(system.subtract(point, current)) /
					// Math.PI * 180
					// + " len1:" + len);
					bestLen = len;
					bestPoint = point;
					bestAz = az;
				}
			}
		}
		if (bestPoint != null)
		{
			points.add(bestPoint);
			Log.log("best1:" + bestPoint + " az1:" + Log.toDegrees(bestAz) + " len1:" + len);
		}

		bestLen = baseLen;
		bestPoint = null;
		bestAz = 0;
		for (double r = -DETECT_RADIAN_DELTA; r >= -TRY_RADIAN_HALF; r -= DETECT_RADIAN_DELTA)
		{
			az = direct + r;

			// Log.log("find az2:" + az / Math.PI * 180);

			point = this.getTryPoint(guider, system, current, az, TRY_STEP, DETECT_DELTA);

			if (point != null && !guider.isBanned(point, bannedRegions))
			{
				len = system.getDistance(current, point);

				if (len > DETECT_DELTA && len > bestLen && Math.abs(len - bestLen) > minError)
				{
					// Log.log(point + " az2:" +
					// system.getAzimuth(system.subtract(point, current)) /
					// Math.PI * 180
					// + " len2:" + len);
					bestLen = len;
					bestPoint = point;
					bestAz = az;
				}
			}
		}
		if (bestPoint != null)
		{
			points.add(bestPoint);
			Log.log("best2:" + bestPoint + " az2:" + Log.toDegrees(bestAz) + " len2:" + len);
		}

		return points;
	}

	public List<Coordinate> plan(LinkedList<Coordinate> result, Guider gd, Coordinates cs,
			Collection<Coordinate> points)
	{
		AbstractGuider guider = (AbstractGuider) gd;
		PlaneCoordinates system = (PlaneCoordinates) cs;

		Set<Region> bannedRegions = new HashSet<Region>();
		this.bannedRegions = bannedRegions;

		Coordinate starting = null, terminal = null;
		int index = 0;
		for (Coordinate p : points)
		{
			if (index == 0)
			{
				starting = p;
			}
			else if (index == 1)
			{
				terminal = p;
			}
			else
			{
				break;
			}
			index++;
		}

		Log.log("starting: " + starting);
		Log.log("terminal: " + terminal);

		result.add(starting);

		this.iter = 0;

		Coordinate end = this.planNext(result, guider, system, bannedRegions, null, starting, terminal);

		if (end != null)
		{
			Log.log("optimizing");
			this.optimizePath(result, guider);
		}
		else
		{
			// return null;
		}

		return result;
	}

	protected Coordinate planNext(LinkedList<Coordinate> path, AbstractGuider guider, PlaneCoordinates system,
			Set<Region> bannedRegions, Coordinate previous, Coordinate current, Coordinate terminal)
	{
		if (this.iter > MAX_ITER && MAX_ITER > 0)
		{
			throw new RuntimeException("Max iteration reached");
		}

		Log.log("iter:" + this.iter);

		do
		{
			List<Coordinate> trys = this.getTryPoints(guider, system, bannedRegions, previous, current, terminal);

			if (trys.isEmpty())
			{
				return null;
			}
			else
			{
				for (Coordinate tr : trys)
				{
					this.iter++;

					path.addLast(tr);
					Log.log("added:" + tr);
					this.refresh();

					Region banned = new PlaneCircleRegion(tr, TRY_STEP / 2.0);
					bannedRegions.add(banned);
					Log.log("banned:" + banned);

					if (guider.isReachableOnLine(DETECT_DELTA, tr, terminal))
					{
						path.addLast(terminal);
						return terminal;
					}
					else
					{
						Coordinate found = this.planNext(path, guider, system, bannedRegions, current, tr, terminal);
						if (found != null)
						{
							return found;
						}
					}

					path.removeLast();
					bannedRegions.remove(banned);
					Log.log("remove:" + banned);
					this.refresh();
				}

				return null;
			}
		}
		while (true);
	}
}
