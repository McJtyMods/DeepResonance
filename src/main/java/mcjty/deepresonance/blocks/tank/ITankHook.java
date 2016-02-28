package mcjty.deepresonance.blocks.tank;

import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 12-8-2015.
 */
public interface ITankHook {

    public void hook(TileTank tank, EnumFacing from);

    public void unHook(TileTank tank, EnumFacing from);

    public void onContentChanged(TileTank tank, EnumFacing from);

}
