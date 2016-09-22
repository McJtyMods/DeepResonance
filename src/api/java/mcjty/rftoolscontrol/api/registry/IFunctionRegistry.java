package mcjty.rftoolscontrol.api.registry;

import mcjty.rftoolscontrol.api.code.Function;

public interface IFunctionRegistry {

    // Register functions in CommonProxy.init
    void register(Function function);

}
