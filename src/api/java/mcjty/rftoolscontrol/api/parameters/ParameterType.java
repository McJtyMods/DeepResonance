package mcjty.rftoolscontrol.api.parameters;

import java.util.HashMap;
import java.util.Map;

public enum ParameterType {
    PAR_STRING("string"),
    PAR_INTEGER("integer"),
    PAR_FLOAT("float"),
    PAR_SIDE("side"),
    PAR_BOOLEAN("boolean"),
    PAR_INVENTORY("inventory"),
    PAR_ITEM("item"),
    PAR_EXCEPTION("exception"),
    PAR_TUPLE("tuple"),
    PAR_FLUID("fluid"),
    PAR_VECTOR("vector"),
    PAR_LONG("long"),
    PAR_NUMBER("number");

    private final String name;

    private static final Map<String, ParameterType> TYPE_MAP = new HashMap<>();

    static {
        for (ParameterType type : values()) {
            TYPE_MAP.put(type.getName(), type);
        }
    }

    ParameterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ParameterType getByName(String name) {
        return TYPE_MAP.get(name);
    }
}
