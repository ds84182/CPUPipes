package ds.mods.CPUPipes.core.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ds.mods.CPUPipes.CPUPipes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockGreyDustOre extends Block {

	public BlockGreyDustOre(int par1) {
		super(par1,Material.rock);
		this.setHardness(1F);
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return par1Random.nextInt(3)+3;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return CPUPipes.greyDustID;
	}

	@Override
	public int quantityDroppedWithBonus(int par1, Random par2Random) {
		int j = par2Random.nextInt(par1 + 2) - 1;

        if (j < 0)
        {
            j = 0;
        }

        return this.quantityDropped(par2Random) * (j + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("CPUPipes:greyDustOre");
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x,
			int y, int z, int metadata) {
		return true;
	}

}
