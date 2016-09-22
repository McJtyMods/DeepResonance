package mcjty.rftoolscontrol.api.parameters;

import mcjty.rftoolscontrol.api.code.Function;

public class ParameterValue {

    private final int variableIndex;
    private final Object value;
    private final Function function;

    private ParameterValue(int variableIndex, Object value, Function function) {
        this.variableIndex = variableIndex;
        this.value = value;
        this.function = function;
    }

    public int getVariableIndex() {
        return variableIndex;
    }

    public Object getValue() {
        return value;
    }

    public Function getFunction() {
        return function;
    }

    public boolean isConstant() {
        return variableIndex == -1 && function == null;
    }

    public boolean isVariable() {
        return variableIndex != -1;
    }

    public boolean isFunction() {
        return function != null;
    }

    public static ParameterValue constant(Object value) {
        return new ParameterValue(-1, value, null);
    }

    public static ParameterValue variable(int index) {
        return new ParameterValue(index, null, null);
    }

    public static ParameterValue function(Function function) {
        return new ParameterValue(-1, null, function);
    }
}
