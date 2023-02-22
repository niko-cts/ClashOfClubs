package net.fununity.clashofclans.commands;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.main.api.command.handler.APICommand;
import net.fununity.main.api.player.APIPlayer;
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
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("cheat")) {
                try {
                    ResourceTypes resourceTypes = ResourceTypes.valueOf(args[1]);
                    ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId()).fillResourceToContainer(resourceTypes, Integer.parseInt(args[2]));
                } catch (IllegalArgumentException exception) {
                    apiPlayer.sendRawMessage("Wrong command execute : /coc cheat <resourceType> <amount>");
                }
            }
        } else if (args.length == 1) {
            if(args[0].equalsIgnoreCase("selection")) {
                if (SCHEMATIC_SETTER.contains(apiPlayer.getUniqueId()))
                    SCHEMATIC_SETTER.remove(apiPlayer.getUniqueId());
                else
                    SCHEMATIC_SETTER.add(apiPlayer.getUniqueId());
                apiPlayer.sendRawMessage("§aSuccessful");
            }
        } else
            apiPlayer.sendRawMessage("Execute: /coc cheat <resourceType> <amount>");
    }

    public static Set<UUID> getSchematicSetter() {
        return SCHEMATIC_SETTER;
    }

    @Override
    public void onConsole(CommandSender commandSender, String[] strings) {
        // not needed
    }
}
