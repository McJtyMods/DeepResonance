package mcjty.deepresonance.commands;

public class CommandDRGen extends DefaultCommand {

    public CommandDRGen() {
        super();
        registerCommand(new CmdSpawnCrystal());
    }

    @Override
    public String getCommandName() {
        return "drgen";
    }
}
