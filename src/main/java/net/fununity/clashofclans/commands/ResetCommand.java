package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.util.CommandUtil;
import net.fununity.main.api.cloud.CloudManager;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetCommand extends APICommand {

    public ResetCommand() {
        super("reset", "command.reset", TranslationKeys.COC_COMMAND_RESET_USAGE, TranslationKeys.COC_COMMAND_RESET_DESCRIPTION);
        setTabRecommendPlayers(true);
    }

    @Override
    public void sendNoPermissionMessage(APIPlayer apiPlayer) {
        apiPlayer.sendMessage(MessagePrefix.ERROR, net.fununity.main.api.messages.TranslationKeys.API_COMMAND_PREMIUM_ONLY);
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length == 0) {
            apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_RESET_SUCCESS);
            CloudManager.getInstance().sendPlayerToLobby(apiPlayer.getPlayer());
            deleteUser(apiPlayer.getPlayer().getUniqueId());
            return;
        }

        if (!apiPlayer.getPlayer().hasPermission(getPermission() + ".other")) {
            super.sendNoPermissionMessage(apiPlayer);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            UUID uuid = CommandUtil.getOrSendErrorMethod(apiPlayer, args[0]);
            if (uuid == null) return;


            apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_RESET_SUCCESS);

            if (Bukkit.getPlayer(uuid) != null)
                CloudManager.getInstance().sendPlayerToLobby(uuid);

            deleteUser(uuid);
        });
    }

    private void deleteUser(UUID uuid) {
        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
            DatabasePlayer.getInstance().deleteUser(uuid);
            DatabaseBuildings.getInstance().deleteAllBuildings(uuid);
        },10L);
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // NOT NEEDED
    }
}
