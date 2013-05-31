package ds.mods.CPUPipes.core.network;

import net.minecraftforge.common.ForgeDirection;

public interface INetworkDeviceHost {
	public INetworkDevice getDevice(Network net);
	public boolean canConnect(ForgeDirection dir);
}
