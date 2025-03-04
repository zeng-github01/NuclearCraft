package nc.recipe.ingredient;

import com.google.common.collect.Lists;
import crafttweaker.api.minecraft.CraftTweakerMC;
import it.unimi.dsi.fastutil.ints.*;
import nc.recipe.*;
import nc.tile.internal.fluid.Tank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import java.util.*;

public class EmptyFluidIngredient implements IFluidIngredient {
	
	public EmptyFluidIngredient() {}
	
	@Override
	public FluidStack getStack() {
		return null;
	}
	
	@Override
	public List<FluidStack> getInputStackList() {
		return Collections.emptyList();
	}
	
	@Override
	public List<FluidStack> getInputStackHashingList() {
		return Lists.newArrayList((FluidStack) null);
	}
	
	@Override
	public List<FluidStack> getOutputStackList() {
		return Collections.emptyList();
	}
	
	@Override
	public int getMaxStackSize(int ingredientNumber) {
		return 0;
	}
	
	@Override
	public void setMaxStackSize(int stackSize) {
	
	}
	
	@Override
	public String getIngredientName() {
		return "null";
	}
	
	@Override
	public String getIngredientNamesConcat() {
		return "null";
	}
	
	@Override
	public IntList getFactors() {
		return new IntArrayList();
	}
	
	@Override
	public IFluidIngredient getFactoredIngredient(int factor) {
		return new EmptyFluidIngredient();
	}
	
	@Override
	public IngredientMatchResult match(Object object, IngredientSorption sorption) {
		if (object == null) {
			return IngredientMatchResult.PASS_0;
		}
		if (object instanceof Tank tank) {
			return new IngredientMatchResult(tank.getFluid() == null, 0);
		}
		return new IngredientMatchResult(object instanceof EmptyFluidIngredient, 0);
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	// CraftTweaker
	
	@Override
	@Optional.Method(modid = "crafttweaker")
	public crafttweaker.api.item.IIngredient ct() {
		return CraftTweakerMC.getILiquidStack(null);
	}
}
