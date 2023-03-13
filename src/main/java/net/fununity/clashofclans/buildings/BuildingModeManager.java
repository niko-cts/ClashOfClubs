package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.buildingmode.ConstructionMode;
import net.fununity.clashofclans.player.buildingmode.MovingMode;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.CircleParticleUtil;
import net.fununity.clashofclans.util.HotbarItems;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * This class stores methods to move and create buildings.
 * @author Niko
 * @since 0.0.2
 */
public class BuildingModeManager {

    private static BuildingModeManager instance;

    /**
     * Gets the singleton instance of this class.
     * @return {@link BuildingModeManager} - instance of this class.
     * @since 0.0.2
     */
    public static BuildingModeManager getInstance() {
        if (instance == null)
            instance = new BuildingModeManager();
        return instance;
    }

    private BuildingModeManager() {
        // not needed atm
    }


    /**
     * Enters the moving mode for a player.
     * @param coCPlayer CoCPlayer - the cocplayer
     * @param apiPlayer APIPlayer - the player who enters.
     * @param generalBuilding GeneralBuilding - the building which should be moved.
     * @since 0.0.1
     */
    public void enterMovingMode(CoCPlayer coCPlayer, APIPlayer apiPlayer, GeneralBuilding generalBuilding) {
        Player player = apiPlayer.getPlayer();
        Location location = player.getLocation().clone();
        location.setY(ClashOfClubs.getBaseYCoordinate() + 1);

        CircleParticleUtil.hideRadius(generalBuilding.getBuildingUUID());
        coCPlayer.setBuildingMode(new MovingMode(generalBuilding, location));
        BuildingLocationUtil.createBuildingModeDecoration(player, coCPlayer);

        Language lang = apiPlayer.getLanguage();

        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemBuilder(HotbarItems.MOVE_BUILDING)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE).split(";")).craft());
        giveOtherItems(apiPlayer, lang);
    }

    /**
     * Building will be moved
     * @param coCPlayer CoCPlayer - the player.
     * @since 0.0.1
     */
    public void moveBuilding(CoCPlayer coCPlayer) {
        MovingMode movingMode = (MovingMode) coCPlayer.getBuildingMode();
        GeneralBuilding building = movingMode.getMovingBuilding();
        Location oldLocation = building.getCoordinate();
        byte oldRotation = building.getRotation();

        building.setRotation(movingMode.getRotation());
        Location newLocation = movingMode.getLocation();
        newLocation.setY(ClashOfClubs.getBaseYCoordinate());

        building.setBaseRelative(coCPlayer.getBaseStartLocation(),
                BuildingLocationUtil.transferInRelatives(coCPlayer.getBaseStartLocation(), building.getBuilding().getSize(), building.getRotation(), newLocation));
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            DatabaseBuildings.getInstance().moveBuilding(building);
            Schematics.removeBuilding(oldLocation, building.getBuilding().getSize(), oldRotation);
            Bukkit.getScheduler().runTaskLaterAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuilding(building), 10L);
        });
    }

    /**
     * Player quits the editor mode
     * @param coCPlayer CoCPlayer - player who leaves mode
     * @since 0.0.1
     */
    public void quitEditorMode(CoCPlayer coCPlayer) {
        if (coCPlayer.getBuildingMode() != null) {
            BuildingLocationUtil.removeBuildingModeDecorations(coCPlayer.getOwner().getPlayer(), coCPlayer.getBuildingMode());
        }

        coCPlayer.setBuildingMode(null);
        ClashOfClubs.getInstance().getPlayerManager().giveDefaultItems(coCPlayer);
    }

    /**
     * Player enters creation mode to create a new building
     * @param coCPlayer CoCPlayer - player who leaves mode
     * @param building IBuilding - the building which should be created.
     * @since 0.0.1
     */
    public void enterCreationMode(CoCPlayer coCPlayer, IBuilding building) {
        APIPlayer apiPlayer = coCPlayer.getOwner();
        Language lang = apiPlayer.getLanguage();
        Player player = apiPlayer.getPlayer();
        Location location = player.getLocation().clone();
        location.setY(ClashOfClubs.getBaseYCoordinate() + 1);

        coCPlayer.setBuildingMode(new ConstructionMode(building, location));
        BuildingLocationUtil.createBuildingModeDecoration(player, coCPlayer);

        player.getInventory().clear();
        player.getInventory().addItem(
                new ItemBuilder(HotbarItems.CREATE_BUILDING)
                        .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME))
                        .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE).split(";")).craft()
        );
        player.getInventory().setItem(6,
                new ItemBuilder(HotbarItems.CREATE_BUILDING_ANOTHER)
                        .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_ANOTHER_NAME))
                        .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_ANOTHER_LORE).split(";")).craft());
        player.getInventory().setItem(7, new ItemBuilder(HotbarItems.CREATE_BUILDING_REMOVE)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_REMOVE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_REMOVE_LORE).split(";")).craft());
        giveOtherItems(apiPlayer, lang);
    }


    private void giveOtherItems(APIPlayer apiPlayer, Language language) {
        Player player = apiPlayer.getPlayer();
        player.getInventory().addItem(new ItemBuilder(HotbarItems.ROTATE_BUILDING)
                .setName(language.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE).split(";")).craft());
        player.getInventory().setItem(8, new ItemBuilder(HotbarItems.CANCEL)
                .setName(language.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(3);

        apiPlayer.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_CONSTRUCTION_HELP);
    }
}
