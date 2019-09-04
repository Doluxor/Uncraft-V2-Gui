package info.mythicmc.zorioux.uncraft;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class UncraftCommand implements CommandExecutor {
    private Plugin plugin = Uncraft.getPlugin(Uncraft.class);
    // /uncraft command with sub commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("uncraft")) return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage(Msg.onlyPlayers);
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("Mythic.Uncraft.Use")) {
            p.sendMessage(Msg.noPerm);
            return true;
        }
        if (args.length == 0) {
            guiMaker(p, 0, 45, "1");

        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
            p.sendMessage(Msg.help);

        } else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("C")) {
            if (!p.hasPermission("Mythic.Uncraft.Create")) {
                p.sendMessage(Msg.noPerm);
                return true;
            }
            createRecipeGui(p);
        } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
            if (!p.hasPermission("Mythic.Uncraft.Delete")) {
                p.sendMessage(Msg.noPerm);
                return true;
            }
            deleteGui(p, 0, 45, "1");
        }else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
            plugin.reloadConfig();
            p.sendMessage(ChatColor.DARK_GREEN + "Reloaded config successfully");
         }else p.sendMessage(ChatColor.DARK_RED + "No such subCommand exist");
        return true;
    }

    /* a method to create a gui with all uncrafted items from config, only 45 item from config will be taken
     * and will be visible in the gui
     * start and end should be done in arithmetic progression which being used in pager system in
     * MainEvents class clickEvent method
     * player is simply the player variable from any method and it is needed to decide which player to open the gui
     * page will be visible in the title of the gui and should be taken from title, parsed to integer then
     * increase/decrease in pager system*/
    public void guiMaker(Player player,int start,int end,String page) {
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "Uncraft Page:" + page);
        int uncraftSize = plugin.getConfig().getStringList("un-craft").size();
        for (int f = 0; f < uncraftSize; f++) {
            if (f < start) continue;
            if (end > uncraftSize) end = uncraftSize;
            if( f == end) break;
            String[] items = plugin.getConfig().getStringList("un-craft").get(f).split("=");
            ItemStack x = new ItemStack(Material.valueOf(items[0].toUpperCase()));

            String allDrops = items[1];
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
            ItemMeta u = x.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "You can uncraft it to:");
            String[] part = all.toString().split("\\[");
            String[] allString = part[1].split("]");
            lore.add(ChatColor.WHITE + allString[0]);
            assert u != null;
            u.setLore(lore);
            x.setItemMeta(u);
            gui.addItem(x);
        }
        ItemStack nextArrow = new ItemStack(Material.ARROW, 1 );
        ItemStack quit = new ItemStack(Material.BARRIER, 1);
        ItemStack backArrow = new ItemStack(Material.ARROW, 1 );
        ItemMeta x = nextArrow.getItemMeta();
        ItemMeta z = quit.getItemMeta();
        ItemMeta y = backArrow.getItemMeta();
        if (x != null)
        x.setDisplayName(ChatColor.DARK_GREEN + "Next page");
        if (z != null)
        z.setDisplayName(ChatColor.DARK_RED + "Exit");
        if (y != null)
        y.setDisplayName(ChatColor.RED + "Previous Page");
        nextArrow.setItemMeta(x);
        quit.setItemMeta(z);
        backArrow.setItemMeta(y);
        gui.setItem(48, backArrow);
        gui.setItem(49, quit);
        gui.setItem(50, nextArrow);
        player.openInventory(gui);
    }

    /* the same method above but for accepting uncrafting process
     * itemsDrop is ArrayList what items will be uncrafted to, you can look more in MainEvents > clickEvent method
     * to understand more */
    public void acceptGui(Player player,String item,String itemDrops){
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "Uncraft");
        //22
        ItemStack accept = new ItemStack(Material.SLIME_BALL);
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemStack itemX  = new ItemStack(Material.valueOf(item.toUpperCase()));
        ItemMeta x       = accept.getItemMeta();
        ItemMeta y       = cancel.getItemMeta();
        ItemMeta z       = itemX.getItemMeta();
        assert x != null;
        x.setDisplayName(ChatColor.DARK_GREEN + "Accept");
        assert y != null;
        y.setDisplayName(ChatColor.DARK_RED + "Cancel");
        assert z != null;
        z.setDisplayName(ChatColor.DARK_BLUE + "Confirm uncrafting");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Would you uncraft this item to");
        String[] part = itemDrops.split("\\[");
        String[] allString = part[1].split("]");
        lore.add(ChatColor.WHITE + allString[0] + "?");
        z.setLore(lore);
        accept.setItemMeta(x); cancel.setItemMeta(y); itemX.setItemMeta(z);
        gui.setItem(22, itemX); gui.setItem(30, accept); gui.setItem(32, cancel);
        player.openInventory(gui);
    }
    /* a gui to create uncrafting recipe in game
     * better than using config to create them which is pain */
    public void createRecipeGui(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "Uncraft Create");
        ItemStack create =  new ItemStack(Material.LIME_TERRACOTTA ,1);
        ItemStack cancel =  new ItemStack(Material.RED_TERRACOTTA ,1);
        ItemMeta x = create.getItemMeta();
        ItemMeta y = cancel.getItemMeta();
        assert x != null;
        x.setDisplayName(ChatColor.DARK_GREEN + "Create Recipe");
        assert y != null;
        y.setDisplayName(ChatColor.DARK_RED + "Cancel");
        create.setItemMeta(x); cancel.setItemMeta(y);
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta z = glass.getItemMeta();
        assert z != null;
        z.setDisplayName(ChatColor.DARK_RED + "X");
        glass.setItemMeta(z);
        for (int h = 0; h < 18; h++) {
            if (h ==3 || h == 5) continue;
            gui.setItem(h,glass);
        }
        gui.setItem(3,create);
        gui.setItem(5,cancel);
        gui.setItem(19,glass);
        gui.setItem(27,glass);
        gui.setItem(28,glass);
        player.openInventory(gui);
    }

    /* a gui to delete existing uncraft recipe from config in game, instead of doing it in config */
    public void deleteGui(Player player,int start,int end,String page) {
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + "Uncraft delete:" + page);
        int uncraftSize = plugin.getConfig().getStringList("un-craft").size();
        for (int f = 0; f < uncraftSize; f++) {
            if (f < start) continue;
            if (end > uncraftSize) end = uncraftSize;
            if( f == end) break;
            String[] items = plugin.getConfig().getStringList("un-craft").get(f).split("=");
            ItemStack x = new ItemStack(Material.valueOf(items[0].toUpperCase()));

            List<String> lore = new ArrayList<>();
            ItemMeta u = x.getItemMeta();
            lore.add(ChatColor.DARK_RED + "Click to remove this from config");
            assert u != null;
            u.setLore(lore);
            x.setItemMeta(u);
            gui.addItem(x);
        }
        ItemStack nextArrow = new ItemStack(Material.ARROW, 1 );
        ItemStack quit = new ItemStack(Material.BARRIER, 1);
        ItemStack backArrow = new ItemStack(Material.ARROW, 1 );
        ItemMeta x = nextArrow.getItemMeta();
        ItemMeta z = quit.getItemMeta();
        ItemMeta y = backArrow.getItemMeta();
        assert x != null;
        x.setDisplayName(ChatColor.DARK_GREEN + "Next page");
        assert z != null;
        z.setDisplayName(ChatColor.DARK_RED + "Exit");
        assert y != null;
        y.setDisplayName(ChatColor.RED + "Previous Page");
        nextArrow.setItemMeta(x);
        quit.setItemMeta(z);
        backArrow.setItemMeta(y);
        gui.setItem(48, backArrow);
        gui.setItem(49, quit);
        gui.setItem(50, nextArrow);
        player.openInventory(gui);
    }

    /* same as accept gui, but for accepting a deletion of recipe */
    public void acceptDeleteGui(Player player,String item){
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_RED + "Delete");
        //22
        ItemStack accept = new ItemStack(Material.SLIME_BALL);
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemStack itemX  = new ItemStack(Material.valueOf(item.toUpperCase()));
        ItemMeta x       = accept.getItemMeta();
        ItemMeta y       = cancel.getItemMeta();
        ItemMeta z       = itemX.getItemMeta();
        assert x != null;
        x.setDisplayName(ChatColor.DARK_GREEN + "Accept");
        assert y != null;
        y.setDisplayName(ChatColor.DARK_RED + "Cancel");
        assert z != null;
        z.setDisplayName(ChatColor.DARK_RED + "Confirm deletion?");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Would you delete this item from config ?");
        z.setLore(lore);
        accept.setItemMeta(x); cancel.setItemMeta(y); itemX.setItemMeta(z);
        gui.setItem(22, itemX); gui.setItem(30, accept); gui.setItem(32, cancel);
        player.openInventory(gui);
    }

}
