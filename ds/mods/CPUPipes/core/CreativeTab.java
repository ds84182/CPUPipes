/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.mods.CPUPipes.core;

import ds.mods.CPUPipes.CPUPipes;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Dwayne
 */
public class CreativeTab extends CreativeTabs {

	public CreativeTab(String label) {
		super(label);
	}

	public CreativeTab(int par1, String par2Str) {
		super(par1, par2Str);
	}

	@Override
	public int getTabIconItemIndex()
    {
        return CPUPipes.cpu.blockID;
    }


}
