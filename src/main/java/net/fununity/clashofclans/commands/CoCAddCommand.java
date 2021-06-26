package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.listener.PlayerInteractListener;
import net.fununity.main.api.command.handler.APISubCommand;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.command.CommandSender;

public class CoCAddCommand extends APISubCommand {

    public CoCAddCommand() {
        super("add", "command.coc", "usage", "description");
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (PlayerInteractListener.getSchematicSaver()[0] == null ||
                PlayerInteractListener.getSchematicSaver()[1] == null) {
            apiPlayer.sendRawMessage("§cEs wurden nicht alle locations angeklickt");
            return;
        }
        if (args.length == 3) {
            Schematics.saveSchematic(BuildingsManager.getInstance().getBuildingById(args[0]),
                    Integer.parseInt(args[1]),
                    LocationUtil.getMinAndMax(PlayerInteractListener.getSchematicSaver()[0],
                            PlayerInteractListener.getSchematicSaver()[1]), args[2]);
            apiPlayer.sendRawMessage("§aSuccessful added " + args[0] + " level " + args[1] + " version " + args[2]);
        } else if (args.length == 2) {
            Schematics.saveSchematic(BuildingsManager.getInstance().getBuildingById(args[0]),
                    Integer.parseInt(args[1]), LocationUtil.getMinAndMax(PlayerInteractListener.getSchematicSaver()[0],
                            PlayerInteractListener.getSchematicSaver()[1]));

            apiPlayer.sendRawMessage("§aSuccessful added " + args[0] + " level " + args[1]);
        } else if (args.length == 1) {
            Schematics.saveSchematic(args[0], LocationUtil.getMinAndMax(PlayerInteractListener.getSchematicSaver()[0], PlayerInteractListener.getSchematicSaver()[1]));
            apiPlayer.sendRawMessage("§aSuccessful added " + args[0]);
        } else
            apiPlayer.sendRawMessage("§cUsage: /coc add <building> (<level>) (<version>)");
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
