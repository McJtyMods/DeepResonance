package mcjty.deepresonance.modules.radiation.item;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.deepresonance.modules.radiation.manager.QuadTree;
import mcjty.deepresonance.modules.radiation.network.PacketGetRadiationLevel;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Map;

public class RadiationMonitorItem extends Item {

    public static final Identifier RADIATION_PROPERTY = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "radiation");

    private static long lastTime = 0;
    public static float radiationStrength = 0.0f;

    public RadiationMonitorItem(Properties properties) {
        super(properties);
    }

    public static float calculateRadiationStrength(Level world, GlobalPos player) {
        ResourceKey<Level> id = player.dimension();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        float maxStrength = -1.0f;
        for (Map.Entry<GlobalPos, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalPos coordinate = source.getKey();
            if (coordinate.dimension() == id) {
                DRRadiationManager.RadiationSource radiationSource = source.getValue();
                float radius = radiationSource.getRadius();
                float radiusSq = radius * radius;
                double distanceSq = player.pos().distSqr(coordinate.pos());
                if (distanceSq < radiusSq) {
                    double distance = Math.sqrt(distanceSq);
                    float strength = (float) (radiationSource.getStrength() * (radius - distance) / radius);
                    int cx = coordinate.pos().getX();
                    int cy = coordinate.pos().getY();
                    int cz = coordinate.pos().getZ();
                    QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                    strength = strength * (float) radiationTree.factor2(cx, cy, cz, player.pos().getX(), player.pos().getY()+1, player.pos().getZ());
                    if (strength > maxStrength) {
                        maxStrength = strength;
                    }
                }
            }
        }
        return maxStrength;
    }

    public static void fetchRadiation(LivingEntity player) {
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() - lastTime > 250) {
            ResourceKey<Level> id = player.level().dimension();
            lastTime = System.currentTimeMillis();
            GlobalPos c = GlobalPos.of(id, player.blockPosition());
            DeepResonanceMessages.sendToServer(PacketGetRadiationLevel.create(c));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        fetchRadiation(SafeClientTools.getClientPlayer());
        if (radiationStrength <= 0.0f) {
            list.accept(ComponentFactory.literal("No radiation detected").withStyle(ChatFormatting.GREEN));
        } else {
            list.accept(ComponentFactory.literal("Radiation: " + (int)radiationStrength + "!").withStyle(ChatFormatting.RED));
        }
    }

}
