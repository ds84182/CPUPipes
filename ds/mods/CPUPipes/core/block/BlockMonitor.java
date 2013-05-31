package ds.mods.CPUPipes.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.tile.TileEntityMonitor;

public class BlockMonitor extends Block {
	public Icon on;
	public Icon off;
	public Icon blank;

	public BlockMonitor(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		return super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return ForgeDirection.VALID_DIRECTIONS[par1] == ForgeDirection.WEST ? on : blank;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("CPUPipes:termOn");
		on = this.blockIcon;
		off = par1IconRegister.registerIcon("CPUPipes:termOff");
		blank = par1IconRegister.registerIcon("CPUPipes:blank");
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityMonitor();
	}
	
	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		if (!par1World.isRemote)
		{
			FMLNetworkHandler.openGui(par5EntityPlayer, CPUPipes.instance, 101, par1World, par2, par3, par4);
			return true;
		}
		return true;
	}

}
