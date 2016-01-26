package mcjty.deepresonance.blocks.generator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.network.PacketGetGeneratorInfo;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GeneratorBlock extends GenericBlock {

    public static final int META_ON = 1;
    public static final int META_OFF = 0;
    public static final int META_HASUPPER = 2;
    public static final int META_HASLOWER = 4;

    public static int tooltipEnergy = 0;
    public static int tooltipRefCount = 0;
    public static int tooltipRfPerTick = 0;

    private static long lastTime = 0;

    public GeneratorBlock() {
        super(DeepResonance.instance, Material.iron, GeneratorTileEntity.class, false);
        setUnlocalizedName(DeepResonance.MODID + ".generatorBlock");
        //setHorizRotation(true); TODO: McJty: HorizRotation
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

    /*private void updateMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & BlockTools.MASK_REDSTONE_IN;
        if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
            meta |= META_HASUPPER;
        }
        if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
            meta |= META_HASLOWER;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, entityLivingBase, itemStack);
        if (!world.isRemote) {
            updateMeta(world, x, y, z);
            if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y+1, z);
            }
            if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y-1, z);
            }
            TileEntity te = world.getTileEntity(x, y, z);
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
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
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
        }
        return drops;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof GeneratorTileEntity) {
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    int energy = network.getEnergy() / network.getGeneratorBlocks();
                    network.setEnergy(network.getEnergy() - energy);
                }

                ((GeneratorTileEntity) te).removeBlockFromNetwork();
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
        if (!world.isRemote) {
            if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y+1, z);
            }
            if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y - 1, z);
            }
        }
    }*/

}
