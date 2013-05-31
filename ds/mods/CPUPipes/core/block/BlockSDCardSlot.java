package ds.mods.CPUPipes.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.*;
import cpw.mods.fml.relauncher.*;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.item.ItemSDCard;
import ds.mods.CPUPipes.core.tile.TileEntitySDCardSlot;

public class BlockSDCardSlot extends Block {

	public Icon sdcardNone;
	public Icon sdcardInsert;
	public Icon sdcardError;
	public Icon blank;

	public BlockSDCardSlot(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntitySDCardSlot();
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		TileEntitySDCardSlot tile = (TileEntitySDCardSlot) par1World.getBlockTileEntity(par2, par3, par4);
		if (tile.sdcard != null) {
			if (!par1World.isRemote) {
				//Drop item
				EntityItem e = new EntityItem(par1World, par2 + 0.5D, par3 + 0.5D, par4 + 0.5D, tile.sdcard);
				par1World.spawnEntityInWorld(e);
			}
			tile.sdcard = null;
		} else {
			//Insert item
			ItemStack held = par5EntityPlayer.getHeldItem();
			if (held != null) {
				if (held.getItem() instanceof ItemSDCard) {
					tile.setInventorySlotContents(0, held);
					held.stackSize = 0;
				}
			}
		}
		par1World.markBlockForUpdate(par2, par3, par4);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		if (par5 == 1) {
			TileEntitySDCardSlot tile = (TileEntitySDCardSlot) par1iBlockAccess.getBlockTileEntity(par2, par3, par4);
			return tile.sdcard != null ? (tile.error ? sdcardError : sdcardInsert) : sdcardNone;
		}
		return blank;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		// TODO Auto-generated method stub
		return par1 == 1 ? sdcardNone : blank;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		sdcardNone = par1IconRegister.registerIcon("CPUPipes:SDCardSlot");
		sdcardInsert = par1IconRegister.registerIcon("CPUPipes:SDCardSlotInsert");
		sdcardError = par1IconRegister.registerIcon("CPUPipes:SDCardSlotInsertError");
		blank = par1IconRegister.registerIcon("CPUPipes:blank");
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		TileEntitySDCardSlot tile = (TileEntitySDCardSlot) par1World.getBlockTileEntity(par2, par3, par4);
		if (!par1World.isRemote)
		{
			if (tile.sdcard != null)
			{
				EntityItem e = new EntityItem(par1World, par2+0.5D, par3+0.5D, par4+0.5D, tile.sdcard);
				par1World.spawnEntityInWorld(e);
			}
		}
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}
	
}
