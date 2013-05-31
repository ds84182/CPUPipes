package ds.mods.CPUPipes.core.tile;

import java.util.HashMap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import ds.mods.CPUPipes.core.network.ILabelHolder;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.network.INetworkDeviceHost;
import ds.mods.CPUPipes.core.network.InventoryNetworkDevice;
import ds.mods.CPUPipes.core.network.Network;
import ds.mods.CPUPipes.core.utils.Vector3;

public class TileEntityInventoryConnector extends TileEntity implements INetworkDeviceHost, ILabelHolder {

	public HashMap<Network, InventoryNetworkDevice> networkToDeviceMap = new HashMap<Network, InventoryNetworkDevice>();
	public Vector3 invvec3;
	public ForgeDirection invdir;
	public IInventory inv;
	public String label;
	public String lastLabel;

	@Override
	public INetworkDevice getDevice(Network net) {
		if (!networkToDeviceMap.containsKey(net)) {
			networkToDeviceMap.put(net, new InventoryNetworkDevice(invvec3.x, invvec3.y, invvec3.z, inv, net, this));
		}
		return networkToDeviceMap.get(net);
	}

	@Override
	public void updateEntity() {
		if (label != null)
		if (!label.equals(lastLabel))
		{
			lastLabel = label;
			if (!worldObj.isRemote)
			{
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeInt(xCoord);
				out.writeInt(yCoord);
				out.writeInt(zCoord);
				out.writeUTF(label);
				PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj.provider.dimensionId, new Packet250CustomPayload("LabelHolder", out.toByteArray()));
			}
		}
		//Search in all forge directions for an inventory
		if (invdir != null) {
			Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
			vec.addDirection(invdir);
			TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
			inv = (IInventory) inv;
		}
		if (inv == null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (dir == ForgeDirection.UP) {
					continue;
				}
				Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
				vec.addDirection(dir);
				TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
				if (tile instanceof IInventory) {
					inv = (IInventory) tile;
					invvec3 = vec;
					invdir = dir;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					break;
				}
			}
		} else {
			Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
			vec.addDirection(invdir);
			TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
			if (tile != inv) {
				inv = null;
				invvec3 = null;
				invdir = null;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return inv != null && dir != ForgeDirection.UP;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.getBoolean("hasLabel")) {
			label = nbt.getString("label");
		} else {
			label = null;
		}
		int dir = nbt.getInteger("dir");
		if (dir > -1) {
			invdir = ForgeDirection.VALID_DIRECTIONS[dir];
			Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
			vec.addDirection(invdir);
			invvec3 = vec;
		}
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (label != null) {
			nbt.setString("label", label);
			nbt.setBoolean("hasLabel", true);
		} else {
			nbt.setBoolean("hasLabel", false);
		}
		if (invdir != null) {
			nbt.setInteger("dir", invdir.ordinal());
		} else {
			nbt.setInteger("dir", -1);
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		if (label != null)
		{
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeInt(xCoord);
			out.writeInt(yCoord);
			out.writeInt(zCoord);
			out.writeUTF(label);
			return new Packet250CustomPayload("LabelHolder", out.toByteArray());
		}
		return null;
	}
}
