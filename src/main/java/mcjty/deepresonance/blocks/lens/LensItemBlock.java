package mcjty.deepresonance.blocks.lens;

import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.lib.container.GenericItemBlock;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class LensItemBlock extends GenericItemBlock {
    public LensItemBlock(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        ForgeDirection direction = BlockTools.getOrientationHoriz(metadata);
        if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN) {
            if (world.isRemote) {
                Logging.warn(player, "You can only place this vertical!");
            }
            return false;
        }
        Block block = world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
        if (block != TankSetup.tank) {
            if (world.isRemote) {
                Logging.warn(player, "You can only place this against a tank!");
            }
            return false;
        }

        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }
}
