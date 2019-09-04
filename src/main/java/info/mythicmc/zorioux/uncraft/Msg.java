package info.mythicmc.zorioux.uncraft;

import org.bukkit.ChatColor;
//this being used to make cleaner code with static msgs
public class Msg {
  static final String onlyPlayers = ChatColor.DARK_RED + "Only players may execute this command!";
  static final String noPerm = ChatColor.DARK_RED + "You do not have permission to use this command";
  public static final String noSpace = ChatColor.DARK_RED + "You do not have enough space in your inventory";
  public static final String itemDamaged = ChatColor.BOLD + "This item is damaged, uncrafting canceled";
  public static final String canNotUncraft= ChatColor.RED + "Error, this item can not be uncrafted";
  public static final String noItem = ChatColor.RED + "You don't have any ";
  public static final String noItem2 = ChatColor.RED + " in your Inventory";
  static final String help = ChatColor.AQUA +  "Plugin used to Uncraft items customized staffs \n" +
          ChatColor.WHITE + "use /uncraft or /uc to open menu to what you can uncraft \n" +
          ChatColor.DARK_BLUE + "Uncraft Plugin by " + ChatColor.GOLD + "Zorioux" + ChatColor.DARK_BLUE + " send any bug to me";
  public static final String noIteminSlot = ChatColor.DARK_RED + "You should put an item in slot 19 to continue recipe";
  public static final String noItemRecipe = ChatColor.DARK_RED + "No item in other slots to create recipe";
  public static final String noItemInConfig = ChatColor.DARK_RED + "No item in config to be deleted";
  public static final String successfullyRemoved = ChatColor.DARK_GREEN + "Successfully removed item from config";
}
