package ds.mods.CPUPipes.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;

public class BlockCPU extends Block {

	public Icon off;
	public Icon on;
	public Icon blank;

	public BlockCPU(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityCPU();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		off = par1IconRegister.registerIcon("CPUPipes:CPU_off");
		on = par1IconRegister.registerIcon("CPUPipes:CPU_on");
		blank = par1IconRegister.registerIcon("CPUPipes:wireConnection");
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getIcon(int, int)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		boolean isOn = false;
		switch (ForgeDirection.VALID_DIRECTIONS[par1]) {
		case DOWN:
			return blank;
		case EAST:
			return isOn ? on : off;
		case NORTH:
			return isOn ? on : off;
		case SOUTH:
			return isOn ? on : off;
		case UNKNOWN:
			return blank;
		case UP:
			return blank;
		case WEST:
			return isOn ? on : off;
		default:
			return blank;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntityCPU tile = (TileEntityCPU) par1iBlockAccess.getBlockTileEntity(par2, par3, par4);
		boolean isOn = tile.on;
		switch (ForgeDirection.VALID_DIRECTIONS[par5]) {
		case DOWN:
			return blank;
		case EAST:
			return isOn ? on : off;
		case NORTH:
			return isOn ? on : off;
		case SOUTH:
			return isOn ? on : off;
		case UNKNOWN:
			return blank;
		case UP:
			return blank;
		case WEST:
			return isOn ? on : off;
		default:
			return blank;
		}
	}

	@Override
	public String getLocalizedName() {
		return "CPU";
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
	{
		TileEntityCPU tile = (TileEntityCPU)par1World.getBlockTileEntity(par2, par3, par4);
		tile.networkDirty = true;
		if (tile.on && tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord))
		{
			tile.on = false;
			tile.cpu.stop();
			if (tile.monitorInit)
			{
				tile.mon.term.write("CPU Stopping");
				tile.mon.term.setCursorPos(1, tile.mon.term.cursor.y+1);
			}
		}
		if (!tile.on && !tile.worldObj.isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord))
		{
			tile.on = true;
			tile.cpu.start();
			if (tile.monitorInit)
			{
				tile.mon.term.write("CPU Starting");
				tile.mon.term.setCursorPos(1, tile.mon.term.cursor.y+1);
			}
		}
		par1World.markBlockForUpdate(par2, par3, par4);
	}
	
	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}

}
