package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCDataPlayer;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.cloud.common.events.cloud.CloudEvent;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.cloud.CloudManager;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

/**
 * The matchmaking system.
 * @author Niko
 * @since 0.0.1
 */
public class MatchmakingSystem {

    private static MatchmakingSystem instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link MatchmakingSystem} - the singleton instance.
     * @since 0.0.1
     */
    public static MatchmakingSystem getInstance() {
        if(instance == null)
            instance = new MatchmakingSystem();
        return instance;
    }

    public static final String ATTACKER_SERVER = "cocattack01";
    private final Map<UUID, UUID> attackWatcher;
    private final Map<UUID, List<CoCDataPlayer>> visitedAttacks;
    private final List<UUID> currentlyDefending;

    private MatchmakingSystem() {
        this.currentlyDefending = new ArrayList<>();
        this.attackWatcher = new HashMap<>();
        this.visitedAttacks = new HashMap<>();
    }

    public void startMatchmakingLooking(APIPlayer attacker, List<CoCDataPlayer> blacklist) {
        attacker.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_LOOKING_FOR_BASES);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            UUID uuid = attacker.getUniqueId();
            CoCDataPlayer attackingBase = getAttackingBase(uuid, blacklist);

            if (!attacker.getPlayer().isOnline())
                return;

            if (attackingBase == null) {
                attacker.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_ATTACK_NO_BASES_FOUND);
                return;
            }

            List<CoCDataPlayer> visited = visitedAttacks.getOrDefault(uuid, new ArrayList<>());
            visited.add(attackingBase);
            visitedAttacks.put(uuid, visited);

            attacker.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_BASE_FOUND);

            PlayerManager.getInstance().getPlayer(uuid).leave(attacker);
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
                this.attackWatcher.put(uuid, attackingBase.getUniqueId());

                Location loc = attackingBase.getLocation().add(20, 0, 20);
                loc.setY(BuildingLocationUtil.getHighestYCoordinate(loc));
                attacker.getPlayer().teleport(loc);
                attacker.getPlayer().getInventory().clear();
                Language lang = attacker.getLanguage();
                attacker.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.BARRIER).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_CANCEL)).craft());
                attacker.getPlayer().getInventory().setItem(0, new ItemBuilder(UsefulItems.UP_ARROW).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_ACCEPT)).craft());
                attacker.getPlayer().getInventory().setItem(1, new ItemBuilder(UsefulItems.RIGHT_ARROW).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_NEXT)).craft());
            });
        });
    }

    private CoCDataPlayer getAttackingBase(UUID attacker, List<CoCDataPlayer> blacklist) {
        CoCDataPlayer dataPlayer = PlayerManager.getInstance().getDataPlayer(attacker);
        List<CoCDataPlayer> allPlayerData = DatabasePlayer.getInstance().getAllPlayerData(attacker);
        allPlayerData.removeIf(online -> PlayerManager.getInstance().isCached(online.getUniqueId()) || blacklist.contains(online) || isCurrentlyDefending(online.getUniqueId()));
        allPlayerData.sort(Comparator.comparingInt(o -> Math.abs(dataPlayer.getElo() - o.getElo())));
        return allPlayerData.isEmpty() ? null : allPlayerData.get(0);
    }

    public void startAttack(APIPlayer attacker) {
        UUID defender = this.attackWatcher.get(attacker.getUniqueId());
        this.attackWatcher.remove(attacker.getUniqueId());
        attacker.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_REQUEST_SEND);
        if (FunUnityAPI.getInstance().getCloudClient() != null)
            FunUnityAPI.getInstance().getCloudClient().forwardToServer(ATTACKER_SERVER, new CloudEvent(CloudEvent.COC_ATTACK_REQUEST).addData(attacker).addData(defender).addData(FunUnityAPI.getInstance().getCloudClient().getServerDefinition().getServerId()));
    }

    public void cancelWatching(APIPlayer player) {
        this.attackWatcher.remove(player.getUniqueId());
        PlayerManager.getInstance().getPlayer(player.getUniqueId()).visit(player, true);
    }

    public void requestFinished(UUID attacker, UUID defender) {
        APIPlayer player = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(attacker);
        if (player == null) {
            FunUnityAPI.getInstance().getCloudClient().forwardToServer(ATTACKER_SERVER, new CloudEvent(CloudEvent.COC_ATTACK_DELETE).addData(attacker));
            return;
        }
        this.currentlyDefending.add(defender);
        player.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_START_ATTACKING);
        CloudManager.getInstance().sendPlayerToServer(ATTACKER_SERVER, attacker);
    }

    public void attackFinished(UUID defender) {
        this.currentlyDefending.remove(defender);
    }

    public boolean isCurrentlyDefending(UUID uuid) {
        return this.currentlyDefending.contains(uuid);
    }

    public Map<UUID, List<CoCDataPlayer>> getVisitedAttacks() {
        return visitedAttacks;
    }

    public Map<UUID, UUID> getAttackWatcher() {
        return attackWatcher;
    }

}
