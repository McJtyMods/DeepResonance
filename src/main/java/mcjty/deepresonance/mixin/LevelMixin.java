package mcjty.deepresonance.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.deepresonance.modules.radiation.manager.RadiationShieldUpdates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
abstract class LevelMixin {

    @WrapOperation(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState deepresonance$updateRadiationShield(LevelChunk chunk, BlockPos pos, BlockState state, int flags,
            Operation<BlockState> original) {
        BlockState previous = original.call(chunk, pos, state, flags);
        if (previous != null && (Object) this instanceof ServerLevel level) {
            RadiationShieldUpdates.onBlockChanged(level, pos, previous, state);
        }
        return previous;
    }
}
