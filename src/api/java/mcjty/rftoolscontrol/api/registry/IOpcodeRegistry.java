package mcjty.rftoolscontrol.api.registry;

import mcjty.rftoolscontrol.api.code.Opcode;

public interface IOpcodeRegistry {

    // Register opcodes in CommonProxy.init
    void register(Opcode opcode);

}
