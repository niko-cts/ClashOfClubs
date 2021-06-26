package net.fununity.clashofclans.language;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.main.api.common.messages.MessageList;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.misc.translationhandler.TranslationHandler;

public class EnglishMessages extends MessageList {

    public EnglishMessages() {
        super(TranslationHandler.getInstance().getLanguageHandler().getLanguageByCode("en"));

        // COMMANDS
        // VISIT
        add(TranslationKeys.COC_COMMAND_VISIT_USAGE, "visit <User>");
        add(TranslationKeys.COC_COMMAND_VISIT_DESCRIPTION, "&7Visit another base.");
        add(TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_HASNOBUILDING, "&cThis user has not played " + ClashOfClubs.getColoredName().replace("§", "&") + " &cyet.");
        add(TranslationKeys.COC_COMMAND_VISIT_ILLEGAL_NOTONLINE, "&cThis user is not online! &7Buy &6premium &7to visit offline player bases.");
        add(TranslationKeys.COC_COMMAND_VISIT_SUCCESS, "&aYou will be send to the base!");

        // HOME
        add(TranslationKeys.COC_COMMAND_HOME_USAGE, "home");
        add(TranslationKeys.COC_COMMAND_HOME_DESCRIPTION, "&7Sends you back to your home.");
        add(TranslationKeys.COC_COMMAND_HOME_SUCCESS, "&aYou will be send back to your home.");

        // RESET
        add(TranslationKeys.COC_COMMAND_RESET_USAGE, "reset (<User>)");
        add(TranslationKeys.COC_COMMAND_RESET_DESCRIPTION, "&7Resets a users home.");
        add(TranslationKeys.COC_COMMAND_RESET_SUCCESS, "&aSuccessfully reset.");


        // PLAYER
        add(TranslationKeys.COC_PLAYER_LOADING_BASE, "&7Loading up your base... Please wait!");
        add(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "&cYou have not enough ${type}&c!");
        add(TranslationKeys.COC_PLAYER_NO_MORE_BUILDINGS, "&cYou can only build that &e${max} &ctimes at this town hall level.");
        add(TranslationKeys.COC_PLAYER_NO_RESOURCE_TANKS, "&cYou have no free container space for this resource!");
        add(TranslationKeys.COC_PLAYER_BUILDERS_WORKING, "&cAll of your builders are currently working!");
        add(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST, "&cRepair your town hall first!");

        // RESOURCE
        add(TranslationKeys.COC_RESOURCE_FOOD, "&dFood");
        add(TranslationKeys.COC_RESOURCE_GOLD, "&6Gold");
        add(TranslationKeys.COC_RESOURCE_ELECTRIC, "&eYellow");
        add(TranslationKeys.COC_RESOURCE_GEMS, "&2Gems");

        // BUILDINGS
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_NAME, "&6Town Hall");
        add(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_DESCRIPTION, "&7The town hall is the main building;&7in your city");
        add(TranslationKeys.COC_BUILDING_GENERAL_BUILDER_NAME, "&6Builder");
        add(TranslationKeys.COC_BUILDING_GENERAL_BUILDER_DESCRIPTION, "&7The town hall is the main building;&7in your city");
        add(TranslationKeys.COC_BUILDING_GENERAL_CLUB_TOWER_NAME, "&6Club Tower");
        add(TranslationKeys.COC_BUILDING_GENERAL_CLUB_TOWER_DESCRIPTION, "&7The club tower represents the ");

