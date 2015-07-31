package mcjty.deepresonance.blocks.crystals;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;

import java.text.DecimalFormat;
import java.util.List;

public class ResonatingCrystalBlock extends GenericBlock {

    public ResonatingCrystalBlock() {
        super(DeepResonance.instance, Material.glass, ResonatingCrystalTileEntity.class, false);
        setBlockName("resonatingCrystalBlock");
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("You can feel the latent power present in this crystal.");
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {

            list.add(EnumChatFormatting.GREEN + "Strength: " + new DecimalFormat("#.##").format(tagCompound.getFloat("strength")) + "%");
            list.add(EnumChatFormatting.GREEN + "Efficiency: " + new DecimalFormat("#.##").format(tagCompound.getFloat("efficiency")) + "%");
            list.add(EnumChatFormatting.GREEN + "Purity: " + new DecimalFormat("#.##").format(tagCompound.getFloat("purity")) + "%");
            list.add(EnumChatFormatting.YELLOW + "Power left: " + new DecimalFormat("#.##").format(tagCompound.getFloat("power")) + "%");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            currenttip.add(EnumChatFormatting.GREEN + "Strength: " + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getStrength()) + "%");
            currenttip.add(EnumChatFormatting.GREEN + "Efficiency: " + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getEfficiency()) + "%");
            currenttip.add(EnumChatFormatting.GREEN + "Purity: " + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getPurity()) + "%");
            currenttip.add(EnumChatFormatting.YELLOW + "Power left: " + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getPower()) + "%");
        }
        return currenttip;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public String getSideIconName() {
        return "crystal";
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
