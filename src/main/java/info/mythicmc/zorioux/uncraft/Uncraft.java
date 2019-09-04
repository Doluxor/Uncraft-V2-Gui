package info.mythicmc.zorioux.uncraft;

import info.mythicmc.zorioux.uncraft.Events.MainEvents;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public final class Uncraft extends JavaPlugin {
    public static File file;
    @Override
    public void onEnable() {

        getCommand("uncraft").setExecutor(new UncraftCommand());
        getServer().getPluginManager().registerEvents(new MainEvents(),this);
        file = new File(getDataFolder(), "config.yml");

        directory();
        config();
        fileCreator();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "uncrafter launched successfully !");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    // method to create files
    private void fileCreator(){

        final String SEPARATOR = System.getProperty("file.separator");

        File file = new File("plugins" + SEPARATOR + "Uncraft" + SEPARATOR + "Logs.log");

        try
        {
            if (file.createNewFile()) getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Uncrafter output: Logs.log File has been successfully created");
            else getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Uncrafter output: Could not create Logs.log File \"file already exist\"");

        }catch (IOException e){
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Uncrafter output: Error occurred while creating Logs.log");
        }
    }
    //creates config
    //using this method because of bukkit's problems with saving config/updating it
    private void config(){
        final String SEPARATOR = System.getProperty("file.separator");
        File file = new File("plugins" + SEPARATOR + "Uncraft" + SEPARATOR + "config.yml");

        try
        {
            if (file.createNewFile()) getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Uncrafter output: config.yml File has been successfully created");
            else getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Uncrafter output: Could not create config.yml File \"file already exist\"");

        }catch (IOException e){
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Uncrafter output: Error occurred while creating config.yml");
        }

    }

    private void directory() {
        final String SEPARATOR = System.getProperty("file.separator");
        File file = new File("plugins" + SEPARATOR + "Uncraft");
        if (!file.exists()){
            if (file.mkdirs()) return;
            else getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Uncrafter output: Error occurred while creating config.yml");
        }
    }
}
