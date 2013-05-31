package ds.mods.CPUPipes.core.utils;

import net.minecraftforge.common.ForgeDirection;

public class Vector3 implements Comparable<Vector3> {
	public int x;
	public int y;
	public int z;
	
	public Vector3()
	{
		this(0,0,0);
	}
	
	public Vector3(int X, int Y, int Z)
	{
		x = X;
		y = Y;
		z = Z;
	}
	
	public void addDirection(ForgeDirection dir)
	{
		x+=dir.offsetX;
		y+=dir.offsetY;
		z+=dir.offsetZ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3)
		{
			Vector3 other = (Vector3) obj;
			if (other.x == x & other.y == y & other.z == z)
			{
				return true;
			}
		}
		return false;
	}
	
	public float mag()
	{
		return x*y*z;
	}

	@Override
	public int compareTo(Vector3 o) {
		if (equals(o))
		{
			return 0;
		}
		return mag()<o.mag() ? -1 : 1;
	}
}
