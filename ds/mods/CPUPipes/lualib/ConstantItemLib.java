package ds.mods.CPUPipes.lualib;

import ds.mods.CPUPipes.core.network.ConstantItemNetworkDevice;
import ds.mods.CPUPipes.core.network.INetworkDevice;
import ds.mods.CPUPipes.core.network.InventoryNetworkDevice;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;
import ds.mods.CPUPipes.lualib.SlotObject.Slot;

public class ConstantItemLib extends OneArgFunction {

	public TileEntityCPU tile;
	public LuaValue yield;

	public ConstantItemLib(TileEntityCPU tile) {
		this.tile = tile;
	}

	@Override
	public LuaValue call(LuaValue env) {
		LuaTable inventory = new LuaTable();
		inventory.set("get", new get());
		inventory.set("getAll", new getAll());
		env.set("ConstantItem", inventory);
		yield = env.get("tick");
		return NIL;
	}

	private class getAll extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			LuaTable tab = new LuaTable();
			for (int i = 0; i<tile.net.devices.size(); i++) {
				INetworkDevice d = tile.net.devices.get(i);
				if (d.getType().equals("ConstantItem"))
				{
					tab.insert(tab.length()+1, new ConstantItemObject((ConstantItemNetworkDevice) d,new Slot(d, 0, ((ConstantItemNetworkDevice) d).getInventory(), tile)));
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
			//System.out.println(arg.toint());
			//System.out.println(tile.net.devices);
			for (int i = 0; i<tile.net.devices.size(); i++) {
				INetworkDevice d = tile.net.devices.get(i);
				//System.out.println(d.getID());
				//System.out.println(d.getType());
				//System.out.println(d.getLabel());
				if (arg.type() == LuaValue.TNUMBER) {
					if (d.getID() == arg.toint() && d.getType().equals("ConstantItem")) {
						//System.out.println("Found");
						SlotObject s = new ConstantItemObject((ConstantItemNetworkDevice) d,new Slot(d, 0, ((ConstantItemNetworkDevice) d).getInventory(),tile));
						s.readOnly = true;
						//System.out.println("Sleeping for "+(i/16F));
						int sleepFor = i/16;
						int startTick = tile.cpu.ticks;
						while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
						return s;
					}
				} else if (arg.type() == LuaValue.TSTRING) {
					if ((d.getLabel() != null && d.getLabel().equals(arg.toString())) && d.getType().equals("ConstantItem")) {
						//System.out.println("Found");
						SlotObject s = new ConstantItemObject((ConstantItemNetworkDevice) d,new Slot(d, 0, ((ConstantItemNetworkDevice) d).getInventory(),tile));
						s.readOnly = true;
						//System.out.println("Sleeping for "+(i/16F));
						int sleepFor = i/16;
						int startTick = tile.cpu.ticks;
						while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
						return s;
					}
				}
			}
			//System.out.println("Sleeping for "+(tile.net.devices.size()/16F));
			int sleepFor = tile.net.devices.size()/16;
			int startTick = tile.cpu.ticks;
			while (tile.cpu.ticks<startTick+sleepFor) {yield.call();};
			return NIL;
		}
	}

	private class ConstantItemObject extends SlotObject
	{
		public ConstantItemNetworkDevice cons;
		public ConstantItemObject(ConstantItemNetworkDevice c, Slot s)
		{
			super(s);
			cons = c;
			this.set("getLabel", new getLabel());
			this.set("getID", new getID());
		}

		private class getLabel extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				return cons.getLabel() != null ? LuaValue.valueOf(cons.getLabel()) : NIL;
			}

		}

		private class getID extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				return LuaValue.valueOf(cons.getID());
			}

		}
	}
}
