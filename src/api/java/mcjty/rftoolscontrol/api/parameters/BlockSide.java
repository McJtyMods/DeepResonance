package mcjty.rftoolscontrol.api.parameters;

import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * This class identifies a side of a network blocked. This basically
 * is an optional nodename and an optional side. If side is null then
 * it means the node or processor itself.
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BlockSide blockSide = (BlockSide) o;

        if (nodeName != null ? !nodeName.equals(blockSide.nodeName) : blockSide.nodeName != null) {
            return false;
        }
        if (side != blockSide.side) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeName != null ? nodeName.hashCode() : 0;
        result = 31 * result + (side != null ? side.hashCode() : 0);
        return result;
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
