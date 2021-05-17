package net.fununity.clashofclans.gui;

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
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TroopsGUI {

    public static void openContainer(APIPlayer apiPlayer, TroopsBuilding building) {
        Language lang = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONTAINER_NAME), Utils.getPerfectInventorySize(building.getTroopAmount().size() + 9));

        for (Map.Entry<ITroop, Integer> entry : building.getTroopAmount().entrySet()) {
            List<String> lore = new ArrayList<>(Arrays.asList(entry.getKey().getDescription(lang)));
            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_CONTAINER_LORE, "${amount}", entry.getValue()+"").split(";")));

            menu.addItem(new ItemBuilder(entry.getKey().getRepresentativeItem())
                    .setName(entry.getKey().getName(lang))
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

    public static void openTraining(APIPlayer apiPlayer, TroopsCreateBuilding building) {
        Language lang = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_NAME),
                Utils.getPerfectInventorySize(building.getTroopsQueue().size() + building.getTroopsQueue().size() % 9 +
                        Troops.values().length + 9));

        int i = 0;
        for (ITroop queue : building.getTroopsQueue()) {
            List<String> lore = new ArrayList<>(Arrays.asList(queue.getDescription(lang)));

            int trainDuration = queue.equals(building.getTroopsQueue().peek()) ? building.getTrainSecondsLeft() : queue.getTrainDuration();

            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_QUEUE_LORE,
                    Arrays.asList("${duration}", "${max}"),
                    Arrays.asList(trainDuration + "", queue.getTrainDuration() + "")).split(";")));

            menu.addItem(new ItemBuilder(queue.getRepresentativeItem())
                    .setName(queue.getName(lang))
                    .setLore(lore).craft());
            i++;
        }

        i += 9 % i;

        for (int j=0; j<Troops.values().length; j++,i++) {
            Troops troop = Troops.values()[j];
            List<String> lore = new ArrayList<>(Arrays.asList(troop.getDescription(lang)));

            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_TRAIN_LORE,
                    Arrays.asList("${minlevel}", "${duration}"),
                    Arrays.asList(troop.getMinBarracksLevel()+"", troop.getTrainDuration()+"")).split(";")));

            menu.setItem(j, new ItemBuilder(troop.getRepresentativeItem())
                    .setName(troop.getName(lang))
                    .setLore(lore).craft());
        }

        menu.setItem(menu.getInventory().getSize() - 9, UsefulItems.LEFT_ARROW, new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                apiPlayer.getPlayer().closeInventory();
                building.getInventory(lang).open(apiPlayer);
            }
        });

        for (int j = menu.getInventory().getSize() - 8; j < menu.getInventory().getSize(); j++)
            menu.setItem(j, UsefulItems.BACKGROUND_GRAY);

        menu.open(apiPlayer);
    }
}
