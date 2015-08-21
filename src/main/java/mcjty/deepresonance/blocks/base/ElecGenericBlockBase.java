package mcjty.deepresonance.blocks.base;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.helper.RegisterHelper;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Elec332 on 20-8-2015.
 */
public class ElecGenericBlockBase extends GenericBlock {

    public ElecGenericBlockBase(Material material, Class<? extends TileEntity> tileEntityClass, String blockName) {
        super(DeepResonance.instance, material, tileEntityClass, false);
        this.blockName = blockName;
        setup();
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    /**
     * The code below was derived from ElecCore, you can find the original code here:
     * https://github.com/Elecs-Mods/ElecCore/blob/master/src/main/java/elec332/core/baseclasses/tileentity/BlockTileBase.java
     */

    private void setup(){
        this.setResistance(4.5F);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeStone);
        this.setBlockName(DeepResonance.MODID + "." + blockName);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    private final String blockName;

    public ElecGenericBlockBase registerTile() {
        GameRegistry.registerTileEntity(this.tileEntityClass, this.blockName);
        return this;
    }

    public ElecGenericBlockBase register() {
        RegisterHelper.registerBlock(this, this.blockName);
        return this;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileBase ?((TileBase)tile).onBlockActivated(player, side, hitX, hitY, hitZ):super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileBase) {
            ((TileBase)tile).onNeighborBlockChange(block);
        }

        super.onNeighborBlockChange(world, x, y, z, block);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileBase?((TileBase)tile).getDrops(fortune):super.getDrops(world, x, y, z, metadata, fortune);
    }
}
