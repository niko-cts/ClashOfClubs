package net.fununity.clashofclans;

import net.fununity.clashofclans.attacking.cloud.CloudNormalListener;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.commands.HomeCommand;
import net.fununity.clashofclans.commands.ResetCommand;
import net.fununity.clashofclans.commands.VisitCommand;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.EnglishMessages;
import net.fununity.clashofclans.language.GermanMessages;
import net.fununity.clashofclans.listener.*;
import net.fununity.clashofclans.listener.interact.PlayerInteractListener;
import net.fununity.clashofclans.listener.interact.PlayerSelectHotbarListener;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.server.ServerSetting;
import net.fununity.main.api.util.RegisterUtil;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Main class of the clash of clubs plugin.
 * @since 0.0.1
 */
public class ClashOfClubs extends JavaPlugin {

    private static ClashOfClubs instance;
    private static final String TITLE = ChatColor.GOLD + "Clash" + ChatColor.GRAY + "Of" + ChatColor.AQUA + "Clubs";
    private static final int BASE_Y_COORDINATE = 50;
    private static final int BASE_SIZE = 200;
    private static final int BASE_BACKGROUND = 13;
    private boolean attackingServer;
    private World playWorld;

    private PlayerManager playerManager;


    /**
     * Will be called, when the plugin enables.
     * @since 0.0.1
     */
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.attackingServer = getConfig().getBoolean("attacking-server");

        FunUnityAPI.getInstance().getActionbarManager().start();
        FunUnityAPI.getInstance().getServerSettings().disable(ServerSetting.PLAYER_INTERACT_ENTITY);

        this.playWorld = getServer().getWorld("world");
        this.playWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.playWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        WorldServer ch = ((CraftWorld) playWorld).getHandle();
        ch.spigotConfig.itemDespawnRate = 5;

        new EnglishMessages();
        new GermanMessages();

        this.playerManager = new PlayerManager();

        if (!attackingServer) {
            FunUnityAPI.getInstance().getCloudClient().getCloudEventManager().addCloudListener(new CloudNormalListener());

            RegisterUtil registerUtil = new RegisterUtil(this);
            registerUtil.addListeners(new JoinListener(), new QuitListener(), new PlayerInteractListener(), new PlayerSelectHotbarListener(), new PlayerMoveListener(), new InventoryClickListener(), new ServerShutdownListener());
            registerUtil.addCommands(new CoCCommand(), new HomeCommand(), new VisitCommand(), new ResetCommand());

            new TickTimerManager();

            registerUtil.register();
        }

        getServer().getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), Schematics::cacheAllSchematics);
        getServer().getScheduler().runTaskTimer(this, () -> {
            // Ticks = (Hours * 1000) - 6000
            Calendar cal = Calendar.getInstance();
            this.playWorld.setTime(1000 * cal.get(Calendar.HOUR_OF_DAY) + 16 * cal.get(Calendar.MINUTE) - 6000);
        }, 20L, 20L * 30);
    }

    /**
     * Will be called, when the plugin disables.
     * @since 0.0.1
     */
    @Override
    public void onDisable() {
        if (attackingServer) return;
        Collection<CoCPlayer> players = getPlayerManager().getPlayers().values();
        if (!players.isEmpty()) {
            getLogger().log(Level.INFO, "Saving data of {0} players", players.size());
            DatabasePlayer.getInstance().updateCompletePlayerData(players);
        }
    }

    /**
     * Get the instance of this class.
     * @return ClashOfClubs - Plugin instance class.
     * @since 0.0.1
     */
    public static ClashOfClubs getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Get the play world.
     * @return World - the play world.
     * @since 0.0.1
     */
    public World getWorld() {
        return playWorld;
    }

    /**
     * Get the size of the background base.
     * @return int - the base background size.
     * @since 0.0.1
     */
    public static int getBaseBackground() {
        return BASE_BACKGROUND;
    }

    /**
     * Get the standard y coordinate.
     * @return int - y coordinate.
     * @since 0.0.1
     */
    public static int getBaseYCoordinate() {
        return BASE_Y_COORDINATE;
    }

    /**
     * Get the base size.
     * @return int - base size x, z
     * @since 0.0.1
     */
    public static int getBaseSize() {
        return BASE_SIZE;
    }

    /**
     * Get the colored name.
     * @return String - colored name.
     * @since 0.0.1
     */
    public static String getColoredName() {
        return TITLE;
    }

    public boolean isAttackingServer() {
        return attackingServer;
    }
}
