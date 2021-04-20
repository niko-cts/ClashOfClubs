package net.fununity.clashofclans;

import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.language.EnglishMessages;
import net.fununity.clashofclans.language.GermanMessages;
import net.fununity.clashofclans.listener.JoinListener;
import net.fununity.clashofclans.listener.PlayerInteractListener;
import net.fununity.clashofclans.listener.PlayerMoveListener;
import net.fununity.clashofclans.listener.QuitListener;
import net.fununity.main.api.util.RegisterUtil;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ClashOfClans extends JavaPlugin {

    private static ClashOfClans instance;
    private World playWorld;

    @Override
    public void onEnable() {
        instance = this;
        this.playWorld = getServer().getWorld("world");

        new EnglishMessages();
        new GermanMessages();

        RegisterUtil registerUtil = new RegisterUtil(this);
        registerUtil.addListeners(new JoinListener(), new QuitListener(), new PlayerInteractListener(), new PlayerMoveListener());
        registerUtil.addCommands(new CoCCommand());
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

    public String getColoredName() {
        return "§6Clash§0of§eIndividual";
    }
}
