package ds.mods.CPUPipes.core.tile;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;

import ds.mods.CPUPipes.core.network.ConstantItemNetworkDevice;
import ds.mods.CPUPipes.core.network.ILabelHolder;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.network.INetworkDeviceHost;
import ds.mods.CPUPipes.core.network.Network;

public class TileEntityConstantItem extends TileEntity implements IInventory,
INetworkDeviceHost, ILabelHolder {

	public HashMap<Network, ConstantItemNetworkDevice> networkToDeviceMap = new HashMap<Network, ConstantItemNetworkDevice>();
	public ItemStack item;
	public String label;
	public String lastLabel;

	@Override
	public INetworkDevice getDevice(Network net) {
		if (!networkToDeviceMap.containsKey(net)) {
			networkToDeviceMap.put(net, new ConstantItemNetworkDevice(xCoord, yCoord, zCoord, worldObj));
		}
		return networkToDeviceMap.get(net);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return item;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return item.splitStack(j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return item;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		item = itemstack;
	}

	@Override
	public String getInvName() {
		return "ConstantItem";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return true;
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
		item = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"));
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
		NBTTagCompound itemnbt = new NBTTagCompound();
		if (item != null)
			item.writeToNBT(itemnbt);
		nbt.setCompoundTag("item", itemnbt);
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
