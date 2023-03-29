package net.fununity.clashofclans.attacking.cloud;

import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.cloud.common.events.cloud.CloudEvent;
import net.fununity.cloud.common.events.cloud.CloudEventListener;

import java.util.UUID;

/**
 * Listener class for cloud events.
 * @author Niko
 * @since 0.0.1
 */
public class CloudNormalListener implements CloudEventListener {
    @Override
    public void newCloudEvent(CloudEvent cloudEvent) {
        switch (cloudEvent.getId()) {
            case CloudEvent.COC_RESPONSE_SPACE_AVAILABLE -> {
                UUID attacker = (UUID) cloudEvent.getData().get(0);
                MatchmakingSystem.getInstance().serverFound(attacker, (String) cloudEvent.getData().get(1));
            }
            case CloudEvent.COC_RESPONSE_NO_SPACE -> {
                UUID attacker = (UUID) cloudEvent.getData().get(0);
                MatchmakingSystem.getInstance().serverDeny(attacker);
            }
            case CloudEvent.COC_RESPONSE_ATTACK_SERVER_STARTED -> {
                String serverId = (String) cloudEvent.getData().get(0);
                MatchmakingSystem.getInstance().serverStarted(serverId);
            }
            case CloudEvent.COC_RESPONSE_ATTACK_SERVER_AMOUNT -> {
                int amount = (int) cloudEvent.getData().get(0);
                MatchmakingSystem.getInstance().serverAmount(amount);
            }
        }
    }
}
