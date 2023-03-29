package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.values.ICoCValue;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public class AttackHistory {

    private final UUID attacker;
    private final UUID defender;
    private final OffsetDateTime date;
    private final int stars;
    private final Map<ICoCValue, Integer> resourcesGathered;
    private final boolean seen;

    public AttackHistory(UUID attacker, UUID defender, OffsetDateTime date, int stars, Map<ICoCValue, Integer> resourcesGathered, boolean seen) {
        this.attacker = attacker;
        this.defender = defender;
        this.date = date;
        this.stars = stars;
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

    public int getResource(ICoCValue type) {
        return resourcesGathered.get(type);
    }

    public boolean isSeen() {
        return seen;
    }
}
