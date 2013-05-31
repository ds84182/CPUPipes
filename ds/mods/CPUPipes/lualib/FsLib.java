package ds.mods.CPUPipes.lualib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.lib.OneArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.TwoArgFunction;
import ds.mods.CPUPipes.luaj.vm2.lib.ZeroArgFunction;

public class FsLib extends OneArgFunction {
	
	public TileEntityCPU tile;
	public ArrayList<FileHandleR> openFilesR = new ArrayList<FsLib.FileHandleR>();
	public ArrayList<FileHandleW> openFilesW = new ArrayList<FsLib.FileHandleW>();

	public FsLib(TileEntityCPU tile) {
		this.tile = tile;
	}

	@Override
	public LuaValue call(LuaValue env) {
		LuaTable fs = new LuaTable();
		fs.set("open", new open());
		fs.set("isSDCardIn", new isSDCardIn());
		fs.set("exists", new exists());
		env.set("fs", fs);
		return NIL;
	}
	
	private class isSDCardIn extends ZeroArgFunction
	{

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(tile.sdcard != null && !tile.sdcard.error && tile.sdcard.sdcard != null);
		}
		
	}
	
	private class exists extends OneArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg) {
			String filename = arg.checkjstring();
			if (filename == null)
				return FALSE;
			if (tile.sdcard == null)
				error("Please insert SDCard");
			return LuaValue.valueOf(tile.sdcard.exists(filename));
		}
		
	}
	
	private class open extends TwoArgFunction
	{

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			String filename = arg1.checkjstring();
			String mode = arg2.checkjstring();
			if (filename == null)
				error("Directory does not exist");
			if (mode == null)
				error("Invalid mode");
			if (tile.sdcard == null)
				error("Please insert SDCard");
			if (mode.equalsIgnoreCase("r"))
			{
				if (!tile.sdcard.exists(filename))
					error("File does not exist");
				File f = tile.sdcard.getFile(filename);
				try {
					return new FileHandleR(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (mode.equalsIgnoreCase("w"))
			{
				return new FileHandleW(filename);
			} else if (mode.equalsIgnoreCase("aw")) {
				if (!tile.sdcard.exists(filename))
					error("File does not exist");
				return new FileHandleW(filename,tile.sdcard.read(filename));
			}
			else
				error("Invalid mode");
			return NIL;
		}
		
	}
	
	private class FileHandleR extends LuaTable {
		RandomAccessFile in;
		
		private FileHandleR()
		{
			openFilesR.add(this);
			this.set("close", new close());
			this.set("readLine", new readLine());
			this.set("readAll", new readAll());
		}
		
		public FileHandleR(RandomAccessFile i)
		{
			this();
			in = i;
		}
		
		public FileHandleR(File f) throws FileNotFoundException
		{
			this();
			in = new RandomAccessFile(f, "r");
		}
		
		private class close extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return NIL;
			}
			
		}
		
		public class readLine extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				try {
					return valueOf(in.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return NIL;
			}
			
		}
		
		public class readAll extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				try {
					byte ch[] = new byte[(int) (in.length()-in.getFilePointer())];
					in.readFully(ch);
					return valueOf(ch);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return NIL;
			}
			
		}

		@Override
		public int type() {
			return TUSERDATA;
		}

		@Override
		public String typename() {
			return "rhandle";
		}
		
	}
	
	private class FileHandleW extends LuaTable {
		String out;
		String filename;
		
		private FileHandleW()
		{
			openFilesW.add(this);
			this.set("close", new close());
			this.set("writeLine", new writeLine());
			this.set("write", new write());
		}
		
		public FileHandleW(String f, String c)
		{
			this();
			filename = f;
			out = c;
		}
		
		public FileHandleW(String f)
		{
			this(f,"");
		}
		
		private class close extends ZeroArgFunction
		{

			@Override
			public LuaValue call() {
				tile.sdcard.write(filename, out);
				return NIL;
			}
			
		}
		
		public class write extends OneArgFunction
		{

			@Override
			public LuaValue call(LuaValue arg) {
				out += arg.checkjstring();
				return NIL;
			}
			
		}
		
		public class writeLine extends OneArgFunction
		{

			@Override
			public LuaValue call(LuaValue arg) {
				out += arg.checkjstring()+"\n";
				return NIL;
			}
			
		}

		@Override
		public int type() {
			return TUSERDATA;
		}

		@Override
		public String typename() {
			return "whandle";
		}
		
	}

}
