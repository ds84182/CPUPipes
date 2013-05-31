package ds.mods.CPUPipes.core.item;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.client.gui.GuiEditLabel;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemLabelStacked extends Item {

	public ItemLabelStacked(int par1) {
		super(par1);
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "Empty Label";
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ItemStack itemstack1 = new ItemStack(CPUPipes.label, 1);
        if (par2World.isRemote)
        	//Show the label gui
        	FMLClientHandler.instance().displayGuiScreen(par3EntityPlayer, new GuiEditLabel(itemstack1));
        else
        {
        	ItemLabel.editingItems.remove(par3EntityPlayer.username);
        }
        --par1ItemStack.stackSize;
        itemstack1.setTagCompound(new NBTTagCompound());
    	itemstack1.getTagCompound().setBoolean("lookForLabel", true);
        System.out.println(itemstack1.getTagCompound().getBoolean("lookForLabel"));
        if (par1ItemStack.stackSize <= 0)
        {
            return itemstack1;
        }
        else
        {
            if (!par3EntityPlayer.inventory.addItemStackToInventory(itemstack1.copy()))
            {
                par3EntityPlayer.dropPlayerItem(itemstack1);
            }

            return par1ItemStack;
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("CPUPipes:emptyLabel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return CPUPipes.tab;
	}

}
