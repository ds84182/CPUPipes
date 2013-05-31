package ds.mods.CPUPipes.core.utils;

import net.minecraftforge.common.ForgeDirection;

public class Vector2 implements Comparable<Vector2> {
	public int x;
	public int y;
	
	public Vector2()
	{
		this(0,0);
	}
	
	public Vector2(int X, int Y)
	{
		x = X;
		y = Y;
	}
	
	public void addDirection(ForgeDirection dir)
	{
		x+=dir.offsetX;
		y+=dir.offsetY;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector2)
		{
			Vector2 other = (Vector2) obj;
			if (other.x == x && other.y == y)
			{
				return true;
			}
		}
		return false;
	}
	
	public float mag()
	{
		return x*y;
	}

	@Override
	public int compareTo(Vector2 o) {
		if (equals(o))
		{
			return 0;
		}
		return mag()<o.mag() ? -1 : 1;
	}
}
