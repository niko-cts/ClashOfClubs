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
        if (cloudEvent.getId() == CloudEvent.COC_ATTACK_REQUEST_FINISHED) {
            UUID attacker = (UUID) cloudEvent.getData().get(0);
            UUID defender = (UUID) cloudEvent.getData().get(1);
            MatchmakingSystem.getInstance().requestFinished(attacker, defender);
        } else if(cloudEvent.getId() == CloudEvent.COC_ATTACK_FINISHED) {
            UUID defender = (UUID) cloudEvent.getData().get(0);
            MatchmakingSystem.getInstance().attackFinished(defender);
        }
    }
}
