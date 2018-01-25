package nc.item.tool;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import nc.Global;
import nc.util.InfoHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NCAxe extends ItemTool {
	
	String[] info;
	
	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});
	private static final float[] ATTACK_DAMAGES = new float[] {6.0F, 8.0F, 8.0F, 8.0F, 6.0F};
	private static final float[] ATTACK_SPEEDS = new float[] {-3.2F, -3.2F, -3.1F, -3.0F, -3.0F};
	
	public NCAxe(String unlocalizedName, ToolMaterial material, String... tooltip) {
		super(material, EFFECTIVE_ON);
		setUnlocalizedName(Global.MOD_ID + "." + unlocalizedName);
		setRegistryName(new ResourceLocation(Global.MOD_ID, unlocalizedName));
		info = InfoHelper.buildInfo(getUnlocalizedName(), tooltip);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getDestroySpeed(stack, state) : this.efficiency;
    }
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		stack.damageItem(1, attacker);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(itemStack, world, tooltip, flag);
        if (info.length > 0) InfoHelper.infoFull(tooltip, info);
    }
}
