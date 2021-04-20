package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.listener.PlayerInteractListener;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CoCCommand extends APICommand {

    private static final Set<UUID> SCHEMATIC_SETTER = new HashSet<>();

    public CoCCommand() {
        super("coc", "command.coc", "usage", "description");
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length == 2) {
            Schematics.saveSchematic(BuildingsManager.getInstance().getBuildingById(args[0]), Integer.parseInt(args[1]),
                    LocationUtil.getMinAndMax(PlayerInteractListener.getSchematicSaver()[0], PlayerInteractListener.getSchematicSaver()[1]));
            apiPlayer.sendRawMessage("§aSuccessful");
            return;
        }
        if (args.length == 1) {
            Schematics.saveSchematic(args[0], LocationUtil.getMinAndMax(PlayerInteractListener.getSchematicSaver()[0], PlayerInteractListener.getSchematicSaver()[1]));
            apiPlayer.sendRawMessage("§aSuccessful");
            return;
        }

        if(SCHEMATIC_SETTER.contains(apiPlayer.getUniqueId()))
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
