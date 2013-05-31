package ds.mods.CPUPipes.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import ds.mods.CPUPipes.core.item.ItemLabel;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;
import ds.mods.CPUPipes.core.tile.TileEntityWriter;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		//System.out.println("SERVRPAKET");
		//FMLLog.fine("SERVRPAKET");
		if (packet.channel.equalsIgnoreCase("CPUEditLabel")) {
			String s = "";
			for (byte b : packet.data) {
				s = s + ((char) b);
			}
			System.out.println(s);
			ItemLabel.editingItems.put(((EntityPlayer) player).username, s);
		} else if (packet.channel.equalsIgnoreCase("CPUEvent")) {
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			int dim = dat.readInt();
			World world = MinecraftServer.getServer().worldServerForDimension(dim);
			TileEntityMonitor tile = (TileEntityMonitor) world.getBlockTileEntity(x, y, z);
			String event = dat.readUTF();
			int nargs = dat.readInt();
			Object[] data = new Object[nargs];
			for (int i = 0; i<nargs; i++)
			{
				int type = dat.readInt();
				switch (type){
				case 0:
				{
					data[i] = dat.readInt();
					break;
				}
				case 1:
				{
					data[i] = dat.readUTF();
					break;
				}
				case 2:
				{
					data[i] = dat.readChar();
					break;
				}
				}
			}
			tile.term.sendEvent(event, data);
		} else if (packet.channel.equalsIgnoreCase("GUIEditor")) {
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			int type = dat.readInt();
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			int dim = dat.readInt();
			World world = MinecraftServer.getServer().worldServerForDimension(dim);
			TileEntityWriter tile = (TileEntityWriter) world.getBlockTileEntity(x, y, z);
			switch (type) {
			case 0: {
				//Content update packet
				int lines = dat.readInt();
				tile.lines.clear();
				for (int i = 0; i < lines; i++) {
					tile.lines.add(dat.readUTF());
				}
				//This will send a packet to the other players
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeInt(0);
				out.writeInt(x);
				out.writeInt(y);
				out.writeInt(z);
				out.writeInt(tile.lines.size());
				for (String line : tile.lines)
				{
					out.writeUTF(line);
				}
				Packet250CustomPayload p = new Packet250CustomPayload("GUIEditor", out.toByteArray());
				//Send packet to every play in the dim except the sendee.
				for (Object e : world.playerEntities)
				{
					if (e != player)
						PacketDispatcher.sendPacketToPlayer(p, (Player)e);
				}
				break;
			}
			case 1: {
				//Save packet
				String compiledString = "";
				for (String s : tile.lines)
				{
					compiledString+=s+"\n";
				}
				boolean success = tile.sdcard.write(dat.readUTF(), compiledString);
				//Send packet to players denoting success
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeInt(1);
				out.writeBoolean(success);
				Packet250CustomPayload p = new Packet250CustomPayload("CPUEditor", out.toByteArray());
				PacketDispatcher.sendPacketToAllInDimension(p, dim);
				break;
			}
			case 2: {
				//Open packet
				String contents = tile.sdcard.read(dat.readUTF());
				if (contents == null)
					return;
				String[] lines = contents.split("\n");
				tile.lines.clear();
				for (String line : lines)
				{
					tile.lines.add(line.replace("\t", "   "));
				}
				tile.dirty = true;
				break;
			}
			}
		}
	}
}
