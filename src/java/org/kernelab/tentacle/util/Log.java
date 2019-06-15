package org.kernelab.tentacle.util;

import org.kernelab.basis.Tools;

public class Log
{
	public static final boolean	LOGGING		= true;

	public static final double	DOUBLE_PI	= 2.0 * Math.PI;

	public static final void log(Iterable<?> coll)
	{
		if (LOGGING)
		{
			Tools.debug(coll);
		}
	}

	public static final void log(Object obj)
	{
		if (LOGGING)
		{
			Tools.debug(obj);
		}
	}

	public static final void log(String text)
	{
		if (LOGGING)
		{
			Tools.debug(text);
		}
	}

	public static void main(String[] args)
	{

	}

	public static final double regulateRadians(double radians)
	{
		while (radians > DOUBLE_PI)
		{
			radians -= DOUBLE_PI;
		}

		while (radians < 0)
		{
			radians += DOUBLE_PI;
		}

		return radians;
	}

	public static final double toDegrees(double radians)
	{
		return Math.toDegrees(regulateRadians(radians));
	}
}
