package net.fununity.clashofclans.util;

import org.bukkit.Material;

/**
 * This class stores the hotbar item types.
 */
public class HotbarItems {

    private HotbarItems() {
        throw new UnsupportedOperationException("HotbarItemsUtil should not be instantiated.");
    }

    public static final Material CREATE_BUILDING = Material.NETHER_STAR;
    public static final Material CREATE_BUILDING_ANOTHER = Material.LEAD;
    public static final Material CREATE_BUILDING_REMOVE = Material.REDSTONE;

    public static final Material MOVE_BUILDING = Material.PISTON;
    public static final Material ROTATE_BUILDING = Material.STICK;
    public static final Material CANCEL = Material.BARRIER;
    public static final Material SHOP = Material.CLOCK;

    public static final Material START_ATTACK = Material.IRON_SWORD;
    public static final Material ATTACK_HISTORY = Material.PAPER;
    public static final Material POINTER = Material.TRIDENT;
    public static final Material TUTORIAL_BOOK = Material.WRITABLE_BOOK;


}
