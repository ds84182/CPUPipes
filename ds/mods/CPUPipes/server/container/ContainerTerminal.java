package ds.mods.CPUPipes.server.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;

public class ContainerTerminal extends Container {
	
	public TileEntityMonitor mon;
	
	public ContainerTerminal(TileEntityMonitor m)
	{
		mon = m;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(mon.xCoord+0.5D, mon.yCoord+0.5D, mon.zCoord+0.5D) <= 64D;
	}

}
