package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.values.CoCValues;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.main.api.command.handler.APISubCommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.messages.TranslationKeys;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.command.CommandSender;

public class CoCCheatCommand extends APISubCommand {

    public CoCCheatCommand() {
        super("cheat", "command.coc.cheat", net.fununity.clashofclans.language.TranslationKeys.COC_COMMAND_CHEAT_USAGE, net.fununity.clashofclans.language.TranslationKeys.COC_COMMAND_CHEAT_DESCRIPTION, 2);
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        try {
            ICoCValue value = CoCValues.getFromName(args[0]);
            CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId());

            if (coCPlayer == null) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.API_PLAYER_NOT_FOUND);
                return;
            }

            coCPlayer.addResourceWithUpdate(value, Double.parseDouble(args[1]));
        } catch (IllegalArgumentException exception) {
            sendCommandUsage(apiPlayer);
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
