package net.fununity.clashofclans.attacking.cloud;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.AttackingHandler;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.cloud.common.events.cloud.CloudEvent;
import net.fununity.cloud.common.events.cloud.CloudEventListener;
import net.fununity.main.api.FunUnityAPI;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Listener class for cloud events.
 * @author Niko
 * @since 0.0.1
 */
public class CloudAttackingListener implements CloudEventListener {

    /**
     * Will be called, when a cloud event was send.
     * @param cloudEvent CloudEvent - the event.
     * @since 0.0.1
     */
    @Override
    public void newCloudEvent(CloudEvent cloudEvent) {
        if(cloudEvent.getId() == CloudEvent.COC_ATTACK_REQUEST) {
            UUID attacker = (UUID) cloudEvent.getData().get(0);
            UUID defender = (UUID) cloudEvent.getData().get(1);
            String serverId = cloudEvent.getData().get(2).toString();
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), ()-> {
                AttackingHandler.createAttackingInstance(attacker, defender);
                FunUnityAPI.getInstance().getCloudClient().forwardToServer(serverId, new CloudEvent(CloudEvent.COC_ATTACK_REQUEST_FINISHED).addData(attacker).addData(defender));
            });
        } else if (cloudEvent.getId() == CloudEvent.COC_ATTACK_DELETE) {
            UUID attacker = (UUID) cloudEvent.getData().get(0);
            AttackingHandler.getAttackingManager(attacker).finishAttack();
        }
    }



}
