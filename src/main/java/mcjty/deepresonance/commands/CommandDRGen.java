package mcjty.deepresonance.commands;

public class CommandDRGen extends DefaultCommand {

    public CommandDRGen() {
        super();
        registerCommand(new CmdSpawnCrystal());
        registerCommand(new CmdCrystal());
        registerCommand(new CmdInfoCrystal());
        registerCommand(new CmdListRadiation());
        registerCommand(new CmdCleanRadiation());
        registerCommand(new CmdShowRadiation());
    }

    @Override
    public String getName() {
        return "drgen";
    }

}
