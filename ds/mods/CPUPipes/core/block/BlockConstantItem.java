package ds.mods.CPUPipes.core.block;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.item.ItemLabel;
import ds.mods.CPUPipes.core.network.ILabelHolder;
import ds.mods.CPUPipes.core.network.INetworkWire;
import ds.mods.CPUPipes.core.tile.TileEntityConstantItem;
import ds.mods.CPUPipes.core.tile.TileEntitySDCardSlot;
import ds.mods.CPUPipes.core.utils.LabelUtils;
import ds.mods.CPUPipes.core.utils.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockConstantItem extends Block {

	public Icon wireCon;
	public Icon icon;
	public Icon blank;

	public BlockConstantItem(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityConstantItem();
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#onBlockActivated(net.minecraft.world.World, int, int, int, net.minecraft.entity.player.EntityPlayer, int, float, float, float)
	 */
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
		else
		{
			if (!par1World.isRemote) {
				FMLNetworkHandler.openGui(par5EntityPlayer, CPUPipes.instance, 100, par1World, par2, par3, par4);
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getBlockTexture(net.minecraft.world.IBlockAccess, int, int, int, int)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		Vector3 vec = new Vector3(par2, par3, par4);
		vec.addDirection(ForgeDirection.VALID_DIRECTIONS[par5]);
		TileEntity other = par1iBlockAccess.getBlockTileEntity(vec.x, vec.y, vec.z);
		if (other instanceof INetworkWire) {
			return wireCon;
		}
		return par5 == 1 ? icon : blank;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getIcon(int, int)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return icon;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#registerIcons(net.minecraft.client.renderer.texture.IconRegister)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("CPUPipes:constantItemTop");
		wireCon = par1IconRegister.registerIcon("CPUPipes:wireConnection");
		blank = par1IconRegister.registerIcon("CPUPipes:blank");
	}
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		TileEntityConstantItem tile = (TileEntityConstantItem) par1World.getBlockTileEntity(par2, par3, par4);
		if (!par1World.isRemote)
		{
			if (tile.item != null)
			{
				EntityItem e = new EntityItem(par1World, par2+0.5D, par3+0.5D, par4+0.5D, tile.item);
				par1World.spawnEntityInWorld(e);
			}
		}
		LabelUtils.dropLabel(tile, par1World, par2, par3, par4);
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}
}
