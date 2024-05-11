package nc.block.fission;

import nc.block.tile.IActivatable;
import nc.enumm.MetaEnums;
import nc.tile.fission.TileFissionShield;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

import static nc.block.property.BlockProperties.ACTIVE;

public class BlockFissionMetaShield extends BlockFissionMetaPart<MetaEnums.NeutronShieldType> implements IActivatable {
	
	public final static PropertyEnum<MetaEnums.NeutronShieldType> TYPE = PropertyEnum.create("type", MetaEnums.NeutronShieldType.class);
	
	public BlockFissionMetaShield() {
		super(MetaEnums.NeutronShieldType.class, TYPE);
		setDefaultState(getDefaultState().withProperty(ACTIVE, Boolean.FALSE));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, ACTIVE);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileFissionShield shield) {
			return state.withProperty(ACTIVE, shield.isShielding);
		}
		return state;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return switch (metadata) {
			case 0 -> new TileFissionShield.BoronSilver();
			default -> new TileFissionShield.BoronSilver();
		};
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getStateFromMeta(meta);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileFissionShield shield) {
			world.setBlockState(pos, state.withProperty(ACTIVE, shield.isShielding), 2);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND || player.isSneaking()) {
			return false;
		}
		return rightClickOnPart(world, pos, player, hand, facing);
	}
	
	// Rendering
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState otherState = world.getBlockState(pos.offset(side));
		Block block = otherState.getBlock();
		
		if (state != otherState) {
			return true;
		}
		
		return block != this && super.shouldSideBeRendered(state, world, pos, side);
	}
}
