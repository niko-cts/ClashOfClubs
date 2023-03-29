package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.spying.SpyingPlayer;
import net.fununity.clashofclans.values.CoCValues;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.clashofclans.values.PlayerValues;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardMenu {

    private ScoreboardMenu() {
        throw new UnsupportedOperationException("ScoreboardMenu is a utility class.");
    }

    private static final String OBJNAME = "COCOBJ";

    /**
     * Displays a scoreboard menu for the given player.
     * @param coCPlayer {@link CoCPlayer} - the player to show.
     * @since 0.0.1
     */
    public static void show(CoCPlayer coCPlayer) {
        APIPlayer player = coCPlayer.getOwner();
        if (ClashOfClubs.getInstance().isAttackingServer() || player == null) return;
        Language lang = player.getLanguage();

        Scoreboard board = player.getPlayer().getScoreboard();

        if (board.getObjective(OBJNAME) != null)
            board.getObjective(OBJNAME).unregister();

        Objective obj = board.getObjective(OBJNAME) == null ? board.registerNewObjective(OBJNAME, OBJNAME, OBJNAME, RenderType.INTEGER) : board.getObjective(OBJNAME);

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(FunUnityAPI.getPrefix());

        obj.getScore(ClashOfClubs.getColoredName()).setScore(42);

        List<ICoCValue> values = CoCValues.canReachWithTownHall(coCPlayer.getTownHallLevel());

        int i = values.size() * 3 - 1;

        StringBuilder chatColors = new StringBuilder(" ");
        for (ICoCValue type : values) {
            if (type == PlayerValues.XP)
                continue;
            chatColors.append(type.getChatColor());
            obj.getScore(chatColors.toString()).setScore(i);
            i--;
            obj.getScore(type.getChatColor() + lang.getTranslation(type.getNameKey())).setScore(i);
            i--;

            StringBuilder builder = new StringBuilder().append(ChatColor.DARK_GRAY).append(SpecialChars.DOUBLE_ARROW_RIGHT).append(" ")
                    .append(type.getChatColor()).append(Math.round(coCPlayer.getResourceAmount(type)));
            if (type instanceof ResourceTypes)
                builder.append(" ").append(ChatColor.GRAY).append("/ ").append(type.getChatColor()).append(coCPlayer.getMaxResourceContainable((ResourceTypes) type));
            obj.getScore(builder.toString()).setScore(i);
            i--;
        }
    }

    /**
     * Displays a scoreboard menu for the given player while attacking another one.
     * @param player {@link APIPlayer} - the player to show.
     * @param coCPlayer {@link CoCPlayer} - the coc player to show.
     * @param spy {@link SpyingPlayer} - the attacking instance.
     * @since 0.0.1
     */
    public static void showSpying(APIPlayer player, CoCPlayer coCPlayer, SpyingPlayer spy) {

    }
}
