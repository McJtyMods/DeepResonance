package mcjty.deepresonance.worldgen;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.ore.ResonatingOreBlock;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.Random;

public class DeepWorldGenerator implements IWorldGenerator {

    public static final String RETRO_NAME = "DeepResGen";
    public static DeepWorldGenerator instance = new DeepWorldGenerator();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generateWorld(random, chunkX, chunkZ, world, true);
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        if (!newGen && !WorldGenConfiguration.retrogen) {
            return;
        }

        if (world.provider.getDimension() == DimensionType.OVERWORLD.getId()) {
            if (WorldGenConfiguration.generateOverworldOre) {
                addOreSpawn(ModBlocks.resonatingOreBlock, (byte) ResonatingOreBlock.OreType.ORE_OVERWORLD.ordinal(), Blocks.STONE, world, random, chunkX * 16, chunkZ * 16,
                        WorldGenConfiguration.minVeinSize, WorldGenConfiguration.maxVeinSize, WorldGenConfiguration.chancesToSpawn, WorldGenConfiguration.minY, WorldGenConfiguration.maxY);
            }
        } else if (world.provider.getDimension() == DimensionType.NETHER.getId()) {
            if (WorldGenConfiguration.generateNetherOre) {
                addOreSpawn(ModBlocks.resonatingOreBlock, (byte) ResonatingOreBlock.OreType.ORE_NETHER.ordinal(), Blocks.NETHERRACK, world, random, chunkX * 16, chunkZ * 16,
                        WorldGenConfiguration.minVeinSize * 2, WorldGenConfiguration.maxVeinSize * 2, WorldGenConfiguration.chancesToSpawn, WorldGenConfiguration.minY, WorldGenConfiguration.maxY);
            }
        } else if (world.provider.getDimension() == DimensionType.THE_END.getId()) {
            if (WorldGenConfiguration.generateEndOre) {
                addOreSpawn(ModBlocks.resonatingOreBlock, (byte) ResonatingOreBlock.OreType.ORE_END.ordinal(), Blocks.END_STONE, world, random, chunkX * 16, chunkZ * 16,
                        WorldGenConfiguration.minVeinSize, WorldGenConfiguration.maxVeinSize, WorldGenConfiguration.chancesToSpawn, WorldGenConfiguration.minY, WorldGenConfiguration.maxY);
            }
        } else {
            if (WorldGenConfiguration.generateOreOtherDimensions) {
                addOreSpawn(ModBlocks.resonatingOreBlock, (byte) ResonatingOreBlock.OreType.ORE_OVERWORLD.ordinal(), Blocks.STONE, world, random, chunkX * 16, chunkZ * 16,
                        WorldGenConfiguration.minVeinSize, WorldGenConfiguration.maxVeinSize, WorldGenConfiguration.chancesToSpawn, WorldGenConfiguration.minY, WorldGenConfiguration.maxY);
            }
        }

