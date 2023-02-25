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

public class TutorialManager {

    private static TutorialManager INSTANCE;

    public static TutorialManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new TutorialManager();
        return INSTANCE;
    }

    private final Map<UUID, TutorialState> state;

    private TutorialManager() {
        this.state = new HashMap<>();
    }

    public void checkIfTutorialNeeded(CoCPlayer coCPlayer) {
        if (coCPlayer.getTownHallLevel() == 0) {
            nextTutorial(coCPlayer, coCPlayer.getResourceAmount(ResourceTypes.GOLD) > 50 ? TutorialState.REPAIR_TOWNHALL : TutorialState.COLLECT_RESOURCE);

        } else if (coCPlayer.getTownHallLevel() == 1 && coCPlayer.getResourceGatherBuildings(ResourceTypes.FOOD).isEmpty()) {
            nextTutorial(coCPlayer, TutorialState.BUILD_FARM);
        }
    }

    public void nextTutorial(CoCPlayer coCPlayer, TutorialState tutorialState) {
        this.state.put(coCPlayer.getUniqueId(), tutorialState);

        APIPlayer owner = coCPlayer.getOwner();

        if (tutorialState == TutorialState.REPAIR_TOWNHALL) {
            owner.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST_ACTIONBAR).setType(ActionbarMessageType.STATIC));
        } else if (tutorialState == TutorialState.BUILD_FARM) {
            ClashOfClubs.getInstance().getPlayerManager().giveDefaultItems(coCPlayer, owner);
        }

        openHelpBook(owner, tutorialState);
    }

    public void finished(CoCPlayer player) {
        switch (this.state.get(player.getUniqueId())) {
            case COLLECT_RESOURCE -> nextTutorial(player, TutorialState.REPAIR_TOWNHALL);
            case REPAIR_TOWNHALL -> nextTutorial(player, TutorialState.BUILD_FARM);
            case BUILD_FARM -> {
                state.remove(player.getUniqueId());
                openHelpBook(player.getOwner(), null);
            }
            default -> {}
        }
    }

    public void openHelpBook(APIPlayer player) {
        openHelpBook(player, getState(player.getUniqueId()));
    }

    public void openHelpBook(APIPlayer player, TutorialState state) {
        player.openBook(new ItemBuilder(Material.WRITTEN_BOOK)
                .addPage(player.getLanguage().getTranslation(TranslationKeys.COC_INV_BOOK_CONTENT + (state != null ? state.name().toLowerCase() : "general"), "${player}", player.getDisplayName()).split(";")).craft());
    }

    public TutorialState getState(UUID uuid) {
        return state.getOrDefault(uuid, null);
    }

    public boolean cantBuild(UUID uuid) {
        TutorialState tutorialState = getState(uuid);
        return tutorialState == TutorialState.REPAIR_TOWNHALL || tutorialState == TutorialState.COLLECT_RESOURCE;
    }


    public enum TutorialState {
        COLLECT_RESOURCE,
        REPAIR_TOWNHALL,
        BUILD_FARM
    }

}
