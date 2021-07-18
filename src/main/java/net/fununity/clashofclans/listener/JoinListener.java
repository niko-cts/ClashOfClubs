package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.main.api.cloud.CloudManager;
import net.fununity.main.api.event.player.APIPlayerJoinEvent;
import net.fununity.main.api.messages.MessagePrefix;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener class for joining events.
 * @author Niko
 * @since 0.0.1
 */
public class JoinListener implements Listener {

    private final BossBar betaInfo;

    public JoinListener() {
        this.betaInfo = ClashOfClubs.getInstance().getServer().createBossBar("§7This game is in beta stage!", BarColor.WHITE, BarStyle.SOLID);
    }

    /**
     * Will be called, when a player joins.
     * @param event PlayerJoinEvent - the event that was triggered.
     * @since 0.0.1
     */
    @EventHandler
    public void onJoin(APIPlayerJoinEvent event) {
        if (MatchmakingSystem.getInstance().isCurrentlyDefending(event.getAPIPlayer().getUniqueId())) {
            event.getAPIPlayer().sendMessage(MessagePrefix.INFO, TranslationKeys.COC_ATTACK_BASE_UNDERATTACK);
            CloudManager.getInstance().sendPlayerToServer(MatchmakingSystem.ATTACKER_SERVER, event.getAPIPlayer().getUniqueId());
            return;
        }

        this.betaInfo.addPlayer(event.getAPIPlayer().getPlayer());

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            CoCPlayer coCPlayer = PlayerManager.getInstance().playerJoins(event.getAPIPlayer());
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> ScoreboardMenu.show(coCPlayer));
        });
    }

}
