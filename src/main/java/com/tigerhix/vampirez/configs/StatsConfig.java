package com.tigerhix.vampirez.configs;

import com.tigerhix.vampirez.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.naming.CommunicationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class StatsConfig {

    public FileConfiguration statsconf;
    public File statsfile;

    private Main plugin;
    private String ip;
    private String port;
    private String login;
    private String password;
    private String dbname;


    public boolean mysqlEnabled = false;
    private int vampireKills;
    private int survivorKills;
    private int vampireWins;
    private int coins;
    private int survivorWins;
    private Connection connection;

    public void openConnectionDatabase() {

//        DriverManager.setLoginTimeout(5);
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.ip+":"+this.port+"/"+this.dbname+"?user="+this.login+"&password="+this.password);
            this.connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void setDatabaseSettings(Main plugin) {
        this.ip = plugin.getConfig().getString("mysql.ip", "127.0.0.1");
        this.port = plugin.getConfig().getString("mysql.port", "3306");
        this.login = plugin.getConfig().getString("mysql.login", "login");
        this.password = plugin.getConfig().getString("mysql.password", "password");
        this.dbname = plugin.getConfig().getString("mysql.database", "db_name");
    }
    private void addMessages(FileConfiguration statsconf) {
        FileConfigurationOptions options = statsconf.options();
        options.header("Stats file");
        options.copyHeader(true);
        save();
    }
    public void setup(final Main plugin) {

        this.plugin = plugin;
        this.mysqlEnabled = true;
        statsfile = new File(plugin.getDataFolder(),"stats.yml");
        statsconf = YamlConfiguration.loadConfiguration(statsfile);
        this.addMessages(statsconf);

        if (this.plugin.getConfig().getBoolean("mysql.enabled")) {
            setDatabaseSettings(plugin);
        }

        try {
            openConnectionDatabase();

            DatabaseMetaData metadata = this.connection.getMetaData();

            // Check if the table exists
            String tableName = "vampires_stats"; // Table name
            ResultSet resultSet = metadata.getTables(null, null, tableName, null);

            if (!resultSet.next()) {
                Bukkit.getConsoleSender().sendMessage("Creating table '" + tableName + "'...");
                Statement statement = connection.createStatement();
                String insertQuery = "CREATE TABLE `"+this.dbname+"`.`vampires_stats` ( `player-name` TEXT NOT NULL DEFAULT 'null' , `vampire-kills` INT(255) NOT NULL DEFAULT '0' , `survivor-kills` INT(255) NOT NULL DEFAULT '0' , `vampire-wins` INT(255) NOT NULL DEFAULT '0' , `survivor-wins` INT(255) NOT NULL DEFAULT '0', `coins` INT(255) NOT NULL DEFAULT '0' ,  `id` INT(255) NOT NULL AUTO_INCREMENT , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
                statement.execute(insertQuery);
            }
            resultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c"+e);
            Bukkit.getConsoleSender().sendMessage("\n§cUsing local file for player stats...");
//            throw new RuntimeException(e);
            setup(this.plugin, false);
        }

    }
    public void setup(final Main plugin, boolean mysql) {

        this.plugin = plugin;
        this.mysqlEnabled = mysql;
        statsfile = new File(plugin.getDataFolder(),"stats.yml");
        statsconf = YamlConfiguration.loadConfiguration(statsfile);
        this.addMessages(statsconf);

        if (!mysql) {
            setDatabaseSettings(plugin);
        }

    }
    public FileConfiguration get() {
        return statsconf;
    }

    public void save() {
        if (!this.plugin.getConfig().getBoolean("mysql.enabled") || !this.mysqlEnabled) {
            try {
                statsconf.save(statsfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void set(String input, Object value) {
//        String input = "players.pixkk.vampire-wins";
        String[] parts = input.split("\\.");
//        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + Arrays.toString(parts));
        String playerName = parts[1];
        String param = parts[2];
        if (this.plugin.getConfig().getBoolean("mysql.enabled") && this.mysqlEnabled) {
            try {
                saveStatsToDatabase(value, playerName, param);
            } catch (SQLException e) {

                openConnectionDatabase();
                try {
                    saveStatsToDatabase(value, playerName, param);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
//                throw new RuntimeException(e);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + e.toString());
            }
        }
        else {
            FileConfiguration fileConfiguration = this.get();
            fileConfiguration.set(input, value);
            save();
        }
    }

    private void saveStatsToDatabase(Object value, String playerName, String param) throws SQLException {
        Statement statement;
        ResultSet resultSet;
        statement = connection.createStatement();

        String query = "SELECT * FROM `vampires_stats` WHERE `player-name` = '" + playerName+"' LIMIT 1";

        resultSet = statement.executeQuery(query);
        if (resultSet.next() == false) {
            query = "INSERT INTO `vampires_stats`(`player-name`, `vampire-kills`, `survivor-kills`, `vampire-wins`, `survivor-wins`, `coins`) VALUES ('" + playerName+"',0,0,0,0,0)";
            statement.execute(query);
        }
        query = "UPDATE `vampires_stats` SET `"+param+"` = "+value+" WHERE `player-name` = '" + playerName+"'";
//        Bukkit.getConsoleSender().sendMessage("query" + query);
        statement.executeUpdate(query);
    }

    public void reloadconf() {
        if(this.plugin.getConfig().getBoolean("mysql.enabled")) {
            setup(this.plugin);
        }
        else {
            statsconf = YamlConfiguration.loadConfiguration(statsfile);
            this.addMessages(statsconf);
        }
    }

    public int getInt(String input) {
//        String input = "players.pixkk.wins";
        String[] parts = input.split("\\.");
//        Bukkit.getConsoleSender().sendMessage(Arrays.toString(parts));
        String playerName = parts[1];
        String param = parts[2];
        if (this.plugin.getConfig().getBoolean("mysql.enabled") && this.mysqlEnabled) {
            Statement statement;
            try {
                statement = connection.createStatement();
                // Execute a query to retrieve data
                String query = "SELECT `"+param+"` FROM `vampires_stats` WHERE `player-name` = '" + playerName + "' LIMIT 1";
//                Bukkit.getConsoleSender().sendMessage(query);
                ResultSet resultSet = statement.executeQuery(query);
//                Bukkit.getConsoleSender().sendMessage(resultSet.toString());

                while (resultSet.next()) {
//                    String playerName = resultSet.getString("player-name");
//                    this.coins = resultSet.getInt("coins");
//                    this.vampireKills = resultSet.getInt("vampire-kills");
//                    this.survivorKills = resultSet.getInt("survivor-kills");
//                    this.vampireWins = resultSet.getInt("vampire-wins");
//                    this.survivorWins = resultSet.getInt("survivor-wins");
                    return resultSet.getInt(param);
                }
                return 0;
            } catch (SQLException e) {
                openConnectionDatabase();
                try {
                    statement = connection.createStatement();
                    // Execute a query to retrieve data
                    String query = "SELECT `"+param+"` FROM `vampires_stats` WHERE `player-name` = '" + playerName + "' LIMIT 1";
//                Bukkit.getConsoleSender().sendMessage(query);
                    ResultSet resultSet = statement.executeQuery(query);
//                Bukkit.getConsoleSender().sendMessage(resultSet.toString());

                    while (resultSet.next()) {
//                    String playerName = resultSet.getString("player-name");
//                    this.coins = resultSet.getInt("coins");
//                    this.vampireKills = resultSet.getInt("vampire-kills");
//                    this.survivorKills = resultSet.getInt("survivor-kills");
//                    this.vampireWins = resultSet.getInt("vampire-wins");
//                    this.survivorWins = resultSet.getInt("survivor-wins");
                        return resultSet.getInt(param);
                    }
                    return 0;
                } catch (SQLException ee) {

                    throw new RuntimeException(ee);
                }
            }
        }
        else {
            return statsconf.getInt(input, 0);
        }
    }
}
