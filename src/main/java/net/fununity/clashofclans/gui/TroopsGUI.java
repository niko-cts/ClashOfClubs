package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.TroopsBuildingManager;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsCreateBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.Utils;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GUI class for Troop menus.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsGUI {

    private TroopsGUI() {
        throw new UnsupportedOperationException("TroopsGUI is a utility class.");
    }

    /**
     * Opens the troops container gui.
     * @param apiPlayer APIPlayer - the player to open.
     * @param building {@link TroopsBuilding} - the troops building.
     * @since 0.0.1
     */
    public static void openContainer(APIPlayer apiPlayer, TroopsBuilding building) {
        Language lang = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONTAINER_NAME),
                Utils.getPerfectInventorySize(building.getTroopAmount().size() + 9));

        for (Map.Entry<ITroop, Integer> entry : building.getTroopAmount().entrySet()) {
            List<String> lore = new ArrayList<>(Arrays.asList(entry.getKey().getDescription(lang)));
            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_CONTAINER_LORE, "${amount}", entry.getValue()+"").split(";")));

            menu.addItem(new ItemBuilder(entry.getKey().getRepresentativeItem())
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .setName(entry.getKey().getName(lang))
                    .setAmount(Math.min(entry.getValue(), 64))
                    .setLore(lore).craft());
        }

        menu.setItem(menu.getInventory().getSize() - 9, UsefulItems.LEFT_ARROW, new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                apiPlayer.getPlayer().closeInventory();
                building.getInventory(lang).open(apiPlayer);
            }
        });

        for (int i = menu.getInventory().getSize() - 8; i < menu.getInventory().getSize(); i++)
            menu.setItem(i, UsefulItems.BACKGROUND_GRAY);

        menu.open(apiPlayer);
    }

    /**
     * Opens the training menu for troop building.
     * @param apiPlayer APIPlayer - player to open.
     * @param building {@link TroopsCreateBuilding} - the building.
     * @since 0.0.1
     */
    public static void openTraining(APIPlayer apiPlayer, TroopsCreateBuilding building) {
        Language lang = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_NAME),
                Utils.getPerfectInventorySize(Math.max(building.getTroopsQueue().size(), 9) +
                        Troops.values().length + 18));

        ITroop topTroop = building.getTroopsQueue().peek();
        for (ITroop queue : building.getTroopsQueue()) {
            List<String> lore = new ArrayList<>(Arrays.asList(queue.getDescription(lang)));

            int trainDuration = queue.equals(topTroop) ? building.getTrainSecondsLeft() : queue.getTrainDuration();

            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_QUEUE_LORE,
                    Arrays.asList("${duration}", "${max}"),
                    Arrays.asList(trainDuration + "", queue.getTrainDuration() + "")).split(";")));

            menu.addItem(new ItemBuilder(queue.getRepresentativeItem())
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .setName(queue.getName(lang))
                    .setDurability((short) (queue.getRepresentativeItem().getMaxDurability() - queue.getRepresentativeItem().getMaxDurability() * trainDuration / queue.getTrainDuration()))
                    .setLore(lore).craft());
        }

        int i = building.getTroopsQueue().size() + 9 - building.getTroopsQueue().size();

        for (int j = i; j < i + 9; j++)
            menu.setItem(j, UsefulItems.BACKGROUND_BLACK);
        i += 9;


        for (int j = 0; j < Troops.values().length; j++, i++) {
            Troops troop = Troops.values()[j];
            List<String> lore = new ArrayList<>(Arrays.asList(troop.getDescription(lang)));

            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_LORE,
                    Arrays.asList("${minlevel}", "${duration}"),
                    Arrays.asList(troop.getMinBarracksLevel() + "", troop.getTrainDuration() + "")).split(";")));

            menu.setItem(i, new ItemBuilder(troop.getRepresentativeItem())
                    .setName(troop.getName(lang))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .setLore(lore).craft(), new ClickAction(true) {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    TroopsBuildingManager.getInstance().startEducation(building, troop);
                    Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> openTraining(apiPlayer, building), 1L);
                }
            });
        }

        menu.setItem(menu.getInventory().getSize() - 9, UsefulItems.LEFT_ARROW, new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                apiPlayer.getPlayer().closeInventory();
                building.getInventory(lang).open(apiPlayer);
            }
        });

        menu.setItem(menu.getInventory().getSize() - 8, new ItemBuilder(UsefulItems.DOWN_ARROW)
                .setName(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_RELOAD)).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                apiPlayer.getPlayer().closeInventory();
                openTraining(apiPlayer, building);
            }
        });


        for (int j = menu.getInventory().getSize() - 7; j < menu.getInventory().getSize(); j++)
            menu.setItem(j, UsefulItems.BACKGROUND_GRAY);

        menu.open(apiPlayer);
    }
}
