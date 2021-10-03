package mcjty.deepresonance.modules.core.client;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;

public class ModelLoaderCoreModule {

    public static final ResourceLocation RESONATING_CRYSTAL = new ResourceLocation(DeepResonance.MODID, "resonating_crystal_model");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
//    public static final BooleanProperty VERY_PURE = BooleanProperty.create("very_pure");

    // @todo 1.16
//    public static final StateContainer<Block, BlockState> stateContainer = RenderingRegistry.instance().registerBlockStateLocation(RESONATING_CRYSTAL, FACING, EMPTY, VERY_PURE);

    public static final ModelProperty<Float> POWER = new ModelProperty<>();
    public static final ModelProperty<Float> PURITY = new ModelProperty<>();

    // @todo 1.16
//    @Nonnull
//    @Override
//    public Collection<ResourceLocation> getHandlerModelLocations() {
//        return Lists.newArrayList(CoreModule.RESONATING_CRYSTAL_BLOCK.getId());
//    }

//    @Override
//    public void registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter, ModelLoader modelLoader, BiConsumer<ModelResourceLocation, IBakedModel> registry) {
//        BlockState state = stateContainer.getBaseState();
//        BiFunction<Float, Float, IBakedModel> itemModelGetter_ = null;
//        for (Direction dir : FACING.getAllowedValues()) {
//            IBakedModel emptyNaturalModel = bakedModelGetter.apply(getLocation(state.with(FACING, dir).with(EMPTY, true).with(VERY_PURE, false)));
//            IBakedModel fullNaturalModel = bakedModelGetter.apply(getLocation(state.with(FACING, dir).with(EMPTY, false).with(VERY_PURE, false)));
//            IBakedModel emptyPureModel = bakedModelGetter.apply(getLocation(state.with(FACING, dir).with(EMPTY, true).with(VERY_PURE, true)));
//            IBakedModel fullPureModel = bakedModelGetter.apply(getLocation(state.with(FACING, dir).with(EMPTY, false).with(VERY_PURE, true)));
//
//            final BiFunction<Float, Float, IBakedModel> modelGetter = (power, purity) -> {
//                if (power == null || purity == null) {
//                    return fullNaturalModel;
//                }
//                if (CrystalHelper.isEmpty(power)) {
//                    if (CrystalHelper.isVeryPure(purity)) {
//                        return emptyPureModel;
//                    } else {
//                        return emptyNaturalModel;
//                    }
//                } else {
//                    if (CrystalHelper.isVeryPure(purity)) {
//                        return fullPureModel;
//                    } else {
//                        return fullNaturalModel;
//                    }
//                }
//            };
//            if (dir == Direction.NORTH) {
//                itemModelGetter_ = modelGetter;
//            }
//
//            IBakedModel wrapped = new WrappedModel(fullNaturalModel) {
//
//                @Nonnull
//                @Override
//                public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
//                    return modelGetter.apply(extraData.getData(POWER), extraData.getData(PURITY)).getQuads(state, side, rand, extraData);
//                }
//
//            };
//            registry.accept(BlockModelShapes.getModelLocation(Preconditions.checkNotNull(CoreModule.RESONATING_CRYSTAL_BLOCK.get()).getDefaultState().with(FACING, dir)), wrapped);
//        }
//
//        final BiFunction<Float, Float, IBakedModel> itemModelGetter = Preconditions.checkNotNull(itemModelGetter_);
//        final ItemOverrideList iol = new AbstractItemOverrideList() {
//
//            @Nullable
//            @Override
//            protected IBakedModel getModel(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
//                float power = AbstractItemBlock.getTileData(stack).getFloat("power");
//                float purity = AbstractItemBlock.getTileData(stack).getFloat("purity");
//                return itemModelGetter.apply(power, purity);
//            }
//
//        };
//        IBakedModel wrapped = new WrappedModel(itemModelGetter.apply(100F, 0F)) { //Full & non-pure (natural)
//
//            @Nonnull
//            @Override
//            public ItemOverrideList getOverrides() {
//                return iol;
//            }
//
//        };
//        registry.accept(new ModelResourceLocation(CoreModule.RESONATING_CRYSTAL_ITEM.getId(), "inventory"), wrapped);
//    }
//
//    private ModelResourceLocation getLocation(BlockState state) {
//        return BlockModelShapes.getModelLocation(RESONATING_CRYSTAL, state);
//    }

}
