package mcjty.deepresonance.api.laser;

import net.minecraft.util.Direction;

public interface ILensMirror {

    Direction bounceLaser(Direction originalHeading);

}
