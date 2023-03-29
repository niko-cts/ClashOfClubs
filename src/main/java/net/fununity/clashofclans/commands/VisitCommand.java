package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.util.CommandUtil;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
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
            UUID uuid = CommandUtil.getOrSendErrorMethod(apiPlayer, args[0]);

            if (uuid == null) return;

            if (!apiPlayer.hasPermission("command.visit.offline") && !ClashOfClubs.getInstance().getPlayerManager().isCached(uuid)) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_NOTONLINE);
                return;
            }

            apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_VISIT_SUCCESS);
            ClashOfClubs.getInstance().getPlayerManager().leaveVisit(apiPlayer);

            CoCPlayer visitPlayer;
            if (ClashOfClubs.getInstance().getPlayerManager().isCached(uuid))
                visitPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
            else
                visitPlayer = ClashOfClubs.getInstance().getPlayerManager().loadPlayer(uuid);

            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> visitPlayer.visit(apiPlayer, true));
        });
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
