package mcjty.deepresonance.modules.machines.tile;

public class SubTileLens {} /* @todo 1.16 extends SubTileLogicBase {

    @CapabilityInject(SubTileLens.class)
    public static Capability<SubTileLens> LENSES;

    private final EnumMap<Direction, ILens> lenses;
    private final EnumMap<Direction, LazyOptional<ILens>> lensCapabilities;
    private VoxelShape shape = VoxelShapes.empty();
    private final LazyOptional<SubTileLens> capability = LazyOptional.of(() -> this);

    public SubTileLens(Data data) {
        super(data);
        this.lenses = Maps.newEnumMap(Direction.class);
        this.lensCapabilities = Maps.newEnumMap(Direction.class);
    }

    @Nonnull
    public EnumSet<Direction> getLenses() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        Collection<Direction> dirs = lenses.keySet();
        return dirs.isEmpty() ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(dirs);
    }

    public boolean addLens(ILens lens, Direction side) {
        if (side == null) {
            return false;
        }
        if (!(lens instanceof DefaultLens)) {
            throw new UnsupportedOperationException(); //todo
        }
        if (!isValidLocation(side)) {
            return false;
        }
        VoxelShape shape = HitboxHelper.rotateFromDown(lens.getHitbox(), side);
        if (VoxelShapes.compare(WorldHelper.getShape(getLevel(), getPos()), shape, IBooleanFunction.AND)) {
            return false;
        }
        addLens_(lens, side);
        return true;
    }

    private void addLens_(ILens lens, Direction side) {
        lenses.put(side, lens);
        VoxelShape shape = new IndexedVoxelShape(HitboxHelper.rotateFromDown(lens.getHitbox(), side), 0, lens);
        if (this.shape == null) {
            this.shape = shape;
        } else {
            this.shape = HitboxHelper.combineShapes(this.shape, shape);
        }
        LazyOptional<ILens> newCap = LazyOptional.of(() -> lens);
        if (lensCapabilities.containsKey(side)) {
            lensCapabilities.computeIfPresent(side, (s, old) -> {
                old.invalidate();
                return newCap;
            });
        } else {
            lensCapabilities.put(side, newCap);
        }
        syncSides();
        WorldHelper.markBlockForUpdate(getLevel(), getPos());
    }

    private void removeLens_(ILens lenz) {
        Direction side = lenses.keySet().stream().filter(dir -> lenses.get(dir) == lenz).findFirst().orElseThrow(NullPointerException::new);
        if (side == null) {
            throw new RuntimeException();
        }
        lenses.remove(side);
        lensCapabilities.remove(side).invalidate();
        this.shape = lenses.entrySet().stream().reduce(VoxelShapes.empty(), (nbc, entry) -> {
            ILens lens = entry.getValue();
            return new IndexedVoxelShape(HitboxHelper.rotateFromDown(lens.getHitbox(), entry.getKey()), 0, lens);
        }, HitboxHelper::combineShapes);

        syncSides();
        WorldHelper.markBlockForUpdate(getLevel(), getPos());
    }

    private void syncSides() {
        CompoundNBT tag = new CompoundNBT();
        lenses.forEach((dir, lens) -> tag.put(dir.name(), lens.serializeNBT()));
        sendPacket(1, tag);
    }

    private void clear() {
        lenses.clear();
        clearCapabilities();
    }

    private void clearCapabilities() {
        lensCapabilities.values().forEach(LazyOptional::invalidate);
        lensCapabilities.clear();
    }

    @SuppressWarnings("all")
    private boolean isValidLocation(Direction side) {
        return WorldHelper.getBlockState(getLevel(), getPos().offset(side)).isSolidSide(getLevel(), getPos().offset(side), side.getOpposite());
    }

//    private void validateCapabilities() {
//        for (Direction dir : lenses.keySet()) {
//            LazyOptional<ILens> cap = lensCapabilities.get(dir);
//            if (cap == null || !cap.isPresent()) {
//                final ILens lens = Preconditions.checkNotNull(lenses.get(dir));
//                lensCapabilities.put(dir, LazyOptional.of(() -> lens));
//            }
//        }
//    }

    @Override
    public void neighborChanged(BlockPos neighborPos, Block changedBlock, boolean observer) {
        lenses.forEach((dir, lens) -> lens.checkNeighbors(getLevel(), getPos(), dir));
        for (Direction direction : Direction.values()) {
            ILens lens = lenses.get(direction);
            if (lens != null) {
                if (!isValidLocation(direction)) {
                    removeLens_(lens);
                    WorldHelper.dropStack(getLevel(), getPos(), lens.getPickBlock());
                }
            }
        }
    }

    @Override
    public void onLoad() {
        if (WorldHelper.isServer(getLevel())) {
            ElecCore.tickHandler.registerCall(() -> lenses.forEach((dir, lens) -> lens.checkNeighbors(getLevel(), getPos(), dir)), getLevel());
        }
    }

    @Override
    public void invalidate() {
        clearCapabilities();
    }

    @Override
    public void onDataPacket(int id, CompoundNBT tag) {
        if (id == 1) {
            clear();
            for (Direction dir : Direction.values()) {
                if (tag.contains(dir.name())) {
                    ILens lens = new DefaultLens();
                    lens.deserializeNBT(tag.getCompound(dir.name()));
                    addLens_(lens, dir);
                }
            }
            WorldHelper.markBlockForRenderUpdate(getLevel(), getPos());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, int data) {
        return shape;
    }

    @Nullable
    @Override
    public ItemStack getStack(@Nonnull RayTraceResult hit, PlayerEntity player) {
        return ((ILens) hit.hitInfo).getPickBlock();
    }

    @Override
    public boolean canBeRemoved() {
        return lenses.isEmpty();
    }

    @Override
    public boolean removedByPlayer(@Nonnull PlayerEntity player, boolean willHarvest, @Nonnull RayTraceResult hit) {
        if (!Preconditions.checkNotNull(getLevel()).isRemote && hit.hitInfo instanceof ILens) {
            ILens lens = (ILens) hit.hitInfo;
            removeLens_(lens);
        }
        return false;
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        clear();
        for (Direction dir : Direction.values()) {
            String s = "lens_" + dir.getName();
            if (compound.contains(s)) {
                ILens lens = new DefaultLens();
                lens.deserializeNBT(compound.getCompound(s));
                addLens_(lens, dir);
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT compound) {
        for (Direction dir : Direction.values()) {
            if (lenses.containsKey(dir)) {
                compound.put("lens_" + dir.getName(), lenses.get(dir).serializeNBT());
            }
        }
        return compound;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null || Preconditions.checkNotNull(getLevel()).isRemote) {
            return LENSES.orEmpty(cap, capability);
        }
        return MachinesModule.LENS_CAPABILITY.orEmpty(cap, lensCapabilities.getOrDefault(side, LazyOptional.empty()));
    }

    static {
        RegistryHelper.registerEmptyCapability(SubTileLens.class);
    }

}
*/