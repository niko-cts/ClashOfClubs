package net.fununity.clashofclans.attacking.spying;

import net.fununity.main.api.player.APIPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpyingPlayer {

    private final APIPlayer attacker;
    private final Set<UUID> visitedAttacks;
    private UUID currentWatching;

    public SpyingPlayer(APIPlayer attacker) {
        this.attacker = attacker;
        this.visitedAttacks = new HashSet<>();
    }

    /*public void startMatchmakingLooking() {
        attacker.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_LOOKING_FOR_BASES);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            UUID uuid = attacker.getUniqueId();
            CocAttackerPlayer attackingBase = getAttackingBase(uuid, blacklist);

            if (!attacker.getPlayer().isOnline())
                return;

            if (attackingBase == null) {
                attacker.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_ATTACK_NO_BASES_FOUND);
                return;
            }

            List<UUID> visited = visitedAttacks.getOrDefault(uuid, new ArrayList<>());
            visited.add(attackingBase);
            visitedAttacks.put(uuid, visited);
        });
    }

    public void spectate() {
        attacker.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_ATTACK_BASE_FOUND);

        ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid).leave(attacker);
        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
            Location loc = attackingBase.getLocation().add(20, 0, 20);
            loc.setY(BuildingLocationUtil.getHighestYCoordinate(loc));
            attacker.getPlayer().teleport(loc);
            attacker.getPlayer().getInventory().clear();
            Language lang = attacker.getLanguage();
            attacker.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.BARRIER).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_CANCEL)).craft());
            attacker.getPlayer().getInventory().setItem(0, new ItemBuilder(UsefulItems.UP_ARROW).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_ACCEPT)).craft());
            attacker.getPlayer().getInventory().setItem(1, new ItemBuilder(UsefulItems.RIGHT_ARROW).setName(lang.getTranslation(TranslationKeys.COC_ATTACK_ITEM_NEXT)).craft());
        });
    }*/

    public UUID getCurrentWatching() {
        return currentWatching;
    }
}
