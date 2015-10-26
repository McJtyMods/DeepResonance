package mcjty.deepresonance.blocks.gencontroller;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;

public class ControllerSounds {
    private GeneratorStartupSound generatorStartupSound;
    private GeneratorLoopSound generatorLoopSound;
    private GeneratorShutdownSound generatorShutdownSound;

    public void playStartup(World worldObj, int xCoord, int yCoord, int zCoord) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorStartupSound = new GeneratorStartupSound(player, worldObj, xCoord, yCoord, zCoord);
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorStartupSound);
    }

    public void playLoop(World worldObj, int xCoord, int yCoord, int zCoord) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorLoopSound = new GeneratorLoopSound(player, worldObj, xCoord, yCoord, zCoord);
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorLoopSound);
    }

    public void playShutdown(World worldObj, int xCoord, int yCoord, int zCoord) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorShutdownSound = new GeneratorShutdownSound(player, worldObj, xCoord, yCoord, zCoord);
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
