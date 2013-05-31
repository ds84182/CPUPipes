package ds.mods.CPUPipes.core.tile;

import cpw.mods.fml.common.network.PacketDispatcher;
import ds.mods.CPUPipes.core.Terminal;
import ds.mods.CPUPipes.server.ServerTerminal;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMonitor extends TileEntity {
	public Terminal term;
	
	@Override
	public void updateEntity()
	{
		if (term == null)
		{
			if (worldObj.isRemote)
			{
				term = new Terminal(0,0, this); //Size will be propagated by server.
			}
			else
			{
				term = new ServerTerminal(48, 16, this);
				PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, getDescriptionPacket());
				term.write("Hello, World!");
			}
		}
		term.update();
	}

	@Override
	public Packet getDescriptionPacket() {
		if (term != null)
		{
			return ((ServerTerminal)term).getCompleteUpdatePacket();
		}
		return null;
	}
}
