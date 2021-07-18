package net.fununity.clashofclans.tickhandler;

import net.fununity.clashofclans.ClashOfClubs;
import org.bukkit.Bukkit;

import java.time.OffsetDateTime;

public class RealTimerHandler {

    private RealTimerHandler() {
        throw new UnsupportedOperationException("RealTimerHandler is a handler class");
    }

    /**
     * Starts a timer, which displays the real world time (sunrise/set at 6 am/pm)
     * @since 0.0.1
     */
    public static void startTimer() {
        Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), () -> {
            OffsetDateTime now = OffsetDateTime.now();
            long minutes = now.getHour() * 1000L - 6000;

            if (minutes < 0)
                minutes += 24000;

            minutes += now.getMinute() * 100L / 6;

            ClashOfClubs.getInstance().getWorld().setTime(minutes);
        }, 60 * 20L, 60 * 20L);
    }
}
