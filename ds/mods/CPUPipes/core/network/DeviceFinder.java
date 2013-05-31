package ds.mods.CPUPipes.core.network;

import java.util.ArrayList;
import java.util.TreeMap;

import ds.mods.CPUPipes.core.utils.Vector3;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class DeviceFinder {

	public static boolean findInList(ArrayList arr, Object o) {
		for (Object obj : arr) {
			if (obj.equals(o)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<INetworkDevice> findDevices(int x, int y, int z, World w, Network n) {
		return findDevices(x, y, z, w, new ArrayList<Vector3>(), new ArrayList<INetworkDevice>(), n);
	}

	public static ArrayList<INetworkDevice> findDevices(int x, int y, int z, World w, ArrayList<Vector3> alreadyScanned, ArrayList<INetworkDevice> devices, Network net) {
		if (!findInList(alreadyScanned, new Vector3(x, y, z)));
		{
			Vector3 vec = new Vector3(x, y, z);
			alreadyScanned.add(vec);
			if (!findInList(alreadyScanned, new Vector3(x, y, z))) {
				throw new IllegalArgumentException("Put the vec in the alreadyScanned, but it doesn't exist!");
			}
			//Look in all the forge directions for a TileEntity that extends INetworkDeviceHost or extends INetworkWire
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				Vector3 dirvec = new Vector3(x, y, z);
				dirvec.addDirection(dir);
				TileEntity tile = w.getBlockTileEntity(dirvec.x, dirvec.y, dirvec.z);
				if (tile instanceof INetworkDeviceHost) {
					if (!findInList(alreadyScanned, dirvec)) {
						INetworkDeviceHost host = (INetworkDeviceHost) tile;
						if (host.canConnect(dir.getOpposite())) {
							net.add(host.getDevice(net));
							//System.out.println("Found new device on network");
						}
						alreadyScanned.add(dirvec);
					}
				} else if (tile instanceof INetworkWire) {
					if (!findInList(alreadyScanned, dirvec)) {
						INetworkWire wire = (INetworkWire) tile;
						if (wire.getNetwork() != net) {
							wire.merge(net);
						}
						findDevices(dirvec.x, dirvec.y, dirvec.z, w, alreadyScanned, devices, net);
					}
				}
			}
		}
		return devices;
	}
}
