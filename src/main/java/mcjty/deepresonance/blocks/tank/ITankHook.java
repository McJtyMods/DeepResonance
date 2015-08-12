package mcjty.deepresonance.blocks.tank;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 12-8-2015.
 */
public interface ITankHook {

    public void hook(TileTank tank, ForgeDirection from);

    public void unHook(TileTank tank, ForgeDirection from);

    public void onContentChanged(TileTank tank, ForgeDirection from);

}
