package nc.recipe.ingredient;

import com.google.common.collect.Lists;
import crafttweaker.api.item.IngredientOr;
import it.unimi.dsi.fastutil.ints.*;
import nc.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import java.util.*;

public class ItemArrayIngredient implements IItemIngredient {
	
	public List<IItemIngredient> ingredientList;
	public List<ItemStack> cachedStackList = new ArrayList<>();
	
	public ItemArrayIngredient(IItemIngredient... ingredients) {
		this(Lists.newArrayList(ingredients));
	}
	
	public ItemArrayIngredient(List<IItemIngredient> ingredientList) {
		this.ingredientList = ingredientList;
		ingredientList.forEach(input -> cachedStackList.add(input.getStack()));
	}
	
	@Override
	public ItemStack getStack() {
		return isValid() ? cachedStackList.get(0).copy() : null;
	}
	
	@Override
	public List<ItemStack> getInputStackList() {
		List<ItemStack> stacks = new ArrayList<>();
		ingredientList.forEach(ingredient -> stacks.addAll(ingredient.getInputStackList()));
		return stacks;
	}
	
	@Override
	public List<ItemStack> getOutputStackList() {
		return isValid() ? Lists.newArrayList(getStack()) : new ArrayList<>();
	}
	
	@Override
	public int getMaxStackSize(int ingredientNumber) {
		return ingredientList.get(ingredientNumber).getMaxStackSize(0);
	}
	
	@Override
	public void setMaxStackSize(int stackSize) {
		for (IItemIngredient ingredient : ingredientList) {
			ingredient.setMaxStackSize(stackSize);
		}
		for (ItemStack stack : cachedStackList) {
			stack.setCount(stackSize);
		}
	}
	
	@Override
	public String getIngredientName() {
		// return ingredientList.get(0).getIngredientName();
		return getIngredientNamesConcat();
	}
	
	@Override
	public String getIngredientNamesConcat() {
		StringBuilder names = new StringBuilder();
		for (IItemIngredient ingredient : ingredientList) {
			names.append(", ").append(ingredient.getIngredientName());
		}
		return "{ " + names.substring(2) + " }";
	}
	
	public String getIngredientRecipeString() {
		StringBuilder names = new StringBuilder();
		for (IItemIngredient ingredient : ingredientList) {
			names.append(", ").append(ingredient.getMaxStackSize(0)).append(" x ").append(ingredient.getIngredientName());
		}
		return "{ " + names.substring(2) + " }";
	}
	
	@Override
	public IntList getFactors() {
		IntList list = new IntArrayList();
		for (IItemIngredient ingredient : ingredientList) {
			list.addAll(ingredient.getFactors());
		}
		return new IntArrayList(list);
	}
	
	@Override
	public IItemIngredient getFactoredIngredient(int factor) {
		List<IItemIngredient> list = new ArrayList<>();
		for (IItemIngredient ingredient : ingredientList) {
			list.add(ingredient.getFactoredIngredient(factor));
		}
		return new ItemArrayIngredient(list);
	}
	
	@Override
	public IngredientMatchResult match(Object object, IngredientSorption sorption) {
		if (object instanceof ItemArrayIngredient arrayIngredient) {
			loop:
			for (IItemIngredient ingredient : ingredientList) {
				for (IItemIngredient ingr : arrayIngredient.ingredientList) {
					if (ingredient.match(ingr, sorption).matches()) {
						continue loop;
					}
				}
				return IngredientMatchResult.FAIL;
			}
			return IngredientMatchResult.PASS_0;
		}
		else {
			for (int i = 0; i < ingredientList.size(); ++i) {
				if (ingredientList.get(i).match(object, sorption).matches()) {
					return new IngredientMatchResult(true, i);
				}
			}
		}
		return IngredientMatchResult.FAIL;
	}
	
	@Override
	public boolean isValid() {
		return cachedStackList != null && !cachedStackList.isEmpty() && cachedStackList.get(0) != null;
	}
	
	// CraftTweaker
	
	@Override
	@Optional.Method(modid = "crafttweaker")
	public crafttweaker.api.item.IIngredient ct() {
		crafttweaker.api.item.IIngredient[] array = new crafttweaker.api.item.IIngredient[ingredientList.size()];
		for (int i = 0; i < ingredientList.size(); ++i) {
			array[i] = ingredientList.get(i).ct();
		}
		return new IngredientOr(array);
	}
}