        if (world.provider.getDimension() == DimensionType.OVERWORLD.getId()) {
            if (WorldGenConfiguration.generateOverworldCrystals) {
                if (WorldGenConfiguration.crystalSpawnChance > 0 && random.nextInt(WorldGenConfiguration.crystalSpawnChance) == 0) {
                    attemptSpawnCrystal(random, chunkX, chunkZ, world, Blocks.STONE, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        } else if (world.provider.getDimension() == DimensionType.NETHER.getId()) {
            if (WorldGenConfiguration.generateNetherCrystals) {
                if (WorldGenConfiguration.crystalSpawnChance > 0 && random.nextInt(WorldGenConfiguration.crystalSpawnChance) == 0) {
                    attemptSpawnCrystal(random, chunkX, chunkZ, world, Blocks.NETHERRACK, 3.0f, 1.5f, 2.0f, 0.5f);
                }
            }
        } else {
            if (WorldGenConfiguration.generateCrystalsOtherDimensions) {
                if (WorldGenConfiguration.crystalSpawnChance > 0 && random.nextInt(WorldGenConfiguration.crystalSpawnChance) == 0) {
                    attemptSpawnCrystal(random, chunkX, chunkZ, world, Blocks.STONE, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }

        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).markDirty();
        }
    }

    private void attemptSpawnCrystal(Random random, int chunkX, int chunkZ, World world, Block spawnOn, float str, float pow, float eff, float pur) {
        for (int i = 0 ; i < WorldGenConfiguration.crystalSpawnTries ; i++) {
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int z = chunkZ * 16 + random.nextInt(16) + 8;
            int y;
            if (world.provider.getDimension() == DimensionType.NETHER.getId()) {
                y = 60;
            } else {
                y = world.getTopSolidOrLiquidBlock(new BlockPos(x, world.provider.getActualHeight(), z)).getY() - 1;
            }
            boolean air = false;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
            while (y > 1 && !air) {
                if (world.isAirBlock(pos)) {
                    air = true;
                }
                y--;
                pos.setPos(x, y, z);
            }
            if (air) {
                while (y > 1 && air) {
                    if (!world.isAirBlock(pos)) {
                        air = false;
                    } else {
                        y--;
                        pos.setPos(x, y, z);
                    }
                }
                if (!air) {
                    if (world.getBlockState(pos).getBlock() == spawnOn) {
                        if (WorldGenConfiguration.verboseSpawn) {
                            Logging.log("Spawned a crystal at: " + x + "," + y + "," + z);
                        }
                        ResonatingCrystalTileEntity.spawnRandomCrystal(world, random, new BlockPos(pos.setPos(x, y+1, z)), str, pow, eff, pur);
                        return;
                    }
                }
            }
        }
    }

    public void addOreSpawn(Block block, byte blockMeta, Block targetBlock, World world, Random random, int blockXPos, int blockZPos, int minVeinSize, int maxVeinSize, int chancesToSpawn, int minY, int maxY) {
        WorldGenMinable minable = new WorldGenMinable(block.getStateFromMeta(blockMeta), (minVeinSize + random.nextInt(maxVeinSize - minVeinSize + 1)), BlockMatcher.forBlock(targetBlock));
        for (int i = 0 ; i < chancesToSpawn ; i++) {
            int posX = blockXPos + random.nextInt(16);
            int posY = minY + random.nextInt(maxY - minY);
            int posZ = blockZPos + random.nextInt(16);
            minable.generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {
        NBTTagCompound genTag = event.getData().getCompoundTag(RETRO_NAME);
        if (!genTag.hasKey("generated")) {
            // If we did not have this key then this is a new chunk and we will have proper ores generated.
            // Otherwise we are saving a chunk for which ores are not yet generated.
            genTag.setBoolean("generated", true);
        }
        event.getData().setTag(RETRO_NAME, genTag);
    }

    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {
        int dim = WorldHelper.getDimID(event.getWorld());

        boolean regen = false;
        NBTTagCompound tag = (NBTTagCompound) event.getData().getTag(RETRO_NAME);
        NBTTagList list = null;
        Pair<Integer,Integer> cCoord = Pair.of(event.getChunk().x, event.getChunk().z);

        if (tag != null) {
            boolean generated = WorldGenConfiguration.retrogen && !tag.hasKey("generated");
            if (generated) {
                if (WorldGenConfiguration.verboseSpawn) {
                    Logging.log("Queuing Retrogen for chunk: " + cCoord.toString() + ".");
                }
                regen = true;
            }
        } else {
            regen = WorldGenConfiguration.retrogen;
        }

        if (regen) {
            ArrayDeque<WorldTickHandler.RetroChunkCoord> chunks = WorldTickHandler.chunksToGen.get(dim);

            if (chunks == null) {
                WorldTickHandler.chunksToGen.put(dim, new ArrayDeque<WorldTickHandler.RetroChunkCoord>(128));
                chunks = WorldTickHandler.chunksToGen.get(dim);
            }
            if (chunks != null) {
                chunks.addLast(new WorldTickHandler.RetroChunkCoord(cCoord, list));
                WorldTickHandler.chunksToGen.put(dim, chunks);
            }
        }
    }

}
