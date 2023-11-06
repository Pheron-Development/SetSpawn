package xyz.kiradev.setspawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SetSpawn extends JavaPlugin implements CommandExecutor, Listener {

    private File spawnFile;
    private FileConfiguration spawnConfig;

    @Override
    public void onEnable() {
        spawnFile = new File(getDataFolder(), "spawn.yml");
        if (!spawnFile.exists()) {
            spawnFile.getParentFile().mkdirs();
            saveResource("spawn.yml", false);
        }
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);

        this.getCommand("setspawn").setExecutor(this);
        this.getCommand("spawn").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (spawnConfig.contains("spawn.world")) {
            String worldName = spawnConfig.getString("spawn.world");
            double x = spawnConfig.getDouble("spawn.x");
            double y = spawnConfig.getDouble("spawn.y");
            double z = spawnConfig.getDouble("spawn.z");

            Location spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
            player.teleport(spawnLocation);
        } else {

            Random random = new Random();
            int x = random.nextInt(1000);
            int z = random.nextInt(1000);
            player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
            player.sendMessage("You've been teleported to a random location.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp()) {
                    Location playerLocation = player.getLocation();
                    spawnConfig.set("spawn.world", playerLocation.getWorld().getName());
                    spawnConfig.set("spawn.x", playerLocation.getX());
                    spawnConfig.set("spawn.y", playerLocation.getY());
                    spawnConfig.set("spawn.z", playerLocation.getZ());
                    saveSpawnConfig();
                    player.sendMessage("Spawn point set!");
                } else {
                    player.sendMessage("You need to be OP to set the spawn point.");
                }
            } else {
                sender.sendMessage("Only players can set the spawn point.");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (spawnConfig.contains("spawn.world")) {
                    String worldName = spawnConfig.getString("spawn.world");
                    double x = spawnConfig.getDouble("spawn.x");
                    double y = spawnConfig.getDouble("spawn.y");
                    double z = spawnConfig.getDouble("spawn.z");

                    Location spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
                    player.teleport(spawnLocation);
                    player.sendMessage("Teleported to spawn!");
                } else {
                    player.sendMessage("Spawn point is not set. Please ask an admin to set it.");
                }
            } else {
                sender.sendMessage("Only players can teleport to the spawn point.");
            }
            return true;
        }

        return false;
    }

    private void saveSpawnConfig() {
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            getLogger().severe("Could not save spawn.yml!");
        }
    }
}