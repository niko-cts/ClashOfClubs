package net.fununity.clashofclans;

import net.fununity.clashofclans.attacking.cloud.CloudNormalListener;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.commands.HomeCommand;
import net.fununity.clashofclans.commands.ResetCommand;
import net.fununity.clashofclans.commands.VisitCommand;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.EnglishMessages;
import net.fununity.clashofclans.language.GermanMessages;
import net.fununity.clashofclans.listener.JoinListener;
import net.fununity.clashofclans.listener.PlayerInteractListener;
import net.fununity.clashofclans.listener.PlayerMoveListener;
import net.fununity.clashofclans.listener.QuitListener;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.server.ServerSetting;
import net.fununity.main.api.util.RegisterUtil;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the clash of clubs plugin.
 * @since 0.0.1
 */
public class ClashOfClubs extends JavaPlugin {

    private static ClashOfClubs instance;
    private static final String TITLE = "§6§lClash§6of§b§lClubs";
    private static final int BASE_Y_COORDINATE = 50;
    private static final int BASE_SIZE = 200;
    private static final int BASE_BACKGROUND = 13;
    private boolean attackingServer;
    private World playWorld;

    private PlayerManager playerManager;
    private TickTimerManager tickTimerManager;

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
        this.playWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        this.playWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        WorldServer ch = ((CraftWorld) playWorld).getHandle();
        ch.spigotConfig.itemDespawnRate = 5;

        new EnglishMessages();
        new GermanMessages();

        this.playerManager = new PlayerManager();

        if (!attackingServer) {
            FunUnityAPI.getInstance().getCloudClient().getCloudEventManager().addCloudListener(new CloudNormalListener());

            RegisterUtil registerUtil = new RegisterUtil(this);
            registerUtil.addListeners(new JoinListener(), new QuitListener(), new PlayerInteractListener(), new PlayerMoveListener());
            registerUtil.addCommands(new CoCCommand(), new HomeCommand(), new VisitCommand(), new ResetCommand());

            this.tickTimerManager = new TickTimerManager();
            registerUtil.register();
        }

        Schematics.cacheAllSchematics();
    }

    /**
     * Will be called, when the plugin disables.
     * @since 0.0.1
     */
    @Override
    public void onDisable() {
        if (attackingServer) return;

        for (CoCPlayer coCPlayer : getPlayerManager().getPlayers().values()) {
            DatabasePlayer.getInstance().updatePlayer(coCPlayer);
            DatabaseBuildings.getInstance().updateBuildings(coCPlayer);
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

    public TickTimerManager getTickTimerManager() {
        return tickTimerManager;
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
