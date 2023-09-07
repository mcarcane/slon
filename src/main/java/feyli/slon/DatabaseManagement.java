package feyli.slon;

import java.sql.*;

import org.bukkit.Bukkit;

public class DatabaseManagement {

    public static void connectToDatabase(Slon plugin) throws ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        String host = "185.142.53.15";
        int port = 3306;
        String database = "arcane_blades";
        String username = "arcane";
        String password = "1/Q*IxiuC._*/X!@";

        Bukkit.getLogger().info("Connecting to the MariaDB database...");

        try {
            plugin.connection = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password);
            Bukkit.getLogger().info("Connected to the MariaDB database.");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to connect to the MariaDB database: " + e.getMessage());
        }
    }
}
