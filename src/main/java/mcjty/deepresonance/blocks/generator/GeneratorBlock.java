package mcjty.deepresonance.blocks.generator;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.network.PacketGetGeneratorInfo;
import mcjty.lib.container.EmptyContainer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.List;

public class GeneratorBlock extends GenericDRBlock<GeneratorTileEntity, EmptyContainer> {

    public static PropertyBool ENABLED = PropertyBool.create("enabled");
    public static PropertyBool UPPER = PropertyBool.create("upper");
    public static PropertyBool LOWER = PropertyBool.create("lower");

    public static int tooltipEnergy = 0;
    public static int tooltipRefCount = 0;
    public static int tooltipRfPerTick = 0;

    private static long lastTime = 0;

    public GeneratorBlock() {
        super(Material.iron, GeneratorTileEntity.class, EmptyContainer.class, "generator", false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            list.add(EnumChatFormatting.YELLOW + "Energy: " + tagCompound.getInteger("energy"));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Part of a generator multi-block.");
            list.add("You can place these in any configuration.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof GeneratorTileEntity) {
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) tileEntity;
            currenttip.add(EnumChatFormatting.GREEN + "ID: " + new DecimalFormat("#.##").format(generatorTileEntity.getNetworkId()));
            if (System.currentTimeMillis() - lastTime > 250) {
                lastTime = System.currentTimeMillis();
                DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetGeneratorInfo(generatorTileEntity.getNetworkId()));
            }
            currenttip.add(EnumChatFormatting.GREEN + "Energy: " + tooltipEnergy + "/" + (tooltipRefCount*GeneratorConfiguration.rfPerGeneratorBlock) + " RF");
            currenttip.add(EnumChatFormatting.YELLOW + Integer.toString(tooltipRfPerTick) + " RF/t");
        }
        return currenttip;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isRemote) {

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof GeneratorTileEntity) {
                ((GeneratorTileEntity) te).addBlockToNetwork();
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    NBTTagCompound tagCompound = itemStack.getTagCompound();
                    network.setEnergy(network.getEnergy() + (tagCompound == null ? 0 : tagCompound.getInteger("energy")));
                    DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(world);
                    generatorNetwork.save(world);
                }
            }
        }

    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, UPPER, LOWER, ENABLED);
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(UPPER, world.getBlockState(pos.up()).getBlock() == GeneratorSetup.generatorBlock)
                .withProperty(LOWER, world.getBlockState(pos.down()).getBlock() == GeneratorSetup.generatorBlock);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getDefaultState().withProperty(ENABLED, (meta & 1) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ENABLED) ? 1 : 0;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess access, BlockPos pos, IBlockState metadata, int fortune) {
        List<ItemStack> drops = super.getDrops(access, pos, metadata, fortune);
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof GeneratorTileEntity) {
            DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
            if (network != null) {
                int energy = network.getEnergy() / network.getGeneratorBlocks();
                if (!drops.isEmpty()) {
                    NBTTagCompound tagCompound = drops.get(0).getTagCompound();
                    if (tagCompound == null) {
                        tagCompound = new NBTTagCompound();
                        drops.get(0).setTagCompound(tagCompound);
                    }
                    tagCompound.setInteger("energy", energy);
                }
            }
        }
        return drops;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof GeneratorTileEntity) {
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    int energy = network.getEnergy() / network.getGeneratorBlocks();
                    network.setEnergy(network.getEnergy() - energy);
                }

                ((GeneratorTileEntity) te).removeBlockFromNetwork();
            }
        }
        super.breakBlock(world, pos, state);
        if (!world.isRemote) {
            if (world.getBlockState(pos.up()).getBlock() == GeneratorSetup.generatorBlock) {
                world.markBlockForUpdate(pos.up());
            }
            if (world.getBlockState(pos.down()).getBlock() == GeneratorSetup.generatorBlock) {
                world.markBlockForUpdate(pos.down());
            }
        }
    }



}
