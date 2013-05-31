package ds.mods.CPUPipes.server.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ds.mods.CPUPipes.core.tile.TileEntityConstantItem;

public class ContainerConstantItem extends Container {
	private TileEntityConstantItem tile;
	private InventoryPlayer inventory;

	public ContainerConstantItem(EntityPlayer player, TileEntityConstantItem tile)
	{
		this.tile = tile;
		inventory = player.inventory;
		IInventory par1IInventory = player.inventory;
		addSlotToContainer(new Slot(this.tile, 0, 80, 35));

	    for (int j = 0; j < 3; j++)
	    {
	      for (int i1 = 0; i1 < 9; i1++)
	      {
	        addSlotToContainer(new Slot(inventory, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
	      }
	    }

	    for (int k = 0; k < 9; k++)
	    {
	      addSlotToContainer(new Slot(inventory, k, 8 + k * 18, 142));
	    }
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i)
    {
		ItemStack itemstack = null;
	    Slot slot = (Slot)this.inventorySlots.get(i);
	    if ((slot != null) && (slot.getHasStack()))
	    {
	      ItemStack itemstack1 = slot.getStack();
	      itemstack = itemstack1.copy();
	      if (i == 0)
	      {
	        if (!mergeItemStack(itemstack1, 1, 37, true))
	        {
	          return null;
	        }
	      }
	      else if (!mergeItemStack(itemstack1, 0, 1, false))
	      {
	        return null;
	      }

	      if (itemstack1.stackSize == 0)
	      {
	        slot.putStack(null);
	      }
	      else
	      {
	        slot.onSlotChanged();
	      }

	      if (itemstack1.stackSize != itemstack.stackSize)
	      {
	        slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
	      }
	      else
	      {
	        return null;
	      }
	    }
	    return itemstack;
    }

}
