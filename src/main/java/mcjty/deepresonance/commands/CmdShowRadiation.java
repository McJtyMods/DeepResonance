package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.debug.DebugBlock;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.varia.GlobalCoordinate;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Map;

public class CmdShowRadiation extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "<level>";
    }

    @Override
    public String getCommand() {
        return "showradiation";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 2) {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        float level = fetchFloat(sender, args, 1, 0);

        World world = sender.getEntityWorld();
        DRRadiationManager manager = DRRadiationManager.getManager(world);

        Map<GlobalCoordinate, DRRadiationManager.RadiationSource> sources = manager.getRadiationSources();
        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> entry : sources.entrySet()) {
            GlobalCoordinate c = entry.getKey();
            if (c.getDimension() == world.provider.getDimension()) {
                DRRadiationManager.RadiationSource source = entry.getValue();
                int cx = c.getCoordinate().getX();
                int cy = c.getCoordinate().getY();
                int cz = c.getCoordinate().getZ();
                int radius = (int) source.getRadius();
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                for (int x = cx-radius; x < cx+radius ; x++) {
                    for (int y = cy-radius; y < cy+radius ; y++) {
                        for (int z = cz-radius; z < cz+radius ; z++) {
                            pos.setPos(x, y, z);
                            Block b = world.getBlockState(pos).getBlock();
                            if (world.isAirBlock(pos) || b == ModBlocks.debugBlock) {
                                QuadTree radiationTree = source.getRadiationTree(world, cx, cy, cz);
                                float rad = (float) radiationTree.factor(cx, cy, cz, x, y, z);
                                double distanceSq = pos.distanceSq(c.getCoordinate());
                                double distance = Math.sqrt(distanceSq);
                                float strength = (float) (source.getStrength() * (radius - distance) / radius) * rad;
                                if (level < 0) {
                                    if (strength < -level && strength > 10) {
                                        world.setBlockState(pos, ModBlocks.debugBlock.getDefaultState().withProperty(DebugBlock.STATUS, DebugBlock.STATUS_BLUE), 3);
                                    } else {
                                        world.setBlockToAir(pos);
                                    }
                                } else {
                                    if (strength >= level * 2) {
                                        world.setBlockState(pos, ModBlocks.debugBlock.getDefaultState().withProperty(DebugBlock.STATUS, DebugBlock.STATUS_RED), 3);
                                    } else if (strength >= level) {
                                        world.setBlockState(pos, ModBlocks.debugBlock.getDefaultState().withProperty(DebugBlock.STATUS, DebugBlock.STATUS_YELLOW), 3);
                                    } else {
                                        world.setBlockToAir(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
