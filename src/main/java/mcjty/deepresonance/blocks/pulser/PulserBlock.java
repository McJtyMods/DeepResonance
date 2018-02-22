package mcjty.deepresonance.blocks.pulser;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;
import mcjty.lib.container.EmptyContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class PulserBlock extends GenericDRBlock<PulserTileEntity, EmptyContainer> {

    public PulserBlock() {
        super(Material.IRON, PulserTileEntity.class, EmptyContainer.class, "pulser", false);
    }

    @Override
    public boolean needsRedstoneCheck() {
        return true;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This machine will send an EMP pulse");
            list.add("every tick when it has collected more than " + ConfigMachines.Pulser.rfPerPulse + "RF");
            list.add("The strength of the input redstone signal");
            list.add("controls the rate of power input");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof PulserTileEntity) {
            int pulsePower = Math.min(((PulserTileEntity) te).getPulsePower(), ConfigMachines.Pulser.rfPerPulse);
            probeInfo.progress(pulsePower, ConfigMachines.Pulser.rfPerPulse);
        }
    }

}
