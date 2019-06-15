package org.kernelab.tentacle.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface Planner extends Runnable
{
	public static final double	TRY_STEP		= 20;
	public static final double	DETECT_DELTA	= 5;
	public static final double	GAP				= 10;

	// public static final double TRY_STEP = 4;
	// public static final double DETECT_DELTA = 1;
	// public static final double GAP = 0;

	public static final double	DETECT_ERROR	= DETECT_DELTA / 10000.0;

	public List<Coordinate> plan(LinkedList<Coordinate> result, Guider guider, Coordinates system,
			Collection<Coordinate> points);
}
