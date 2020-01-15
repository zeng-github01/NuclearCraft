package nc.container.processor;

import nc.container.slot.SlotProcessorInput;
import nc.container.slot.SlotSpecificInput;
import nc.recipe.NCRecipes;
import nc.tile.processor.TileItemFluidProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMelter extends ContainerItemFluidProcessor {

	public ContainerMelter(EntityPlayer player, TileItemFluidProcessor tileEntity) {
		super(player, tileEntity, NCRecipes.melter);
		
		addSlotToContainer(new SlotProcessorInput(tileEntity, recipeHandler, 0, 56, 35));
		
		addSlotToContainer(new SlotSpecificInput(tileEntity, 1, 132, 64, speedUpgrade));
		addSlotToContainer(new SlotSpecificInput(tileEntity, 2, 152, 64, energyUpgrade));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + 9*i + 9, 8 + 18*j, 84 + 18*i));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + 18*i, 142));
		}
	}
}
