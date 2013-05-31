package ds.mods.CPUPipes.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.item.ItemLabel;
import ds.mods.CPUPipes.core.network.ILabelHolder;
import ds.mods.CPUPipes.core.network.INetworkWire;
import ds.mods.CPUPipes.core.tile.TileEntityInventoryConnector;
import ds.mods.CPUPipes.core.utils.LabelUtils;
import ds.mods.CPUPipes.core.utils.Vector3;
import net.minecraft.creativetab.CreativeTabs;

public class BlockInventoryConnector extends Block {

	public Icon inventoryCon;
	public Icon icon;
	public Icon wireCon;
	public Icon blank;

	public BlockInventoryConnector(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityInventoryConnector();
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		ItemStack item = par5EntityPlayer.getHeldItem();
		if (item != null && item.getItem() instanceof ItemLabel) {
			NBTTagCompound nbt = item.getTagCompound();
			if (nbt != null) {
				LabelUtils.setLabel((ILabelHolder) par1World.getBlockTileEntity(par2, par3, par4), nbt.getString("label"), par1World, par2, par3, par4);
				item.stackSize = 0;
				return true;
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4,
			int par5, int par6) {
		LabelUtils.dropLabel((ILabelHolder) par1World.getBlockTileEntity(par2, par3, par4), par1World, par2, par3, par4);
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		inventoryCon = par1IconRegister.registerIcon("CPUPipes:inventoryConnection");
		blank = par1IconRegister.registerIcon("CPUPipes:blank");
		icon = par1IconRegister.registerIcon("CPUPipes:inventoryTop");
		wireCon = par1IconRegister.registerIcon("CPUPipes:wireConnection");
		this.blockIcon = icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntityInventoryConnector tile = (TileEntityInventoryConnector) par1iBlockAccess.getBlockTileEntity(par2, par3, par4);
		if (ForgeDirection.UP.ordinal() == par5) {
			return icon;
		}
		if (tile != null && tile.invdir != null) {
			if (par5 == tile.invdir.ordinal()) {
				return inventoryCon;
			}
		}
		Vector3 vec = new Vector3(par2, par3, par4);
		vec.addDirection(ForgeDirection.VALID_DIRECTIONS[par5]);
		TileEntity other = par1iBlockAccess.getBlockTileEntity(vec.x, vec.y, vec.z);
		if (other instanceof INetworkWire) {
			return wireCon;
		}
		return blank;
	}

	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}
}
