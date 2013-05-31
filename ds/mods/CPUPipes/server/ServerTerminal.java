package ds.mods.CPUPipes.server;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import ds.mods.CPUPipes.core.Terminal;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;

public class ServerTerminal extends Terminal {
	public TileEntityMonitor mon;
	public boolean lastCursorBlink;
	public boolean dirty;

	public ServerTerminal(int w, int h, TileEntityMonitor m) {
		super(w, h, m);
		mon = m;
	}

	@Override
	public void write(String text) {
		/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(0);
		out.writeInt(cursor.x);
		out.writeInt(cursor.y);
		out.writeUTF(text);
		sendPacket(out);*/
		super.write(text);
		dirty = true;
	}
	
	@Override
	public void setCursorPos(int x, int y) {
		super.setCursorPos(x, y);
		dirty = true;
		/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(1);
		out.writeInt(x);
		out.writeInt(y);
		sendPacket(out);*/
	}

	@Override
	public void clear() {
		super.clear();
		dirty = true;
		/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(2);
		sendPacket(out);*/
	}

	@Override
	public void clearLine() {
		super.clearLine();
		dirty = true;
		/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(3);
		out.writeInt(cursor.y);
		sendPacket(out);*/
	}
	
	//Always call this, even with only 1 as an argument!
	@Override
	public void scroll(int n) {
		super.scroll(n);
		dirty = true;
		/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(4);
		out.writeInt(n);
		sendPacket(out);*/
	}
	
	public void update()
	{
		if (lastCursorBlink != cursorBlink)
		{
			lastCursorBlink = cursorBlink;
			dirty = true;
			/*ByteArrayDataOutput out = ByteStreams.newDataOutput();
			addCoords(out);
			out.writeByte(5);
			out.writeBoolean(cursorBlink);
			sendPacket(out);*/
		}
		if (dirty)
		{
			PacketDispatcher.sendPacketToAllAround(mon.xCoord, mon.yCoord, mon.zCoord, 64, mon.worldObj.provider.dimensionId, getCompleteUpdatePacket());
			dirty = false;
		}
	}

	public void addCoords(ByteArrayDataOutput out)
	{
		out.writeInt(mon.xCoord);
		out.writeInt(mon.yCoord);
		out.writeInt(mon.zCoord);
	}

	public void sendPacket(ByteArrayDataOutput out)
	{//new Packet250CustomPayload("TermUpdate", out.toByteArray())
		PacketDispatcher.sendPacketToAllAround(mon.xCoord, mon.yCoord, mon.zCoord, 64, mon.worldObj.provider.dimensionId, new Packet250CustomPayload("TermUpdate", out.toByteArray()));
	}
	
	public Packet getCompleteUpdatePacket()
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		addCoords(out);
		out.writeByte(-1);
		out.writeInt(this.size.x);
		out.writeInt(this.size.y);
		out.writeInt(this.cursor.x);
		out.writeInt(this.cursor.y);
		out.writeBoolean(this.cursorBlink);
		for (int i=0; i<this.screen.length; i++)
		{
			out.writeChar(this.screen[i]);
		}
		return new Packet250CustomPayload("TermUpdate", out.toByteArray());
	}

	@Override
	public void sendEvent(String event, Object... data) {
		//Get the TileEntityCPUs around
		//System.out.println("Event "+event);
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity tile = mon.worldObj.getBlockTileEntity(mon.xCoord+dir.offsetX, mon.yCoord+dir.offsetY, mon.zCoord+dir.offsetZ);
			if (tile instanceof TileEntityCPU)
			{
				ServerCPU cpu = (ServerCPU) ((TileEntityCPU)tile).cpu;
				if (cpu.thread != null)
				{
					//cpu.thread.eventQueue
					LuaValue[] lst = new LuaValue[data.length+1];
					lst[0] = LuaValue.valueOf(event);
					for (int i = 0; i<data.length; i++)
					{
						//System.out.println(data[i]);
						if (data[i] instanceof Integer)
						{
							lst[i+1] = LuaValue.valueOf((Integer)data[i]);
						} else if (data[i] instanceof String)
						{
							lst[i+1] = LuaValue.valueOf((String)data[i]);
						} else if (data[i] instanceof Character)
						{
							lst[i+1] = LuaValue.valueOf((Character)data[i]);
						}
					}
					cpu.thread.eventQueue.push(LuaValue.varargsOf(lst));
					synchronized (cpu.thread)
					{
						cpu.thread.notify();
					}
				}
			}
		}
	}

}
