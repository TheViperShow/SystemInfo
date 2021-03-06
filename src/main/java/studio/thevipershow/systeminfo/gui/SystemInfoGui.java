package studio.thevipershow.systeminfo.gui;

import studio.thevipershow.systeminfo.SystemInfo;
import studio.thevipershow.systeminfo.oshi.SystemValues;
import studio.thevipershow.systeminfo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class SystemInfoGui {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("d\\M\\u h:m:s a");

    private static final SystemValues values = SystemValues.getInstance();

    private SystemInfoGui() {
    }

    private static final Set<Integer> backgroundSlots = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 26, 25, 24, 23, 22, 21, 20, 19, 10));

    /**
     * This methods creates the GUI to a Player
     *
     * @param player a valid Player
     */
    public static void createGui(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, "SystemInfo");
        player.openInventory(inventory);
        fillBackground(inventory);
        Bukkit.getScheduler().runTaskTimer(SystemInfo.getInstance(), r -> {
            if (player.getOpenInventory().getTitle().equals("SystemInfo")) {
                updateInventory(inventory);
            } else {
                r.cancel();
            }
        }, 2L, 20L);
    }

    /**
     * This method generates an animation that consists in taking a list of integers that represents
     * inventory slots, then generating items with material parameter for each slot creating a cool effect
     *
     * @param inventory the inventory where the items will be set.
     */
    private static void fillBackground(Inventory inventory) {
        Iterator<Integer> invSlot = SystemInfoGui.backgroundSlots.iterator();
        Bukkit.getScheduler().runTaskTimer(SystemInfo.getInstance(), r -> {
            if (invSlot.hasNext()) {
                createCustomItem(inventory, Material.BLACK_STAINED_GLASS_PANE, invSlot.next(), "", "");
            } else {
                r.cancel();
            }
        }, 1L, 1L);
    }

    /**
     * This methods creates and sets in an inventory a new custom ItemStack from the given parameters:
     *
     * @param inv         this is the inventory where the item should be set.
     * @param material    this is the material that the new ItemStack will have.
     * @param invSlot     this is in which slot of the inventory the item will be set.
     * @param displayName the display name of the new ItemStack (this does support color codes with &).
     * @param loreText    the lore of the new ItemStack (this does support color codes with & and multiple lines with \n).
     * @throws IllegalArgumentException if amount of items is illegal, or the slot is illegal.
     */
    private static void createCustomItem(Inventory inv, Material material, int invSlot, String displayName, String... loreText) {
        if ((invSlot >= 0 && invSlot <= inv.getSize())) {
            ItemStack item;
            List<String> lore = new ArrayList<>();
            item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.color(displayName));
            for (String s : loreText) {
                lore.add(Utils.color(s));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(invSlot - 1, item);
        }
    }

    /**
     * This methods updates the inventory with new items
     *
     * @param inventory the target inventory where items will be set.
     */
    private static void updateInventory(Inventory inventory) {
        createCustomItem(inventory, Material.LIME_CONCRETE, 11, "&2Processor",
                "&7Vendor: &a" + values.getCpuVendor(),
                "&7Model: &a" + values.getCpuModel() + " " + values.getCpuModelName(),
                "&7Clock Speed: &a" + values.getCpuMaxFrequency() + " GHz",
                "&7Physical Cores: &a" + values.getCpuCores(),
                "&7Logical Cores: &a" + values.getCpuThreads());

        createCustomItem(inventory, Material.GREEN_CONCRETE, 13, "&2Memory",
                "&7Total: &a" + values.getMaxMemory(),
                "&7Available: &a" + values.getAvailableMemory(),
                "&7Swap Used: &a" + values.getUsedSwap(),
                "&7Swap Allocated: &a" + values.getTotalSwap());

        createCustomItem(inventory, Material.LIGHT_BLUE_CONCRETE, 15, "&2Operating system",
                "&7Name: &a" + values.getOSFamily() + " " + values.getOSManufacturer(),
                "&7Version: &a" + values.getOSVersion(),
                "&7Active Processes: &a" + values.getRunningProcesses());

        createCustomItem(inventory, Material.BLUE_CONCRETE, 17, "&2Uptime",
                "&7Jvm uptime: &a" + ChronoUnit.MINUTES.between(SystemInfo.getInstance().getStartupTime(), LocalDateTime.now()) + " min.",
                "&7Current time: &a" + LocalDateTime.now().format(TIME_FORMATTER));
    }
}
