package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CoCCommand extends APICommand {


    public CoCCommand() {
        super("coc", "command.coc", "usage", "description");
        addSubCommand(new CoCRebuildCommand());
    }

    @Override
    public void onCommand(APIPlayer apiPlayer, String[] args) {
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("cheat")) {
                try {
                    ResourceTypes resourceTypes = ResourceTypes.valueOf(args[1]);
                    ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId()).fillResourceToContainer(resourceTypes, Integer.parseInt(args[2]));
                } catch (IllegalArgumentException exception) {
                    apiPlayer.sendRawMessage("Wrong command execute : /coc cheat <resourceType> <amount>");
                }
            }
        } else
            apiPlayer.sendRawMessage("Execute: /coc cheat <resourceType> <amount>");
    }


    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
