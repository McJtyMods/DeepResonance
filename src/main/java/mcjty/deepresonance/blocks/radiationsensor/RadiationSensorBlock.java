package mcjty.deepresonance.blocks.radiationsensor;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RadiationSensorBlock extends Block {

    public RadiationSensorBlock() {
        super(Material.glass);
        setHardness(3.0f);
        setResistance(500.0f);
        setSoundType(SoundType.GLASS);
        setHarvestLevel("pickaxe", 0);
        setUnlocalizedName(DeepResonance.MODID + ".radiation_sensor");
        setRegistryName("radiation_sensor");
        setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerBlock(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
