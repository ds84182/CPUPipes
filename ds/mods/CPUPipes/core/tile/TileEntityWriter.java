package ds.mods.CPUPipes.core.tile;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;
import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import ds.mods.CPUPipes.core.utils.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;

public class TileEntityWriter extends TileEntity {

			public TileEntitySDCardSlot sdcard;
			public ForgeDirection sdcardDir;
			public Vector3 sdcardVector;
			public ArrayList<String> lines = new ArrayList<String>();
			public boolean dirty = false;

			@Override
			public void updateEntity() {
						//System.out.println(dirty);
						if (sdcard == null) {
									for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
												Vector3 vec = new Vector3(xCoord, yCoord, zCoord);
												vec.addDirection(dir);
												TileEntity tile = worldObj.getBlockTileEntity(vec.x, vec.y, vec.z);
												if (tile instanceof TileEntitySDCardSlot) {
															sdcard = (TileEntitySDCardSlot) tile;
															sdcardDir = dir;
															sdcardVector = vec;
												}
									}
						} else {
									//Poll if it still exists
						}
						if (dirty && worldObj.isRemote)
						{
									dirty = false;
									ByteArrayDataOutput out = ByteStreams.newDataOutput();
									out.writeInt(0);
									out.writeInt(xCoord);
									out.writeInt(yCoord);
									out.writeInt(zCoord);
									out.writeInt(worldObj.provider.dimensionId);
									out.writeInt(lines.size());
									for (String line : lines) {
												out.writeUTF(line);
									}
									Packet250CustomPayload p = new Packet250CustomPayload("GUIEditor", out.toByteArray());
									PacketDispatcher.sendPacketToServer(p);
						}
						if (dirty && !worldObj.isRemote)
						{
									dirty = false;
									ByteArrayDataOutput out = ByteStreams.newDataOutput();
									out.writeInt(0);
									out.writeInt(xCoord);
									out.writeInt(yCoord);
									out.writeInt(zCoord);
									out.writeInt(lines.size());
									for (String line : lines)
									{
												System.out.println("lol"+line);
												out.writeUTF(line);
									}
									Packet250CustomPayload p = new Packet250CustomPayload("GUIEditor", out.toByteArray());
									PacketDispatcher.sendPacketToAllInDimension(p, worldObj.provider.dimensionId);
						}
			}

			@Override
			public Packet getDescriptionPacket() {
						NBTTagCompound tag = new NBTTagCompound();
						writeToNBT(tag);
						return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
			}

			@Override
			public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
						readFromNBT(pkt.customParam1);
			}
			
			@Override
			public void writeToNBT(NBTTagCompound nbt)
			{
						FMLLog.fine("NBT Write");
						System.out.println("NBT Write!");
						super.writeToNBT(nbt);
						nbt.setInteger("lineCount", lines.size());
						for (String line : lines)
						{
									System.out.println("Appending "+line);
									nbt.setString("line"+lines.indexOf(line), line);
						}
			}
			
			@Override
			public void readFromNBT(NBTTagCompound nbt)
			{
						FMLLog.fine("NBT Read");
						System.out.println("NBT Read!");
						super.readFromNBT(nbt);
						int c = nbt.getInteger("lineCount");
						for (int i = 0; i<c; i++)
						{
									String line = nbt.getString("line"+i);
									System.out.println("Appending "+line);
									lines.add(line);
						}
						if (worldObj != null)
									dirty = !worldObj.isRemote;
						else
									dirty = true;
			}
}
