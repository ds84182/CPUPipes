package ds.mods.CPUPipes.core;

import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.core.utils.Vector2;

public class Terminal {
	public TileEntityMonitor tile;
	public char[] screen;
	public Vector2 size;
	public Vector2 cursor = new Vector2(1,1);
	public boolean cursorBlink = true;
	
	public Terminal(int w, int h, TileEntityMonitor t)
	{
		size = new Vector2(w,h);
		screen = new char[w*h];
		tile = t;
		for (int i = 0; i<screen.length; i++)
		{
			screen[i] = ' ';
		}
	}
	
	public void write(String text)
	{
		if (cursor.x > 0 && cursor.x <= size.x && cursor.y > 0 && cursor.y <= size.y && text != null)
		{
			for (int i = 0; i<text.length(); i++)
			{
				if (cursor.x <= size.x)
				{
					setChar(cursor.x,cursor.y,text.charAt(i));
					cursor.x++;
				}
			}
		}
		else
		{
			cursor.x+=text.length();
		}
	}
	
	public void setChar(int x, int y, char c)
	{
		screen[((y-1)*size.x)+(x-1)] = c;
	}
	
	public char getChar(int x, int y)
	{
		return screen[((y-1)*size.x)+(x-1)];
	}
	
	public void setCursorPos(int x, int y)
	{
		cursor.x = x;
		cursor.y = y;
	}
	
	public void clear()
	{
		for (int i = 0; i<screen.length; i++)
		{
			screen[i] = ' ';
		}
	}
	
	public void clearLine()
	{
		for (int x = 0; x<size.x; x++)
		{
			setChar(x+1,cursor.y,' ');
		}
	}
	
	public void scroll()
	{
		for (int y = 1; y<size.y; y++)
		{
			for (int x = 0; x<size.x; x++)
			{
				setChar(x+1,y,getChar(x+1,y+1));
			}
		}
		int oldY = cursor.y;
		cursor.y = size.y;
		clearLine();
		cursor.y = oldY;
	}
	
	public void scroll(int n)
	{
		for (int i = 0; i<n; i++)
		{
			scroll();
		}
	}
	
	public void update()
	{
		
	}
	
	public void sendEvent(String event, Object... data)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeInt(tile.xCoord);
		out.writeInt(tile.yCoord);
		out.writeInt(tile.zCoord);
		out.writeInt(tile.worldObj.provider.dimensionId);
		out.writeUTF(event);
		out.writeInt(data.length);
		for (Object obj : data)
		{
			if (obj instanceof Integer)
			{
				out.writeInt(0);
				out.writeInt((Integer) obj);
			}
			else if (obj instanceof String)
			{
				out.writeInt(1);
				out.writeUTF((String) obj);
			}
			else if (obj instanceof Character)
			{
				out.writeInt(2);
				out.writeChar((Character)obj);
			}
		}
		Packet250CustomPayload packet = new Packet250CustomPayload("CPUEvent", out.toByteArray());
		PacketDispatcher.sendPacketToServer(packet);
	}
}
