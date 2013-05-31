package ds.mods.CPUPipes.core.network;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstantItemNetworkDevice implements INetworkDevice {

	public int x, y, z, id;
	public World world;

	public ConstantItemNetworkDevice(int X, int Y, int Z, World w) {
		x = X;
		y = Y;
		z = Z;
		world = w;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public String getType() {
		return "ConstantItem";
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	public IInventory getInventory() {
		return (IInventory) world.getBlockTileEntity(x, y, z);
	}

	@Override
	public Object call(int method, Object... args) {
		return null;
	}

	@Override
	public String getLabel() {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null)
		{
			return ((ILabelHolder)tile).getLabel();
		}
		return null;
	}
}
