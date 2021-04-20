package net.fununity.clashofclans.language;

import net.fununity.main.api.common.messages.MessageList;
import net.fununity.misc.translationhandler.TranslationHandler;

public class GermanMessages extends MessageList {

    public GermanMessages() {
        super(TranslationHandler.getInstance().getLanguageHandler().getLanguageByCode("de"));

        add(TranslationKeys.COC_GUI_BUILDING_HP_NAME, "&6Trefferpunkte");
        add(TranslationKeys.COC_RESOURCE_ELIXIR, "");
        add(TranslationKeys.COC_RESOURCE_GOLD, "");
        add(TranslationKeys.COC_RESOURCE_DARK, "");
        add(TranslationKeys.COC_GUI_BUILDING_HP_LORE, "");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME, "");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "");
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_NAME, "");
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_DESCRIPTION, "");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_NAME, "");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_DESCRIPTION, "");
        add(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_NAME, "");
        add(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_DESCRIPTION, "&7The miner mines gold for you.");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_NAME, "");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_LORE, "");
        add(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, "");

        add(TranslationKeys.COC_GUI_GATHER_CLOCK_NAME, "${type} &6per hour");
        add(TranslationKeys.COC_GUI_GATHER_CLOCK_LORE, "&7This building gathers;${resource}&7/hour");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_NAME, "&eCollect resource");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_LORE, "&7Click to collect the gathered resource;&7Amount: ${resource}");

        add(TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY, "&cThere is a building in the way!");
        add(TranslationKeys.COC_CONSTRUCTION_MOVED, "&aBuilding successfully moved");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME, "&eMove Building");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE, "&7Hold this item and look on the;&7ground to see, where you could place it.;&7Click on the block;&7you want to move the building.");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_NAME, "&cCancel operation");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_LORE, "&7Click to cancel the move operation.");

        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME, "&eCreate Building");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE, "&7Hold this item and look on the;&7ground to see, where you could place the building.;&7Click on the block;&7you want to construct the building.");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME, "&aCancel operation");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE, "&7Click to cancel the construct operation");
        add(TranslationKeys.COC_CONSTRUCTION_BUILD, "&aBuilding will be constructed!");

        insertIntoLanguage();
    }
}
