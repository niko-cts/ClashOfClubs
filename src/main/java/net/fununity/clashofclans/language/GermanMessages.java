package net.fununity.clashofclans.language;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.main.api.common.messages.MessageList;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.misc.translationhandler.TranslationHandler;

public class GermanMessages extends MessageList {

    public GermanMessages() {
        super(TranslationHandler.getInstance().getLanguageHandler().getLanguageByCode("de"));

        String theodor = "&eTheodore&7: ";

        // COMMANDS
        // VISIT
        add(TranslationKeys.COC_COMMAND_VISIT_USAGE, "visit <User>");
        add(TranslationKeys.COC_COMMAND_VISIT_DESCRIPTION, "&7Besuche eine andere Basis");
        add(TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_HASNOBUILDING, "&cDieser Spieler hat noch nicht gespielt." + ClashOfClubs.getColoredName().replace("§", "&") + " ");
        add(TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_NOTONLINE, "&cDieser Spieler ist &coffline&7! &7Kauf &6premium &7um einen offline Spieler zu besuchen.");
        add(TranslationKeys.COC_COMMAND_VISIT_SUCCESS, "&aYou will be send to the base!");

        // HOME
        add(TranslationKeys.COC_COMMAND_HOME_USAGE, "home");
        add(TranslationKeys.COC_COMMAND_HOME_DESCRIPTION, "&7Bringt dich zurück zu deiner Basis.");
        add(TranslationKeys.COC_COMMAND_HOME_SUCCESS, "&aDu wirst zurück du deiner Basis geschickt.");

        // RESET
        add(TranslationKeys.COC_COMMAND_RESET_USAGE, "reset (<User>)");
        add(TranslationKeys.COC_COMMAND_RESET_DESCRIPTION, "&7Setzt deine Basis zurück");
        add(TranslationKeys.COC_COMMAND_RESET_SUCCESS, "&aZurücksetzung Erfolgreich.");

        // COC
        add(TranslationKeys.COC_COMMAND_COC_USAGE, "coc");
        add(TranslationKeys.COC_COMMAND_COC_DESCRIPTION, "&7Sends the help message and commands.");

        // CHEAT
        add(TranslationKeys.COC_COMMAND_CHEAT_USAGE, "coc cheat <type> <amount>");
        add(TranslationKeys.COC_COMMAND_CHEAT_DESCRIPTION, "&7Gives you an amount of the given type.");


        // REBUILD
        add(TranslationKeys.COC_COMMAND_REBUILD_USAGE, "coc rebuild (<User>)");
        add(TranslationKeys.COC_COMMAND_REBUILD_DESCRIPTION, "&7Rebuilds a player base.");
        add(TranslationKeys.COC_COMMAND_REBUILD_NO_BASE, "&cThere is no base stored for this player.");
        add(TranslationKeys.COC_COMMAND_REBUILD_SUCCESS, "&aRebuild started...");

        // PLAYER
        add(TranslationKeys.COC_PLAYER_LOADING_PLAYER_DATA_TITLE, "&7Loading player data...");
        add(TranslationKeys.COC_PLAYER_LOADING_PLAYER_DATA_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + "&7" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_RESOURCES_TITLE, "&7Fetching up resources...");
        add(TranslationKeys.COC_PLAYER_LOADING_RESOURCES_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + "&7" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_TROOPS_TITLE, "&7Fetching up troops...");
        add(TranslationKeys.COC_PLAYER_LOADING_TROOPS_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + "&7" + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_FINISHED_TITLE, "&aLoading finished!");
        add(TranslationKeys.COC_PLAYER_LOADING_FINISHED_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_BASE_TITLE, "&7Generating &6base");
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_BASE_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + "&7" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_PLACING_TITLE, "&7Generating &6buildings");
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_PLACING_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + "&7" + SpecialChars.LINE_EXTRA_BOLD);
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_FINISHED_TITLE, "&7Setup &6finished!");
        add(TranslationKeys.COC_PLAYER_LOADING_NEW_FINISHED_SUBTITLE, "&a" + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD + SpecialChars.LINE_EXTRA_BOLD);

        add(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "&cDu hast nicht genug ${type}&c!");
        add(TranslationKeys.COC_PLAYER_NO_MORE_BUILDINGS, "&cDu kannst davon nur &e${max} &cmit deinem Rathaus level bauen.");
        add(TranslationKeys.COC_PLAYER_NO_RESOURCE_TANKS, "&cDeine Lager sind voll!");
        add(TranslationKeys.COC_PLAYER_BUILDERS_WORKING, "&cAlle Bauarbeiter sind derzeit beschäftigt.");
        add(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST_MESSAGE, "&cRepariere dein Rathaus als erstes!");
        add(TranslationKeys.COC_PLAYER_TUTORIAL_REPAIR_TOWNHALL_FIRST_ACTIONBAR, "&cRepariere dein Rathaus als erstes!");

        // tutorial actionbars
        add(TranslationKeys.COC_PLAYER_TUTORIAL_REPAIR_TOWNHALL_FIRST_ACTIONBAR, theodor + "Let's repair your town hall!");
        add(TranslationKeys.COC_PLAYER_TUTORIAL_COLLECT_RESOURCE, theodor + "Collect gold at the miner first!");
        add(TranslationKeys.COC_PLAYER_TUTORIAL_BUILD_FARM_ACTIONBAR, theodor + "Build a &dFarm &7and &dBarn&7!");
        add(TranslationKeys.COC_PLAYER_TUTORIAL_DEFENSE_ACTIONBAR, theodor + "Build a &6Cannon&7!");
        add(TranslationKeys.COC_PLAYER_TUTORIAL_TROOPS_ACTIONBAR, theodor + "Build an &6Army camp &7and a &6Barack&7!");

        // RESOURCE
        add(TranslationKeys.COC_RESOURCE_FOOD, "&dNahrung");
        add(TranslationKeys.COC_RESOURCE_GOLD, "&6Gold");
        add(TranslationKeys.COC_RESOURCE_ELECTRIC, "&eElektrizität");
        add(TranslationKeys.COC_VALUES_GEMS, "&2Edelsteine");
        add(TranslationKeys.COC_VALUES_ELO, "&eElo");
        add(TranslationKeys.COC_VALUES_XP, "&aEXP");

        // BUILDINGS
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_NAME, "&6Rathaus");
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_DESCRIPTION, "&7Das Rathaus ist das Hauptgebäude;&7in deinem Dorf.");
        add(TranslationKeys.COC_BUILDING_GENERAL_BUILDER_NAME, "&6Bauarbeiter");
        add(TranslationKeys.COC_BUILDING_GENERAL_BUILDER_DESCRIPTION, "&7Die Bauarbeiter bauen die Gebäude;&7in deinem Dorf.");

        // CONTAINER
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_NAME, "&6Goldlager");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_DESCRIPTION, "&7In diesem Lager wird dein Gold gelagert.");
        add(TranslationKeys.COC_BUILDING_CONTAINER_BARN_NAME, "&dCarrot Barn");
        add(TranslationKeys.COC_BUILDING_CONTAINER_BARN_DESCRIPTION, "&7This building stores your carrots.");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GENERATOR_NAME, "&eGenerator");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GENERATOR_DESCRIPTION, "&7This building generates &eelectricity;&7while consuming &8coal&7.");
        add(TranslationKeys.COC_BUILDING_CONTAINER_LOREDETAILS, ";&7ResourceType: ${type};&7Maximum amount: ${color}${max}");

        // GATHER
        add(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_NAME, "&6Gold miner");
        add(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_DESCRIPTION, "&7The miner mines gold for you.");
        add(TranslationKeys.COC_BUILDING_GATHER_FARM_NAME, "&dFarm");
        add(TranslationKeys.COC_BUILDING_GATHER_FARM_DESCRIPTION, "&7Farms your food.");
        add(TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_NAME, "&8Coal drill");
        add(TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_DESCRIPTION, "&7This building gathers coal for the &6Generator&7.");
        add(TranslationKeys.COC_BUILDING_GATHER_LOREDETAILS, ";&7Gathers ${type};&7Maximum storage amount: ${color}${max};&7Resource per hour: ${color}${max}");

        // DEFENSE
        add(TranslationKeys.COC_BUILDING_DEFENSE_LOREDETAILS, ";&7Max hp: ${hp};&7Damage per second: ${damage};&7Attacks flying enemies: ${flying};&7Prioritize Trooptype: ${prioritize}");

        // BARACK BUILDINGS
        add(TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_NAME, "&6Baracks");
        add(TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_DESCRIPTION, "&7This building trains your troops.");

        // RANDOM
        add(TranslationKeys.COC_BUILDING_RANDOM_BUSH_NAME, "&2Bush");
        add(TranslationKeys.COC_BUILDING_RANDOM_BUSH_DESCRIPTION, "&7This is a random object,;&7which you can destroy.");

        // GUIS
        add(TranslationKeys.COC_GUI_BUILDING_NAME, "&6${name} &8" + SpecialChars.DOUBLE_ARROW_RIGHT + " &6Level ${level}");
        add(TranslationKeys.COC_GUI_BUILDING_HP_NAME, "&6Hitpoints");
        add(TranslationKeys.COC_GUI_BUILDING_HP_LORE, "&c${current}&7/&c${max}");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME, "&6Upgrade");
        add(TranslationKeys.COC_GUI_BUILDING_NO_UPGRADE_NAME, "&7No further upgrades");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "&7Click to upgrade;&7Upgrade cost: &e${cost}");
        add(TranslationKeys.COC_GUI_BUILDING_REPAIR_NAME, "&6Repair");
        add(TranslationKeys.COC_GUI_BUILDING_REPAIR_LORE, "&7Click to repair;&7Repair cost: &e${cost}");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_NAME, "&6Move Building");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_LORE, "&7Click to move the building.");
        add(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, "&6Amount: ${color}${current}&7/${color}${max}");

        add(TranslationKeys.COC_GUI_BUILDING_DESTROY_NAME, "&cDestroy");
        add(TranslationKeys.COC_GUI_BUILDING_DESTROY_LORE, "&7Zerstöre das Gebäude.;&7Zerstörungs kosten: ${cost}");

        add(TranslationKeys.COC_GUI_GATHER_CLOCK_NAME, "${type} &6per hour");
        add(TranslationKeys.COC_GUI_GATHER_CLOCK_LORE, "&7This building gathers;${resource}&7/hour");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_NAME, "&eCollect resource");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_LORE, "&7Click to collect the gathered resource;&7Amount: ${resource}");

        add(TranslationKeys.COC_GUI_BUILDING_UNDERCONSTRUCTION, "&eUnder construction: &e${left} left");

        add(TranslationKeys.COC_INV_CONSTRUCTION_NAME, "&6Construction menu");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_DECORATIVE_NAME, "&2Decorative Buildings");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_DECORATIVE_LORE, "&7Open the decorative buildings menu;&7Includes all decorations and builders!");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_DEFENSE_NAME, "&eDefense");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_DEFENSE_LORE, "&7Open the defense buildings menu;&7Includes buildings to defend your base.");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_NAME, "&dResources");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_LORE, "&7Open the resource buildings menu;&7Includes Miners, Pumps and containers!");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_NAME, "&cTroops");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_LORE, "&7Open the troop buildings menu;&7Includes barracks and troops field");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_NAME, "&6Buildings menu");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_LORE, "&7Building cost: &e${cost} ${type};&7Amounts of building;&7with town hall level ${level}: &e${amount}&7/&e${max}");
        add(TranslationKeys.COC_CONSTRUCTION_TIMELEFT, "&7Time left:");

        // TROOPS
        add(TranslationKeys.COC_TROOPS_TYPE_LAND, "&eLand");
        add(TranslationKeys.COC_TROOPS_TYPE_FLYING, "&aFlying");
        add(TranslationKeys.COC_TROOPS_LOREDETAILS, ";&7Type: ${type};&7Max hp: &c${hp};&7Damage: &e${damage};&7Prioritize building: ${prioritize};&7Size: &e${size}");
        add(TranslationKeys.COC_TROOPS_BARBARIAN_NAME, "&eBarbarian");
        add(TranslationKeys.COC_TROOPS_BARBARIAN_DESCRIPTION, "&7This idyet will run to the;&7next building and attacks it.");


        // TROOPS
        add(TranslationKeys.COC_GUI_TROOPS_CONTAINER_NAME, "&6Container");
        add(TranslationKeys.COC_GUI_TROOPS_CONTAINER_LORE, "&7There are currently ${current} of ${max};&7troops in this building.");
        add(TranslationKeys.COC_GUI_CONTAINER_NAME, "&6Troops Container");
        add(TranslationKeys.COC_GUI_CONTAINER_LORE, "&7Amount of units: &e${amount}");
        add(TranslationKeys.COC_GUI_TROOPS_TRAIN_NAME, "&6Train your troops");
        add(TranslationKeys.COC_GUI_TROOPS_TRAIN_LORE, "&7Click to train troops.");
        add(TranslationKeys.COC_GUI_TRAIN_NAME, "&6Train troops");
        add(TranslationKeys.COC_GUI_TRAIN_QUEUE_LORE, "&7This troop is currently in queue;&7Duration left: &e${duration} &7/ &e${max} seconds");
        add(TranslationKeys.COC_GUI_TRAIN_QUEUE_FINISHED_LORE, "&7This troop is educated;&7It waits till there is more space in an &6army camp&7.");
        add(TranslationKeys.COC_GUI_TRAIN_LORE, "&7Barracks minimum level: &e${minlevel};&7Train duration: &e${duration} seconds");
        add(TranslationKeys.COC_GUI_TRAIN_FULL, "&7You've reached the &cmaximum amount of troops &7in this building!");

        // ATTACK HISTORY GUI
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_TITLE, "&6Attack history");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_NEW_NAME, "&6New &cAttacks&6!");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_NEW_LORE, "${name}&7: &6${stars} Stars &7" + SpecialChars.DOUBLE_ARROW_RIGHT + " &c${elo} elo, &d${food} Food &7and &e${gold} Gold");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ATTACKS_NAME, "&cSee attacks");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ATTACKS_LORE, "&7Look up all of your attacks!");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_DEFENSE_NAME, "&6See defenses");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_DEFENSE_LORE, "&7Look up all of your defenses!");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_ATTACK_TITLE, "&6CoC - All attacks");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_DEFENSE_TITLE, "&6CoC - All defenses");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_ATTACK_LORE, "&7Attacked at ${date};&7Stars: &6${stars};&7Elo: &c${elo}; ;&7Food gained: &d${food};&7Gold gained: &e${gold}");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_DEFENSE_LORE, "&7Defensed at ${date};&7Stars: &6${stars};&7Elo loss: &c${elo}; ;&7Food lost: &d${food};&7Gold lost: &e${gold}");



        // ITEMS
        add(TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY, "&cThere is a building in the way!");
        add(TranslationKeys.COC_CONSTRUCTION_MOVED, "&aBuilding successfully moved");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME, "&eMove Building");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE, "&7Hold this item and look on the;&7ground to see, where you could place the building.;&7Click on the block;&7you want to move the building.");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME, "&eRotate Building");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE, "&7Click to rotate the building");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_NAME, "&cCancel operation");
        add(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_LORE, "&7Click to cancel the move operation.");

        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME, "&eCreate Building");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE, "&7Hold this item and look on the;&7ground to see, where you could place the building.;&7Click on the block;&7you want to construct the building.");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_ANOTHER_NAME, "&7Add &aanother building");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_ANOTHER_LORE, "&7&aConfirms the current selection;&7and adds another selection.;;&7Use this, if you want to create more than;&7one building from the same type.");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_REMOVE_NAME, "&cRemove &7last building");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_REMOVE_LORE, "&cDeny &7the last selection");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME, "&aCancel operation");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE, "&7Click to cancel the construct operation");
        add(TranslationKeys.COC_CONSTRUCTION_BUILD, "&aBuilding will be constructed!");


        add(TranslationKeys.COC_INV_BOOK_CONTENT + "collect_resource", """
                Welcome, ${player}&0!
                I'm Theodore, your trusty builder. Let's get started, shall we?

                We need to repair the town hall to start your settlement!
                But the repairment requires &6gold&0.;Gold and other resources are collected by gatherer buildings

                &0Go to the &6&lGold miner&0 and click on it.\s
                You can also use the pointer to click through the air.
                Click "Collect" to fill all mined gold in your gold stock.;Each building has it's own GUI

                You can see your gold filled up in the gold stock for example!""");

        add(TranslationKeys.COC_INV_BOOK_CONTENT + "repair_townhall", """
                Perfect, ${player}&0!

                Let's repair the townhall now.

                Most buildings can be upgraded through their GUI.
                If you upgrade buildings their attributes improve.

                E.g. The &6gold miner&0's storage and mining speed improves""");

        add(TranslationKeys.COC_INV_BOOK_CONTENT + "build_farm", """
                Great work, friend!

                Now, it's time to dive into the exciting part of the game.

                Open up your inventory and locate the "Construction menu.";Here, you can begin constructing new buildings! Keep in mind that any construction requires resources, so plan accordingly.
                To get started, we recommend building a 'Farm' and a 'Barn' to collect food. Navigate to the Construction Menu and select the 'Farm' option.;Once you have selected the 'Farm', you can choose where to place the building by looking at the ground. If you are happy with the placement and no other buildings occupy the area, you can begin construction. So keep building and have fun!;Don't worry if you're not completely satisfied with the placement of your other buildings!

                You have the ability to move and rotate them to a new spot.
                Simply select the building you want to adjust, and choose the "Move" option.""");
        add(TranslationKeys.COC_INV_BOOK_CONTENT + "troops", """
                Awesome!
                The cannon shoots at attacking enemies.

                Let's train some own troops so we can attack bases, shall we?;Build an &6Army camp &0and a &6Barrack&0.
                You can train some troops in the &6Barrack&0.
                Trained troops will move to the &6Army camp&0.""");
        add(TranslationKeys.COC_INV_BOOK_CONTENT + "finished", """
                Great job, my friend!
                When you attack other bases you can:
                - Loot resources
                - Receive elo
                But watch out: The enemy has also defenses!
                You can start attacking players at the right corner of your inventory.;&2Attacking system is not yet implemented.;The tutorial ends here, but I'll give you my book.
                If you have any questions just read through it!""");
        add(TranslationKeys.COC_INV_BOOK_CONTENT + "general", "TODO add normal help book");

        insertIntoLanguage();
    }
}
