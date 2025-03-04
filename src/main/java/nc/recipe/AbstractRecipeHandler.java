package nc.recipe;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import nc.recipe.ingredient.*;
import nc.tile.internal.fluid.Tank;
import nc.util.*;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.annotations.*;

import javax.annotation.*;
import java.util.*;

@ZenClass("mods.nuclearcraft.AbstractRecipeHandler")
@ZenRegister
public abstract class AbstractRecipeHandler<RECIPE extends IRecipe> {
	
	protected @Nonnull List<RECIPE> recipeList = new ArrayList<>();
	
	protected @Nonnull Long2ObjectMap<ObjectSet<RECIPE>> recipeCache = new Long2ObjectOpenHashMap<>();
	
	protected static final List<Class<?>> validItemInputs = Lists.newArrayList(IItemIngredient.class, ArrayList.class, String.class, Item.class, Block.class, ItemStack.class, ItemStack[].class);
	protected static final List<Class<?>> validFluidInputs = Lists.newArrayList(IFluidIngredient.class, ArrayList.class, String.class, Fluid.class, FluidStack.class, FluidStack[].class);
	protected static final List<Class<?>> validItemOutputs = Lists.newArrayList(IItemIngredient.class, String.class, Item.class, Block.class, ItemStack.class);
	protected static final List<Class<?>> validFluidOutputs = Lists.newArrayList(IFluidIngredient.class, String.class, Fluid.class, FluidStack.class);
	
	protected static final List<Class<?>> needItemFixing = Lists.newArrayList(Item.class, Block.class);
	protected static final List<Class<?>> needFluidFixing = Lists.newArrayList(Fluid.class);
	
	public static final IntList INVALID = new IntArrayList(new int[] {-1});
	
	public abstract void addRecipes();
	
	@ZenMethod
	public abstract String getName();
	
	@ZenMethod
	public abstract List<RECIPE> getRecipeList();
	
	public abstract void addRecipe(Object... objects);
	
	public @Nullable RecipeInfo<RECIPE> getRecipeInfoFromInputs(List<ItemStack> itemInputs, List<Tank> fluidInputs) {
		long hash = RecipeHelper.hashMaterialsRaw(itemInputs, fluidInputs);
		if (recipeCache.containsKey(hash)) {
			ObjectSet<RECIPE> set = recipeCache.get(hash);
			for (RECIPE recipe : set) {
				if (recipe != null) {
					RecipeMatchResult matchResult = recipe.matchInputs(itemInputs, fluidInputs);
					if (matchResult.isMatch) {
						return new RecipeInfo<>(recipe, matchResult);
					}
				}
			}
		}
		return null;
	}
	
	public @Nullable RECIPE getRecipeFromIngredients(List<IItemIngredient> itemIngredients, List<IFluidIngredient> fluidIngredients) {
		for (RECIPE recipe : recipeList) {
			if (recipe.matchIngredients(itemIngredients, fluidIngredients).isMatch) {
				return recipe;
			}
		}
		return null;
	}
	
	public @Nullable RECIPE getRecipeFromProducts(List<IItemIngredient> itemProducts, List<IFluidIngredient> fluidProducts) {
		for (RECIPE recipe : recipeList) {
			if (recipe.matchProducts(itemProducts, fluidProducts).isMatch) {
				return recipe;
			}
		}
		return null;
	}
	
	public abstract boolean isValidRecipe(RECIPE recipe);
	
	public @Nullable Boolean addRecipe(RECIPE recipe) {
		if (recipe == null || !isValidRecipe(recipe)) {
			return null;
		}
		else {
			return recipeList.add(recipe);
		}
	}
	
	public boolean removeRecipe(RECIPE recipe) {
		return recipe != null && recipeList.remove(recipe);
	}
	
	public void removeAllRecipes() {
		recipeList.clear();
		recipeCache.clear();
	}
	
	protected void initRecipeIngredients() {
		for (RECIPE recipe : recipeList) {
			recipe.getItemIngredients().forEach(IItemIngredient::init);
			recipe.getFluidIngredients().forEach(IFluidIngredient::init);
			recipe.getItemProducts().forEach(IItemIngredient::init);
			recipe.getFluidProducts().forEach(IFluidIngredient::init);
		}
	}
	
	public void init() {
	
	}
	
	public void postInit() {
		initRecipeIngredients();
	}
	
	public void preReload() {
	
	}
	
	public void postReload() {
		postInit();
		refreshCache();
	}
	
	public void refreshCache() {
		recipeCache.clear();
		fillHashCache();
	}
	
	protected abstract void fillHashCache();
	
