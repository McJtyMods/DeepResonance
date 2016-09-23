package mcjty.rftoolscontrol.api.code;

import mcjty.rftoolscontrol.api.parameters.Parameter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Representation of a compiled opcode
 */
public interface ICompiledOpcode {

    @Nonnull
    List<Parameter> getParameters();
}
