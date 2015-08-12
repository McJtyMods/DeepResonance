package mcjty.deepresonance.commands;

public class CommandDRGen extends DefaultCommand {

    public CommandDRGen() {
        super();
        registerCommand(new CmdSpawnCrystal());
        registerCommand(new CmdListRadiation());
        registerCommand(new CmdCleanRadiation());
    }

    @Override
    public String getCommandName() {
        return "drgen";
    }
}
