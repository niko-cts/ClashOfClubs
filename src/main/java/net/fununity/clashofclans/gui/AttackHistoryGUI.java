package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.history.AttackHistory;
import net.fununity.clashofclans.attacking.history.AttackHistoryDatabase;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.common.player.PlayerTextures;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.PlayerDataUtil;
import net.fununity.main.api.util.Utils;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The GUI class for the attack history menu.
 * @author Niko
 * @since 0.0.1
 */
public class AttackHistoryGUI {

    private AttackHistoryGUI() {
        throw new UnsupportedOperationException("AttackHistory should not be instantiated.");
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy mm:HH");

    /**
     * Open the main history gui.
     * @param player APIPlayer - player to open.
     * @since 0.0.1
     */
    public static void openHistory(APIPlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            Language lang = player.getLanguage();

            CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_TITLE), 9 * 3);

            List<AttackHistory> baseAttacks = AttackHistoryDatabase.getInstance().getBaseAttacks(player.getUniqueId(), false);

            if (!baseAttacks.isEmpty()) {
                UUID[] uuids = new UUID[baseAttacks.size()];
                for (int i = 0; i < baseAttacks.size(); i++)
                    uuids[i] = baseAttacks.get(i).getAttacker();

                String[] playerNames = PlayerDataUtil.getPlayerNames(uuids);
                List<String> lore = new ArrayList<>();
                for (int i = 0; i < baseAttacks.size(); i++) {
                    AttackHistory history = baseAttacks.get(i);
                    lore.add(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_NEW_LORE,
                            Arrays.asList("${name}", "${elo}", "${date}", "${gold}", "{food}", "${stars}"),
                            Arrays.asList(playerNames[i], history.getElo() + "", DATE_FORMAT.format(history.getDate()),
                                    history.getResourcesGathered(ResourceTypes.GOLD) + "", history.getResourcesGathered(ResourceTypes.FOOD) + "",
                                    history.getStars() + "")));
                }

                menu.setItem(9 + 2, new ItemBuilder(Material.PAPER)
                        .setName(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_NEW_NAME))
                        .setLore(lore).craft());

                AttackHistoryDatabase.getInstance().seen(baseAttacks);
            }

            menu.setItem(9 + 4, new ItemBuilder(Material.IRON_SWORD)
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .setName(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_ATTACKS_NAME))
                    .setLore(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_ATTACKS_LORE).split(";")).craft(), new ClickAction(true) {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () ->
                            openSpecified(player, AttackHistoryDatabase.getInstance().getBaseDefends(player.getUniqueId(), true), true));
                }
            });
            menu.setItem(9 + 6, new ItemBuilder(Material.SHIELD)
                    .setName(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_DEFENSE_NAME))
                    .setLore(lang.getTranslation(TranslationKeys.COC_GUI_ATTACKHISTORY_DEFENSE_LORE).split(";")).craft(), new ClickAction(true) {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () ->
                            openSpecified(player, AttackHistoryDatabase.getInstance().getBaseAttacks(player.getUniqueId(), true), false));
                }
            });

            menu.fill(UsefulItems.BACKGROUND_BLACK);
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> menu.open(player));
        });
    }

    /**
     * Open the specified gui for defenses/attack histories.
     * @param player APIPlayer - player to open.
     * @param histories List<AttackHistory> - the histories to display.
     * @param attack boolean - histories are either <u>attack</u> or defense histories
     * @since 0.0.1
     */
    private static void openSpecified(APIPlayer player, List<AttackHistory> histories, boolean attack) {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            Language lang = player.getLanguage();
            CustomInventory menu = new CustomInventory(lang.getTranslation(attack ? TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_ATTACK_TITLE : TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_DEFENSE_TITLE),
                    Utils.getPerfectInventorySize(Math.max(histories.size(), 1) + 9));

            histories.removeIf(h -> h.getDate().isBefore(OffsetDateTime.now().minusDays(7)));

            UUID[] uuids = new UUID[histories.size()];
            for (int i = 0; i < histories.size(); i++)
                uuids[i] = histories.get(i).getAttacker();

            PlayerTextures[] playerTexture = PlayerDataUtil.getPlayerTextures(uuids);
            String[] playerNames = PlayerDataUtil.getPlayerNames(uuids);

            for (int i = 0; i < histories.size(); i++) {
                AttackHistory history = histories.get(i);
                PlayerTextures texture = playerTexture[i];
                menu.addItem(new ItemBuilder(UsefulItems.PLAYER_HEAD).setSkullOwner(texture).setName(playerNames[i])
                        .setLore(lang.getTranslation(attack ? TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_ATTACK_LORE : TranslationKeys.COC_GUI_ATTACKHISTORY_ALL_DEFENSE_LORE,
                                Arrays.asList("${name}", "${elo}", "${date}", "${gold}", "${food}", "${stars}"),
                                Arrays.asList(playerNames[i], history.getElo() + "", DATE_FORMAT.format(history.getDate()), history.getResourcesGathered(ResourceTypes.GOLD) + "", history.getResourcesGathered(ResourceTypes.FOOD) + "", history.getStars() + "")).split(";")).craft());
            }


            menu.setItem(menu.getInventory().getSize() - 9, UsefulItems.LEFT_ARROW, new ClickAction(true) {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    openHistory(player);
                }
            });
            for (int i = menu.getInventory().getSize() - 8; i < menu.getInventory().getSize(); i++)
                menu.setItem(i, UsefulItems.BACKGROUND_BLACK);
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> menu.open(player));
        });
    }
}
