package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.command.CommandSender;

public class HomeCommand extends APICommand {
    public HomeCommand() {
        super("home", "", TranslationKeys.COC_COMMAND_HOME_USAGE, TranslationKeys.COC_COMMAND_HOME_DESCRIPTION, "hause");
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] strings) {
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId());
        ClashOfClubs.getInstance().getPlayerManager().getPlayers().entrySet().stream()
                .filter(e -> e.getValue().getVisitors().contains(apiPlayer.getUniqueId())).findFirst().ifPresent(e -> e.getValue().leave(apiPlayer));
        player.visit(apiPlayer, true);
        apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_HOME_SUCCESS);
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        //
    }
}
