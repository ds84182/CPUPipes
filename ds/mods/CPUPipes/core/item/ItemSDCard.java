package ds.mods.CPUPipes.core.item;

import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;

public class ItemSDCard extends Item {

	public ItemSDCard(int par1) {
		super(par1);
		this.setMaxStackSize(1);
	}

	public int getID(ItemStack item, World world) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
			item.setItemDamage(world.getUniqueDataId("sdcard"));
			//item.getTagCompound().setInteger("id", world.getUniqueDataId("sdcard"));
			item.getTagCompound().setFloat("errorChance", 0.05F);
		}
		return item.getItemDamage();
	}

	public boolean getError(ItemStack item, World world) {
		if (!item.hasTagCompound()) {
			item.setTagCompound(new NBTTagCompound());
			item.setItemDamage(world.getUniqueDataId("sdcard"));
			//item.getTagCompound().setInteger("id", world.getUniqueDataId("sdcard"));
			item.getTagCompound().setFloat("errorChance", 0.05F);
		}
		return new Random().nextFloat() <= item.getTagCompound().getFloat("errorChance");
	}

	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "SDCard "+par1ItemStack.getItemDamage();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("CPUPipes:SDCard");
	}

	@Override
	public boolean isItemTool(ItemStack par1ItemStack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return CPUPipes.tab;
	}
	
}
