package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;

@Command(label = "stop", usage = "stop",
        description = "Stops the server", permission = "server.stop")
public final class StopCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
		if (sender != null) {
            CommandHandler.sendMessage(sender, Grasscutter.getLanguage().This_command_can_only_run_from_console);
            return;
        }
        CommandHandler.sendMessage(null, Grasscutter.getLanguage().Stop_message);
        for (Player p : Grasscutter.getGameServer().getPlayers().values()) {
            CommandHandler.sendMessage(p, Grasscutter.getLanguage().Stop_message);
        }

        System.exit(1);
    }
}
