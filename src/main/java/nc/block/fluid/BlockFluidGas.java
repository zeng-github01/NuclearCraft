package nc.block.fluid;

import nc.fluid.FluidGas;
import net.minecraft.block.material.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockFluidGas extends NCBlockFluid {
	
	private static final Material GAS = new MaterialLiquid(MapColor.AIR);
	
	public BlockFluidGas(FluidGas fluid) {
		super(fluid, GAS);
	}
	
	@Override
	public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (isSourceBlock(worldIn, pos)) {
			worldIn.setBlockToAir(pos);
		}
	}
	
	@Override
	protected boolean canMixWithFluids(World world, BlockPos pos, IBlockState state) {
		return false;
	}
	
	@Override
	protected boolean shouldMixWithAdjacentFluid(World world, BlockPos pos, IBlockState state, IBlockState otherState) {
		return false;
	}
	
	@Override
	protected IBlockState getSourceMixingState(World world, BlockPos pos, IBlockState state) {
		return Blocks.OBSIDIAN.getDefaultState();
	}
	
	@Override
	protected IBlockState getFlowingMixingState(World world, BlockPos pos, IBlockState state) {
		return Blocks.COBBLESTONE.getDefaultState();
	}
	
	@Override
	protected boolean canSetFireToSurroundings(World world, BlockPos pos, IBlockState state, Random rand) {
		return false;
	}
	
	@Override
	protected IBlockState getFlowingIntoWaterState(World world, BlockPos pos, IBlockState state, Random rand) {
		return null;
	}
}
