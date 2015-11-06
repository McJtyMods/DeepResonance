package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.deepresonance.network.PacketGetTankInfo;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class BlockTank extends ElecGenericBlockBase {

    private IIcon iconSideProvide;
    private IIcon iconSideAccept;
    private IIcon iconTopProvide;
    private IIcon iconTopAccept;
    private IIcon iconBottomProvide;
    private IIcon iconBottomAccept;

    public BlockTank(String name) {
        super(Material.rock, TileTank.class, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
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
        Map<ForgeDirection, Integer> settings = tankTile.getSettings();
        int i = settings.get(accessor.getSide());
        currentTip.add("Mode: "+(i == TileTank.SETTING_NONE ? "none" : (i == TileTank.SETTING_ACCEPT ? "accept" : "provide")));
        currentTip.add("Fluid: "+ DRFluidRegistry.getFluidName(clientRenderFluid));
        currentTip.add("Amount: "+totalFluidAmount + " (" + tankCapacity + ")");
        if (fluidData != null) {
            currentTip.add(EnumChatFormatting.YELLOW + "Quality: " + (int)(fluidData.getQuality() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Purity: " + (int)(fluidData.getPurity() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Power: " + (int)(fluidData.getStrength() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Efficiency: " + (int)(fluidData.getEfficiency() * 100) + "%");
        }
        if (System.currentTimeMillis() - lastTime > 100) {
            lastTime = System.currentTimeMillis();
            DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetTankInfo(tankTile.xCoord, tankTile.yCoord, tankTile.zCoord));
        }
        return currentTip;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
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
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTank) {
            TileTank tank = (TileTank) tile;
            for (Map.Entry<ITankHook, ForgeDirection> entry : tank.getConnectedHooks().entrySet()) {
                entry.getKey().hook(tank, entry.getValue());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sidex, float sidey, float sidez) {
        TileEntity tile = world.getTileEntity(x, y, z);
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

            ForgeDirection direction = ForgeDirection.getOrientation(side);
            int i = tank.settings.get(direction);
            if (i < TileTank.SETTING_MAX) {
                i++;
            } else {
                i = TileTank.SETTING_NONE;
            }
            tank.settings.put(direction, i);
            tank.markDirty();
            world.markBlockForUpdate(x, y, z);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, sidex, sidey, sidez);
    }

    private void fillFromContainer(EntityPlayer player, TileTank tank) {
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(player.getHeldItem());
        if (fluidStack != null) {
            int fill = tank.fill(ForgeDirection.UNKNOWN, fluidStack, false);
            if (fill == fluidStack.amount) {
                tank.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                if (!player.capabilities.isCreativeMode) {
                    ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(player.getHeldItem());
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
                }
            }
        }
    }

    private void extractIntoContainer(EntityPlayer player, TileTank tank) {
        FluidStack fluidStack = tank.drain(ForgeDirection.UNKNOWN, 1, false);
        if (fluidStack != null) {
            int capacity = FluidContainerRegistry.getContainerCapacity(fluidStack, player.getHeldItem());
            if (capacity != 0) {
                fluidStack = tank.drain(ForgeDirection.UNKNOWN, capacity, false);
                if (fluidStack != null && fluidStack.amount == capacity) {
                    fluidStack = tank.drain(ForgeDirection.UNKNOWN, capacity, true);
                    ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, player.getHeldItem());
                    if (filledContainer != null) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
                    } else {
                        // Try to insert the fluid back into the tank
                        tank.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    }
                }
            }
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileTank){
            TileTank tank = (TileTank) tile;
            if (tank.getClientRenderFluid() != null)
                return tank.getClientRenderFluid().getLuminosity();
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public String getTopIconName() {
        return "tankTop";
    }

    @Override
    public String getBottomIconName() {
        return "tankBottom";
    }

    @Override
    public String getSideIconName() {
        return "tankSide";
    }

    public IIcon getSideIcon() {
        return iconSide;
    }

    public IIcon getBottomIcon() {
        return iconBottom;
    }

    public IIcon getTopIcon() {
        return iconTop;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        iconSideAccept = iconRegister.registerIcon(modBase.getModId() + ":tankSideAccept");
        iconSideProvide = iconRegister.registerIcon(modBase.getModId() + ":tankSideProvide");
        iconTopAccept = iconRegister.registerIcon(modBase.getModId() + ":tankTopAccept");
        iconTopProvide = iconRegister.registerIcon(modBase.getModId() + ":tankTopProvide");
        iconBottomAccept = iconRegister.registerIcon(modBase.getModId() + ":tankBottomAccept");
        iconBottomProvide = iconRegister.registerIcon(modBase.getModId() + ":tankBottomProvide");
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return world.getBlock(x, y, z) != this;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (te instanceof TileTank) {
            TileTank tileTank = (TileTank) te;
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            if (dir == ForgeDirection.DOWN) {
                if (tileTank.isInput(dir)) {
                    return iconBottomAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconBottomProvide;
                }
                return iconBottom;
            } else if (dir == ForgeDirection.UP) {
                if (tileTank.isInput(dir)) {
                    return iconTopAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconTopProvide;
                }
                return iconTop;
            } else {
                if (tileTank.isInput(dir)) {
                    return iconSideAccept;
                } else if (tileTank.isOutput(dir)) {
                    return iconSideProvide;
                }
                return iconSide;
            }
        } else {
            return getIcon(side, 0);
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (dir == ForgeDirection.DOWN) {
            return iconBottom;
        } else if (dir == ForgeDirection.UP) {
            return iconTop;
        } else {
            return iconSide;
        }
    }
}
