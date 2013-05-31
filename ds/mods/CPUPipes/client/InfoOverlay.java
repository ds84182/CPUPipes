package ds.mods.CPUPipes.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;
import ds.mods.CPUPipes.core.network.ILabelHolder;

public class InfoOverlay {
	@ForgeSubscribe
	public void renderOverlay(RenderGameOverlayEvent.Post event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		MovingObjectPosition mop = mc.objectMouseOver;
		if (mop != null)
		if (mop.sideHit != -1)
		{
			FontRenderer fr = mc.fontRenderer;
			World world = mc.theWorld;
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (tile != null)
			{
				int textX = (int) (event.resolution.getScaledWidth()/2D)+2;
				int textY = (int) (event.resolution.getScaledHeight()/2D)+2;
				if (tile instanceof ILabelHolder)
				{
					ILabelHolder lh = (ILabelHolder) tile;
					fr.drawStringWithShadow("Label: "+lh.getLabel(), textX, textY, 0xFFFFFF);
					textY+=fr.FONT_HEIGHT+1;
				}
			}
		}
		
	}
}
