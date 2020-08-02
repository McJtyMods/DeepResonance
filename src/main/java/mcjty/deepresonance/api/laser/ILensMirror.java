package mcjty.deepresonance.api.laser;

import net.minecraft.util.Direction;

/**
 * Created by Elec332 on 28-7-2020
 */
public interface ILensMirror {

    Direction bounceLaser(Direction originalHeading);

}
