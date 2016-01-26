package mcjty.deepresonance.blocks.gencontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ControllerSounds {

    private GeneratorStartupSound generatorStartupSound;
    private GeneratorLoopSound generatorLoopSound;
    private GeneratorShutdownSound generatorShutdownSound;

    public void playStartup(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        generatorStartupSound = new GeneratorStartupSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorStartupSound);
    }

    public void playLoop(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        generatorLoopSound = new GeneratorLoopSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorLoopSound);
    }

    public void playShutdown(World worldObj, BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        generatorShutdownSound = new GeneratorShutdownSound(player, worldObj, pos.getX(), pos.getY(), pos.getZ());
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorShutdownSound);
    }

    public void stopShutdown() {
        if (generatorShutdownSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorShutdownSound);
            generatorShutdownSound = null;
        }
    }

    public void stopLoop() {
        if (generatorLoopSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorLoopSound);
            generatorLoopSound = null;
        }
    }

    public void stopStartup() {
        if (generatorStartupSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorStartupSound);
            generatorStartupSound = null;
        }
    }

    public boolean isStartupPlaying() {
        return generatorStartupSound != null;
    }

    public boolean isLoopPlaying() {
        return generatorLoopSound != null;
    }

    public boolean isShutdownPlaying() {
        return generatorShutdownSound != null;
    }
}
