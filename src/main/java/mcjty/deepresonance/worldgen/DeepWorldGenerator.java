package mcjty.deepresonance.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class DeepWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        addOreSpawn(ModBlocks.resonatingOreBlock, (byte) 0, Blocks.stone, world, random, chunkX * 16, chunkZ * 16,
                WorldGenConfiguration.minVeinSize, WorldGenConfiguration.maxVeinSize, WorldGenConfiguration.chancesToSpawn, WorldGenConfiguration.minY, WorldGenConfiguration.maxY);


        if (WorldGenConfiguration.crystalSpawnChance > 0 && random.nextInt(WorldGenConfiguration.crystalSpawnChance) == 0) {
            attemptSpawnCrystal(random, chunkX, chunkZ, world);
        }
    }

    private void attemptSpawnCrystal(Random random, int chunkX, int chunkZ, World world) {
        System.out.println("Crystal Spawn attempt at: " + chunkX+","+chunkZ);
        for (int i = 0 ; i < WorldGenConfiguration.crystalSpawnTries ; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = world.getTopSolidOrLiquidBlock(x, z)-1;
            boolean air = false;
            while (y > 1 && !air) {
                if (world.isAirBlock(x, y, z)) {
                    air = true;
                }
                y--;
            }
            if (air) {
                while (y > 1 && air) {
                    if (!world.isAirBlock(x, y, z)) {
                        air = false;
                    } else {
                        y--;
                    }
                }
                if (!air) {
                    if (world.getBlock(x, y, z) == Blocks.stone) {
                        System.out.println("Found a good spot at: " + x + "," + y + "," + z);
                        world.setBlock(x, y+1, z, ModBlocks.resonatingCrystalBlock, 0, 3);
                        return;
                    }
                }
            }
        }
    }

    public void addOreSpawn(Block block, byte blockMeta, Block targetBlock,
                            World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
        WorldGenMinable minable = new WorldGenMinable(block, blockMeta, (minVeinSize - random.nextInt(maxVeinSize - minVeinSize)), targetBlock);
        for (int i = 0 ; i < chancesToSpawn ; i++) {
            int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            minable.generate(world, random, posX, posY, posZ);
        }
    }

}
