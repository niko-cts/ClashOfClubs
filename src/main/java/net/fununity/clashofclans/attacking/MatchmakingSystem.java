package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.database.DatabaseAttackBots;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.cloud.client.CloudClient;
import net.fununity.cloud.common.events.cloud.CloudEvent;
import net.fununity.cloud.common.server.ServerType;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.cloud.CloudManager;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    public static final float RESOURCE_MULTIPLER = 0.4f; // the multiplier a building amount contains when attacking
    private Set<DatabaseAttackBots.BotData> defenderBots;

    private final Map<UUID, Integer> waitingForAttackServer;
    private final Map<UUID, UUID> attackerDefender;
    private final Map<UUID, String> defendingServer; // defenderUUID, Serverid

    private int attackingServer;
    private boolean newServerStarting;

    /**
     * Instantiates this class.
     * @since 1.0.1
     */
    private MatchmakingSystem() {
        this.attackerDefender = new HashMap<>();
        this.waitingForAttackServer = new HashMap<>();
        this.defendingServer = new HashMap<>();
        this.attackingServer = 0;
        this.newServerStarting = false;
    }

    /**
     * Starts to request an attack server if space is available.
     * @param player APIPlayer - the attacker player.
     * @param defender UUID - the defender uuid.
     * @since 1.0.1
     */
    public void startRequest(APIPlayer player, UUID defender) {
        UUID attacker = player.getUniqueId();
        this.waitingForAttackServer.put(attacker, 0);
        this.attackerDefender.put(attacker, defender);

        player.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_ATTACK_START_REQUEST);

        if (attackingServer == 0) {
            startNewAttackServer();
            player.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_ATTACK_START_SERVER_CREATE);
        } else
            CloudClient.getInstance().forwardToServerType(ServerType.COCATTACK, new CloudEvent(CloudEvent.COC_REQUEST_SPACE).addData(attacker).addData(CloudClient.getInstance().getClientId()));
    }


    /**
     * Attack server send COC_RESPONSE_SPACE_AVAILABLE
     * @param attacker UUID - the attacker uuid.
     * @param serverId String - the server id that has space available.
     * @since 1.0.1
     */
    public void serverFound(UUID attacker, String serverId) {
        this.waitingForAttackServer.remove(attacker);

        APIPlayer player = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(attacker);
        if (player == null || !this.attackerDefender.containsKey(attacker)) {
            cancelAttackRequest(attacker, serverId);
            return;
        }

        player.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_ATTACK_START_FOUND);

        CloudClient.getInstance().forwardToServer(serverId, new CloudEvent(CloudEvent.COC_REQUEST_LOAD).addData(attacker).addData(attackerDefender.get(attacker)).addData(CloudClient.getInstance().getClientId()),
                answer -> serverLoadedBase(attacker, serverId));
    }

    /**
     * The attack server loaded the base and the player can transfer.
     * @param attacker UUID - the attacker uuid.
     * @param serverId String - the server id of the attack server.
     * @since 1.0.1
     */
    private void serverLoadedBase(UUID attacker, String serverId) {
        APIPlayer player = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(attacker);
        if (player == null || !this.attackerDefender.containsKey(attacker)) {
            cancelAttackRequest(attacker, serverId);
            return;
        }

        defendingServer.put(attackerDefender.get(attacker), serverId);
        CloudManager.getInstance().sendPlayerToServer(serverId, attacker);
    }

    /**
     * An attack server sent a COC_RESPONSE_NO_SPACE event.
     * @param uuid UUID - the attacker uuid.
     * @since 1.0.1
     */
    public void serverDeny(UUID uuid) {
        if (!waitingForAttackServer.containsKey(uuid)) return;

        APIPlayer player = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid);
        if (player == null) {
            removePlayer(uuid);
            return;
        }

        int denies = waitingForAttackServer.get(uuid) + 1;
        this.waitingForAttackServer.put(uuid, denies);
        if (denies >= attackingServer) {
            startNewAttackServer();
            player.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_ATTACK_START_SERVER_CREATE);
        }
    }

    /**
     * Will create a new attack server.
     * @since 1.0.1
     */
    private void startNewAttackServer() {
        if (newServerStarting) return;
        this.newServerStarting = true;
        CloudClient.getInstance().sendEvent(new CloudEvent(CloudEvent.SERVER_CREATE_BY_TYPE).addData(ServerType.COCATTACK));
    }

    /**
     * Attack server notified its activation.
     * @param serverId String - the id of the server.
     * @since 1.0.1
     */
    public void serverStarted(String serverId) {
        this.newServerStarting = false;

        this.waitingForAttackServer.keySet().forEach(u ->
                CloudClient.getInstance().forwardToServer(serverId,
                        new CloudEvent(CloudEvent.COC_REQUEST_SPACE).addData(u).addData(CloudClient.getInstance().getClientId())));
    }

    /**
     * Sends the attack server an CANCELATTACK event to make space again for the player.
     * @param uuid UUID - the uuid of the player.
     * @param serverId String - the server id to send the event.
     * @since 1.0.1
     */
    private void cancelAttackRequest(UUID uuid, String serverId) {
        CloudClient.getInstance().forwardToServer(serverId, new CloudEvent(CloudEvent.COC_RESPONSE_CANCELATTACK).addData(uuid));
    }

    /**
     * Removes the player from the lists.
     * @param uuid UUID - the uuid of the player.
     * @since 1.0.1
     */
    public void removePlayer(UUID uuid) {
        if (this.attackerDefender.containsKey(uuid)) {
            this.defendingServer.remove(this.attackerDefender.get(uuid));
            this.attackerDefender.remove(uuid);
        }
        this.waitingForAttackServer.remove(uuid);
    }

    /**
     * An attack was finished.
     * @param attacker UUID - the uuid of the attacker.
     * @since 1.0.1
     */
    public void attackFinished(UUID attacker) {
        if (this.attackerDefender.containsKey(attacker)) {
            this.defendingServer.remove(this.attackerDefender.get(attacker));
            this.attackerDefender.remove(attacker);
        }
        this.waitingForAttackServer.remove(attacker);
    }

    /**
     * Check if the player defends currently its base.
     * @param uuid UUID - the uuid of the player.
     * @return boolean - player is defending its base.
     * @since 0.0.1
     */
    public String defendingServer(UUID uuid) {
        return this.defendingServer.getOrDefault(uuid, null);
    }

    public Set<DatabaseAttackBots.BotData> getDefenderBots() {
        if (defenderBots == null) {
            defenderBots = DatabaseAttackBots.getInstance().getNormalBotData();
        }
        return defenderBots;
    }

    public void serverAmount(int amount) {
        this.attackingServer = amount;
    }
}
