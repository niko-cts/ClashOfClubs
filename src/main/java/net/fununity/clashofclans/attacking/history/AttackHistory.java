package net.fununity.clashofclans.attacking.history;

import net.fununity.clashofclans.ResourceTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class AttackHistory {

    private final UUID attacker;
    private final UUID defender;
    private final OffsetDateTime date;
    private final int stars;
    private final int elo;
    private final Map<ResourceTypes, Double> resourcesGathered;
    private final boolean seen;

    public AttackHistory(UUID attacker, UUID defender, OffsetDateTime date, int stars, int elo, Map<ResourceTypes, Double> resourcesGathered, boolean seen) {
        this.attacker = attacker;
        this.defender = defender;
        this.date = date;
        this.stars = stars;
        this.elo = elo;
        this.resourcesGathered = resourcesGathered;
        this.seen = seen;
    }

    public UUID getAttacker() {
        return attacker;
    }

    public UUID getDefender() {
        return defender;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public int getStars() {
        return stars;
    }

    public int getElo() {
        return elo;
    }

    public int getResourcesGathered(ResourceTypes type) {
        return (int) Math.round(resourcesGathered.getOrDefault(type, 0.0)-0.4);
    }

    public boolean isSeen() {
        return seen;
    }
}