	protected boolean prepareMaterialListTuples(RECIPE recipe, List<Pair<List<ItemStack>, List<FluidStack>>> materialListTuples) {
		List<List<ItemStack>> itemInputLists = StreamHelper.map(recipe.getItemIngredients(), IItemIngredient::getInputStackHashingList);
		List<List<FluidStack>> fluidInputLists = StreamHelper.map(recipe.getFluidIngredients(), IFluidIngredient::getInputStackHashingList);
		
		int itemInputCount = itemInputLists.size(), fluidInputCount = fluidInputLists.size(), totalInputCount = itemInputCount + fluidInputCount;
		int[] inputNumbers = new int[totalInputCount];
		
		int[] maxNumbers = new int[totalInputCount];
		for (int i = 0; i < itemInputCount; ++i) {
			int maxNumber = itemInputLists.get(i).size() - 1;
			if (maxNumber < 0) {
				return false;
			}
			maxNumbers[i] = maxNumber;
		}
		for (int i = 0; i < fluidInputCount; ++i) {
			int maxNumber = fluidInputLists.get(i).size() - 1;
			if (maxNumber < 0) {
				return false;
			}
			maxNumbers[i + itemInputCount] = maxNumber;
		}
		
		RecipeTupleGenerator.INSTANCE.generateMaterialListTuples(materialListTuples, maxNumbers, inputNumbers, itemInputLists, fluidInputLists, false);
		
		return true;
	}
	
	public static void addValidItemInput(Class<?> itemInputType) {
		validItemInputs.add(itemInputType);
	}
	
	public static void addValidFluidInput(Class<?> fluidInputType) {
		validFluidInputs.add(fluidInputType);
	}
	
	public static void addValidItemOutput(Class<?> itemOutputType) {
		validItemOutputs.add(itemOutputType);
	}
	
	public static void addValidFluidOutput(Class<?> fluidOutputType) {
		validFluidOutputs.add(fluidOutputType);
	}
	
