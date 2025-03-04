package nc.recipe.processor;

import com.google.common.collect.*;
import nc.init.NCItems;
import nc.util.*;
import net.minecraft.init.*;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

import static nc.config.NCConfig.*;

public class PressurizerRecipes extends BasicProcessorRecipeHandler {
	
	public PressurizerRecipes() {
		super("pressurizer", 1, 0, 1, 0);
	}
	
	@Override
	public void addRecipes() {
		if (!default_processor_recipes_global || !default_processor_recipes[11]) {
			return;
		}
		
		addRecipe("dustGraphite", "coal", 1D, 1D);
		addRecipe("ingotGraphite", "ingotPyrolyticCarbon", 1D, 1D);
		addRecipe("dustDiamond", "gemDiamond", 1D, 1D);
		addRecipe("dustRhodochrosite", "gemRhodochrosite", 1D, 1D);
		addRecipe(Lists.newArrayList("dustQuartz", "dustNetherQuartz"), "gemQuartz", 1D, 1D);
		addRecipe(oreStack("dustObsidian", 4), Blocks.OBSIDIAN, 1.5D, 1.5D);
		addRecipe("dustBoronNitride", "gemBoronNitride", 1D, 1D);
		addRecipe("dustFluorite", "gemFluorite", 1D, 1D);
		addRecipe("dustVilliaumite", "gemVilliaumite", 1D, 1D);
		addRecipe("dustCarobbiite", "gemCarobbiite", 1D, 1D);
		
		addRecipe("dustSteel", "sinteredSteel", 2D, 2D);
		addRecipe("dustZirconia", "sinteredZirconia", 2D, 2D);
		
		if (!OreDictHelper.oreExists("ingotMolybdenum")) {
			addRecipe(oreStack("dustMolybdenum", 9), "blockMolybdenum", 9D, 1D);
		}
		if (!OreDictHelper.oreExists("ingotCopperOxide")) {
			addRecipe(oreStack("dustCopperOxide", 9), "blockCopperOxide", 9D, 1D);
		}
		
		addRecipe(oreStackList(Lists.newArrayList("dustWheat", "foodFlour"), 2), NCItems.graham_cracker, 0.5D, 0.5D);
		
		// IC2
		addRecipe(oreStack("dustClay", 4), "dustSiliconDioxide", 1D, 1D);
		
		// Tech Reborn
		addRecipe(RegistryHelper.itemStackFromRegistry("techreborn:part:34"), RegistryHelper.itemStackFromRegistry("techreborn:plates:2"), 1D, 1D);
		
		// AE2
		addRecipe("dustEnder", Items.ENDER_PEARL, 1D, 1D);
		
		addPlatePressingRecipes();
	}
	
	private static final Set<String> PLATE_BLACKLIST = Sets.newHashSet("Graphite");
	
	public void addPlatePressingRecipes() {
		for (String ore : OreDictionary.getOreNames()) {
			if (ore.startsWith("plate")) {
				String type = ore.substring(5);
				if (PLATE_BLACKLIST.contains(type)) {
					continue;
				}
				String ingot = "ingot" + type, gem = "gem" + type;
				if (OreDictHelper.oreExists(ingot)) {
					addRecipe(ingot, ore, 1D, 1D);
				}
				else if (OreDictHelper.oreExists(gem)) {
					addRecipe(gem, ore, 1D, 1D);
				}
			}
			if (ore.startsWith("plateDense")) {
				String plate = "plate" + ore.substring(10);
				if (OreDictHelper.oreExists(plate)) {
					addRecipe(oreStack(plate, 9), ore, 2D, 2D);
				}
			}
		}
	}
}
