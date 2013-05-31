package ds.mods.CPUPipes.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import ds.mods.CPUPipes.client.gui.GuiConstantItem;
import ds.mods.CPUPipes.client.gui.GuiTerminal;
import ds.mods.CPUPipes.core.tile.TileEntityConstantItem;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.server.container.ContainerConstantItem;
import ds.mods.CPUPipes.server.container.ContainerTerminal;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch (ID)
		{
		case 100:
			return new ContainerConstantItem(player, (TileEntityConstantItem) world.getBlockTileEntity(x, y, z));
		case 101:
			return new ContainerTerminal((TileEntityMonitor) world.getBlockTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch (ID)
		{
		case 100:
			return new GuiConstantItem(player, (TileEntityConstantItem) world.getBlockTileEntity(x, y, z));
		case 101:
			return new GuiTerminal((TileEntityMonitor) world.getBlockTileEntity(x, y, z));
		}
		return null;
	}

}
