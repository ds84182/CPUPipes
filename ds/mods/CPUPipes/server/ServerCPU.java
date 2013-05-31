package ds.mods.CPUPipes.server;
import java.io.File;
import java.io.RandomAccessFile;

import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.network.CPU;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.core.utils.Vector2;
import ds.mods.CPUPipes.luaj.vm2.Globals;
import ds.mods.CPUPipes.luaj.vm2.LuaString;
import ds.mods.CPUPipes.luaj.vm2.LuaThread;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.Varargs;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.jse.JsePlatform;
import ds.mods.CPUPipes.lualib.ConstantItemLib;
import ds.mods.CPUPipes.lualib.FsLib;
import ds.mods.CPUPipes.lualib.InventoryLib;
import ds.mods.CPUPipes.lualib.MainLib;
import ds.mods.CPUPipes.lualib.TermLib;

public class ServerCPU extends CPU {

	public Globals _G;
	public CPUThread thread;
	public String code;
	public boolean crashCPU = false;

	public ServerCPU(TileEntityCPU tile) {
		super(tile);
		this.tile = tile;
		try {
			File f = new File(CPUPipes.class.getResource("lua/bios.lua").toURI());
			RandomAccessFile fin = new RandomAccessFile(f, "r");
			byte[] contents = new byte[(int) f.length()];
			fin.readFully(contents);
			fin.close();
			char[] cCont = new char[contents.length];
			for (int i = 0; i<contents.length; i++)
			{
				cCont[i] = (char) contents[i];
			}
			code = String.valueOf(cCont);
		} catch (Exception e) {
			System.out.println("Failed to load CPUPipes BIOS");
			code = "";
			e.printStackTrace();
		}
	}

	@Override
	public void start()
	{
		if (started)
			return;
		crashCPU = false;
		_G = JsePlatform.debugGlobals();
		LuaValue sethook = _G.get("debug").get("sethook");
		_G.load(new MainLib(tile));
		_G.load(new InventoryLib(tile));
		_G.load(new ConstantItemLib(tile));
		_G.load(new FsLib(tile));
		_G.load(new TermLib(tile));
		_G.set("yield", _G.get("coroutine").get("yield"));
		LuaValue[] vals = new LuaValue[1];
		vals[0] = LuaValue.valueOf("cpu "+tile.xCoord+","+tile.yCoord+","+tile.zCoord);
		Varargs ret = _G.get("load").invoke(LuaValue.valueOf(code),LuaValue.varargsOf(vals));
		if (ret.arg1() == LuaValue.NIL)
		{
			System.out.println(ret.arg(2).tojstring());
			return;
		}
		final LuaThread t = new LuaThread(_G, ret.arg1());
		//A little trick I borrowed from ComputerCraft
		sethook.invoke(new LuaValue[]{t, new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				if (crashCPU) {
					t.yield(LuaValue.NONE);
					//error("Too long without yield");
					thread.terminated = true;
					//thread.running = false;
					//thread.waiting = false;
				}
				return LuaValue.NIL;
			}
		}, LuaValue.NIL, LuaValue.valueOf(100000)});
		thread = new CPUThread(t);
		thread.eventQueue.push(LuaValue.NONE);
		thread.start();
		started = true;
	}

	@Override
	public void stop()
	{
		if (!started)
			return;
		if (thread != null)
		{
			crashCPU = true;
			while (!thread.waiting && thread.running) {System.out.println("Trying to terminate");};
			synchronized(thread)
			{
				thread.notify();
			}
		}
		started = false;
	}

	@Override
	public void tick() {
		ticks++;
		if (thread != null) {
			if (thread.running) {
				thread.runningFor++;
				if (thread.runningFor >= 20) {
					//Running for a second! Lets terminate!
					System.out.println("Terminating Thread...");
					thread.terminated = true;
					crashCPU = true;
					thread = null;
				}
			} else {
				thread.runningFor = 0;
			}
			if (thread != null && thread.error != null)
			{
				//System.out.println("Error: "+thread.error);
				print(thread.error);
				/*for (String line : thread.error.split("\n"))
				{
					if (tile.monitorInit)
					{
						tile.mon.term.write(line.replace('\t', ' '));
						tile.mon.term.setCursorPos(1, tile.mon.term.cursor.y+1);
					}
				}*/
				thread = null;
				tile.on = false;
			}
			if (thread != null && thread.waiting)
			{
				synchronized(thread)
				{
					thread.notify();
				}
			}
		}
		//_G.get("load").call("print(Inventory.get(1).getSlot(0).getCount())").call();
	}
	
	public void newLine()
	{
		if (tile.monitorInit)
		{
			Vector2 size = tile.mon.term.size;
			Vector2 cursor = tile.mon.term.cursor;
			if (cursor.y+1 <= size.y)
			{
				tile.mon.term.setCursorPos(1, cursor.y+1);
			}
			else
			{
				tile.mon.term.setCursorPos(1, size.y);
				tile.mon.term.scroll(1);
			}
		}
	}
	
	public void write(String s)
	{
		if (tile.monitorInit)
		{
			Vector2 size = tile.mon.term.size;
			Vector2 cursor = tile.mon.term.cursor;
			LuaString text = LuaValue.valueOf(s);
			while (text.length() > 0)
			{
				LuaValue whitespaceLV = text.get("match").call(text,LuaValue.valueOf("^[ \t]+"));
				if (whitespaceLV != LuaValue.NIL)
				{
					String whitespace = whitespaceLV.checkjstring();
					tile.mon.term.write(whitespace.replace('\t', ' '));
					text = text.substring(whitespace.length(), text.length());
				}
				LuaValue newlineLV = text.get("match").call(text,LuaValue.valueOf("^\n"));
				if (newlineLV != LuaValue.NIL)
				{
					newLine();
					text = text.substring(1, text.length());
				}
				LuaValue textLV = text.get("match").call(text,LuaValue.valueOf("^[^ \t\n]+"));
				if (textLV != LuaValue.NIL)
				{
					text = text.substring(textLV.length(), text.length());
					String wtext = textLV.checkjstring();
					System.out.println(wtext);
					System.out.println(wtext.length());
					if (wtext.length() > size.x)
					{
						while (wtext.length() > 0)
						{
							if (cursor.x > size.x)
							{
								newLine();
							}
							int ox = cursor.x;
							tile.mon.term.write(wtext);
							System.out.println("Writing "+wtext);
							wtext = wtext.substring((size.x-ox)+1);
						}
					}
					else
					{
						if (cursor.x + (wtext.length()-1) > size.x)
						{
							newLine();
						}
						tile.mon.term.write(wtext);
					}
				}
			}
		}
	}
	
	public void print(Object... data)
	{
		for (Object o : data)
		{
			write(o.toString());
		}
		write("\n");
	}
}
