package nc.config;

import nc.util.Lang;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.client.config.*;

import java.util.List;

public interface IConfigCategory {
	
	default GuiScreen buildChildScreen(String categoryName, GuiConfig owningScreen, IConfigElement configElement) {
		Configuration config = NCConfig.getConfig();
		ConfigElement newElement = new ConfigElement(config.getCategory(categoryName));
		List<IConfigElement> propertiesOnScreen = newElement.getChildElements();
		String windowTitle = Lang.localize("gui.nc.config.category." + categoryName);
		return new GuiConfig(owningScreen, propertiesOnScreen, owningScreen.modID, configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart, windowTitle);
	}
}
