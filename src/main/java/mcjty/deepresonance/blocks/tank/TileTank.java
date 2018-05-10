package mcjty.deepresonance.blocks.tank;

import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.tanks.TankGrid;
import mcjty.deepresonance.varia.FluidTankWrapper;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Elec332 on 9-8-2015.
 */
public class TileTank extends GenericTileEntity implements IElecCoreNetworkTile {

    public TileTank(){
        super();
        this.settings = Maps.newHashMap();
        for (EnumFacing direction : EnumFacing.VALUES){
            settings.put(direction, Mode.SETTING_NONE);
        }
        this.multiBlockSaveData = new NBTTagCompound();
        this.tankHooks = Maps.newHashMap();
        this.input = new FluidTankWrapper(){

            @Override
            protected IFluidTank getTank() {
                return multiBlock;
            }

            @Override
            protected boolean canDrain() {
                return false;
            }

        };
        this.output = new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return multiBlock;
            }

            @Override
            protected boolean canFill() {
                return false;
            }

        };
        this.inputOutput = new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return multiBlock;
            }

        };
    }

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    private final IFluidHandler input;
    private final IFluidHandler output;
    private final IFluidHandler inputOutput;

    private NBTTagCompound multiBlockSaveData;

    protected Map<EnumFacing, Mode> settings;
    private Map<EnumFacing, ITankHook> tankHooks;
    private boolean hooksInit = false;
    private static Map<ChunkPos, Map<BlockPos, EnumFacing>> toLoad;

    @Override
    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
    }


    private List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, int x, int z) {
        List<EntityPlayerMP> ret = new ArrayList<>();
        if(world instanceof WorldServer) {
            PlayerChunkMap playerManager = ((WorldServer)world).getPlayerChunkMap();

            for (EntityPlayerMP player : ((WorldServer) getWorld()).getMinecraftServer().getPlayerList().getPlayers()) {
                Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (playerManager.isPlayerWatchingChunk(player, chunk.x, chunk.z)) {
                    ret.add(player);
                }
            }
        }

        return ret;
    }

    public void sendPacket(int ID, NBTTagCompound data) {
        for (EntityPlayerMP player : getAllPlayersWatchingBlock(this.getWorld(), this.pos.getX(), this.pos.getZ())) {
            player.connection.sendPacket(new SPacketUpdateTileEntity(this.pos, ID, data));
        }
    }

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(() -> {
            if (WorldHelper.chunkLoaded(getWorld(), pos)) {
                onTileLoaded();
            }
        }, getWorld());
    }

    @Override
    public void invalidate() {
        if (!isInvalid()){
            super.invalidate();
            onTileUnloaded();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        onTileUnloaded();
    }

    public void onTileLoaded() {
        if (!getWorld().isRemote) {
            initHooks();
        }
    }

    public void onTileUnloaded() {
        if (!getWorld().isRemote) {
            for (EnumFacing f : EnumFacing.VALUES) {
                Optional.ofNullable(toLoad.get(getChunk(pos.offset(f)))).ifPresent(map -> map.remove(pos));
            }
            for (Map.Entry<EnumFacing, ITankHook> entry : getConnectedHooks().entrySet()){
                entry.getValue().unHook(this, entry.getKey().getOpposite());
            }
            getConnectedHooks().clear();
            hooksInit = false;
        }
    }

    public void onNeighborChange(){
        Map<EnumFacing, ITankHook> hookMap = getConnectedHooks();
        for (EnumFacing facing : EnumFacing.VALUES){
            ITankHook tankHook = hookMap.get(facing);
            BlockPos pos = getPos().offset(facing);
            TileEntity tile = WorldHelper.chunkLoaded(getWorld(), pos) ? getWorld().getTileEntity(pos) : null;
            if (((tile == null) != (tankHook == null)) || (tile != tankHook)){
                hookMap.remove(facing);
                if (tile != null){
                    Optional.ofNullable(toLoad.get(getChunk(pos))).ifPresent(set -> set.remove(getPos()));
                }
                if (tile instanceof ITankHook){
                    ((ITankHook) tile).hook(this, facing.getOpposite());
                    hookMap.put(facing, (ITankHook) tile);
                }
            } else if (tankHook != null){
                tankHook.onContentChanged(this, facing.getOpposite());
            }
        }
    }

    private void initHooks(){
        tankHooks.clear();
        for (EnumFacing facing : EnumFacing.VALUES){
            BlockPos offPos = getPos().offset(facing);
            if (!WorldHelper.chunkLoaded(getWorld(), offPos)) {
                ChunkPos chP = getChunk(offPos);
                Map<BlockPos, EnumFacing> dta = toLoad.get(chP);
                if (dta == null) {
                    toLoad.put(chP, dta = Maps.newHashMap());
                }
                dta.put(offPos, facing);
                continue;
            }
            initLoaded(facing);
        }
        hooksInit = true;
    }

    private void initLoaded(EnumFacing facing){
        TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
        if (tile instanceof ITankHook){
            tankHooks.put(facing, (ITankHook) tile);
            ((ITankHook) tile).hook(this, facing.getOpposite());
        }
    }

    private TankGrid multiBlock;
    public FluidStack myTank;
    public Fluid lastSeenFluid;

    public Map<EnumFacing, Mode> getSettings() {
        return settings;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("settings", Constants.NBT.TAG_COMPOUND);
        if (tagList != null){
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                EnumFacing side = EnumFacing.values()[tag.getInteger("dir")];
                Mode mode = Mode.values()[tag.getInteger("n")];
                settings.put(side, mode);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<EnumFacing, Mode> entry : settings.entrySet()){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dir", entry.getKey().ordinal());
            tag.setInteger("n", entry.getValue().ordinal());
            tagList.appendTag(tag);
        }
        tagCompound.setTag("settings", tagList);
        return tagCompound;
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        this.myTank = getFluidStackFromNBT(tagCompound);

        multiBlockSaveData = tagCompound.getCompoundTag("multiBlockData");
        if (tagCompound.hasKey("lastSeenFluid")) { /* legacy compat */
            this.lastSeenFluid = FluidRegistry.getFluid(tagCompound.getString("lastSeenFluid"));
        } else if (multiBlockSaveData.hasKey("lastSeenFluid")){
            this.lastSeenFluid = FluidRegistry.getFluid(multiBlockSaveData.getString("lastSeenFluid"));
        }
    }

    public static FluidStack getFluidStackFromNBT(NBTTagCompound tagCompound) {
        NBTTagCompound mbTag = tagCompound.getCompoundTag("multiBlockData");
        FluidStack s;
        if (tagCompound.hasKey("fluid")) { /* legacy compat */
            s = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("fluid"));
        } else if (mbTag.hasKey("fluid")){
            s = FluidStack.loadFluidStackFromNBT(mbTag.getCompoundTag("fluid"));
        } else {
            s = null;
        }
        return s;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        if (multiBlock != null) {
            multiBlock.setDataToTile(this);
        }
        tagCompound.setTag("multiBlockData", multiBlockSaveData);
    }

    public void setTank(TankGrid multiBlock) {
        this.multiBlock = multiBlock;
    }

    public TankGrid getTank(){
        return this.multiBlock;
    }

    public void setSaveData(NBTTagCompound nbtTagCompound) {
        this.multiBlockSaveData = nbtTagCompound;
        this.myTank = FluidStack.loadFluidStackFromNBT(nbtTagCompound.getCompoundTag("fluid"));
        this.lastSeenFluid = FluidRegistry.getFluid(nbtTagCompound.getString("lastSeenFluid"));
    }

    public Fluid getClientRenderFluid() {
        return clientRenderFluid;
    }

    @SideOnly(Side.CLIENT)
    public float getRenderHeight() {
        return renderHeight;
    }

    public FluidStack getFluid() {
        return multiBlock == null ? null : multiBlock.getFluid();
    }

    public NBTTagCompound getFluidTag() {
        return getFluid() == null ? null : getFluid().tag;
    }

    public int getFluidAmount() {
        return multiBlock == null ? 0 : multiBlock.getFluidAmount();
    }

    public int getCapacity() {
        return multiBlock == null ? 0 : multiBlock.getCapacity();
    }

    private void notifyChanges(boolean b){
        if (multiBlock != null && b){
            for (Map.Entry<EnumFacing, ITankHook> entry : getConnectedHooks().entrySet()){
                entry.getValue().onContentChanged(this, entry.getKey().getOpposite());
            }
        }
    }

    private Map<EnumFacing, ITankHook> getConnectedHooks(){
        return tankHooks;
    }

    public boolean isInput(EnumFacing direction){
        return direction == null || settings.get(direction) == Mode.SETTING_ACCEPT;
    }

    public boolean isOutput(EnumFacing direction){
        return direction == null || settings.get(direction) == Mode.SETTING_PROVIDE;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (isInput(facing) || isOutput(facing))) || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
            if (facing == null) {
                return (T) inputOutput;
            }
            if (isOutput(facing)){
                return (T) output;
            }
            if (isInput(facing)){
                return (T) input;
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        //this.clientRenderFluid = FluidRegistry.getFluid(tagCompound.getString("fluid"));
        //this.renderHeight = tagCompound.getFloat("render");
    }

    public static final int ID_GENERIC = 1;
    public static final int ID_SETFLUID = 2;
    public static final int ID_SETHEIGHT = 3;

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if (packet.getTileEntityType() == ID_GENERIC) {
            super.onDataPacket(net, packet);
        } else {
            this.onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }
    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
        switch (id){
            case ID_SETFLUID:
                this.clientRenderFluid = FluidRegistry.getFluid(tag.getString("fluid"));
                return;
            case ID_SETHEIGHT:
                this.renderHeight = tag.getFloat("render");
        }
    }

    private ChunkPos getChunk(){
        return getChunk(getPos());
    }

    private ChunkPos getChunk(BlockPos pos){
        return new ChunkPos(pos);
    }

    public enum Mode implements IStringSerializable {
        SETTING_NONE("none"),
        SETTING_ACCEPT("accept"),   // Blue
        SETTING_PROVIDE("provide"); // Yellow

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return name;
        }

    }

    static {
        toLoad = Maps.newHashMap();
        MinecraftForge.EVENT_BUS.register(new Object(){

            @SubscribeEvent
            public void onChunkLoad(ChunkEvent.Load event){
                Map<BlockPos, EnumFacing> dta = toLoad.remove(event.getChunk().getPos());
                if (dta != null && ConfigMachines.general.experimentalChunkBorderFix){
                    World world = event.getWorld();
                    for (Map.Entry<BlockPos, EnumFacing> e : dta.entrySet()){
                        if (!WorldHelper.chunkLoaded(world, e.getKey())){
                            continue;
                        }
                        TileEntity tile = world.getTileEntity(e.getKey());
                        if (tile instanceof TileTank){
                            ((TileTank) tile).initLoaded(e.getValue());
                        }
                    }
                }
            }

        });
    }

}
