package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.cloud.CloudManager;
import net.fununity.main.api.command.handler.APISubCommand;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.ConsoleUtil;
import net.fununity.main.api.util.PlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CoCRebuildCommand extends APISubCommand {

    public CoCRebuildCommand() {
        super("rebuild", "command.coc.rebuild", TranslationKeys.COC_COMMAND_REBUILD_USAGE, TranslationKeys.COC_COMMAND_REBUILD_DESCRIPTION);
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length == 0) {
            CloudManager.getInstance().sendPlayerToLobby(apiPlayer.getUniqueId());
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(ClashOfClubs.getInstance(), ()->{
            UUID uuid = args.length == 0 ? apiPlayer.getUniqueId() : PlayerDataUtil.getPlayerUUID(args[0]);

            if (uuid == null || ClashOfClubs.getInstance().getPlayerManager().isCached(uuid)) {
                if (args.length != 0)
                    apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_COMMAND_REBUILD_NO_BASE);
                return;
            }


            if (args.length != 0)
                apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_COMMAND_REBUILD_SUCCESS);

            CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().loadPlayer(uuid);
            Schematics.removeBuilding(coCPlayer.getBaseStartLocation(), new int[]{ClashOfClubs.getBaseSize(), ClashOfClubs.getBaseSize()}, (byte) 0);
            Schematics.createPlayerBase(coCPlayer.getBaseStartLocation(), coCPlayer.getAllBuildings()); // recreates the player base if not on same server
        }, 10L);

    }


    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
