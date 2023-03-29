package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.database.DatabaseAttackBots;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.Utils;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AttackLookingGUI {

    private static final Map<UUID, List<DatabaseAttackBots.BotData>> LAST_BOT = new ConcurrentHashMap<>(); // player, bot
    private static final int BOT_ATTACK_COST = 100;

    /**
     * This opens the main gui of the Attack looking GUI.
     * The player can decide here whether their opens the singleplayer attack or multiplayer.
     *
     * @param player APIPlayer - the player to open.
     * @since 1.0.1
     */
    public static void open(APIPlayer player) {
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        Language language = player.getLanguage();

        if (!coCPlayer.hasTroops()) {
            player.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_ATTACK_NO_TROOPS);
            player.playSound(Sound.ENTITY_VILLAGER_NO);
            return;
        }

        CustomInventory menu = new CustomInventory(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_TITLE), 27);
        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.setItem(11, new ItemBuilder(UsefulItems.SKELETON_SKULL).setName(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_LORE).split(";")).craft(), new ClickAction(true) {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> openSingleplayer(apiPlayer));
            }
        });

        menu.setItem(15, new ItemBuilder(UsefulItems.PLAYER_HEAD).setName(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_MULTIPLAYER_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_MULTIPLAYER_LORE).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                apiPlayer.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_FEATURE_NOT_IMPLEMENTED);
            }
        });

        menu.open(player);
    }

    /**
     * Opens the singleplayer menu for the given Player.
     * @param apiPlayer APIPlayer - the player to open.
     * @since 1.0.1
     */
    private static void openSingleplayer(APIPlayer apiPlayer) {
        UUID uuid = apiPlayer.getUniqueId();
        if (!LAST_BOT.containsKey(uuid)) {
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
                MatchmakingSystem.getInstance().getDefenderBots();
                LAST_BOT.put(uuid, DatabaseAttackBots.getInstance().getAllDoneBots(uuid));
                Bukkit.getScheduler().runTaskLaterAsynchronously(ClashOfClubs.getInstance(), () -> LAST_BOT.remove(uuid), 20 * 60);
                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> openSingleplayer(apiPlayer));
            });
            return;
        }

        Language language = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_TITLE), Utils.getPerfectInventorySize(19 + MatchmakingSystem.getInstance().getDefenderBots().size()));

        for (int i = 0; i < 9; i++)
            menu.setItem(i, UsefulItems.BACKGROUND_BLACK);

        boolean canFight = true;
        int i = 0;
        List<DatabaseAttackBots.BotData> botData = LAST_BOT.get(uuid);
        for (DatabaseAttackBots.BotData defenderBot : MatchmakingSystem.getInstance().getDefenderBots()) {
            i++;
            DatabaseAttackBots.BotData bot = botData.stream().filter(b -> b.botUUID().equals(defenderBot.botUUID())).findFirst().orElse(null);
            final boolean canFightThis = canFight;
            if (canFight && (bot == null || bot.won()))
                canFight = false;

            Map<ResourceTypes, Integer> obtainableResource = new EnumMap<>(ResourceTypes.class);
            for (Map.Entry<ResourceTypes, Integer> entry : defenderBot.resources().entrySet()) {
                int amount = entry.getValue() - (bot != null ? bot.resources().getOrDefault(entry.getKey(), 0) : 0);
                obtainableResource.put(entry.getKey(), Math.round(MatchmakingSystem.RESOURCE_MULTIPLER * amount));
            }

            String[] botLore = canFightThis ? language.getTranslation(TranslationKeys.COC_GUI_ATTACK_BOT_LORE,
                            Arrays.asList("${food}", "${gold}"),
                            Arrays.asList("" + obtainableResource.get(ResourceTypes.FOOD),
                                    "" + obtainableResource.get(ResourceTypes.GOLD))).split(";") : new String[0];

            menu.addItem(new ItemBuilder(Material.ZOMBIE_HEAD)
                            .addEnchantment(canFightThis ? Enchantment.ARROW_FIRE : null, 1, true, false)
                            .setName(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_BOT_NAME, "${id}", i + ""))
                            .setLore(botLore).craft(),
                    new ClickAction(true) {
                        @Override
                        public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                            if (canFightThis && (bot == null || obtainableResource.values().stream().anyMatch(r -> r > 0))) {
                                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> acceptSingleplayer(apiPlayer, defenderBot.botUUID()));
                            } else {
                                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_ALREADYPLAYED);
                            }
                        }
                    });
        }

        menu.fill(UsefulItems.BACKGROUND_BLACK);

        menu.open(apiPlayer);
    }

    /**
     * Accepts the attack for the given Player with the given bot.
     * @param apiPlayer APIPlayer - the player to start the attack.
     * @param botUUID UUID - the uuid of the bot.
     * @since 1.0.1
     */
    private static void acceptSingleplayer(APIPlayer apiPlayer, UUID botUUID) {
        Language language = apiPlayer.getLanguage();
        CustomInventory menu = new CustomInventory(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_ACCEPT_TITLE), 27);
        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.setItem(18, new ItemBuilder(UsefulItems.LEFT_ARROW).setName(" ").craft(),
                new ClickAction() {
                    @Override
                    public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                        openSingleplayer(apiPlayer);
                    }
                });
        menu.setItem(13, new ItemBuilder(UsefulItems.UP_ARROW).setName(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_ACCEPT_NAME))
                        .setLore(language.getTranslation(TranslationKeys.COC_GUI_ATTACK_SINGLEPLAYER_ACCEPT_LORE, "${food}", BOT_ATTACK_COST + "").split(";")).craft(),
                new ClickAction() {
                    @Override
                    public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId());
                        if (coCPlayer == null) {
                            setCloseInventory(true);
                            return;
                        }
                        if (coCPlayer.getResourceAmount(ResourceTypes.FOOD) >= BOT_ATTACK_COST) {
                            setCloseInventory(true);
                            System.out.println(coCPlayer.getResourceAmount(ResourceTypes.FOOD));
                            coCPlayer.removeResourceWithUpdate(ResourceTypes.FOOD, BOT_ATTACK_COST);
                            System.out.println("Removed resource " + coCPlayer.getResourceAmount(ResourceTypes.FOOD));
                            MatchmakingSystem.getInstance().startRequest(apiPlayer, botUUID);
                        } else {
                            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "${type}", ResourceTypes.FOOD.getColoredName(language));
                            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
                            openSingleplayer(apiPlayer);
                        }
                    }
                });

        menu.open(apiPlayer);
    }
}
