package org.kernelab.tentacle.core;

import java.awt.Graphics2D;

public interface Region
{
	public boolean contains(Coordinate point, Coordinates system);

	public void draw(Graphics2D g);
}
