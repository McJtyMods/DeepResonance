package mcjty.deepresonance.blocks.tank;

import elec332.core.client.IIconRegistrar;
import elec332.core.client.ITextureLoader;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.client.DRResourceLocation;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.network.PacketGetTankInfo;
import mcjty.lib.container.EmptyContainer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockTank extends GenericDRBlock<TileTank, EmptyContainer> implements ITextureLoader {

    public static final PropertyEnum<TileTank.Mode> NORTH = PropertyEnum.create("north", TileTank.Mode.class);
    public static final PropertyEnum<TileTank.Mode> SOUTH = PropertyEnum.create("south", TileTank.Mode.class);
    public static final PropertyEnum<TileTank.Mode> WEST = PropertyEnum.create("west", TileTank.Mode.class);
    public static final PropertyEnum<TileTank.Mode> EAST = PropertyEnum.create("east", TileTank.Mode.class);
    public static final PropertyEnum<TileTank.Mode> UP = PropertyEnum.create("up", TileTank.Mode.class);
    public static final PropertyEnum<TileTank.Mode> DOWN = PropertyEnum.create("down", TileTank.Mode.class);

    public static final PropertyInteger DUMMY_RCL = PropertyInteger.create("dummy_rcl", 0, 1);

    public BlockTank() {
        super(Material.rock, TileTank.class, EmptyContainer.class, "tank", true);
    }

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite iconSide, iconTop, iconBottom;

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankTESR());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            FluidStack fluidStack = TileTank.getFluidStackFromNBT(tagCompound);
            if (fluidStack != null) {
                list.add(EnumChatFormatting.GREEN + "Fluid: " + DRFluidRegistry.getFluidName(fluidStack));
                list.add(EnumChatFormatting.GREEN + "Amount: " + DRFluidRegistry.getAmount(fluidStack) + " mb");
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This tank can hold up to 16 buckets of liquid.");
            list.add("It is also capable of mixing the characteristics");
            list.add("of liquid crystal.");
            list.add("Place a comparator next to this tank to detect");
            list.add("how filled the tank is");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    // For Waila:
    private long lastTime;
    public int totalFluidAmount = 0;
    public int tankCapacity = 0;
    public LiquidCrystalFluidTagData fluidData = null;
    public Fluid clientRenderFluid = null;

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileTank tankTile = (TileTank) accessor.getTileEntity();
        Map<EnumFacing, TileTank.Mode> settings = tankTile.getSettings();
        TileTank.Mode i = settings.get(accessor.getSide());
        currentTip.add("Mode: "+(i == TileTank.Mode.SETTING_NONE ? "none" : (i == TileTank.Mode.SETTING_ACCEPT ? "accept" : "provide")));
        currentTip.add("Fluid: "+ DRFluidRegistry.getFluidName(clientRenderFluid));
        currentTip.add("Amount: "+totalFluidAmount + " (" + tankCapacity + ")");
        if (fluidData != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            currentTip.add(EnumChatFormatting.YELLOW + "Quality: " + decimalFormat.format(fluidData.getQuality() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Purity: " + decimalFormat.format(fluidData.getPurity() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Strength: " + decimalFormat.format(fluidData.getStrength() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Efficiency: " + decimalFormat.format(fluidData.getEfficiency() * 100) + "%");
        }
        if (System.currentTimeMillis() - lastTime > 100) {
            lastTime = System.currentTimeMillis();
            DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetTankInfo(tankTile.getPos()));
        }
        return currentTip;
    }

    @Override
    public int getComparatorInputOverride(World world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileTank) {
            TileTank tank = (TileTank) tile;
            if (tank.getMultiBlock() != null)
                return tank.getMultiBlock().getComparatorInputOverride();
        }
        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileTank) {
            ((TileTank) tile).onNeighborChange();
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float sidex, float sidey, float sidez) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank)tile;

            if (player.getHeldItem() != null) {
                if (FluidContainerRegistry.isEmptyContainer(player.getHeldItem())) {
                    if (!world.isRemote) {
                        extractIntoContainer(player, tank);
                    }
                    return true;
                } else if (FluidContainerRegistry.isFilledContainer(player.getHeldItem())) {
                    if (!world.isRemote) {
                        fillFromContainer(player, tank);
                    }
                    return true;
                }
            }
            TileTank.Mode i = tank.settings.get(side);
            switch (i) {
                case SETTING_NONE:
                    i = TileTank.Mode.SETTING_ACCEPT;
                    break;
                case SETTING_ACCEPT:
                    i = TileTank.Mode.SETTING_PROVIDE;
                    break;
                case SETTING_PROVIDE:
                    i = TileTank.Mode.SETTING_NONE;
                    break;
            }
            tank.settings.put(side, i);
            tank.markDirty();
            WorldHelper.markBlockForUpdate(world, pos);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, side, sidex, sidey, sidez);
    }

    private void fillFromContainer(EntityPlayer player, TileTank tank) {
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(player.getHeldItem());
        if (fluidStack != null) {
            int fill = tank.fill(null, fluidStack, false);
            if (fill == fluidStack.amount) {
                tank.fill(null, fluidStack, true);
                if (!player.capabilities.isCreativeMode) {
                    ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(player.getHeldItem());
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
                }
            }
        }
    }

    private void extractIntoContainer(EntityPlayer player, TileTank tank) {
        FluidStack fluidStack = tank.drain(null, 1, false);
        if (fluidStack != null) {
            int capacity = FluidContainerRegistry.getContainerCapacity(fluidStack, player.getHeldItem());
            if (capacity != 0) {
                fluidStack = tank.drain(null, capacity, false);
                if (fluidStack != null && fluidStack.amount == capacity) {
                    fluidStack = tank.drain(null, capacity, true);
                    ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, player.getHeldItem());
                    if (filledContainer != null) {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        if (!player.inventory.addItemStackToInventory(filledContainer)) {
                            EntityItem entityItem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, filledContainer);
                            player.worldObj.spawnEntityInWorld(entityItem);
                        }
                        player.openContainer.detectAndSendChanges();
                    } else {
                        // Try to insert the fluid back into the tank
                        tank.fill(null, fluidStack, true);
                    }
                }
            }
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank) tile;
            if (tank.getClientRenderFluid() != null) {
                return tank.getClientRenderFluid().getLuminosity();
            }
        }
        return super.getLightValue(world, pos);
    }

//    @Override
//    public int getRenderType() {
//        return 2;
//    }


    @SideOnly(Side.CLIENT)
    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSideIcon() {
        return iconSide;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getBottomIcon() {
        return iconBottom;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTopIcon() {
        return iconTop;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return WorldHelper.getBlockAt(worldIn, pos) != this;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileTank te = (TileTank) world.getTileEntity(pos);
        Map<EnumFacing, TileTank.Mode> settings = te.getSettings();
        TileTank.Mode north = settings.get(EnumFacing.NORTH);
        TileTank.Mode south = settings.get(EnumFacing.SOUTH);
        TileTank.Mode west = settings.get(EnumFacing.WEST);
        TileTank.Mode east = settings.get(EnumFacing.EAST);
        TileTank.Mode down = settings.get(EnumFacing.DOWN);
        TileTank.Mode up = settings.get(EnumFacing.UP);
        return state.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east).withProperty(UP, up).withProperty(DOWN, down).withProperty(DUMMY_RCL, 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, SOUTH, WEST, EAST, UP, DOWN, DUMMY_RCL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar iIconRegistrar) {
        iconSide = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tankSide"));
        iconTop = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tankTop"));
        iconBottom = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tankBottom"));
    }

}
