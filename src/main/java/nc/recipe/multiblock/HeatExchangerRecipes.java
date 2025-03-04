package nc.recipe.multiblock;

import nc.recipe.BasicRecipeHandler;

import java.util.*;

import static nc.config.NCConfig.*;
import static nc.init.NCCoolantFluids.COOLANTS;

public class HeatExchangerRecipes extends BasicRecipeHandler {
	
	public HeatExchangerRecipes() {
		super("heat_exchanger", 0, 1, 0, 1);
	}
	
	@Override
	public void addRecipes() {
		// Hot NaK -> NaK
		
		addRecipe(fluidStack("nak_hot", 1), fluidStack("nak", 1), fission_heater_cooling_rate[0] * heat_exchanger_coolant_mult, 700, 300);
		for (int i = 1; i < COOLANTS.size(); ++i) {
			addRecipe(fluidStack(COOLANTS.get(i) + "_nak_hot", 1), fluidStack(COOLANTS.get(i) + "_nak", 1), fission_heater_cooling_rate[i] * heat_exchanger_coolant_mult, 700, 300);
		}
		
		// Steam <-> Water
		
		addRecipe(fluidStack("water", 1), fluidStack("high_pressure_steam", 4), 128D, 300, 1200);
		addRecipe(fluidStack("preheated_water", 1), fluidStack("high_pressure_steam", 4), 64D, 400, 1200);
		
		if (!heat_exchanger_alternate_exhaust_recipe) {
			addRecipe(fluidStack("exhaust_steam", 1), fluidStack("low_pressure_steam", 1), 4D, 500, 800);
		}
		else {
			addRecipe(fluidStack("exhaust_steam", 1), fluidStack("steam", 1), 4D, 500, 800);
		}
		
		addRecipe(fluidStack("high_pressure_steam", 1), fluidStack("steam", 4), 16D, 1200, 800);
		
		addRecipe(fluidStack("condensate_water", 1), fluidStack("preheated_water", 1), 32D, 300, 400);
	}
	
	@Override
	protected List<Object> fixedExtras(List<Object> extras) {
		ExtrasFixer fixer = new ExtrasFixer(extras);
		fixer.add(Double.class, 16000D);
		fixer.add(Integer.class, 300);
		fixer.add(Integer.class, 300);
		return fixer.fixed;
	}
	
	@Override
	public List<Object> getFactoredExtras(List<Object> extras, int factor) {
		List<Object> factored = new ArrayList<>(extras);
		factored.set(0, (double) extras.get(0) / factor);
		return factored;
	}
}
