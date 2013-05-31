package ds.mods.CPUPipes.core.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import ds.mods.CPUPipes.core.network.CPU;
import ds.mods.CPUPipes.core.utils.Vector3;
import ds.mods.CPUPipes.server.ServerCPU;

public class TileEntityCPU extends TileEntityWire {

	public CPU cpu;
	public ItemStack temp;
	
	public TileEntitySDCardSlot sdcard;
	public ForgeDirection sdcardDir;
	public Vector3 sdcardVector;
	
	public TileEntityMonitor mon;
	public ForgeDirection monDir;
	public Vector3 monVector;
	public boolean monitorInit = false;
	
	public boolean on = false;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (sdcard == null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
				vec.addDirection(dir);
				TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
				if (tile instanceof TileEntitySDCardSlot) {
					sdcard = (TileEntitySDCardSlot) tile;
					sdcardDir = dir;
					sdcardVector = vec;
				}
			}
		} else {
			//Poll if it still exists
		}
		if (mon == null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
				vec.addDirection(dir);
				TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
				if (tile instanceof TileEntityMonitor) {
					mon = (TileEntityMonitor) tile;
					monDir = dir;
					monVector = vec;
					monitorInit = false;
				}
			}
		} else {
			//Poll if it still exists
		}
		if (!monitorInit && mon != null && mon.term != null)
		{
			mon.term.clear();
			mon.term.setCursorPos(1, 1);
			mon.term.cursorBlink = true;
			mon.term.write("Monitor connected");
			mon.term.setCursorPos(1, mon.term.cursor.y+1);
			monitorInit = true;
		}
		if (cpu == null && net != null) {
			if (!worldObj.isRemote) {
				cpu = new ServerCPU(this);
			} else {
				cpu = new CPU(this);
			}
			cpu.start();
			if (monitorInit)
			{
				mon.term.write("CPU Starting");
				mon.term.setCursorPos(1, mon.term.cursor.y+1);
			}
		}
		if (cpu != null) {
			cpu.tick();
			on = cpu.started;
		}
	}

	@Override
	public void onChunkUnload() {
		if (cpu != null)
		{
			cpu.stop();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		temp = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"));
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound itemnbt = new NBTTagCompound();
		if (temp != null)
			temp.writeToNBT(itemnbt);
		nbt.setCompoundTag("item", itemnbt);
	}
	
}
