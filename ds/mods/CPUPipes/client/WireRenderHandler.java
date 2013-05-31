package ds.mods.CPUPipes.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ds.mods.CPUPipes.CPUPipes;
import ds.mods.CPUPipes.core.network.INetworkDeviceHost;
import ds.mods.CPUPipes.core.network.INetworkWire;

public class WireRenderHandler implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks render) {
		block.setBlockBounds(0.5F-(3/32F), 0.5F-(3/32F), 0.5F-(3/32F), 0.5F+(3/32F), 0.5F+(3/32F), 0.5F+(3/32F));
		render.setRenderBoundsFromBlock(block);
		Icon icon = block.getIcon(0,0);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		render.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		render.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		render.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		render.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		render.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		render.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		block.setBlockBounds(0.5F-(3/32F), 0.5F-(3/32F), 0.5F-(3/32F), 0.5F+(3/32F), 0.5F+(3/32F), 0.5F+(3/32F));
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity tile = world.getBlockTileEntity(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if (tile instanceof INetworkWire || (tile instanceof INetworkDeviceHost && ((INetworkDeviceHost)tile).canConnect(dir.getOpposite())))
			{
				switch (dir)
				{
				case DOWN:
					block.setBlockBounds(0.5F-(3/32F),0F,0.5F-(3/32F),0.5F+(3/32F),0.5F-(3/32F),0.5F+(3/32F));
					break;
				case EAST:
					block.setBlockBounds(0.5F+(3/32F),0.5F-(3/32F),0.5F-(3/32F),1F,0.5F+(3/32F),0.5F+(3/32F));
					break;
				case NORTH:
					block.setBlockBounds(0.5F-(3/32F),0.5F-(3/32F),0F,0.5F+(3/32F),0.5F+(3/32F),0.5F-(3/32F));
					break;
				case SOUTH:
					block.setBlockBounds(0.5F-(3/32F),0.5F-(3/32F),0.5F+(3/32F),0.5F+(3/32F),0.5F+(3/32F),1F);
					break;
				case UNKNOWN:
					break;
				case UP:
					block.setBlockBounds(0.5F-(3/32F),0.5F+(3/32F),0.5F-(3/32F),0.5F+(3/32F),1F,0.5F+(3/32F));
					break;
				case WEST:
					block.setBlockBounds(0F,0.5F-(3/32F),0.5F-(3/32F),0.5F-(3/32F),0.5F+(3/32F),0.5F+(3/32F));
					break;
				default:
					break;
				
				}
				 //East
				renderer.setRenderBoundsFromBlock(block);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}
		block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return CPUPipes.proxy.wireModel;
	}

}
