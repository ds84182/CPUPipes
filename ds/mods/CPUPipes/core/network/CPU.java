package ds.mods.CPUPipes.core.network;

import net.minecraft.item.ItemStack;
import ds.mods.CPUPipes.core.tile.TileEntityCPU;

public class CPU {

	public TileEntityCPU tile;
	public boolean started;
	public int ticks;

	public CPU(TileEntityCPU tile) {
		this.tile = tile;
	}

	public void tick() {
		ticks++;
	}

	public void start() {
		started = true;
	}

	public boolean hasStarted() {
		return started;
	}

	public void stop() {
		started = false;
	}
}
