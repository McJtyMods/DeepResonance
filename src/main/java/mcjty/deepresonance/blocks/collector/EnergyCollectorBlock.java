package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EnergyCollectorBlock extends GenericBlock {

    public EnergyCollectorBlock() {
        super(DeepResonance.instance, Material.iron, EnergyCollectorTileEntity.class, false);
        setBlockName("energyCollectorBlock");
        setHorizRotation(true);
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
            list.add(EnumChatFormatting.WHITE + "Part of a generator multi-block.");
            list.add(EnumChatFormatting.WHITE + "Place this on top of a generator with");
            list.add(EnumChatFormatting.WHITE + "crystals nearby.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        if (!world.isRemote) {
            if (world.getBlock(x, y - 1, z) == GeneratorSetup.generatorBlock) {
                TileEntity te = world.getTileEntity(x, y - 1, z);
                if (te instanceof GeneratorTileEntity) {
                    DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                    if (network != null) {
                        network.setActive(false);
                        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
                        generatorNetwork.save(world);
                    }
                }
            }
        }
    }

    @Override
    public String getSideIconName() {
        return "energyCollector";
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
