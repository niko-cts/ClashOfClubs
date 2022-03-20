package net.fununity.clashofclans;

import net.fununity.clashofclans.attacking.cloud.CloudAttackingListener;
import net.fununity.clashofclans.attacking.cloud.CloudNormalListener;
import net.fununity.clashofclans.attacking.listener.AttackingJoinListener;
import net.fununity.clashofclans.attacking.listener.AttackingPlayerInteractListener;
import net.fununity.clashofclans.attacking.listener.AttackingQuitListener;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.commands.HomeCommand;
import net.fununity.clashofclans.commands.ResetCommand;
import net.fununity.clashofclans.commands.VisitCommand;
import net.fununity.clashofclans.language.EnglishMessages;
import net.fununity.clashofclans.language.GermanMessages;
import net.fununity.clashofclans.listener.JoinListener;
import net.fununity.clashofclans.listener.PlayerInteractListener;
import net.fununity.clashofclans.listener.PlayerMoveListener;
import net.fununity.clashofclans.listener.QuitListener;
import net.fununity.clashofclans.tickhandler.BuildingTickHandler;
import net.fununity.clashofclans.tickhandler.RealTimerHandler;
import net.fununity.clashofclans.tickhandler.ResourceTickHandler;
import net.fununity.clashofclans.tickhandler.TroopsTickHandler;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.server.ServerSetting;
import net.fununity.main.api.util.RegisterUtil;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main class of the clash of clubs plugin.
 * @since 0.0.1
 */
public class ClashOfClubs extends JavaPlugin {

    private static ClashOfClubs instance;
    private static final String TITLE = "§6§lClash§6of§e§lClubs";
    private static final int BASE_Y_COORDINATE = 50;
    private static final int BASE_SIZE = 200;
    private static final int BASE_BACKGROUND = 13;
    private boolean attackingServer;
    private final List<UUID> loadedBases;
    private World playWorld;

    public ClashOfClubs() {
        this.loadedBases = new ArrayList<>();
    }

    /**
     * Will be called, when the plugin enables.
     * @since 0.0.1
     */
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        FunUnityAPI.getInstance().getServerSettings().disable(ServerSetting.PLAYER_INTERACT_ENTITY);
        FunUnityAPI.getInstance().getActionbarManager().start();
        this.playWorld = getServer().getWorld("world");

        this.attackingServer = getConfig().getBoolean("attacking-server");

        new EnglishMessages();
        new GermanMessages();

        RegisterUtil registerUtil = new RegisterUtil(this);
        if (attackingServer) {
            FunUnityAPI.getInstance().getCloudClient().getCloudEventManager().addCloudListener(new CloudAttackingListener());
            registerUtil.addListeners(new AttackingJoinListener(), new AttackingQuitListener(), new AttackingPlayerInteractListener());
        } else {
            FunUnityAPI.getInstance().getCloudClient().getCloudEventManager().addCloudListener(new CloudNormalListener());
            registerUtil.addListeners(new JoinListener(), new QuitListener(), new PlayerInteractListener(), new PlayerMoveListener());
            registerUtil.addCommands(new CoCCommand(), new HomeCommand(), new VisitCommand(), new ResetCommand());

            ResourceTickHandler.startTimer();
            TroopsTickHandler.startTimer();
            BuildingTickHandler.startTimer();
        }
        registerUtil.register();
        RealTimerHandler.startTimer();

        Schematics.cacheAllSchematics();
    }

    /**
     * Will be called, when the plugin disables.
     * @since 0.0.1
     */
    @Override
    public void onDisable() {
        if (!attackingServer)
            ResourceTickHandler.syncResources();
    }

    /**
     * Get the instance of this class.
     * @return ClashOfClubs - Plugin instance class.
     * @since 0.0.1
     */
    public static ClashOfClubs getInstance() {
        return instance;
    }

    public List<UUID> getLoadedBases() {
        return loadedBases;
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
