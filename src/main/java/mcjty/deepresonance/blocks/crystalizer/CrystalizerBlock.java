package mcjty.deepresonance.blocks.crystalizer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.base.ElecGenericBlockBase;
import mcjty.deepresonance.blocks.smelter.SmelterTileEntity;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CrystalizerBlock extends ElecGenericBlockBase {

    public CrystalizerBlock(String blockName) {
        super(Material.rock, SmelterTileEntity.class, blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        currentTip.add("TESTERT");
        return currentTip;
    }

    @Override
    public String getIdentifyingIconName() {
        return "smelterActive";
    }
}
