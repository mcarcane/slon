package feyli.slon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

public class Snapshot {
    public static Runnable savePlayers(Slon plugin) {
        return () -> {
            String baseStatement = "INSERT INTO player_snapshots (player_uuid, player_name, timestamp, ping, dead, world, coordinates, health, max_health, food_level, player_inventory, main_hand_item, off_hand_item, armor, ender_inventory, gamemode, exp_level, view_distance, vehicle, in_water, block_at_head_level, block_under_feet, discovered_recipes_length, on_ground) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 7200000);

            try {
                if (!plugin.connection.isValid(5)) {
                    try {
                        DatabaseManagement.connectToDatabase(plugin);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {

                    Connection connection = plugin.connection;
                    try {
                        connection.prepareStatement("SELECT 1").executeQuery();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    PreparedStatement preparedStatement = connection.prepareStatement(baseStatement);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Location location = player.getLocation();
                        String blockAtHeadLevel = Objects.requireNonNull(location.getWorld()).getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).getType().toString();
                        String blockUnderFeet = Objects.requireNonNull(location.getWorld()).getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).getType().toString();
                        PlayerInventory inventory = player.getInventory();
                        preparedStatement.setString(1, player.getUniqueId().toString());
                        preparedStatement.setString(2, player.getName());
                        preparedStatement.setTimestamp(3, timestamp);
                        preparedStatement.setInt(4, player.getPing());
                        preparedStatement.setBoolean(5, player.isDead());
                        if (location.getWorld() != null) preparedStatement.setString(6, location.getWorld().getName());
                        else preparedStatement.setString(6, null);
                        preparedStatement.setString(7, "{\"x\": " + location.getX() + ", \"y\": " + location.getY() + ", \"z\": " + location.getZ() + "}");
                        preparedStatement.setInt(8, (int) player.getHealth());
                        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) preparedStatement.setInt(9, (int) Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                        else preparedStatement.setNull(9, java.sql.Types.SMALLINT);
                        preparedStatement.setInt(10, player.getFoodLevel());
                        if (inventory.isEmpty()) {
                            preparedStatement.setString(11, null);
                            preparedStatement.setString(12, null);
                        } else {
                            preparedStatement.setString(11, Arrays.toString(inventory.getContents()));
                        }
                        preparedStatement.setString(12, inventory.getItemInMainHand().toString());
                        preparedStatement.setString(13, inventory.getItemInOffHand().toString());
                        preparedStatement.setString(13, inventory.getItemInOffHand().toString());
                        preparedStatement.setString(14, Arrays.toString(inventory.getArmorContents()));
                        if (player.getEnderChest().isEmpty()) {
                            preparedStatement.setString(15, null);
                        } else {
                            preparedStatement.setString(15, Arrays.toString(player.getEnderChest().getContents()));
                        }
                        preparedStatement.setString(16, player.getGameMode().toString());
                        preparedStatement.setInt(17, player.getLevel());
                        preparedStatement.setInt(18, player.getClientViewDistance());
                        if (player.getVehicle() != null) preparedStatement.setString(19, Objects.requireNonNull(player.getVehicle()).toString());
                        else preparedStatement.setString(19, null);
                        preparedStatement.setBoolean(20, player.isInWater());
                        preparedStatement.setString(21, blockAtHeadLevel);
                        preparedStatement.setString(22, blockUnderFeet);
                        preparedStatement.setInt(23, player.getDiscoveredRecipes().size());
                        //noinspection deprecation
                        preparedStatement.setBoolean(24, player.isOnGround());
                        preparedStatement.addBatch();
                    }

                    preparedStatement.executeBatch();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
