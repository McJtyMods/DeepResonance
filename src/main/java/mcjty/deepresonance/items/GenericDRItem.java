package mcjty.deepresonance.items;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.McJtyRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenericDRItem extends Item {

    public GenericDRItem(String name) {
        setUnlocalizedName(DeepResonance.MODID + "." + name);
        setRegistryName(name);
        setCreativeTab(DeepResonance.setup.getTab());
        McJtyRegister.registerLater(this, DeepResonance.instance);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


}
