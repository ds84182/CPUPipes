package ds.mods.CPUPipes.core.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.network.ILabelHolder;

public class LabelUtils {
	public static void setLabel(ILabelHolder labelHolder, String label, World world, int x, int y, int z)
	{
		dropLabel(labelHolder,world,x,y,z);
		labelHolder.setLabel(label);
	}
	
	public static void dropLabel(ILabelHolder labelHolder, World world, int x, int y, int z)
	{
		if (labelHolder.getLabel() != null && !world.isRemote)
		{
			ItemStack other = new ItemStack(CPUPipes.label, 1);
			//Drop the other label
			other.setTagCompound(new NBTTagCompound());
			other.getTagCompound().setString("label", labelHolder.getLabel());
			EntityItem entity = new EntityItem(world, x+0.5D, y+0.5D, z+0.5D, other);
			world.spawnEntityInWorld(entity);
		}
	}
}
