package net.fununity.clashofclans.util;

import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.PlayerDataUtil;

import java.util.UUID;

/**
 * Utility class for the COCCommands.
 * @author Niko
 * @since 1.0.2
 */
public class CommandUtil {

    private CommandUtil() {
        throw new UnsupportedOperationException("CommandUtil is a utility class.");
    }

    /**
     * Will try to get the uuid from the providied name and send the player error messages if not available.
     * @param apiPlayer APIPlayer - player to send error messages.
     * @param name String - the name of the getting player.
     * @return UUID - uuid of the given name.
     * @since 1.0.2
     */
    public static UUID getOrSendErrorMethod(APIPlayer apiPlayer, String name) {
        UUID uuid = PlayerDataUtil.getPlayerUUID(name);
        if (uuid == null) {
            apiPlayer.sendMessage(MessagePrefix.ERROR, net.fununity.main.api.messages.TranslationKeys.API_PLAYER_NOT_FOUND);
            return null;
        }
        if (!DatabasePlayer.getInstance().contains(uuid)) {
            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_HASNOBUILDING);
            return null;
        }
        return uuid;
    }

}
