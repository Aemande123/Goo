package com.xeno.goo.fluids;

import com.xeno.goo.entities.GooEntity;
import com.xeno.goo.setup.Registry;
import com.xeno.goo.setup.Resources;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public abstract class FungalGoo extends GooBase
{
    private static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            Registry.FUNGAL_GOO, Registry.FUNGAL_GOO_FLOWING,
            FluidAttributes.builder(
                    Resources.GooTextures.Still.FUNGAL_GOO,
                    Resources.GooTextures.Flowing.FUNGAL_GOO)
                    .translationKey("fluid.goo.fungal_goo"))
            .bucket(() -> Items.AIR)
            .block(Registry.FUNGAL_GOO_BLOCK)
            .slopeFindDistance(3)
            .levelDecreasePerBlock(1)
            .explosionResistance(100f);

    public FungalGoo() {
        super(PROPERTIES);
    }

    @Override
    public void doEffect(ServerWorld world, ServerPlayerEntity player, GooEntity goo, Entity entityHit, BlockPos pos) { }


    @Override
    public GooEntity createEntity(World world, LivingEntity sender, FluidStack goo, Hand isHeld)
    {
        return null;
    }

    @Override
    public int decayRate()
    {
        return 1;
    }

    @Override
    public Fluid getStillFluid() { return Registry.FUNGAL_GOO.get(); }

    @Override
    public Fluid getFlowingFluid() { return Registry.FUNGAL_GOO_FLOWING.get(); }

    @Override
    protected BlockState getBlockState(FluidState state)
    {
        return Registry.FUNGAL_GOO_BLOCK.get().getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
    }

    public static class Flowing extends FungalGoo {

        public Flowing()
        {
            super();
        }

        @Override
        public boolean isSource(FluidState state) { return true; }

        @Override
        public int getLevel(FluidState state) { return state.get(FlowingFluidBlock.LEVEL); }
    }

    public static class Source extends FungalGoo {

        public Source()
        {
            super();
        }

        @Override
        public boolean isSource(FluidState state) { return false; }

        @Override
        public int getLevel(FluidState state) { return state.get(FlowingFluidBlock.LEVEL); }
    }
}
