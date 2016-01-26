package mcjty.deepresonance.blocks.gencontroller;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
       // @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class GeneratorControllerBlock extends GenericBlock {

    public GeneratorControllerBlock() {
        super(DeepResonance.instance, Material.iron, GeneratorControllerTileEntity.class, false);
        setUnlocalizedName(DeepResonance.MODID + ".generatorControllerBlock");
        //TODO: McJty: HorizRotation
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Part of a generator multi-block.");
            list.add("Use this block to turn on/off the reactor with");
            list.add("a redstone signal.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public String getIdentifyingIconName() {
        return "generatorControllerOn";
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        checkRedstone(world, pos);
    }

    /*@Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
        return true;
    }

    @Override
    public IIcon getIconInd(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        if (BlockTools.getRedstoneSignalIn(meta)) {
            return iconOn;
        } else {
            return iconOff;
        }
    }*/
}
