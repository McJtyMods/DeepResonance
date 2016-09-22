package mcjty.rftoolscontrol.api.code;

import mcjty.rftoolscontrol.api.parameters.ParameterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Function {

    private final String id;
    private final String name;
    private final IFunctionRunnable functionRunnable;
    private final List<String> description;
    private final ParameterType returnType;

    private Function(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.functionRunnable = builder.functionRunnable;
        this.description = new ArrayList<>(builder.description);
        this.returnType = builder.returnType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public IFunctionRunnable getFunctionRunnable() {
        return functionRunnable;
    }

    public List<String> getDescription() {
        return description;
    }

    public ParameterType getReturnType() {
        return returnType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final IFunctionRunnable NOOP = ((processor, program) -> null);

        private String id;
        private IFunctionRunnable functionRunnable = NOOP;
        private List<String> description = Collections.emptyList();
        private ParameterType returnType;
        private String name;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder runnable(IFunctionRunnable runnable) {
            this.functionRunnable = runnable;
            return this;
        }

        public Builder description(String... description) {
            this.description = new ArrayList<>();
            Collections.addAll(this.description, description);
            return this;
        }

        public Builder type(ParameterType type) {
            this.returnType = type;
            return this;
        }

        public Function build() {
            return new Function(this);
        }
    }
}
