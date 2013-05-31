package ds.mods.CPUPipes.lualib;

import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.Varargs;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.TwoArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;

public class TermLib extends OneArgFunction {
	
	public TileEntityCPU tile;
	
	public TermLib(TileEntityCPU t)
	{
		tile = t;
	}

	@Override
	public LuaValue call(LuaValue env) {
		LuaTable term = new LuaTable();
		term.set("isTermConnected", new isTermConnected());
		term.set("write", new write());
		term.set("getSize", new getSize());
		term.set("getCursorPos", new getCursorPos());
		term.set("setCursorPos", new setCursorPos());
		term.set("scroll", new scroll());
		term.set("clear", new clear());
		term.set("clearLine", new clearLine());
		env.set("term", term);
		return NIL;
	}
	
	private class isTermConnected extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(tile.monitorInit);
		}
		
	}
	
	private class clear extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			tile.mon.term.clear();
			return NIL;
		}
		
	}
	
	private class clearLine extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			tile.mon.term.clearLine();
			return NIL;
		}
		
	}
	
	private class write extends OneArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg) {
			if (tile.monitorInit)
				tile.mon.term.write(arg.checkjstring());
			return NIL;
		}
		
	}
	
	private class getSize extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return NIL;
		}

		@Override
		public Varargs invoke(Varargs varargs) {
			if (tile.monitorInit)
				return varargsOf(new LuaValue[]{valueOf(tile.mon.term.size.x),valueOf(tile.mon.term.size.y)});
			return NONE;
		}
		
	}
	
	private class getCursorPos extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return NIL;
		}

		@Override
		public Varargs invoke(Varargs varargs) {
			if (tile.monitorInit)
				return varargsOf(new LuaValue[]{valueOf(tile.mon.term.cursor.x),valueOf(tile.mon.term.cursor.y)});
			return NONE;
		}
		
	}
	
	private class setCursorPos extends TwoArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			if (tile.monitorInit)
				tile.mon.term.setCursorPos(arg1.checkint(), arg2.checkint());
			return NIL;
		}
		
	}
	
	private class scroll extends OneArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg) {
			if (tile.monitorInit)
			{
				if (arg.isnil())
				{
					tile.mon.term.scroll(1);
				}
				else
				{
					tile.mon.term.scroll(arg.checkint());
				}
			}
			return null;
		}
		
	}

}
