package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.stream.Collectors;

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
        if (player == null) return;
        Language lang = player.getLanguage();

        Scoreboard board = player.getPlayer().getScoreboard();

        if (board.getObjective(OBJNAME) != null)
            board.getObjective(OBJNAME).unregister();

        Objective obj = board.getObjective(OBJNAME) == null ? board.registerNewObjective(OBJNAME, OBJNAME, OBJNAME, RenderType.INTEGER) : board.getObjective(OBJNAME);

        obj.setDisplayName(FunUnityAPI.getPrefix());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore(ClashOfClubs.getColoredName()).setScore(42);

        ResourceTypes[] types = ResourceTypes.canReachWithTownHall(coCPlayer.getTownHallLevel());

        int i = types.length * 3 + 1;
        obj.getScore("    ").setScore(i);
        i--;
        obj.getScore("§8» §b" + coCPlayer.getExp() + " §3EXP").setScore(i);
        i--;

        for (ResourceTypes type : types) {
            obj.getScore(" " + type.getChatColor()).setScore(i);
            i--;
            obj.getScore(type.getChatColor() + lang.getTranslation(type.getNameKey())).setScore(i);
            i--;
            obj.getScore("§8" + SpecialChars.DOUBLE_ARROW_RIGHT + "" + type.getChatColor()  + coCPlayer.getResource(type)).setScore(i);
            i--;
        }
    }

    /**
     * Displays a scoreboard menu for the given player while attacking another one.
     * @param player {@link APIPlayer} - the player to show.
     * @param coCPlayer {@link CoCPlayer} - the coc player to show.
     * @param attackingManager {@link PlayerAttackingManager} - the attacking instance.
     * @since 0.0.1
     */
    public static void showAttacking(APIPlayer player, CoCPlayer coCPlayer, PlayerAttackingManager attackingManager) {
        Language lang = player.getLanguage();

        Scoreboard board = player.getPlayer().getScoreboard();

        if (board.getObjective(OBJNAME) != null)
            board.getObjective(OBJNAME).unregister();

        Objective obj = board.getObjective(OBJNAME) == null ? board.registerNewObjective(OBJNAME, OBJNAME, OBJNAME, RenderType.INTEGER) : board.getObjective(OBJNAME);

        obj.setDisplayName(FunUnityAPI.getPrefix());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore(ClashOfClubs.getColoredName()).setScore(42);

        ResourceTypes[] types = new ResourceTypes[]{ResourceTypes.GOLD, ResourceTypes.FOOD};

        int i = types.length * 3 + 2;
        obj.getScore("    ").setScore(i);
        i--;
        obj.getScore(lang.getTranslation(TranslationKeys.COC_ATTACKING_BOARD_STARS)).setScore(i);
        i--;

        obj.getScore("§8" + SpecialChars.DOUBLE_ARROW_RIGHT + " " + attackingManager.getStars()).setScore(i);
        i--;

        for (ResourceTypes type : types) {
            obj.getScore(" " + type.getChatColor()).setScore(i);
            i--;
            obj.getScore(type.getChatColor() + lang.getTranslation(type.getNameKey())).setScore(i);
            i--;
            List<GeneralBuilding> collect = attackingManager.getBuildingsOnField().stream()
                    .filter(b -> b instanceof ResourceContainerBuilding && ((ResourceContainerBuilding) b).getContainingResourceType() == type).collect(Collectors.toList());
            int amount = 0;

            for (GeneralBuilding building : collect)
                amount += ((ResourceContainerBuilding)building).getAmount();

            obj.getScore("§8" + SpecialChars.DOUBLE_ARROW_RIGHT + " " + type.getChatColor() + amount).setScore(i);
            i--;
        }

    }
}
