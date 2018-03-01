package mcjty.deepresonance.blocks.tank;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITextureLoader;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.client.DRResourceLocation;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.network.PacketGetTankInfo;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.varia.FluidTools;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
        super(Material.ROCK, TileTank.class, EmptyContainer.class, "tank", true);
    }

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite iconSide, iconTop, iconBottom;

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankTESR());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            FluidStack fluidStack = TileTank.getFluidStackFromNBT(tagCompound);
            if (fluidStack != null) {
                list.add(TextFormatting.GREEN + "Fluid: " + DRFluidRegistry.getFluidName(fluidStack));
                list.add(TextFormatting.GREEN + "Amount: " + DRFluidRegistry.getAmount(fluidStack) + " mb");
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This tank can hold up to 16 buckets of liquid.");
            list.add("It is also capable of mixing the characteristics");
            list.add("of liquid crystal.");
            list.add("Place a comparator next to this tank to detect");
            list.add("how filled the tank is");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
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
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TileTank) {
            TileTank tank = (TileTank) te;

            Map<EnumFacing, TileTank.Mode> settings = tank.getSettings();
            TileTank.Mode i = settings.get(data.getSideHit());
            probeInfo.text(TextFormatting.GREEN + "Mode: " + (i == TileTank.Mode.SETTING_NONE ? "none" : (i == TileTank.Mode.SETTING_ACCEPT ? "accept" : "provide")));
//            if (tank.getFluid() != null && tank.getFluid().getFluid() != null) {
//                probeInfo.text(TextFormatting.GREEN + "Fluid: " + DRFluidRegistry.getFluidName(tank.getFluid().getFluid()));
//            }
//            probeInfo.progress(tank.getFluidAmount(), tank.getCapacity(),
//                    probeInfo.defaultProgressStyle()
//                        .suffix("B")
//                        .filledColor(0xff005588).alternateFilledColor(0xff001133));

            LiquidCrystalFluidTagData lcd = LiquidCrystalFluidTagData.fromStack(tank.getFluid());
            if (lcd != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                probeInfo.text(TextFormatting.YELLOW + "Quality: " + decimalFormat.format(lcd.getQuality() * 100) + "%");
                probeInfo.text(TextFormatting.YELLOW + "Purity: " + decimalFormat.format(lcd.getPurity() * 100) + "%");
                probeInfo.text(TextFormatting.YELLOW + "Strength: " + decimalFormat.format(lcd.getStrength() * 100) + "%");
                probeInfo.text(TextFormatting.YELLOW + "Efficiency: " + decimalFormat.format(lcd.getEfficiency() * 100) + "%");
            }
        }
    }

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
            currentTip.add(TextFormatting.YELLOW + "Quality: " + decimalFormat.format(fluidData.getQuality() * 100) + "%");
            currentTip.add(TextFormatting.YELLOW + "Purity: " + decimalFormat.format(fluidData.getPurity() * 100) + "%");
            currentTip.add(TextFormatting.YELLOW + "Strength: " + decimalFormat.format(fluidData.getStrength() * 100) + "%");
            currentTip.add(TextFormatting.YELLOW + "Efficiency: " + decimalFormat.format(fluidData.getEfficiency() * 100) + "%");
        }
        if (System.currentTimeMillis() - lastTime > 100) {
            lastTime = System.currentTimeMillis();
            DRMessages.INSTANCE.sendToServer(new PacketGetTankInfo(tankTile.getPos()));
        }
        return currentTip;
    }



    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank) {
            TileTank tank = (TileTank) tile;
            if (tank.getTank() != null) {
                return tank.getTank().getComparatorInputOverride();
            }
        }
        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank) {
            ((TileTank) tile).onNeighborChange();
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if (player.capabilities.isCreativeMode){
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileTank) {
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound tagCompound = new NBTTagCompound();
                ((TileTank) tile).writeRestorableToNBT(tagCompound);
                stack.setTagCompound(tagCompound);
                WorldHelper.dropStack(worldIn, pos, stack);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank)tile;

            ItemStack mainItem = player.getHeldItem(EnumHand.MAIN_HAND);
            if (!mainItem.isEmpty()) {
                if (FluidTools.isEmptyContainer(mainItem)) {
                    if (((TileTank) tile).getTank() != null) {
                        if (!world.isRemote) {
                            extractIntoContainer(player, tank.getTank());
                        }
                    }
                    return true;
                } else if (FluidTools.isFilledContainer(mainItem)) {
                    if (((TileTank) tile).getTank() != null) {
                        if (!world.isRemote) {
                            fillFromContainer(player, tank.getTank());
                        }
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
            world.notifyNeighborsOfStateChange(pos, this, false);
            world.markBlockRangeForRenderUpdate(pos, pos);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    private void fillFromContainer(EntityPlayer player, IFluidHandler tank) {
        FluidStack fluidStack = FluidTools.convertBucketToFluid(player.getHeldItem(EnumHand.MAIN_HAND));
        if (fluidStack != null) {
            int fill = tank.fill(fluidStack, false);
            if (fill == fluidStack.amount) {
                tank.fill(fluidStack, true);
                if (!player.capabilities.isCreativeMode) {
                    ItemStack emptyContainer = FluidTools.drainContainer(player.getHeldItem(EnumHand.MAIN_HAND));
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
                }
            }
        }
    }

    private void extractIntoContainer(EntityPlayer player, IFluidHandler tank) {
        FluidStack fluidStack = tank.drain(1, false);
        if (fluidStack != null) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND).copy();
            if (1 <= 0) {
                heldItem.setCount(0);
            } else {
                heldItem.setCount(1);
            }
            int capacity = FluidTools.getCapacity(fluidStack, heldItem);
            if (capacity != 0) {
                fluidStack = tank.drain(capacity, false);
                if (fluidStack != null && fluidStack.amount == capacity) {
                    fluidStack = tank.drain(capacity, true);
                    ItemStack filledContainer = FluidTools.fillContainer(fluidStack, heldItem);
                    if (!filledContainer.isEmpty()) {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        if (!player.inventory.addItemStackToInventory(filledContainer)) {
                            EntityItem entityItem = new EntityItem(player.getEntityWorld(), player.posX, player.posY, player.posZ, filledContainer);
                            player.getEntityWorld().spawnEntity(entityItem);
                        }
                        player.openContainer.detectAndSendChanges();
                    } else {
                        // Try to insert the fluid back into the tank
                        tank.fill(fluidStack, true);
                    }
                }
            }
        }
    }


    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank) tile;
            if (tank.getClientRenderFluid() != null) {
                return tank.getClientRenderFluid().getLuminosity();
            }
        }
        return super.getLightValue(state, world, pos);
    }

//    @Override
//    public int getRenderType() {
//        return 2;
//    }


//    @Override
//    public boolean canRenderInLayer(BlockRenderLayer layer) {
//        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
//    }
//
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
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
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.getBlockState(pos.offset(side)).getBlock() != this;
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST, UP, DOWN, DUMMY_RCL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar iIconRegistrar) {
        iconSide = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tankside"));
        iconTop = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tanktop"));
        iconBottom = iIconRegistrar.registerSprite(new DRResourceLocation("blocks/tankbottom"));
    }

}
