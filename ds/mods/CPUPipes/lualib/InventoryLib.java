package ds.mods.CPUPipes.lualib;

import net.minecraft.item.ItemStack;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.network.InventoryNetworkDevice;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.server.ServerCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;
import ds.mods.CPUPipes.lualib.SlotObject.Slot;

public class InventoryLib extends OneArgFunction {

	public TileEntityCPU tile;
	public LuaValue yield;

	public InventoryLib(TileEntityCPU tile) {
		this.tile = tile;
	}

	@Override
	public LuaValue call(LuaValue env) {
		LuaTable inventory = new LuaTable();
		inventory.set("get", new get());
		inventory.set("getAll", new getAll());
		env.set("Inventory", inventory);
		yield = env.get("tick");
		return NIL;
	}

	private class getAll extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaTable tab = new LuaTable();
			for (int i = 0; i<tile.net.devices.size(); i++) {
				INetworkDevice d = tile.net.devices.get(i);
				if (d.getType().equals("Inventory"))
				{
					tab.insert(tab.length()+1, new InventoryObject((InventoryNetworkDevice) d));
				}
			}
			//System.out.println("Sleeping for "+(tile.net.devices.size()/16F));
			int sleepFor = tile.net.devices.size()/16;
			int startTick = tile.cpu.ticks;
			while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
			return tab;
		}

	}

	private class get extends OneArgFunction {

		@Override
		public LuaValue call(LuaValue arg) {
			//System.out.println(arg.toString());
			//System.out.println(tile.net.devices);
			for (int i = 0; i<tile.net.devices.size(); i++) {
				INetworkDevice d = tile.net.devices.get(i);
				//System.out.println(d.getID());
				//System.out.println(d.getType());
				//System.out.println(d.getLabel());
				if (arg.type() == LuaValue.TNUMBER) {
					if (d.getID() == arg.toint() && d.getType().equals("Inventory")) {
						//System.out.println("Found");
						InventoryObject inv = new InventoryObject((InventoryNetworkDevice) d);
						System.out.println("Sleeping for "+(i/16F));
						int sleepFor = i/16;
						int startTick = tile.cpu.ticks;
						while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
						return inv;
					}
				} else if (arg.type() == LuaValue.TSTRING) {
					if (d.getLabel() != null)
					{
						if (d.getLabel().equals(arg.toString()) && d.getType().equals("Inventory")) {
							//System.out.println("Found");
							InventoryObject inv = new InventoryObject((InventoryNetworkDevice) d);
							System.out.println("Sleeping for "+(i/16F));
							int sleepFor = i/16;
							int startTick = tile.cpu.ticks;
							while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
							return inv;
						}
					}
				}
			}
			//System.out.println("Sleeping for "+(tile.net.devices.size()/16F));
			int sleepFor = tile.net.devices.size()/16;
			int startTick = tile.cpu.ticks;
			while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
			return LuaValue.NIL;
		}
	}

	private class InventoryObject extends LuaTable {

		public InventoryNetworkDevice inv;

		public InventoryObject(InventoryNetworkDevice inv) {
			this.inv = inv;
			this.set("getSize", new getSize());
			this.set("getSlot", new getSlot());
			this.set("getLabel", new getLabel());
			this.set("getID", new getID());
			this.set("contains", new contains());
			this.set("getFirstSlotOf", new getFirstSlotOf());
			this.set("add", new add());
		}

		private class getSlot extends OneArgFunction {

			@Override
			public LuaValue call(LuaValue arg) {
				return new SlotObject(new SlotObject.Slot(inv, arg.toint(), inv.inv, tile));
			}
		}

		private class getLabel extends ZeroArgFunction {

			@Override
			public LuaValue call() {
				return LuaValue.valueOf(inv.getLabel());
			}

		}

		private class getID extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				return LuaValue.valueOf(inv.getID());
			}

		}

		private class add extends OneArgFunction {

			@Override
			public LuaValue call(LuaValue arg) {
				try
				{
					if (arg.typename().equals("Slot")) {
						Slot s = ((SlotObject) arg).slot;
						if (((SlotObject) arg).readOnly) {
							LuaValue.error("Slot expected, got ReadOnly Slot");
						}
						for (int i = 0; i < inv.inv.getSizeInventory(); i++) {
							ItemStack myitem = inv.inv.getStackInSlot(i);
							ItemStack other = s.inv.getStackInSlot(s.slot);
							if (other == null)
							{
								//System.out.println("Sleeping for "+(i/24F));
								int sleepFor = i/24;
								int startTick = tile.cpu.ticks;
								while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
								break;
							}
							if (myitem == null)
							{
								//System.out.println("Sleeping for "+(i/24F));
								int sleepFor = i/24;
								int startTick = tile.cpu.ticks;
								while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
								new SlotObject.Slot(inv, i, inv.inv, tile).add(s);
								//System.out.println("SLot add sucessful");
								break;
							} else if (myitem.isItemEqual(other) && myitem.stackSize<myitem.getMaxStackSize()) {
								//System.out.println("Sleeping for "+(i/24F));
								int sleepFor = i/24;
								int startTick = tile.cpu.ticks;
								while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
								new SlotObject.Slot(inv, i, inv.inv, tile).add(s);
								//System.out.println("SLot add sucessful");
								break;
							}
						}
					} else {
						LuaValue.error("Slot expected, got " + arg.typename());
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				//System.out.println("Sleeping for "+(inv.inv.getSizeInventory()/24F));
				int sleepFor = inv.inv.getSizeInventory()/24;
				int startTick = tile.cpu.ticks;
				while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
				return LuaValue.NIL;
			}
		}

		private class getFirstSlotOf extends OneArgFunction {

			@Override
			public LuaValue call(LuaValue arg) {
				if (arg.typename().equals("Slot")) {
					SlotObject slot = (SlotObject) arg;
					for (int i = 0; i < inv.inv.getSizeInventory(); i++) {
						if (inv.inv.getStackInSlot(i).isItemEqual(slot.slot.inv.getStackInSlot(slot.slot.slot))) {
							//System.out.println("Sleeping for "+(i/24F));
							int sleepFor = i/24;
							int startTick = tile.cpu.ticks;
							while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
							return new SlotObject(new SlotObject.Slot(inv, i, inv.inv, tile));
						}
					}
				} else {
					LuaValue.error("Slot expected, got " + arg.typename());
				}
				//System.out.println("Sleeping for "+(inv.inv.getSizeInventory()/24F));
				int sleepFor = inv.inv.getSizeInventory()/24;
				int startTick = tile.cpu.ticks;
				while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
				return null;
			}
		}

		private class contains extends OneArgFunction {

			@Override
			public LuaValue call(LuaValue arg) {
				if (arg.typename().equals("Slot")) {
					SlotObject slot = (SlotObject) arg;
					for (int i = 0; i < inv.inv.getSizeInventory(); i++) {
						if (inv.inv.getStackInSlot(i).isItemEqual(slot.slot.inv.getStackInSlot(slot.slot.slot))) {
							//System.out.println("Sleeping for "+(i/24F));
							int sleepFor = i/24;
							int startTick = tile.cpu.ticks;
							while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
							return LuaValue.TRUE;
						}
					}
				} else {
					LuaValue.error("Slot expected, got " + arg.typename());
				}
				//System.out.println("Sleeping for "+(inv.inv.getSizeInventory()/24F));
				int sleepFor = inv.inv.getSizeInventory()/24;
				int startTick = tile.cpu.ticks;
				while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
				return LuaValue.FALSE;
			}
		}

		private class getSize extends OneArgFunction {

			@Override
			public LuaValue call(LuaValue arg) {
				return LuaValue.valueOf(inv.inv.getSizeInventory());
			}
		}

		@Override
		public int type() {
			return LuaValue.TUSERDATA;
		}

		@Override
		public String typename() {
			return "Inventory";
		}
	}
}
