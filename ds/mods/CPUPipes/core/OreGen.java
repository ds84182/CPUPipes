package ds.mods.CPUPipes.core;

import ds.mods.CPUPipes.CPUPipes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.OreGenEvent;

public class OreGen {
	@ForgeSubscribe
	public void generateOres(OreGenEvent.Pre event)
	{
		WorldGenMinable grayDustOreGen = new WorldGenMinable(CPUPipes.greyDustOreID, 16);
		for (int l = 0; l < event.rand.nextInt(15)+1; l++)
        {
            int i1 = event.worldX + event.rand.nextInt(16);
            int j1 = event.rand.nextInt(48);
            int k1 = event.worldZ + event.rand.nextInt(16);
            grayDustOreGen.generate(event.world, event.rand, i1, j1, k1);
        }
	}
}
