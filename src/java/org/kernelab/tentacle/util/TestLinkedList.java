package org.kernelab.tentacle.util;

import java.util.LinkedList;
import java.util.ListIterator;

import org.kernelab.basis.Tools;

public class TestLinkedList
{
	public static void main(String[] args)
	{
		LinkedList<Integer> list = new LinkedList<Integer>();

		for (int i = 0; i < 10; i++)
		{
			list.add(i);
		}

		ListIterator<Integer> iter = list.listIterator();

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug("hasLast:" + iter.hasPrevious());
		Tools.debug("last:" + iter.previous());

		Tools.debug("hasLast:" + iter.hasPrevious());
		Tools.debug("last:" + iter.previous());

		Tools.debug("remove");
		iter.remove();

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug("hasNext:" + iter.hasNext());
		Tools.debug("next:" + iter.next());

		Tools.debug(list);
	}
}
