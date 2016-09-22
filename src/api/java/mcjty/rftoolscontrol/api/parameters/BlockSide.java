package mcjty.rftoolscontrol.api.parameters;

import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * How to identify a side on a block
 */
public class BlockSide {
    @Nullable private final String nodeName;          // An inventory on a network
    @Nullable private final EnumFacing side;      // The side at which the inventory can be found

    public BlockSide(@Nullable String name, @Nullable EnumFacing side) {
        this.nodeName = (name == null || name.isEmpty()) ? null : name;
        this.side = side;
    }

    @Nullable
    public String getNodeName() {
        return nodeName;
    }

    public boolean hasNodeName() {
        return nodeName != null && !nodeName.isEmpty();
    }

    @Nullable
    public EnumFacing getSide() {
        return side;
    }

    @Override
    public String toString() {
        if (side == null) {
            return "*";
        } else {
            return side.toString();
        }
    }

    public String getStringRepresentation() {
        EnumFacing facing = getSide();

        String s = facing == null ? "" : StringUtils.left(facing.getName().toUpperCase(), 1);
        if (getNodeName() == null) {
            return s;
        } else {
            return StringUtils.left(getNodeName(), 7) + " " + s;
        }
    }

}
