package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.command.handler.APISubCommand;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.command.CommandSender;

public class CoCCommand extends APICommand {

    public CoCCommand() {
        super("coc", "command.coc", TranslationKeys.COC_COMMAND_COC_USAGE, TranslationKeys.COC_COMMAND_COC_DESCRIPTION, "clashofclubs");
        addSubCommand(new CoCRebuildCommand());
        addSubCommand(new CoCCheatCommand());
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        for (APISubCommand apiSubCommand : getSubCommandList()) {
            if (apiPlayer.hasPermission(apiSubCommand.getPermission()))
                apiSubCommand.sendCommandUsage(apiPlayer);
        }
        apiPlayer.sendRawMessage("ToDo: Help message"); // todo help message
    }


    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
