package mcjty.deepresonance.blocks.valve;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.base.ElecTileBase;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.network.Argument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class ValveTileEntity extends ElecTileBase implements ITankHook {

    public static String CMD_SETTINGS = "settings";

    public ValveTileEntity() {
    }

    private TileTank bottomTank;
    private TileTank topTank;
    private int progress = 0;

    private float minPurity = 0;
    private float minStrength = 0;
    private float minEfficiency = 0;

    @Override
    protected void checkStateServer() {
        progress--;
        markDirty();
        if (progress > 0) {
            return;
        }
        progress = ConfigMachines.Valve.ticksPerOperation;

        if (topTank == null || bottomTank == null) {
            return;
        }

        FluidStack fluidStack = topTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Valve.rclPerOperation, false);
        if (fluidStack != null && fillBottomTank(fluidStack.amount)) {
            LiquidCrystalFluidTagData data = LiquidCrystalFluidTagData.fromStack(fluidStack);
            if (data == null) {
                return;
            }
            if (data.getPurity() < minPurity) {
                return;
            }
            if (data.getStrength() < minStrength) {
                return;
            }
            if (data.getEfficiency() < minEfficiency) {
                return;
            }

            fluidStack = topTank.drain(ForgeDirection.UNKNOWN, ConfigMachines.Valve.rclPerOperation, true);
            bottomTank.fill(ForgeDirection.UNKNOWN, fluidStack, true);
        }
    }

    public float getMinEfficiency() {
        return minEfficiency;
    }

    public void setMinEfficiency(float minEfficiency) {
        this.minEfficiency = minEfficiency;
        markDirty();
    }

    public float getMinPurity() {
        return minPurity;
    }

    public void setMinPurity(float minPurity) {
        this.minPurity = minPurity;
        markDirty();
    }

    public float getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(float minStrength) {
        this.minStrength = minStrength;
        markDirty();
    }

    private boolean fillBottomTank(int amount) {
        return bottomTank.fill(ForgeDirection.UNKNOWN, new FluidStack(DRFluidRegistry.liquidCrystal, amount), false) == amount;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setFloat("minPurity", minPurity);
        tagCompound.setFloat("minStrength", minStrength);
        tagCompound.setFloat("minEfficiency", minEfficiency);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        progress = tagCompound.getInteger("progress");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        minPurity = tagCompound.getFloat("minPurity");
        minStrength = tagCompound.getFloat("minStrength");
        minEfficiency = tagCompound.getFloat("minEfficiency");
    }

    @Override
    public void hook(TileTank tank, ForgeDirection direction) {
        if (direction == ForgeDirection.DOWN){
            if (validRCLTank(tank)) {
                bottomTank = tank;
            }
        } else if (topTank == null){
            if (validRCLTank(tank)){
                topTank = tank;
            }
        }
    }

    @Override
    public void unHook(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(bottomTank, tank)){
            bottomTank = null;
            notifyNeighboursOfDataChange();
        } else if (tilesEqual(topTank, tank)){
            topTank = null;
            notifyNeighboursOfDataChange();
        }
    }

    @Override
    public void onContentChanged(TileTank tank, ForgeDirection direction) {
        if (tilesEqual(topTank, tank)){
            if (!validRCLTank(tank)) {
                topTank = null;
            }
        }
        if (tilesEqual(bottomTank, tank)){
            if (!validRCLTank(tank)) {
                bottomTank = null;
            }
        }
    }

    private boolean validRCLTank(TileTank tank){
        Fluid fluid = DRFluidRegistry.getFluidFromStack(tank.getFluid());
        return fluid == null || fluid == DRFluidRegistry.liquidCrystal;
    }

    private boolean tilesEqual(TileTank first, TileTank second){
        return first != null && second != null && first.myLocation().equals(second.myLocation()) && WorldHelper.getDimID(first.getWorldObj()) == WorldHelper.getDimID(second.getWorldObj());
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_SETTINGS.equals(command)) {
            double purity = args.get("purity").getDouble();
            double strength = args.get("strength").getDouble();
            double efficiency = args.get("efficiency").getDouble();
            setMinPurity((float) purity);
            setMinStrength((float) strength);
            setMinEfficiency((float) efficiency);
            return true;
        }
        return false;
    }
}
