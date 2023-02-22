package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class stores methods to move buildings and create them.
 * @author Niko
 * @since 0.0.2
 */
public class BuildingsMoveManager {

    private static BuildingsMoveManager instance;

    /**
     * Gets the singleton instance of this class.
     * @return {@link BuildingsMoveManager} - instance of this class.
     * @since 0.0.2
     */
    public static BuildingsMoveManager getInstance() {
        if (instance == null)
            instance = new BuildingsMoveManager();
        return instance;
    }

    private BuildingsMoveManager() {
        // not needed atm
    }

    /**
     * Enters the moving mode for a player.
     * @param apiPlayer APIPlayer - the player who enters.
     * @param generalBuilding GeneralBuilding - the building which should be moved.
     * @since 0.0.1
     */
    public void enterMovingMode(APIPlayer apiPlayer, GeneralBuilding generalBuilding) {
        Player player = apiPlayer.getPlayer();
        ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId()).setBuildingMode(player.getLocation(), generalBuilding, generalBuilding.getRotation());
        Language lang = apiPlayer.getLanguage();
        player.getInventory().setItem(0, new ItemBuilder(Material.PISTON)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE).split(";")).craft());
        player.getInventory().setItem(1, new ItemBuilder(Material.STICK)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }

    /**
     * Building will be moved
     * @param buildingMode Object[] - move data
     * @since 0.0.1
     */
    public void moveBuilding(Object[] buildingMode) {
        GeneralBuilding building = (GeneralBuilding) buildingMode[1];
        Location oldLocation = building.getCoordinate();
        byte oldRotation = building.getRotation();

        building.setRotation((byte) buildingMode[2]);
        building.setCoordinate(BuildingLocationUtil.getCoordinate(building.getBuilding().getSize(), building.getRotation(), (Location) buildingMode[0]));
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
        Player player = coCPlayer.getOwner().getPlayer();

        coCPlayer.setBuildingMode(null, null, null);
        player.getInventory().setItem(0, new ItemStack(Material.AIR));
        player.getInventory().setItem(1, new ItemStack(Material.AIR));
        player.getInventory().setItem(4, new ItemStack(Material.AIR));
    }

    /**
     * Player enters creation mode to create a new building
     * @param coCPlayer CoCPlayer - player who leaves mode
     * @param building IBuilding - the building which should be created.
     * @since 0.0.1
     */
    public void enterCreationMode(CoCPlayer coCPlayer, IBuilding building) {
        APIPlayer apiPlayer = coCPlayer.getOwner();
        Player player = apiPlayer.getPlayer();
        coCPlayer.setBuildingMode(player.getLocation(), building, (byte) 0);
        Language lang = apiPlayer.getLanguage();
        player.getInventory().setItem(0, new ItemBuilder(Material.NETHER_STAR)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE).split(";")).craft());
        player.getInventory().setItem(1, new ItemBuilder(Material.STICK)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }
}
