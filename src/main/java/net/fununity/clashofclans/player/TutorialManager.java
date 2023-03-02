package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.actionbar.ActionbarMessageType;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class manages the tutorial for new players
 * @author Niko
 * @since 0.0.2
 */
public class TutorialManager {

    private static TutorialManager INSTANCE;

    /**
     * Get the singleton instance.
     * @return TutorialManager - instance of this class.
     */
    public static TutorialManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new TutorialManager();
        return INSTANCE;
    }

    private final Map<UUID, TutorialState> state;

    private TutorialManager() {
        this.state = new HashMap<>();
    }

    /**
     * Will check if a tutorial is needed and starts it.
     * @param coCPlayer CoCPlayer - player to check.
     */
    public void checkIfTutorialNeeded(CoCPlayer coCPlayer) {
        if (getState(coCPlayer.getUniqueId()) != null) // could be null due the construction finished while player quit
            return;

        switch (coCPlayer.getTownHallLevel()) {
            case 0 ->
                    startTutorialState(coCPlayer, coCPlayer.getResourceAmount(ResourceTypes.GOLD) > 50 ? TutorialState.REPAIR_TOWNHALL : TutorialState.COLLECT_RESOURCE);
            case 1 -> {
                if (coCPlayer.getResourceGatherBuildings(ResourceTypes.FOOD).isEmpty() || coCPlayer.getResourceContainerBuildings(ResourceTypes.FOOD).isEmpty())
                    startTutorialState(coCPlayer, TutorialState.BUILD_FARM);
                else if (coCPlayer.getDefenseBuildings().size() == 1)
                    startTutorialState(coCPlayer, TutorialState.DEFENSE);
                else if (coCPlayer.getTroopsCreateBuildings().isEmpty() || coCPlayer.getTroopsCampBuildings().isEmpty())
                    startTutorialState(coCPlayer, TutorialState.TROOPS);
            }
            default -> {}
        }
    }

    /**
     * Starts the tutorial of the state.
     * @param coCPlayer CoCPlayer - the player to with start
     * @param tutorialState TutorialState - the state to start.
     * @since 0.0.2
     */
    public void startTutorialState(CoCPlayer coCPlayer, TutorialState tutorialState) {
        this.state.put(coCPlayer.getUniqueId(), tutorialState);

        APIPlayer owner = coCPlayer.getOwner();

        String actionbarMessage;
        switch (tutorialState) {
            case COLLECT_RESOURCE -> actionbarMessage = TranslationKeys.COC_PLAYER_TUTORIAL_COLLECT_RESOURCE;
            case REPAIR_TOWNHALL -> actionbarMessage = TranslationKeys.COC_PLAYER_TUTORIAL_REPAIR_TOWNHALL_FIRST_ACTIONBAR;
            case BUILD_FARM -> {
                actionbarMessage = TranslationKeys.COC_PLAYER_TUTORIAL_BUILD_FARM_ACTIONBAR;
                ClashOfClubs.getInstance().getPlayerManager().giveDefaultItems(coCPlayer, owner);
            }
            case DEFENSE -> actionbarMessage = TranslationKeys.COC_PLAYER_TUTORIAL_DEFENSE_ACTIONBAR;
            default -> actionbarMessage = TranslationKeys.COC_PLAYER_TUTORIAL_TROOPS_ACTIONBAR;
        }

        owner.sendActionbar(new ActionbarMessage(actionbarMessage).setType(ActionbarMessageType.STATIC));

        openHelpBook(owner, tutorialState);
    }

    /**
     * Call method if tutorial state is finished and starts a next one.
     * @param player CoCPlayer - the player
     */
    public void finished(CoCPlayer player) {
        TutorialState state = this.state.getOrDefault(player.getUniqueId(), TutorialState.FINISHED);

        if (state == TutorialState.FINISHED || state.ordinal() + 1 == TutorialState.values().length) {
            this.state.remove(player.getUniqueId());
            player.getOwner().clearActionbar();
            openHelpBook(player.getOwner(), state);
            return;
        }

        startTutorialState(player, TutorialState.values()[state.ordinal() + 1]);
    }

    /**
     * Opens the help book.
     * @param player APIPlayer - the player to open
     */
    public void openHelpBook(APIPlayer player) {
        openHelpBook(player, getState(player.getUniqueId()));
    }

    /**
     * Opens the help book at the current state.
     * @param player APIPlayer - the player to open
     * @param state TutorialState - the state to open.
     * @since 0.0.1
     */
    public void openHelpBook(APIPlayer player, TutorialState state) {
        player.getPlayer().closeInventory();
        player.openBook(new ItemBuilder(Material.WRITTEN_BOOK)
                .addPage(player.getLanguage().getTranslation(TranslationKeys.COC_INV_BOOK_CONTENT + (state != null ? state.name().toLowerCase() : "general"), "${player}", player.getDisplayName()).split(";")).craft());
    }

    public TutorialState getState(UUID uuid) {
        return state.getOrDefault(uuid, null);
    }

    /**
     * Check if player is not allowed to build
     * @param uuid UUID - uuid of player.
     * @return boolean - player is not allowed to build
     */
    public boolean cantBuild(UUID uuid) {
        TutorialState tutorialState = getState(uuid);
        return tutorialState == TutorialState.REPAIR_TOWNHALL || tutorialState == TutorialState.COLLECT_RESOURCE;
    }


    public enum TutorialState {
        COLLECT_RESOURCE,
        REPAIR_TOWNHALL,
        BUILD_FARM,
        DEFENSE,
        TROOPS,
        FINISHED
    }

}
