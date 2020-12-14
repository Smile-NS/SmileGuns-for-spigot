package gun.m4.gun;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static gun.m4.WriteConfig.PLUGIN;
import static org.bukkit.Material.CROSSBOW;

public class Reload {

    public static final Map<Player, BukkitRunnable> isReloading = new HashMap<>();
    
    private static final int RELOAD_TIME = GunCreate.getTime();
    private static final Material ITEM = GunCreate.getReloadingItemType();

    private final Player player;
    private final PlayerInventory inv;

    public Reload(Player player){
        this.player = player;
        inv = player.getInventory();
    }


    public void reloadByDropping(PlayerDropItemEvent event, final int amount){
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getType() != CROSSBOW && itemStack.getType() != ITEM) return;
        if (!GunCreate.isGun(itemStack)) return;
        if (isReloading.containsKey(player)){
            event.setCancelled(true);
            return;
        }

        item.remove();
        ItemStack gun = new ItemStack(ITEM);
        inv.setItemInMainHand(gun);
        normalReload(amount, itemStack);
    }

    public void normalReload(final int amount, ItemStack gun){
        if (isReloading.containsKey(player)) return;

        player.playSound(player.getEyeLocation(), "guns.m4a1.reload-start", 1.0f, 1.0f);
        ItemStack reloadingGun = GunCreate.setReloadingGun(new ItemStack(ITEM), gun);
        inv.setItemInMainHand(reloadingGun);

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(GunCreate.playerBaseSpeed);
        final int DAMAGE = ITEM.getMaxDurability();

        GunEffect.actionbar("reloading...", player);
        setItemDamage(inv.getItemInMainHand(), DAMAGE);

        BukkitRunnable task = new BukkitRunnable() {
            private int next = 0;

            public void run() {
                next++;

                if (next == RELOAD_TIME && isReloading.containsKey(player)) {
                    finish(amount);
                    cancel();
                    return;
                }
                addItemDamage(player, RELOAD_TIME, DAMAGE);
            }
        };
        isReloading.put(player, task);
        task.runTaskTimer(PLUGIN, 0L, 1L);
    }

    private static void addItemDamage(Player player, final int TIME, final int DAMAGE){
        ItemStack gun = player.getInventory().getItemInMainHand();
        ItemMeta meta = gun.getItemMeta();
        int damage = ((Damageable) meta).getDamage();

        setItemDamage(gun, damage - DAMAGE / TIME);
    }

    private static void setItemDamage(ItemStack item, int damage){
        ItemMeta meta = item.getItemMeta();
        ((Damageable) meta).setDamage(damage);
        item.setItemMeta(meta);
    }

    private void finish(int amount){
        player.playSound(player.getEyeLocation(), "guns.m4a1.reload-finish", 1.0f, 1.0f);

        ItemStack gun = new ItemStack(CROSSBOW);
        int modelData = player.isSneaking() ? 2 : 1;
        inv.setItemInMainHand(GunCreate.setGun(gun, modelData, amount));

        GunEffect.actionbar(String.valueOf(amount), player);
        isReloading.remove(player);
    }

    public static void changeSlot(Player player, PlayerItemHeldEvent event){
        int previous = event.getPreviousSlot();
        PlayerInventory inv = player.getInventory();
        ItemStack selected = inv.getItem(previous);

        if (selected == null || selected.getType() != ITEM) return;
        if (!isReloading.containsKey(player)) return;

        setItemDamage(selected, 0);
        isReloading.get(player).cancel();
        isReloading.remove(player);

        int amount = GunCreate.getBulletAmount(selected);
        if (amount > 0)
            inv.setItemInMainHand(GunCreate.setGun(new ItemStack(CROSSBOW), 1, amount));
    }
}
