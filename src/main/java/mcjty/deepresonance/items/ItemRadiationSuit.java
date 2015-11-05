package mcjty.deepresonance.items;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 29-9-2015.
 */
public class ItemRadiationSuit extends ItemArmor {

    private final String textureSuffix;

    public ItemRadiationSuit(ArmorMaterial material, int renderIndex, int armorType, String name) {
        super(material, renderIndex, armorType);
        setUnlocalizedName(name);
        this.textureSuffix = name;
        setTextureName(DeepResonance.MODID+":radiationSuit"+name);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("Every chest piece of the radiation suit");
        list.add("adds a bit of protection for radiation");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return DeepResonance.MODID+":textures/items/suitTexture"+textureSuffix+".png";
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName() + armorType;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    public static int countSuitPieces(EntityLivingBase entity){
        int cnt = 0;
        for (int i = 1; i < 5; i++) {
            ItemStack stack = entity.getEquipmentInSlot(i);
            if (stack != null && (stack.getItem() instanceof ItemRadiationSuit)) {
                cnt++;
            }
        }
        return cnt;
    }
}
