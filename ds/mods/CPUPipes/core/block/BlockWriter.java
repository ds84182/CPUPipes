package ds.mods.CPUPipes.core.block;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.client.gui.GuiEditor;
import ds.mods.CPUPipes.core.tile.TileEntityWriter;

public class BlockWriter extends Block {

	public Icon top;
	public Icon blank;

	public BlockWriter(int par1, Material par2Material) {
		super(par1, par2Material);
	}
	
	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		return new TileEntityWriter();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		top = par1IconRegister.registerIcon("CPUPipes:programWriterTop");
		blank = par1IconRegister.registerIcon("CPUPipes:blank");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return par1 == 1 ? top : blank;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		if (par1World.isRemote)
        	FMLClientHandler.instance().displayGuiScreen(par5EntityPlayer, new GuiEditor((TileEntityWriter) par1World.getBlockTileEntity(par2, par3, par4)));
		return true;
	}
	
	@Override
	public CreativeTabs getCreativeTabToDisplayOn() {
		return CPUPipes.tab;
	}

}
