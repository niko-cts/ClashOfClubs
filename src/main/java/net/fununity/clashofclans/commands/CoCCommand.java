package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.AttackingHandler;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CoCCommand extends APICommand {

    private static final Set<UUID> SCHEMATIC_SETTER = new HashSet<>();

    public CoCCommand() {
        super("coc", "command.coc", "usage", "description");
        addSubCommand(new CoCAddCommand());
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("cheat")) {
                for (ResourceTypes type : ResourceTypes.values()) {
                    PlayerManager.getInstance().getPlayer(apiPlayer.getUniqueId()).addResource(type, Integer.parseInt(args[1]));
                }
                return;
            }
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("attack")) {
                apiPlayer.sendRawMessage("§aLoading please wait..");
                Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
                    PlayerAttackingManager attackInstance = AttackingHandler.createAttackingInstance(apiPlayer.getUniqueId(), UUID.fromString("cf177f71-cd90-40c3-a5b4-d648c2e3b447"));
                    Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> attackInstance.playerJoined(apiPlayer));
                });
                return;
            }
            apiPlayer.sendRawMessage("§aSuccessful");
            return;
        }

        if (SCHEMATIC_SETTER.contains(apiPlayer.getUniqueId()))
            SCHEMATIC_SETTER.remove(apiPlayer.getUniqueId());
        else
            SCHEMATIC_SETTER.add(apiPlayer.getUniqueId());
        apiPlayer.sendRawMessage("§aSuccessful");
    }

    public static Set<UUID> getSchematicSetter() {
        return SCHEMATIC_SETTER;
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
