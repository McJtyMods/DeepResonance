package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
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
        if (tagCompound != null && tagCompound.hasKey("fluid")) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
            list.add(EnumChatFormatting.GREEN + "Fluid: "+ DRFluidRegistry.getFluidName(fluidStack));
            list.add(EnumChatFormatting.GREEN + "Amount: "+ DRFluidRegistry.getAmount(fluidStack) + " mb");
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

    private long lastTime;

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileTank tankTile = (TileTank) accessor.getTileEntity();
        Map<ForgeDirection, Integer> settings = tankTile.getSettings();
        int i = settings.get(accessor.getSide());
        currentTip.add("Mode: "+(i == TileTank.SETTING_NONE ? "none" : (i == TileTank.SETTING_ACCEPT ? "accept" : "provide")));
        currentTip.add("Fluid: "+ DRFluidRegistry.getFluidName(tankTile.getClientRenderFluid()));
        currentTip.add("Amount: "+tankTile.getTotalFluidAmount() + " (" + tankTile.getTankCapacity() + ")");
        LiquidCrystalFluidTagData fluidData = tankTile.getFluidData();
        if (fluidData != null) {
            currentTip.add(EnumChatFormatting.YELLOW + "Quality: " + (int)(fluidData.getQuality() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Purity: " + (int)(fluidData.getPurity() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Power: " + (int)(fluidData.getStrength() * 100) + "%");
            currentTip.add(EnumChatFormatting.YELLOW + "Efficiency: " + (int)(fluidData.getEfficiency() * 100) + "%");
        }
        if (System.currentTimeMillis() - lastTime > 100){
            lastTime = System.currentTimeMillis();
            tankTile.sendPacketToServer(1, new NBTTagCompound());
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
