package ds.mods.CPUPipes.core.tile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.PacketDispatcher;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.item.ItemSDCard;
import ds.mods.CPUPipes.core.utils.RelativeResolver;

public class TileEntitySDCardSlot extends TileEntity implements IInventory {

	public ItemStack sdcard;
	public boolean error = false;

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return sdcard;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return sdcard.splitStack(j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return sdcard;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		sdcard = itemstack.copy();
		if (!worldObj.isRemote) {
			error = CPUPipes.sdcard.getError(sdcard, worldObj);
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, nbt);
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 128, worldObj.provider.dimensionId, packet);
		}
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.customParam1);
	}

	@Override
	public String getInvName() {
		return "SDCardSlot";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
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
		return itemstack.getItem() instanceof ItemSDCard;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		error = nbt.getBoolean("error");
		sdcard = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("sdcard"));
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("error", error);
		NBTTagCompound itemnbt = new NBTTagCompound();
		if (sdcard != null) {
			sdcard.writeToNBT(itemnbt);
		}
		nbt.setCompoundTag("sdcard", itemnbt);
	}

	public String read(String filename) {
		if (sdcard == null || error) {
			return null;
		}
		filename = RelativeResolver.resolve(filename);
		File f = new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj) + filename);
		try {
			DataInputStream dat = new DataInputStream(new FileInputStream(f));
			byte[] array = new byte[(int) f.length()];
			dat.readFully(array);
			dat.close();
			char[] charArray = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				charArray[i] = (char) array[i];
			}
			return String.valueOf(charArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File getFile(String filename)
	{
		if (sdcard == null || error) return null;
		filename = RelativeResolver.resolve(filename);
		return new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj) + filename);
	}

	public boolean exists(String filename)
	{
		if (sdcard == null || error) {
			return false;
		}
		new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj)).mkdirs();
		filename = RelativeResolver.resolve(filename);
		return new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj) + filename).exists();
	}

	public boolean write(String filename, String fileContents) {
		if (sdcard == null || error) {
			return false;
		}
		new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj)).mkdirs();
		if ((this.getUsedSize() + fileContents.length()) > CPUPipes.sdcardSpace) {
			return false;
		}
		filename = RelativeResolver.resolve(filename);
		File f = new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj) + filename);
		try {
			f.createNewFile();
			DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
			out.write(fileContents.getBytes());
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	//Awesome function borrowed from StackOverflow!
	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				length += file.length();
			} else {
				length += folderSize(file);
			}
		}
		return length;
	}

	public int getUsedSize() {
		return (int) folderSize(new File(CPUPipes.proxy.getWorldDir(worldObj), "sdcard/" + CPUPipes.sdcard.getID(sdcard, worldObj)));
	}
}
