package ds.mods.CPUPipes.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;
import ds.mods.CPUPipes.core.PacketHandler;
import ds.mods.CPUPipes.core.Terminal;
import ds.mods.CPUPipes.core.network.ILabelHolder;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.core.tile.TileEntityWriter;
import ds.mods.CPUPipes.core.utils.Vector2;

public class ClientPacketHandler extends PacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		//System.out.println("CLIENTPAKET");
		if (packet.channel.equalsIgnoreCase("GUIEditor")) {
			//System.out.println("CLIENTPAKET");
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			int type = dat.readInt();
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			World world = Minecraft.getMinecraft().theWorld;
			TileEntityWriter tile = (TileEntityWriter) world.getBlockTileEntity(x, y, z);
			switch(type)
			{
			case 0:
			{
				//Content update packet
				int lines = dat.readInt();
				tile.lines.clear();
				for (int i = 0; i < lines; i++) {
					tile.lines.add(dat.readUTF());
				}
				break;
			}
			}
		} else if (packet.channel.equalsIgnoreCase("LabelHolder")) {
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			World world = Minecraft.getMinecraft().theWorld;
			ILabelHolder label = (ILabelHolder) world.getBlockTileEntity(x, y, z);
			label.setLabel(dat.readUTF());
		} else if (packet.channel.equalsIgnoreCase("TermUpdate")) {
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			World world = Minecraft.getMinecraft().theWorld;
			TileEntityMonitor mon = (TileEntityMonitor) world.getBlockTileEntity(x, y, z);
			byte type = dat.readByte();
			switch (type)
			{
			case -1:
			{
				//Commence full update!
				int w = dat.readInt();
				int h = dat.readInt();
				if (mon.term == null)
					mon.term = new Terminal(w, h, mon);
				if (!mon.term.size.equals(new Vector2(w,h)))
				{
					mon.term = new Terminal(w, h, mon);
				}
				mon.term.cursor.x = dat.readInt();
				mon.term.cursor.y = dat.readInt();
				mon.term.cursorBlink = dat.readBoolean();
				for (int i = 0; i<mon.term.screen.length; i++)
				{
					mon.term.screen[i] = dat.readChar();
				}
				break;
			}
			case 0:
			{
				//Write
				mon.term.setCursorPos(dat.readInt(), dat.readInt());
				mon.term.write(dat.readUTF());
				break;
			}
			case 1:
			{
				//Set cursor position
				mon.term.setCursorPos(dat.readInt(), dat.readInt());
				break;
			}
			case 2:
			{
				//Clear
				mon.term.clear();
				break;
			}
			case 3:
			{
				//Clearline
				mon.term.cursor.y = dat.readInt();
				mon.term.clearLine();
				break;
			}
			case 4:
			{
				//Scroll
				mon.term.scroll(dat.readInt());
				break;
			}
			case 5:
			{
				//Cursor blink
				mon.term.cursorBlink = dat.readBoolean();
				break;
			}
			}
		}
	}

}
