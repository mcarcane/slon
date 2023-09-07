package feyli.slon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class PlayerChatListener implements Listener {
    private final Slon plugin;

    public PlayerChatListener(Slon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        try {
            if (!plugin.connection.isValid(5)) {
                try {
                    DatabaseManagement.connectToDatabase(plugin);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Player player = event.getPlayer();

        try {
            PreparedStatement preparedStatement = plugin.connection.prepareStatement("INSERT INTO chat_logs (player_uuid, player_name, timestamp, content, world, on_ground, block_at_head_level) VALUES (?, ?, ?, ?, ?, ?, ?)");
            Location location = player.getLocation();
            String blockAtHeadLevel = Objects.requireNonNull(location.getWorld()).getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).getType().toString();
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setNull(3, java.sql.Types.TIMESTAMP);
            preparedStatement.setString(4, event.getMessage());
            preparedStatement.setString(5, location.getWorld().getName());
            //noinspection deprecation
            preparedStatement.setBoolean(6, player.isOnGround());
            preparedStatement.setString(7, blockAtHeadLevel);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        event.setCancelled(false);
    }
}
