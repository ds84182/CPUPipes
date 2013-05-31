package ds.mods.CPUPipes.client;

import java.io.File;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ds.mods.CPUPipes.core.CommonProxy;

public class ClientProxy extends CommonProxy {
	public void registerRenders()
	{
		this.wireModel = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(wireModel, new WireRenderHandler());
		MinecraftForge.EVENT_BUS.register(new InfoOverlay());
	}
	
	public File getWorldDir(World w)
	{
		return new File(MinecraftServer.getServer().getFile("."),"saves/"+w.getSaveHandler().getWorldDirectoryName());
	}
}
