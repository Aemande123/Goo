package com.xeno.goo.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeSolidifierTargetPacket implements IGooModPacket
{
    private RegistryKey<World> worldRegistryKey;
    private BlockPos pos;
    private ItemStack target;
    private ItemStack newTarget;
    private int changeTargetTimer;

    public ChangeSolidifierTargetPacket(PacketBuffer buf) {
        read(buf);
    }

    @Override
    public void read(PacketBuffer buf)
    {
        worldRegistryKey = RegistryKey.func_240903_a_(Registry.WORLD_KEY, buf.readResourceLocation());
        pos = buf.readBlockPos();
        target = buf.readItemStack();
        newTarget = buf.readItemStack();
        changeTargetTimer = buf.readInt();
    }

    public ChangeSolidifierTargetPacket(RegistryKey<World> registryKey, BlockPos pos, ItemStack target, ItemStack newTarget, int changeTargetTimer) {
        this.worldRegistryKey = registryKey;
        this.pos = pos;
        this.target = target;
        this.newTarget = newTarget;
        this.changeTargetTimer = changeTargetTimer;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(worldRegistryKey.func_240901_a_());
        buf.writeBlockPos(pos);
        buf.writeItemStack(target);
        buf.writeItemStack(newTarget);
        buf.writeInt(changeTargetTimer);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (supplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                if (Minecraft.getInstance().world == null) {
                    return;
                }
                if (Minecraft.getInstance().world.func_234923_W_() != worldRegistryKey) {
                    return;
                }
                TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
                if (te instanceof ChangeSolidifierTargetPacket.IChangeSolidifierTargetReceiver) {
                    ((ChangeSolidifierTargetPacket.IChangeSolidifierTargetReceiver) te).updateSolidifierTarget(target, newTarget, changeTargetTimer);
                }
            }
        });

        supplier.get().setPacketHandled(true);
    }

    public interface IChangeSolidifierTargetReceiver {

        /**
         * @param target the actual target of the solidifier, whatever it is currently or was before the change event
         * @param newTarget the target we'll change to if the change is confirmed within the time limit
         * @param changeTargetTimer the time left to confirm change, this is actually not important, just needs nonzero
         */
        void updateSolidifierTarget(ItemStack target, ItemStack newTarget, int changeTargetTimer);
    }
}
