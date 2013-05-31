package ds.mods.CPUPipes.core.tile;

import net.minecraft.tileentity.TileEntity;
import ds.mods.CPUPipes.core.network.DeviceFinder;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.network.INetworkWire;
import ds.mods.CPUPipes.core.network.Network;

public class TileEntityWire extends TileEntity implements INetworkWire {

			public Network net = new Network();
			public boolean networkDirty = true;

			@Override
			public void merge(Network net) {
						//for (INetworkDevice device : this.net.devices) {
									//net.add(device);
						//}
						//this.net.devices.clear();
						this.net = net;
			}

			@Override
			public Network getNetwork() {
						return net;
			}

			public void updateEntity() {
						if (networkDirty) {
									networkDirty = false;
									net.devices.clear();
									net.nextIDlist.clear();
									DeviceFinder.findDevices(xCoord, yCoord, zCoord, worldObj, net);
						}
			}
}
