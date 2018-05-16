package mcjty.deepresonance.blocks.collector;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EnergyCollectorBlock extends GenericDRBlock<EnergyCollectorTileEntity, EmptyContainer> {

    public EnergyCollectorBlock() {
        super(Material.IRON, EnergyCollectorTileEntity.class, EmptyContainer::new, "energy_collector", false);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(EnergyCollectorTileEntity.class, new EnergyCollectorTESR());
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Part of a generator multi-block.");
            list.add("Place this on top of a generator with");
            list.add("crystals nearby.");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos.down());
            if (te instanceof GeneratorTileEntity) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) te;
                DRGeneratorNetwork.Network network = generatorTileEntity.getNetwork();
                if (network != null) {
                    network.incCollectorBlocks();
                    DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
                    generatorNetwork.save();
                    EnergyCollectorTileEntity energyCollectorTileEntity = (EnergyCollectorTileEntity) world.getTileEntity(pos);
                    energyCollectorTileEntity.setNetworkID(generatorTileEntity.getNetworkId());
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof EnergyCollectorTileEntity) {
            EnergyCollectorTileEntity energyCollectorTileEntity = (EnergyCollectorTileEntity) te;
            energyCollectorTileEntity.disableCrystalGlow();
        }

        super.breakBlock(world, pos, state);
        if (!world.isRemote) {
            if (world.getBlockState(pos.down()).getBlock() == GeneratorSetup.generatorBlock) {
                te = world.getTileEntity(pos.down());
                if (te instanceof GeneratorTileEntity) {
                    DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                    if (network != null) {
                        network.decCollectorBlocks();
                        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
                        generatorNetwork.save();
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }


    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
