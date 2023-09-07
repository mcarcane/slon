package feyli.slon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class Slon extends JavaPlugin {
    Connection connection;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                DatabaseManagement.connectToDatabase(this);
                Bukkit.getPluginManager().registerEvents(new PlayerChatListener(this), this);
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, Snapshot.savePlayers(this), 0, 40L);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
