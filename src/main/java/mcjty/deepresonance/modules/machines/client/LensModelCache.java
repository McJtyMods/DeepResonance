package mcjty.deepresonance.modules.machines.client;

public class LensModelCache {} /* @todo 1.16 extends SimpleModelCache<EnumSet<Direction>> {

    public static final EnumSet<Direction> EMPTY_DIRS = EnumSet.noneOf(Direction.class);
    public static final LensModelCache INSTANCE = new LensModelCache();
    private static final EnumMap<Direction, QuadTransformer> transformations = new EnumMap<>(Direction.class);

    private IBakedModel model;

    private LensModelCache() {
        super(new DeepResonanceResourceLocation("block/lens"));
    }

    public IBakedModel setModel(IBakedModel model) {
        this.model = Preconditions.checkNotNull(model);
        return this;
    }

    @Override
    protected EnumSet<Direction> get(ItemStack stack) {
        return EnumSet.of(Direction.NORTH);
    }

    @Override
    protected EnumSet<Direction> get(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile != null) {
            return tile.getCapability(SubTileLens.LENSES).map(SubTileLens::getLenses).orElse(EMPTY_DIRS);
        }
        return EMPTY_DIRS;
    }

    @Override
    protected void bakeQuads(List<BakedQuad> quads, Direction side, EnumSet<Direction> key) {
        for (Direction dir : key) {
            quads.addAll(transformations.get(dir).processMany(model.getQuads(null, side, new Random(), EmptyModelData.INSTANCE)));
        }
    }

    static {
        for (Direction dir : Direction.values()) {
            TransformationMatrix m = new TransformationMatrix(new Vector3f(0, 0, 1), RenderHelper.quatFromXYZDegrees(new Vector3f(-90, 0, 0)), null, null);

            int x = dir.getAxis() == Direction.Axis.Z ? 180 + 90 * dir.getAxisDirection().getOffset() : (dir == Direction.UP ? 180 : 0);
            int z = dir.getAxis() == Direction.Axis.X ? 180 - 90 * dir.getAxisDirection().getOffset() : 0;
            Vector3f loc = new Vector3f(0, 0, 0);
            switch (dir.getOpposite()) {
                case SOUTH:
                    loc.add(0, 1, 0);
                    break;
                case NORTH:
                    loc.add(0, 0, 1);
                    break;
                case WEST:
                    loc.add(1, 0, 0);
                    break;
                case EAST:
                    loc.add(0, 1, 0);
                    break;
                case DOWN:
                    loc.add(0, 1, 1);
            }
            m = RenderHelper.merge(m, new TransformationMatrix(loc, RenderHelper.quatFromXYZDegrees(new Vector3f(x, 0, z)), null, null)).getTransformaion();

            transformations.put(dir, new QuadTransformer(m));
        }
    }

}
*/