	public static boolean isValidItemInputType(Object itemInput) {
		for (Class<?> itemInputType : validItemInputs) {
			if (itemInput instanceof ArrayList<?> list && itemInputType == ArrayList.class) {
				for (Object obj : list) {
					if (isValidItemInputType(obj)) {
						return true;
					}
				}
			}
			else if (itemInputType.isInstance(itemInput)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidFluidInputType(Object fluidInput) {
		for (Class<?> fluidInputType : validFluidInputs) {
			if (fluidInput instanceof ArrayList<?> list && fluidInputType == ArrayList.class) {
				for (Object obj : list) {
					if (isValidFluidInputType(obj)) {
						return true;
					}
				}
			}
			else if (fluidInputType.isInstance(fluidInput)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidItemOutputType(Object itemOutput) {
		for (Class<?> itemOutputType : validItemOutputs) {
			if (itemOutputType.isInstance(itemOutput)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidFluidOutputType(Object fluidOutput) {
		for (Class<?> fluidOutputType : validFluidOutputs) {
			if (fluidOutputType.isInstance(fluidOutput)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean requiresItemFixing(Object object) {
		for (Class<?> objectType : needItemFixing) {
			if (objectType.isInstance(object)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean requiresFluidFixing(Object object) {
		for (Class<?> objectType : needFluidFixing) {
			if (objectType.isInstance(object)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidItemInput(ItemStack stack) {
		for (RECIPE recipe : recipeList) {
			for (IItemIngredient input : recipe.getItemIngredients()) {
				if (input.match(stack, IngredientSorption.NEUTRAL).matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isValidFluidInput(FluidStack stack) {
		for (RECIPE recipe : recipeList) {
			for (IFluidIngredient input : recipe.getFluidIngredients()) {
				if (input.match(stack, IngredientSorption.NEUTRAL).matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isValidItemOutput(ItemStack stack) {
		for (RECIPE recipe : recipeList) {
			for (IItemIngredient output : recipe.getItemProducts()) {
				if (output.match(stack, IngredientSorption.OUTPUT).matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isValidFluidOutput(FluidStack stack) {
		for (RECIPE recipe : recipeList) {
			for (IFluidIngredient output : recipe.getFluidProducts()) {
				if (output.match(stack, IngredientSorption.OUTPUT).matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Recipe Ingredients
	
	public static OreIngredient oreStack(String oreType, int stackSize) {
		if (!OreDictHelper.oreExists(oreType)) {
			return null;
		}
		return new OreIngredient(oreType, stackSize);
	}
	
	public static FluidIngredient fluidStack(String fluidName, int stackSize) {
		if (!FluidRegHelper.fluidExists(fluidName)) {
			return null;
		}
		return new FluidIngredient(fluidName, stackSize);
	}
	
	public static List<OreIngredient> oreStackList(List<String> oreTypes, int stackSize) {
		List<OreIngredient> oreStackList = new ArrayList<>();
		for (String oreType : oreTypes) {
			OreIngredient oreIngredient = oreStack(oreType, stackSize);
			if (oreIngredient != null) {
				oreStackList.add(oreIngredient);
			}
		}
		return oreStackList;
	}
	
	public static List<FluidIngredient> fluidStackList(List<String> fluidNames, int stackSize) {
		List<FluidIngredient> fluidStackList = new ArrayList<>();
		for (String fluidName : fluidNames) {
			FluidIngredient fluidIngredient = fluidStack(fluidName, stackSize);
			if (fluidIngredient != null) {
				fluidStackList.add(fluidIngredient);
			}
		}
		return fluidStackList;
	}
	
	public static EmptyItemIngredient emptyItemStack() {
		return new EmptyItemIngredient();
	}
	
	public static EmptyFluidIngredient emptyFluidStack() {
		return new EmptyFluidIngredient();
	}
	
	public static ChanceItemIngredient chanceItemStack(ItemStack stack, int chancePercent) {
		if (stack == null) {
			return null;
		}
		return new ChanceItemIngredient(new ItemIngredient(stack), chancePercent);
	}
	
	public static ChanceItemIngredient chanceItemStack(ItemStack stack, int chancePercent, int minStackSize) {
		if (stack == null) {
			return null;
		}
		return new ChanceItemIngredient(new ItemIngredient(stack), chancePercent, minStackSize);
	}
	
	public static ChanceItemIngredient chanceOreStack(String oreType, int stackSize, int chancePercent) {
		if (!OreDictHelper.oreExists(oreType)) {
			return null;
		}
		return new ChanceItemIngredient(oreStack(oreType, stackSize), chancePercent);
	}
	
	public static ChanceItemIngredient chanceOreStack(String oreType, int stackSize, int chancePercent, int minStackSize) {
		if (!OreDictHelper.oreExists(oreType)) {
			return null;
		}
		return new ChanceItemIngredient(oreStack(oreType, stackSize), chancePercent, minStackSize);
	}
	
	public static ChanceFluidIngredient chanceFluidStack(String fluidName, int stackSize, int chancePercent, int stackDiff) {
		if (!FluidRegHelper.fluidExists(fluidName)) {
			return null;
		}
		return new ChanceFluidIngredient(fluidStack(fluidName, stackSize), chancePercent, stackDiff);
	}
	
	public static ChanceFluidIngredient chanceFluidStack(String fluidName, int stackSize, int chancePercent, int stackDiff, int minStackSize) {
		if (!FluidRegHelper.fluidExists(fluidName)) {
			return null;
		}
		return new ChanceFluidIngredient(fluidStack(fluidName, stackSize), chancePercent, stackDiff, minStackSize);
	}
	
	public static List<ChanceItemIngredient> chanceOreStackList(List<String> oreTypes, int stackSize, int chancePercent) {
		List<ChanceItemIngredient> oreStackList = new ArrayList<>();
		for (String oreType : oreTypes) {
			ChanceItemIngredient chanceItemIngredient = chanceOreStack(oreType, stackSize, chancePercent);
			if (chanceItemIngredient != null) {
				oreStackList.add(chanceItemIngredient);
			}
		}
		return oreStackList;
	}
	
	public static List<ChanceItemIngredient> chanceOreStackList(List<String> oreTypes, int stackSize, int chancePercent, int minStackSize) {
		List<ChanceItemIngredient> oreStackList = new ArrayList<>();
		for (String oreType : oreTypes) {
			ChanceItemIngredient chanceItemIngredient = chanceOreStack(oreType, stackSize, chancePercent, minStackSize);
			if (chanceItemIngredient != null) {
				oreStackList.add(chanceItemIngredient);
			}
		}
		return oreStackList;
	}
	
	public static List<ChanceFluidIngredient> chanceFluidStackList(List<String> fluidNames, int stackSize, int chancePercent, int stackDiff) {
		List<ChanceFluidIngredient> fluidStackList = new ArrayList<>();
		for (String fluidName : fluidNames) {
			ChanceFluidIngredient chanceFluidIngredient = chanceFluidStack(fluidName, stackSize, chancePercent, stackDiff);
			if (chanceFluidIngredient != null) {
				fluidStackList.add(chanceFluidIngredient);
			}
		}
		return fluidStackList;
	}
	
	public static List<ChanceFluidIngredient> chanceFluidStackList(List<String> fluidNames, int stackSize, int chancePercent, int stackDiff, int minStackSize) {
		List<ChanceFluidIngredient> fluidStackList = new ArrayList<>();
		for (String fluidName : fluidNames) {
			ChanceFluidIngredient chanceFluidIngredient = chanceFluidStack(fluidName, stackSize, chancePercent, stackDiff, minStackSize);
			if (chanceFluidIngredient != null) {
				fluidStackList.add(chanceFluidIngredient);
			}
		}
		return fluidStackList;
	}
}