        // CONTAINER
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_NAME, "&6Gold stock");
        add(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_DESCRIPTION, "&7This building stores your gold.");
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
        add(TranslationKeys.COC_BUILDING_DEFENSE_LOREDETAILS, ";&7Max hp: ${hp};&7Damage per second: ${damage};&7Radius: &e${radius};&7Attacks flying enemies: ${flying};&7Prioritize Trooptype: ${prioritize}");
        add(TranslationKeys.COC_BUILDING_DEFENSE_CANNON_NAME, "&6Cannon");
        add(TranslationKeys.COC_BUILDING_DEFENSE_CANNON_DESCRIPTION, "&7The cannon is a basic defense building.");
        add(TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_NAME, "&eArcher tower");
        add(TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_DESCRIPTION, "&7The archer tower is a tower :)");

        // BARACK BUILDINGS
        add(TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_NAME, "&6Baracks");
        add(TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_DESCRIPTION, "&7This building trains your troops.");
        add(TranslationKeys.COC_BUILDING_TROOPS_ARMYCAMP_NAME, "&6Armycamp");
        add(TranslationKeys.COC_BUILDING_TROOPS_ARMYCAMP_DESCRIPTION, "&7This building stores your troops.");

        // RANDOM
        add(TranslationKeys.COC_BUILDING_RANDOM_BUSH_NAME, "&2Bush");
        add(TranslationKeys.COC_BUILDING_RANDOM_BUSH_DESCRIPTION, "&7This is a random object,;&7which you can destroy.");

        // TROOPS
        add(TranslationKeys.COC_TROOPS_TYPE_LAND, "&eLand");
        add(TranslationKeys.COC_TROOPS_TYPE_FLYING, "&aFlying");
        add(TranslationKeys.COC_TROOPS_LOREDETAILS, ";&7Type: ${type};&7Max hp: &c${hp};&7Damage: &e${damage};&7Prioritize Defense: ${prioritize};&7Size: &e${size};&7Cost: ${cost}");
        add(TranslationKeys.COC_TROOPS_BARBARIAN_NAME, "&eBarbarian");
        add(TranslationKeys.COC_TROOPS_BARBARIAN_DESCRIPTION, "&7This idyet will run to the;&7next building and attacks it.");

        // WALLS
        add(TranslationKeys.COC_WALLS_STRAIGHT_WALL_NAME, "&6Normal wall &7(&e│&7)");
        add(TranslationKeys.COC_WALLS_STRAIGHT_WALL_DESCRIPTION, "&7Normal wall to prevent enemy;&7troops from entering your base.");
        add(TranslationKeys.COC_WALLS_CORNER_WALL_NAME, "&6Corner wall &7(&e∟&7)");
        add(TranslationKeys.COC_WALLS_CORNER_WALL_DESCRIPTION, "&7Corner wall to prevent enemy;&7troops from entering your base.");
        add(TranslationKeys.COC_WALLS_CROSS_WALL_NAME, "&6Cross wall &7(&e┼&7)");
        add(TranslationKeys.COC_WALLS_CROSS_WALL_DESCRIPTION, "&7Cross wall to prevent enemy;&7troops from entering your base.");
        add(TranslationKeys.COC_WALLS_HALFCROSS_WALL_NAME, "&6Half-Cross wall &7(&e┤&7)");
        add(TranslationKeys.COC_WALLS_HALFCROSS_WALL_DESCRIPTION, "&7Half-Cross wall to prevent enemy;&7troops from entering your base.");
        add(TranslationKeys.COC_WALLS_GATE_NAME, "&6Gate");
        add(TranslationKeys.COC_WALLS_GATE_DESCRIPTION, "&7A gate, which opens;&7and closes automatically for your base.");

        // GUIS
        add(TranslationKeys.COC_GUI_BUILDING_NAME, "&6${name} &8" + SpecialChars.DOUBLE_ARROW_RIGHT + " &6Level ${level}");
        add(TranslationKeys.COC_GUI_BUILDING_HP_NAME, "&6Hitpoints");
        add(TranslationKeys.COC_GUI_BUILDING_HP_LORE, "&c${current}&7/&c${max}");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME, "&6Upgrade");
        add(TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "&7Click to upgrade;&7Upgrade cost: &e${cost}");
        add(TranslationKeys.COC_GUI_BUILDING_REPAIR_NAME, "&6Repair");
        add(TranslationKeys.COC_GUI_BUILDING_REPAIR_LORE, "&7Click to repair;&7Repair cost: &e${cost}");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_NAME, "&6Move Building");
        add(TranslationKeys.COC_GUI_BUILDING_MOVING_LORE, "&7Click to move the building.");
        add(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, "&6Amount: ${color}${current}&7/${color}${max}");

        add(TranslationKeys.COC_GUI_BUILDING_DESTROY_NAME, "&cDestroy");
        add(TranslationKeys.COC_GUI_BUILDING_DESTROY_LORE, "&7Destroy this building.;&7Destroy cost: ${cost}");

        add(TranslationKeys.COC_GUI_GATHER_CLOCK_NAME, "${type} &6per hour");
        add(TranslationKeys.COC_GUI_GATHER_CLOCK_LORE, "&7This building gathers;${resource}&7/hour");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_NAME, "&eCollect resource");
        add(TranslationKeys.COC_GUI_GATHER_TAKE_LORE, "&7Click to collect the gathered resource;&7Amount: ${resource}");

        add(TranslationKeys.COC_GUI_BUILDING_UNDERCONSTRUCTION, "&eUnder construction: &e${left} left");

        add(TranslationKeys.COC_GUI_CONSTRUCTION_NAME, "&6Construction menu");
        add(TranslationKeys.COC_GUI_CONSTRUCTION_LORE, "&7Open the construction menu;&7You can buy new buildings;&7and decorations there.");
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

        // ATTACK GUI
        add(TranslationKeys.COC_GUI_ATTACK_NAME, "&6Start matchmaking search...");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_LORE, "&7Click to start matchmaking.;&7You will be send to a;&7preview of the base you can attack.");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_NAME, "&eView attack history");
        add(TranslationKeys.COC_GUI_ATTACKHISTORY_LORE, "&7Open the attack history menu.");


        // TROOPS
        add(TranslationKeys.COC_GUI_TROOPS_CONTAINER_NAME, "&6Container");
        add(TranslationKeys.COC_GUI_TROOPS_CONTAINER_LORE, "&7There are currently ${current} of ${max};&7troops in this building.;&7Click to open the gui.");
        add(TranslationKeys.COC_GUI_CONTAINER_NAME, "&6Troops Container");
        add(TranslationKeys.COC_GUI_CONTAINER_LORE, "&7Amount of units: &e${amount}");
        add(TranslationKeys.COC_GUI_TROOPS_TRAIN_NAME, "&6Train your troops");
        add(TranslationKeys.COC_GUI_TROOPS_TRAIN_LORE, "&7Click to train troops.");
        add(TranslationKeys.COC_GUI_TRAIN_NAME, "&6Train troops");
        add(TranslationKeys.COC_GUI_TRAIN_QUEUE_LORE, "&7This troop is currently in queue;&7Duration left: &e${duration} &7/ &e${max} seconds");
        add(TranslationKeys.COC_GUI_TRAIN_LORE, "&7Barracks minimum level: &e${minlevel};&7Train duration: &e${duration} seconds");
        add(TranslationKeys.COC_GUI_TRAIN_RELOAD, "&eReload this menu");

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
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME, "&aCancel operation");
        add(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE, "&7Click to cancel the construct operation");
        add(TranslationKeys.COC_CONSTRUCTION_BUILD, "&aBuilding will be constructed!");

        // ATTACKING
        add(TranslationKeys.COC_ATTACKING_BOARD_STARS, "&6Stars");
        add(TranslationKeys.COC_ATTACK_FINISHED,
                "&8------{ ${name} }--------\n" +
                        "      ${stars}  \n" +
                        "&6Attack finished!\n" +
                        "&cElo points &7received: &c${elo}\n" +
                        "&6Gold &7gathered: &e${gold}\n" +
                        "&dFood &7gathered: &d${food}\n" +
                        "&8------{ ${name} }--------\n" +
                        "\n" +
                        "&7You will be send back in 5 seconds...");
        add(TranslationKeys.COC_ATTACK_START_ATTACKING, "&aThe attacking scenery was prepared! You will be send to the base.");
        add(TranslationKeys.COC_ATTACK_REQUEST_SEND, "&aAttacking base set! You will be send to it in a few moments.");
        add(TranslationKeys.COC_ATTACK_NO_BASES_FOUND, "&cThere was no base found.");
        add(TranslationKeys.COC_ATTACK_BASE_FOUND, "&aA new base was found...");
        add(TranslationKeys.COC_ATTACK_BASE_UNDERATTACK, "&eYour base is currently under attack! &7You need to wait until the attack is finished.");
        add(TranslationKeys.COC_ATTACK_LOOKING_FOR_BASES, "&7Looking for bases to attack, please wait...");
        add(TranslationKeys.COC_ATTACK_ITEM_NEXT, "&6Next base");
        add(TranslationKeys.COC_ATTACK_ITEM_ACCEPT, "&aAttack this base");
        add(TranslationKeys.COC_ATTACK_ITEM_CANCEL, "&cCancel search");

        insertIntoLanguage();
    }
}
