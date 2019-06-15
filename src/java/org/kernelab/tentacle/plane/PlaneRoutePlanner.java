package org.kernelab.tentacle.plane;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.kernelab.tentacle.AppTentacle;
import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Coordinates;
import org.kernelab.tentacle.core.Guider;
import org.kernelab.tentacle.semi.AbstractGuider;
import org.kernelab.tentacle.semi.AbstractPlanner;

public class PlaneRoutePlanner extends AbstractPlanner
{
	public PlaneRoutePlanner(AppTentacle app)
	{
		super(app);
	}

	protected LinkedList<Coordinate> linkAllPoints(LinkedList<Coordinate> result, AbstractGuider guider,
			PlaneCoordinates system, Collection<Coordinate> points)
	{
		Collection<Coordinate> rests = new HashSet<Coordinate>(points);

		while (!rests.isEmpty())
		{
			int bestLast = -1, bestNext = -1;
			Coordinate bestPoint = null;
			double bestDist = Double.POSITIVE_INFINITY;

			for (Coordinate p : rests)
			{
				if (result.isEmpty())
				{
					bestPoint = p;
					break;
				}

				int index = -1;
				Coordinate last = null, next = null;
				double dist = -1;

				for (Coordinate r : result)
				{
					index++;
					last = next;
					next = r;

					if (index == 0)
					{
						dist = system.getDistance(p, r);
						if (dist < bestDist)
						{
							bestDist = dist;
							bestPoint = p;
							bestLast = -1;
							bestNext = index;
						}
					}
					else
					{
						if (index == result.size() - 1)
						{
							dist = system.getDistance(p, r);
							if (dist < bestDist)
							{
								bestDist = dist;
								bestPoint = p;
								bestLast = index;
								bestNext = -1;
							}
						}

						if (last != null && next != null)
						{
							dist = system.getDistance(last, p) + system.getDistance(p, next);
							if (dist < bestDist)
							{
								bestDist = dist;
								bestPoint = p;
								bestLast = index - 1;
								bestNext = index;
							}
						}
					}
				}
			}

			rests.remove(bestPoint);

			if (bestLast == -1 && bestNext == -1)
			{
				result.add(bestPoint);
			}
			else if (bestLast == -1 && bestNext != -1)
			{
				result.addFirst(bestPoint);
			}
			else if (bestLast != -1 && bestNext == -1)
			{
				result.addLast(bestPoint);
			}
			else if (bestLast != -1 && bestNext != -1)
			{
				result.add(bestNext, bestPoint);
			}

			this.refresh();
		}

		return result;
	}

	public List<Coordinate> plan(LinkedList<Coordinate> result, Guider gd, Coordinates cs,
			Collection<Coordinate> points)
	{
		AbstractGuider guider = (AbstractGuider) gd;
		PlaneCoordinates system = (PlaneCoordinates) cs;

		return this.linkAllPoints(result, guider, system, points);
	}
}
