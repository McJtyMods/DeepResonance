package mcjty.rftoolscontrol.api.code;

import mcjty.rftoolscontrol.api.parameters.ParameterDescription;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Opcode {

    private final String id;
    private final OpcodeOutput opcodeOutput;
    private final boolean isEvent;
    private final List<ParameterDescription> parameters;
    private final String outputDescription;
    private final IOpcodeRunnable runnable;
    private final List<String> description;
    private final boolean deprecated;

    private final int iconU;
    private final int iconV;
    private final String iconResource;

    private Opcode(Builder builder) {
        this.id = builder.id;
        this.opcodeOutput = builder.opcodeOutput;
        this.isEvent = builder.isEvent;
        this.parameters = new ArrayList<>(builder.parameters);
        this.iconU = builder.iconU;
        this.iconV = builder.iconV;
        this.runnable = builder.runnable;
        this.description = new ArrayList<>(builder.description);
        this.outputDescription = builder.outputDescription;
        this.deprecated = builder.deprecated;
        this.iconResource = builder.iconResource;
    }

    public String getId() {
        return id;
    }

    public OpcodeOutput getOpcodeOutput() {
        return opcodeOutput;
    }

    public boolean isEvent() {
        return isEvent;
    }

    @Nonnull
    public List<ParameterDescription> getParameters() {
        return parameters;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Nonnull
    public IOpcodeRunnable getRunnable() {
        return runnable;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getOutputDescription() {
        return outputDescription;
    }

    public int getIconU() {
        return iconU;
    }

    public int getIconV() {
        return iconV;
    }

    @Nullable
    public String getIconResource() {
        return iconResource;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Opcode opcode = (Opcode) o;

        if (!id.equals(opcode.id)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Opcode{" + id + '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static class Builder {

        private static final IOpcodeRunnable NOOP = ((processor, program, opcode) -> IOpcodeRunnable.OpcodeResult.POSITIVE);

        private String id;
        private OpcodeOutput opcodeOutput = OpcodeOutput.SINGLE;
        private boolean isEvent = false;
        private int iconU;
        private int iconV;
        private String iconResource;
        private List<ParameterDescription> parameters = new ArrayList<>();
        private IOpcodeRunnable runnable = NOOP;
        private List<String> description = Collections.emptyList();
        private String outputDescription = "No result";
        private boolean deprecated = false;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder deprecated(boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        public Builder description(String... description) {
            this.description = new ArrayList<>();
            Collections.addAll(this.description, description);
            return this;
        }

        public Builder outputDescription(String outputDescription) {
            this.outputDescription = outputDescription;
            return this;
        }

        public Builder runnable(IOpcodeRunnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public Builder opcodeOutput(OpcodeOutput opcodeOutput) {
            this.opcodeOutput = opcodeOutput;
            return this;
        }

        public Builder isEvent(boolean isEvent) {
            this.isEvent = isEvent;
            return this;
        }

        public Builder icon(int u, int v) {
            this.iconU = u;
            this.iconV = v;
            return this;
        }

        public Builder icon(int u, int v, String iconLocation) {
            this.iconU = u;
            this.iconV = v;
            this.iconResource = iconLocation;
            return this;
        }

        public Builder parameter(ParameterDescription parameter) {
            parameters.add(parameter);
            return this;
        }

        public Opcode build() {
            return new Opcode(this);
        }
    }
}
