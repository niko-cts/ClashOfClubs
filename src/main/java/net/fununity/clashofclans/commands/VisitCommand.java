package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.PlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class VisitCommand extends APICommand {

    public VisitCommand() {
        super("visit", "", TranslationKeys.COC_COMMAND_VISIT_USAGE, TranslationKeys.COC_COMMAND_VISIT_DESCRIPTION, "besuchen");
        setTabRecommendPlayers(true);
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length != 1) {
            sendCommandUsage(apiPlayer);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            UUID uuid = PlayerDataUtil.getPlayerUUID(args[0]);
            if (uuid == null) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, net.fununity.main.api.messages.TranslationKeys.API_PLAYER_NOT_FOUND);
                return;
            }
            if (!DatabasePlayer.getInstance().contains(uuid)) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_HASNOBUILDING);
                return;
            }
            if(!apiPlayer.hasPermission("command.visit.offline") && !PlayerManager.getInstance().isCached(uuid)) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_NOTONLINE);
                return;
            }
            apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_VISIT_SUCCESS);
            PlayerManager.getInstance().getPlayer(apiPlayer.getUniqueId()).leave(apiPlayer);
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () ->
                    PlayerManager.getInstance().getPlayer(uuid).visit(apiPlayer, true));
        });
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
