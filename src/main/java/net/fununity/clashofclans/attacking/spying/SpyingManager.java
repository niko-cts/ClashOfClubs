package net.fununity.clashofclans.attacking.spying;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.player.APIPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpyingManager {

    private static SpyingManager instance;

    public static SpyingManager getInstance() {
        if (instance == null)
            instance = new SpyingManager();
        return instance;
    }

    private final Map<UUID, SpyingPlayer> attackerMap;

    private SpyingManager() {
        this.attackerMap = new HashMap<>();
    }


    public void cancelWatching(APIPlayer player) {
        this.attackerMap.remove(player.getUniqueId());
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        coCPlayer.visit(player, true);
        ClashOfClubs.getInstance().getPlayerManager().giveDefaultItems(coCPlayer);
    }

    public boolean isSpying(UUID uuid) {
        return this.attackerMap.containsKey(uuid);
    }

    public void nextSpy(UUID uniqueId) {

    }

    public void startAttack(APIPlayer player) {
        UUID uuid = player.getUniqueId();
        MatchmakingSystem.getInstance().startRequest(player, attackerMap.get(uuid).getCurrentWatching());
        this.attackerMap.remove(uuid);
    }
}
