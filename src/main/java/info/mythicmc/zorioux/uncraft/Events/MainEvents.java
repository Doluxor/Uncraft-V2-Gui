package info.mythicmc.zorioux.uncraft.Events;


import info.mythicmc.zorioux.uncraft.Msg;
import info.mythicmc.zorioux.uncraft.Uncraft;
import info.mythicmc.zorioux.uncraft.UncraftCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainEvents implements Listener {
    private Plugin plugin = Uncraft.getPlugin(Uncraft.class);
    private UncraftCommand ucMethods = new UncraftCommand();

    //spaghetti warning!
    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        FileConfiguration uncraft = YamlConfiguration.loadConfiguration(Uncraft.file);
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDate = time.format(myFormatObj);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        int size = plugin.getConfig().getStringList("un-craft").size();

        //pager system
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta() != null) {
                ItemMeta xItem = e.getCurrentItem().getItemMeta();
                if (xItem.hasDisplayName()) {
                    if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft Page:") &&
                            xItem.getDisplayName().equals(ChatColor.DARK_GREEN + "Next page")) {
                        e.setCancelled(true);
                        // getting page number and doing arithmetic progression for paging
                        // for next page
                        String[] w = e.getView().getTitle().split(":");
                        int number = Integer.parseInt(w[1]);
                        int start = number*45;
                        int end = (number + 1)*45;
                        int x = number + 1;
                        String page = Integer.toString(x);
                        double sizeX = plugin.getConfig().getStringList("un-craft").size();
                        // a way to prevent making empty pages
                        if (end / 45 >=
                                Math.ceil((sizeX/45)+1)) return;
                        ucMethods.guiMaker(p,start,end,page);

                      // exit button
                    } else if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft Page:") &&
                            xItem.getDisplayName().equals(ChatColor.DARK_RED + "Exit")) {

                        e.setCancelled(true);
                        p.closeInventory();

                    } else if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft Page:") &&
                            xItem.getDisplayName().equals(ChatColor.RED + "Previous Page")) {
                        e.setCancelled(true);
                        // getting page number and doing arithmetic progression for paging
                        // for previews page
                        String[] w = e.getView().getTitle().split(":");
                        int number = Integer.parseInt(w[1]);
                        if (number == 1) return;
                        int start = (number*45) - 90;
                        int end = ((number + 1)*45) - 90;
                        int x = number - 1;
                        String page = Integer.toString(x);
                        ucMethods.guiMaker(p,start,end,page);

                    }
                }
            }
        }
        //when clicking an item other than action buttons, and switch to accept gui
        if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft Page:")) {
            e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getItemMeta() != null) {
                        ItemMeta xItem = e.getCurrentItem().getItemMeta();
                        if (xItem.hasDisplayName()) {
                              if (xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Next page")
                                 || xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "Exit")
                                   || xItem.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Previous Page")) return;
                        }
                    }
                }
            // proceeding to accept gui
            for (int x = 0; x < size; x++){
               String[ ] item = plugin.getConfig().getStringList("un-craft").get(x).split("=");
               if (!(item[0].equalsIgnoreCase(e.getCurrentItem().getType().toString()))) continue;

                   String allDrops = item[1];
                   String[] dropsArray = allDrops.split("&");
                   HashMap<String, String> dropsAndQuantities = new HashMap<>();

                for (String aDropsArray : dropsArray) {
                    String[] dropQ = aDropsArray.split("#");
                    dropsAndQuantities.put(dropQ[0], dropQ[1]);
                }
                   ArrayList<String> all = new ArrayList<>();

                   for (String i : dropsAndQuantities.keySet()) {
                       int quantity = Integer.parseInt(dropsAndQuantities.get(i));
                       all.add(i + " x" + quantity);
                   }

                   ucMethods.acceptGui(p, item[0], all.toString());
                   break;
            }

        }
        // accept gui buttons and what they do
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Uncraft")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta() != null) {
                    ItemMeta xItem = e.getCurrentItem().getItemMeta();
                    if (xItem.hasDisplayName()) {
                        //cancels uncrafting and switch to first page of /uncraft
                        if (xItem.getDisplayName().equals(ChatColor.DARK_RED + "Cancel")) {
                            p.closeInventory();
                            ucMethods.guiMaker(p,0,45,"1");
                          // accepting uncrafting
                        } else if (xItem.getDisplayName().equals(ChatColor.DARK_GREEN + "Accept")) {
                            e.setCancelled(true);
                            ItemStack stackItem = e.getView().getItem(22);
                            if (stackItem == null) return;
                                String item = stackItem.getType().toString();


                                int x = plugin.getConfig().getStringList("un-craft").size();
                                // loop for checking item uncrafting is valid and exist in config
                                for (int w = 0; w < x; w++) {
                                    String[] configItem = plugin.getConfig().getStringList("un-craft").get(w).split("=");
                                    if (!(item.equalsIgnoreCase(configItem[0]))) {
                                        // once loop reach it's last loop, it will close and send error
                                        if (w + 1 == x) {
                                            p.closeInventory();
                                            p.sendMessage(Msg.canNotUncraft);
                                            return;
                                        }
                                        continue;
                                    }

                                    String[] dropsArray = configItem[1].split("&");
                                    int dropsQuantity = dropsArray.length;
                                    HashMap<String, String> dropsAndQuantities = new HashMap<>();

                                    // loop to put items and quantities in hashmap
                                    for (String aDropsArray : dropsArray) {
                                        String[] dropQ = aDropsArray.split("#");
                                        dropsAndQuantities.put(dropQ[0], dropQ[1]);
                                    }

                                    //check how many empty slots
                                    int slots = 0;
                                    for (int i = 0; i < 36; i++) {

                                        if (p.getInventory().getItem(i) == null) {

                                            slots++;
                                        }
                                    }
                                    // check if there is enough slots in player inventory
                                    if (!(slots + 1 > dropsQuantity)) {
                                        p.sendMessage(Msg.noSpace);
                                        return;
                                    }
                                    //loop to continue uncrafting
                                    for (int l = 0; l < 36; l++) {
                                        ItemStack stack = p.getInventory().getItem(l);
                                        if (stack != null) {
                                            if (stack.getType().toString().equalsIgnoreCase(configItem[0])) {
                                                if (p.getInventory().getItem(l) != null) {
                                                    //check if item is not used, also it is meant to allow uncrafting fixed items
                                                    if (stack instanceof Damageable) {
                                                        Damageable dmg = (Damageable) stack.getItemMeta();
                                                        ItemStack itemDuraStack = new ItemStack(Material.valueOf(configItem[0].toUpperCase()));

                                                        Damageable dmg2 = (Damageable) itemDuraStack.getItemMeta();
                                                        if (dmg2 == null) return;
                                                        double dura = dmg2.getHealth();
                                                        if (dmg == null) return;
                                                        if (dmg.getHealth() != dura) {
                                                            p.sendMessage(Msg.itemDamaged);
                                                            return;
                                                        }
                                                    }
                                                    //removing 1 item of player's inventory
                                                    int u = stack.getAmount();
                                                    stack.setAmount(u - 1);
                                                    ArrayList<String> all = new ArrayList<>();

                                                    //gives items
                                                    PlayerInventory inventory = p.getInventory();
                                                    for (String i : dropsAndQuantities.keySet()) {
                                                        int quantity = Integer.parseInt(dropsAndQuantities.get(i));
                                                        inventory.addItem(new ItemStack(Material.valueOf(i.toUpperCase()), quantity));
                                                        //arraylist for logs
                                                        all.add(i + " x" + quantity);
                                                    }
                                                    String world = p.getWorld().getName();
                                                    int cx = p.getLocation().getBlockX();
                                                    int cy = p.getLocation().getBlockY();
                                                    int cz = p.getLocation().getBlockZ();
                                                    //gets OS's separator, it is different from linux and windows
                                                    final String SEPARATOR = System.getProperty("file.separator");

                                                    try {
                                                        // writing in logs.log
                                                        FileWriter myWriter = new FileWriter("plugins" + SEPARATOR + "Uncraft" + SEPARATOR + "Logs.log", true);
                                                        myWriter.write("\r\n" + formattedDate + "  ::  " + "Player " + p.getName() + " uncrafted " + configItem[0] + " to "
                                                                + all + " :: at " + cx + " " + cy + " " + cz + " in " + world);

                                                        myWriter.close();

                                                    } catch (IOException b) {
                                                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED +
                                                                "Uncrafter output: Could not write into Logs.log");

                                                    }
                                                    //sending log in console as well
                                                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN +
                                                            "Player " + p.getName() + " uncrafted " + configItem[0] + " to "
                                                            + all + " :: at " + cx + " " + cy + " " + cz + " in " + world);
                                                    p.sendMessage(ChatColor.DARK_GREEN + "uncrafted successfully");
                                                    return;
                                                }
                                            }
                                            //send player msg if he don't have the item in his inventory at the end of loop
                                        } else if (l + 1 == 36) {
                                            p.sendMessage(Msg.noItem + item + Msg.noItem2);
                                            return;
                                        }


                                    }
                                }
                        }

                    }
                }
            }
        }
        // uncraft accept gui
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Uncraft Create")) {
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta() != null) {
                    ItemMeta xItem = e.getCurrentItem().getItemMeta();
                    if (xItem.hasDisplayName()) {
                        //accepting creation of recipe
                        if (xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Create Recipe")) {
                            e.setCancelled(true);
                            ItemStack stackX = e.getView().getItem(18);
                            if (stackX == null) return;
                            // stop the creation if the slot of item to be uncrafted is empty
                            if (stackX.getType().toString().equalsIgnoreCase("Air")) {
                                p.sendMessage(Msg.noIteminSlot);
                                return;
                            }
                            HashMap<String,Integer> itemsMap = new HashMap<>();
                            //gets the rest of items for the recipe
                            for(int x = 20; x < 54; x++) {
                                if (x == 27 || x == 28 ) continue;
                                ItemStack stack = e.getView().getItem(x);

                                if (stack != null) {
                                    if (!(stack.getType().toString().equalsIgnoreCase("Air"))) {
                                        String item = stack.getType().toString();
                                        int quantity =stack.getAmount();
                                        itemsMap.put(item, quantity);
                                        // cancels process if there is no items for the recipe
                                    } else if (x == 53 && itemsMap.size() == 0) {
                                        p.sendMessage(Msg.noItemRecipe);
                                        return;
                                    }
                                }
                            }
                            ItemStack stack = e.getView().getItem(18);
                            if (stack == null) return;
                            String uncrafted = stack.getType().toString();
                            StringBuilder first = new StringBuilder();
                            int h = -1;
                            //creating the string to be stored in config
                            for (String i : itemsMap.keySet()) {
                                int quantity = itemsMap.get(i);
                                String recipe = i.toUpperCase() + "#" + quantity;
                                // check if tehre is more than 1 item in the recipe to build the string
                                if (itemsMap.size() > 1) {
                                    // builds the string in the proper way
                                    h = h + 1;
                                    if (h == 0){
                                     first = new StringBuilder(recipe);
                                    }else if (h > 0) {
                                        first.append("&").append(recipe);
                                    }
                                //else if there is only 1 item, builds other type of string
                                } else {
                                    first = new StringBuilder(recipe);
                                }
                            }
                            // builds the final string the item and drops and quantities
                            String fullRecipe = uncrafted + "=" + first;
                            List<String> list = plugin.getConfig().getStringList("un-craft");
                            list.add(fullRecipe);
                            plugin.getConfig().set("un-craft", list);
                            //saving the string in the config
                            try {
                                uncraft.save(Uncraft.file);
                                plugin.saveConfig();
                            } catch (IOException b){
                                p.sendMessage(ChatColor.DARK_RED + "Could not save the recipe, Error occured");
                                return;
                            }
                            p.sendMessage(ChatColor.DARK_GREEN + "Successfully added recipe to config!");
                            ucMethods.createRecipeGui(p);
                            //cancel button action
                        }else if (xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "Cancel")) {
                            e.setCancelled(true);
                            p.closeInventory();
                            //the glass panes
                        }else if (xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "X")) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
        // delete gui, it is the same as normal uncrafting gui, pager system for deleting gui
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta() != null) {
                ItemMeta xItem = e.getCurrentItem().getItemMeta();
                if (xItem.hasDisplayName()) {
                    //next page button
                    if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft delete:") &&
                            xItem.getDisplayName().equals(ChatColor.DARK_GREEN + "Next page")) {
                        e.setCancelled(true);
                        String[] w = e.getView().getTitle().split(":");
                        int number = Integer.parseInt(w[1]);
                        int start = number*45;
                        int end = (number + 1)*45;
                        int x = number + 1;
                        String page = Integer.toString(x);
                        double sizeX = plugin.getConfig().getStringList("un-craft").size();
                        if ( end /45 >= Math.ceil((sizeX/45)+1)) return;
                        ucMethods.deleteGui(p,start,end,page);
                        // exit button
                    } else if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft delete:") &&
                            xItem.getDisplayName().equals(ChatColor.DARK_RED + "Exit")) {
                        e.setCancelled(true);
                        p.closeInventory();
                        // previews page button
                    } else if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft delete:") &&
                            xItem.getDisplayName().equals(ChatColor.RED + "Previous Page")) {
                        e.setCancelled(true);
                        String[] w = e.getView().getTitle().split(":");
                        int number = Integer.parseInt(w[1]);
                        if (number == 1) return;
                        int start = (number*45) - 90;
                        int end = ((number + 1)*45) - 90;
                        int x = number - 1;
                        String page = Integer.toString(x);
                        ucMethods.deleteGui(p,start,end,page);

                    }
                }
            }
        }
        // another condition for clicking any item in deletion gui other than action buttons
        if (e.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Uncraft delete:")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta() != null) {
                    ItemMeta xItem = e.getCurrentItem().getItemMeta();
                    if (xItem.hasDisplayName()) {
                        if (xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Next page")
                                || xItem.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "Exit")
                                || xItem.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Previous Page")) return;
                    }
                }
            }
            //loop to create accept deletion gui
            for (int x = 0; x < size; x++){
                String[ ] item = plugin.getConfig().getStringList("un-craft").get(x).split("=");
                if (!(item[0].equalsIgnoreCase(e.getCurrentItem().getType().toString()))) continue;
                ucMethods.acceptDeleteGui(p, item[0]);
                break;
            }

        }
        // accept deletion gui actions
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_RED + "Delete")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta() != null) {
                    ItemMeta xItem = e.getCurrentItem().getItemMeta();
                    if (xItem.hasDisplayName()) {
                        // if clicked cancel button, close inventory and opens delete gui page 1
                        if (xItem.getDisplayName().equals(ChatColor.DARK_RED + "Cancel")) {
                            p.closeInventory();
                            ucMethods.deleteGui(p,0,45,"1");
                            //if clicked accept, deletes the item from config
                        } else if (xItem.getDisplayName().equals(ChatColor.DARK_GREEN + "Accept")) {
                            e.setCancelled(true);
                            ItemStack stack = e.getView().getItem(22);
                            if (stack == null) return;
                            String item = stack.getType().toString();
                            int x = plugin.getConfig().getStringList("un-craft").size();
                            for (int w = 0; w < x; w++) {
                                String[] configItem = plugin.getConfig().getStringList("un-craft").get(w).split("=");
                                //loops continue if no item match, if reached the end of loop, error msg sent and cancels
                                if (!(item.equalsIgnoreCase(configItem[0]))) {
                                    if (w + 1 == x) {
                                        p.closeInventory();
                                        p.sendMessage(Msg.noItemInConfig);
                                        return;
                                    }
                                    //else if matches item, continues the deletion
                                } else if (item.equalsIgnoreCase(configItem[0])) {
                                    List<String> list = plugin.getConfig().getStringList("un-craft");
                                    list.remove(w);
                                    plugin.getConfig().set("un-craft", list);
                                    //saves config after deletion
                                   try {
                                        uncraft.save(Uncraft.file);
                                        plugin.saveConfig();
                                    } catch (IOException b){
                                        p.sendMessage(ChatColor.DARK_RED + "Could not save the recipe, Error occured");
                                        return;
                                    }
                                    //send success msg and reopen deletion gui page 1
                                    p.sendMessage(Msg.successfullyRemoved);
                                    ucMethods.deleteGui(p,0,45,"1");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
