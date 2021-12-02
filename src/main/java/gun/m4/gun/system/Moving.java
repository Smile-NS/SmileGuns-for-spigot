package gun.m4.gun.system;

import gun.m4.gun.GunConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Material.CROSSBOW;

public abstract class Moving {


    protected final Player player;
    protected final PlayerInventory inv;
    protected final Plugin PLUGIN;
    protected int next = 0;

    protected static boolean running = false;
    protected static final List<Moving> taskList = new ArrayList<>();

    protected Moving(Player player, Plugin plugin) {
        this.player = player;
        this.inv = player.getInventory();
        this.PLUGIN = plugin;
    }

    protected abstract boolean tickTask();

    protected void runTask() {
        running = true;
        BukkitRunnable task = new BukkitRunnable() {

            public void run() {

                if (taskList.isEmpty()) {
                    cancel();
                    running = false;
                    return;
                }

                for (Moving moving : new ArrayList<>(taskList)) {
                    moving.next++;
                    if (!moving.tickTask()) taskList.remove(moving);
                }
            }
        };
        task.runTaskTimer(PLUGIN, 0L, 1L);
    }

    public static void onSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() != CROSSBOW) return;
        if (!GunConfig.isGun(item)) return;

        if (player.isSneaking()) {
            GunConfig.setGun(item, 1, GunConfig.getBulletAmount(item));
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(GunConfig.playerBaseSpeed);
        } else {
            GunConfig.setGun(item, 2, GunConfig.getBulletAmount(item));
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(GunConfig.playerBaseSpeed * 0.7);
        }
    }

    public static void cancelChangingHand(PlayerSwapHandItemsEvent event, Player player){
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (GunConfig.isGun(gun)) event.setCancelled(true);
    }

    public static void cancelChangingHand(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCursor();
        Inventory inv = event.getClickedInventory();

        if (event.getSlot() != 40 || !GunConfig.isGun(Objects.requireNonNull(item))) return;
        event.setCancelled(true);

        if (inv.firstEmpty() == -1) player.getWorld().dropItem(player.getLocation(), item);
        event.setCurrentItem(null);
        inv.addItem(item);
    }

    public static void cancelAttackDamage(EntityDamageByEntityEvent event){
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;
        if (!Reload.playerMap.containsKey(player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (GunConfig.isGun(item)) event.setCancelled(true);
    }
}
