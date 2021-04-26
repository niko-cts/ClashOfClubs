package net.fununity.clashofclans;

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
import net.fununity.clashofclans.tickhandler.ResourceTickHandler;
import net.fununity.clashofclans.tickhandler.TroopsTickHandler;
import net.fununity.main.api.util.RegisterUtil;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ClashOfClans extends JavaPlugin {

    private static ClashOfClans instance;
    private static final String TITLE = "§6Clash§0of§eClubs";
    private static final int BASE_Y_COORDINATE = 50;
    private static final int BASE_SIZE = 100;
    private static final int BASE_BACKGROUND = 10;
    private World playWorld;

    @Override
    public void onEnable() {
        instance = this;
        this.playWorld = getServer().getWorld("world");

        new EnglishMessages();
        new GermanMessages();

        RegisterUtil registerUtil = new RegisterUtil(this);
        registerUtil.addListeners(new JoinListener(), new QuitListener(), new PlayerInteractListener(), new PlayerMoveListener());
        registerUtil.addCommands(new CoCCommand(), new HomeCommand(), new VisitCommand(), new ResetCommand());
        registerUtil.register();

        ResourceTickHandler.startTimer();
        TroopsTickHandler.startTimer();
        BuildingTickHandler.startTimer();
    }

    @Override
    public void onDisable() {
        ResourceTickHandler.syncResources();
    }

    public static ClashOfClans getInstance() {
        return instance;
    }

    public World getPlayWorld() {
        return playWorld;
    }

    public static int getBaseBackground() {
        return BASE_BACKGROUND;
    }

    public static int getBaseYCoordinate() {
        return BASE_Y_COORDINATE;
    }

    public static int getBaseSize() {
        return BASE_SIZE;
    }

    public String getColoredName() {
        return TITLE;
    }
}
