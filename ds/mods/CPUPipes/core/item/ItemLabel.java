package ds.mods.CPUPipes.core.item;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.client.gui.GuiEditor;

public class ItemLabel extends Item {
	public static HashMap<String,String> editingItems = new HashMap<String, String>();

	public ItemLabel(int par1) {
		super(par1);
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean isItemTool(ItemStack par1ItemStack) {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.item.Item#getItemDisplayName(net.minecraft.item.ItemStack)
	 */
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "Label: "+(par1ItemStack.hasTagCompound() ? par1ItemStack.getTagCompound().getString("label") : "");
	}

	/* (non-Javadoc)
	 * @see net.minecraft.item.Item#registerIcons(net.minecraft.client.renderer.texture.IconRegister)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("CPUPipes:label");
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote)
		{
			if (!par1ItemStack.hasTagCompound())
				par1ItemStack.setTagCompound(new NBTTagCompound());
			NBTTagCompound nbt = par1ItemStack.getTagCompound();
			if (nbt != null)
			{
				if (nbt.getBoolean("lookForLabel"))
				{
					EntityPlayer player = (EntityPlayer)par3Entity;
					if (editingItems.get(player.username) != null)
					{
						nbt.setString("label", editingItems.get(player.username));
						System.out.println("Got label");
						editingItems.remove(player.username);
						nbt.setBoolean("lookForLabel", false);
					}
				}
			}
		}
	}

}
