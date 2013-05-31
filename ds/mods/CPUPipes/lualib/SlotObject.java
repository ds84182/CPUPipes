package ds.mods.CPUPipes.lualib;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;
import ds.mods.CPUPipes.server.ServerCPU;

/**
 * <summary>
 * A LuaJ object that lets the user modify slots within their program.
 * </summary>
 * @author Dwayne
 *
 */

public class SlotObject extends LuaTable {
	public Slot slot;
	public boolean readOnly = false;

	/**
	 * Initializes a SlotObject with a {@link Slot}
	 * @param s
	 */
	public SlotObject(Slot s)
	{
		slot = s;
		set("add", new add());
		set("getCount", new getCount());
		set("getMaxCount", new getMaxCount());
		set("equals", new equals());
		set("isEmpty", new isEmpty());
		set("split", new split());
	}

	public void AssertReadOnly()
	{
		if (readOnly)
			LuaValue.error("Cannot modify a read only stack!");
	}

	private class getCount extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(slot.getCount());
		}

	}

	private class getMaxCount extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(slot.getMaxCount());
		}

	}

	private class isEmpty extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(slot.isEmpty());
		}

	}

	private class add extends OneArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg) {
			AssertReadOnly();
			if (arg.typename().equals("Slot"))
			{
				((SlotObject)arg).AssertReadOnly();
				slot.add(((SlotObject)arg).slot);
			}
			else
			{
				LuaValue.error("Slot expected, got "+arg.typename());
			}
			return null;
		}

	}

	private class equals extends OneArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg) {
			if (arg.typename().equals("Slot"))
			{
				return LuaValue.valueOf(slot.equals(((SlotObject)arg).slot));
			}
			else
			{
				LuaValue.error("Slot expected, got "+arg.typename());
			}
			return null;
		}

	}

	private class split extends OneArgFunction {

		@Override
		public LuaValue call(LuaValue arg) {
			Slot temp = slot.split(arg.checkint());
			if (temp != null)
			{
				SlotObject o = new SlotObject(temp);
				//o.readOnly = true;
				return o;
			}
			return NIL;
		}

	}

	@Override
	public int type() {
		return LuaValue.TUSERDATA;
	}

	@Override
	public String typename() {
		return "Slot";
	}

	public static class Slot
	{
		public TileEntityCPU cpu;
		public IInventory inv;
		public INetworkDevice device;
		public int slot;
		public LuaValue yield;
		
		public Slot(INetworkDevice d, int s, IInventory i, TileEntityCPU c)
		{
			device = d;
			slot = s;
			inv = i;
			cpu = c;
			yield = ((ServerCPU)cpu.cpu)._G.get("yield");
		}

		public boolean isEmpty() {
			return inv.getStackInSlot(slot) == null ? true : false;
		}

		public void add(Slot s)
		{
			ItemStack item = s.inv.getStackInSlot(s.slot);
			ItemStack myitem = inv.getStackInSlot(slot);
			if (item == null)
				return;
			if (myitem == null)
			{
				s.inv.setInventorySlotContents(s.slot, null);
				inv.setInventorySlotContents(slot, item);
				int sleepFor = new Random().nextInt(1);
				int startTick = cpu.cpu.ticks;
				while (cpu.cpu.ticks<startTick+sleepFor) {yield.call();};
			}
			else if (myitem.isItemEqual(item))
			{
				if (item.stackSize+myitem.stackSize<=myitem.getMaxStackSize())
				{
					myitem.stackSize+=item.stackSize;
					s.inv.setInventorySlotContents(s.slot, null);
					inv.setInventorySlotContents(slot, myitem);
					int sleepFor = new Random().nextInt(1);
					int startTick = cpu.cpu.ticks;
					while (cpu.cpu.ticks<startTick+sleepFor) {yield.call();};
				}
				else if (myitem.stackSize<myitem.getMaxStackSize())
				{
					//Take away as much as I can
					item.stackSize -= myitem.getMaxStackSize()-myitem.stackSize;
					myitem.stackSize = myitem.getMaxStackSize();
					s.inv.setInventorySlotContents(s.slot, item);
					inv.setInventorySlotContents(slot, myitem);
					int sleepFor = new Random().nextInt(1);
					int startTick = cpu.cpu.ticks;
					while (cpu.cpu.ticks<startTick+sleepFor) {yield.call();};
				}
			}
		}

		public boolean equals(Slot s)
		{
			ItemStack myitem = inv.getStackInSlot(slot);
			ItemStack other = s.inv.getStackInSlot(s.slot);
			if (myitem != null && other != null)
				return myitem.isItemEqual(other);
			return false;
		}

		public int getCount()
		{
			if (inv.getStackInSlot(slot) == null)
			{
				return 0;
			}
			return inv.getStackInSlot(slot).stackSize;
		}

		public int getMaxCount()
		{
			if (inv.getStackInSlot(slot) == null)
			{
				return 0;
			}
			return inv.getStackInSlot(slot).getMaxStackSize();
		}

		//N is how many items we want in the second stack.
		public Slot split(int n)
		{
			ItemStack item = inv.getStackInSlot(slot);
			//If there is an item in the CPU block, dispose of it
			if (item != null)
			{
				if (cpu.temp != null)
				{
					EntityItem e = new EntityItem(cpu.worldObj, cpu.xCoord+0.5D, cpu.yCoord+0.5D, cpu.zCoord+0.5D, item);
					cpu.worldObj.spawnEntityInWorld(e);
					cpu.temp = null;
				}
				if (item.stackSize-n>=0)
				{
					ItemStack newStack = item.splitStack(n);
					cpu.temp = newStack;
					inv.setInventorySlotContents(slot, item);
					Slot nslot = new Slot(null, 0, new IInventory() {

						@Override
						public void setInventorySlotContents(int i, ItemStack itemstack) {
							cpu.temp = null;
							//This is because we are not supposed to accept items. Give me an item and I will kill it.
						}

						@Override
						public void openChest() {

						}

						@Override
						public void onInventoryChanged() {

						}

						@Override
						public boolean isUseableByPlayer(EntityPlayer entityplayer) {
							return false;
						}

						@Override
						public boolean isStackValidForSlot(int i, ItemStack itemstack) {
							return false;
						}

						@Override
						public boolean isInvNameLocalized() {
							return false;
						}

						@Override
						public ItemStack getStackInSlotOnClosing(int i) {
							return cpu.temp;
						}

						@Override
						public ItemStack getStackInSlot(int i) {
							return cpu.temp;
						}

						@Override
						public int getSizeInventory() {
							return 1;
						}

						@Override
						public int getInventoryStackLimit() {
							return 64;
						}

						@Override
						public String getInvName() {
							return "CPUTemp";
						}

						@Override
						public ItemStack decrStackSize(int i, int j) {
							//No no no! Not in my IInventory!
							return null;
						}

						@Override
						public void closeChest() {

						}
					}, cpu);
					int sleepFor = new Random().nextInt(1);
					int startTick = cpu.cpu.ticks;
					while (cpu.cpu.ticks<startTick+sleepFor) {yield.call();};
					return nslot;
				}
			}
			return null;
		}
	}

}
