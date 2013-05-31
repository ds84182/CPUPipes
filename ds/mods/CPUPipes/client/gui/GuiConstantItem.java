package ds.mods.CPUPipes.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.tile.TileEntityConstantItem;
import ds.mods.CPUPipes.server.container.ContainerConstantItem;

public class GuiConstantItem extends GuiContainer {

	public GuiConstantItem(EntityPlayer player, TileEntityConstantItem tile) {
		super(new ContainerConstantItem(player, tile));
		CPUPipes.proxy.getWorldDir(player.worldObj);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.client.gui.GuiScreen#doesGuiPauseGame()
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.mc.renderEngine.bindTexture("/mods/CPUPipes/textures/gui/singleItem.png");
		int l = (this.width - this.xSize) / 2;
	    int i1 = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(l, i1, 0, 0, this.xSize, this.ySize);
	}

}
