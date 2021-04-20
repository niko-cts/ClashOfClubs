package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardMenu {

    private ScoreboardMenu() {
        throw new UnsupportedOperationException("ScoreboardMenu is a utility class.");
    }

    private static final String OBJNAME = "COCOBJ";

    public static void show(APIPlayer player, CoCPlayer coCPlayer) {
        if (player == null) return;
        Language lang = player.getLanguage();

        Scoreboard board = player.getPlayer().getScoreboard();

        if (board.getObjective(OBJNAME) != null)
            board.getObjective(OBJNAME).unregister();

        Objective obj = board.getObjective(OBJNAME) == null ? board.registerNewObjective(OBJNAME, OBJNAME, OBJNAME, RenderType.HEARTS) : board.getObjective(OBJNAME);

        obj.setDisplayName(FunUnityAPI.getPrefix());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore(ClashOfClans.getInstance().getColoredName()).setScore(42);

        int i = ResourceTypes.values().length * 2 + 1;
        obj.getScore("   ").setScore(i);
        i--;
        obj.getScore("§8» §a" + Bukkit.getOnlinePlayers().size() + " Players").setScore(i);
        i--;

        for (ResourceTypes value : ResourceTypes.values()) {
            obj.getScore(value.getChatColor() + lang.getTranslation(value.getNameKey())).setScore(i);
            i--;
            obj.getScore("§8" + SpecialChars.DOUBLE_ARROW_RIGHT + " " + value.getChatColor()  + coCPlayer.getResource(value)).setScore(i);
            i--;
        }

    }

}
