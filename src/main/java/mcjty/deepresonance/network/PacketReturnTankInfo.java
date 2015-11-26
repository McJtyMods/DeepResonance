package mcjty.deepresonance.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import net.minecraft.nbt.NBTTagCompound;

public class PacketReturnTankInfo implements IMessage {
    private int amount;
    private int capacity;
    private String fluidName;
    private NBTTagCompound tag;

    @Override
    public void fromBytes(ByteBuf buf) {
        amount = buf.readInt();
        capacity = buf.readInt();
        fluidName = NetworkTools.readString(buf);
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(amount);
        buf.writeInt(capacity);
        NetworkTools.writeString(buf, fluidName);
        ByteBufUtils.writeTag(buf, tag);
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public String getFluidName() {
        return fluidName;
    }

    public int getAmount() {
        return amount;
    }

    public int getCapacity() {
        return capacity;
    }

    public PacketReturnTankInfo() {
    }

    public PacketReturnTankInfo(int amount, int capacity, String fluidName, NBTTagCompound tag) {
        this.amount = amount;
        this.capacity = capacity;
        this.fluidName = fluidName;
        this.tag = tag;
    }
}