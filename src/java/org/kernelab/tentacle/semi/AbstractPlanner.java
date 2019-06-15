package org.kernelab.tentacle.semi;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import org.kernelab.tentacle.AppTentacle;
import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Planner;
import org.kernelab.tentacle.util.Log;

public abstract class AbstractPlanner implements Planner
{
	private Collection<Coordinate>	points;

	private LinkedList<Coordinate>	result;

	private AppTentacle				app;

	private boolean					running;

	public AbstractPlanner(AppTentacle app)
	{
		this.setApp(app);
	}

	public AppTentacle getApp()
	{
		return app;
	}

	public AbstractGuider getGuider()
	{
		return this.getApp().getGuider();
	}

	public Collection<Coordinate> getPoints()
	{
		return points;
	}

	public LinkedList<Coordinate> getResult()
	{
		return result;
	}

	public AbstractCoordinates getSystem()
	{
		return this.getApp().getCoordinateSystem();
	}

	public boolean isRunning()
	{
		return running;
	}

	protected LinkedList<Coordinate> optimizePath(LinkedList<Coordinate> path, AbstractGuider guider)
	{
		boolean optimized = false;
		Coordinate last = null, current = null, next = null;

		do
		{
			optimized = false;
			last = null;
			current = null;
			next = null;

			ListIterator<Coordinate> iter = path.listIterator();

			while (iter.hasNext())
			{
				last = current;
				current = next;
				next = iter.next();

				if (last != null && current != null && next != null)
				{
					if (guider.isReachableOnLine(DETECT_DELTA, last, next))
					{
						iter.previous();
						iter.previous();
						iter.remove();
						iter.next();
						current = last;
						optimized = true;
					}
				}
			}
		}
		while (optimized);

		return path;
	}

	protected void refresh()
	{
		this.getApp().repaint();
	}

	public void run()
	{
		if (!this.isRunning())
		{
			this.setRunning(true);

			this.getApp().hintStatus("Calculating");
			try
			{
				this.plan(this.getResult(), this.getGuider(), this.getSystem(), this.getPoints());
				this.getApp().hintStatus("Done");

				if (this.getResult() != null)
				{
					Log.log("path:");
					Log.log(this.getResult());
				}
			}
			catch (Exception e)
			{
				this.getApp().hintStatus("Error");
				e.printStackTrace();
			}
			catch (Error e)
			{
				this.getApp().hintStatus("Error");
				e.printStackTrace();
			}
			finally
			{
				this.setRunning(false);
				this.refresh();
			}
		}
	}

	public void setApp(AppTentacle app)
	{
		this.app = app;
	}

	public void setPoints(Collection<Coordinate> points)
	{
		this.points = points;
	}

	public void setResult(LinkedList<Coordinate> result)
	{
		this.result = result;
	}

	protected void setRunning(boolean running)
	{
		this.running = running;
	}
}
