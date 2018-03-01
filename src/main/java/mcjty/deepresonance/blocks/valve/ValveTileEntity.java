package mcjty.deepresonance.blocks.valve;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.ITankHook;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class ValveTileEntity extends GenericTileEntity implements ITankHook, ITickable {

    public static String CMD_SETTINGS = "settings";
    public static final String CMD_RSMODE = "rsMode";

    public ValveTileEntity() {
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    private TileTank bottomTank;
    private TileTank topTank;
    private int progress = 0;

    private float minPurity = 1.0f;
    private float minStrength = 1.0f;
    private float minEfficiency = 1.0f;
    private int maxMb = 0;

    @Override
    public void update() {
        if (!getWorld().isRemote){
            checkStateServer();
        }
    }

    private void checkStateServer() {
        if (!isMachineEnabled()) {
            return;
        }

        progress--;
        markDirty();
        if (progress > 0) {
            return;
        }
        progress = ConfigMachines.valve.ticksPerOperation;

        if (topTank == null || bottomTank == null || topTank.getTank() == null || bottomTank.getTank() == null) {
            return;
        }

        FluidStack fluidStack = topTank.getTank().drain(ConfigMachines.valve.rclPerOperation, false);
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

            if (maxMb > 0) {
                // We have to check maximum volume
                int fluidAmount = bottomTank.getFluidAmount();
                if (fluidAmount < maxMb) {
                    int toDrain = Math.min(maxMb - fluidAmount, ConfigMachines.valve.rclPerOperation);
                    fluidStack = topTank.getTank().drain(toDrain, true);
                    bottomTank.getTank().fill(fluidStack, true);
                }
            } else {
                fluidStack = topTank.getTank().drain(ConfigMachines.valve.rclPerOperation, true);
                bottomTank.getTank().fill(fluidStack, true);
            }
        }
    }

    public int getMaxMb() {
        return maxMb;
    }

    public void setMaxMb(int maxMb) {
        this.maxMb = maxMb;
        markDirty();
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
        return bottomTank.getTank().fill(new FluidStack(DRFluidRegistry.liquidCrystal, amount), false) == amount;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("progress", progress);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setFloat("minPurity", minPurity);
        tagCompound.setFloat("minStrength", minStrength);
        tagCompound.setFloat("minEfficiency", minEfficiency);
        tagCompound.setInteger("maxMb", maxMb);
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
        maxMb = tagCompound.getInteger("maxMb");
    }

    @Override
    public void hook(TileTank tank, EnumFacing direction) {
        if (direction == EnumFacing.DOWN){
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
    public void unHook(TileTank tank, EnumFacing direction) {
        if (tilesEqual(bottomTank, tank)){
            bottomTank = null;
            notifyAndMarkDirty();
        } else if (tilesEqual(topTank, tank)){
            topTank = null;
            notifyAndMarkDirty();
        }
    }

    @Override
    public void onContentChanged(TileTank tank, EnumFacing direction) {
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
        return first != null && second != null && first.getPos().equals(second.getPos()) && WorldHelper.getDimID(first.getWorld()) == WorldHelper.getDimID(second.getWorld());
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_RSMODE.equals(command)) {
            String m = args.get("rs").getString();
            setRSMode(RedstoneMode.getMode(m));
            return true;
        } else if (CMD_SETTINGS.equals(command)) {
            double purity = args.get("purity").getDouble();
            double strength = args.get("strength").getDouble();
            double efficiency = args.get("efficiency").getDouble();
            int maxMb = args.get("maxMb").getInteger();
            setMinPurity((float) purity);
            setMinStrength((float) strength);
            setMinEfficiency((float) efficiency);
            setMaxMb(maxMb);
            return true;
        }
        return false;
    }

    protected void notifyAndMarkDirty(){
        if (WorldHelper.chunkLoaded(getWorld(), pos)){
            this.markDirty();
            this.getWorld().notifyNeighborsOfStateChange(pos, blockType, false);
        }
    }

}
