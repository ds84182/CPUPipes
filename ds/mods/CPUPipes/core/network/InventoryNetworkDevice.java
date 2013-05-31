package ds.mods.CPUPipes.core.network;

import net.minecraft.inventory.IInventory;
import ds.mods.CPUPipes.core.tile.TileEntityInventoryConnector;

public class InventoryNetworkDevice implements INetworkDevice {

			public int x;
			public int y;
			public int z;
			public int id;
			public IInventory inv;
			public Network net;
			public TileEntityInventoryConnector tile;

			public InventoryNetworkDevice(int x, int y, int z, IInventory inventory, Network network, TileEntityInventoryConnector t) {
						this.x = x;
						this.y = y;
						this.z = z;
						inv = inventory;
						net = network;
						tile = t;
						id = net.getNextID("Inv");
			}

			@Override
			public int getX() {
						return x;
			}

			@Override
			public int getY() {
						return y;
			}

			@Override
			public int getZ() {
						return z;
			}

			@Override
			public String getType() {
						return "Inventory";
			}

			@Override
			public int getID() {
						return id;
			}

			@Override
			public Object call(int method, Object... args) {
						return inv;
			}

			@Override
			public void setID(int id) {
						this.id = id;
			}

			@Override
			public String getLabel() {
						return tile.getLabel();
			}
}
