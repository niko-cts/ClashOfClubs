package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.TroopsBuildingManager;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
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
        menu.setSpecialHolder(building.getBuildingUUID() + "-training");

        boolean first = true;
        for (ITroop troop : building.getTroopsQueue()) {
            List<String> lore = new ArrayList<>(Arrays.asList(troop.getDescription(lang)));

            int timeLeft = first ? building.getTrainSecondsLeft() : troop.getTrainDuration();


            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_QUEUE_LORE,
                    Arrays.asList("${duration}", "${max}"),
                    Arrays.asList(timeLeft + "", troop.getTrainDuration() + "")).split(";")));

            menu.addItem(new ItemBuilder(troop.getRepresentativeItem())
                    .setName(troop.getName(lang))
                    .setLore(lore)
                    .setDamage((int) (troop.getRepresentativeItem().getMaxDurability() * Math.max(1.0, timeLeft) / troop.getTrainDuration()))
                    .craft(), new ClickAction(true) {
                @Override
                public void onRightClick(APIPlayer apiPlayer, ItemStack itemStack, int slot) {
                    building.removeTroop(troop);
                    Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), ()->openTraining(apiPlayer, building), 1);
                }

                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    building.removeTroop(troop);
                    Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), ()->openTraining(apiPlayer, building), 1);
                }
            });
            first = false;
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
                    .setLore(lore).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if (TroopsBuildingManager.getInstance().startEducation(apiPlayer, building, troop)) {
                        setCloseInventory(true);
                        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), ()->openTraining(apiPlayer, building), 1);
                    }
                }
            });
        }

        menu.setItem(menu.getInventory().getSize() - 9, UsefulItems.LEFT_ARROW, new ClickAction(true) {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> building.getInventory(lang).open(apiPlayer), 1L);
            }
        });


        for (int j = menu.getInventory().getSize() - 8; j < menu.getInventory().getSize(); j++)
            menu.setItem(j, UsefulItems.BACKGROUND_GRAY);

        menu.open(apiPlayer);
    }
}
