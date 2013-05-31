package ds.mods.CPUPipes.lualib;

import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;
import ds.mods.CPUPipes.server.CPUThread;
import ds.mods.CPUPipes.server.ServerCPU;

public class MainLib extends OneArgFunction {
	public TileEntityCPU tile;
	public LuaValue env;
	
	public MainLib(TileEntityCPU t)
	{
		tile = t;
	}

	@Override
	public LuaValue call(LuaValue arg) {
		env = arg;
		env.set("tick", new tick());
		return NIL;
	}
	
	private class tick extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			CPUThread thread = ((ServerCPU)tile.cpu).thread;
			if (thread != null)
			{
				thread.eventQueue.add(varargsOf(new LuaValue[]{valueOf("tick")}));
				env.get("yield").call();
			}
			return NIL;
		}
		
	}

}
