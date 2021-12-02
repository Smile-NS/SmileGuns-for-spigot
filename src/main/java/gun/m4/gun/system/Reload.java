package gun.m4.gun.system;

import gun.m4.gun.GunConfig;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.CROSSBOW;

public class Reload extends Moving {

    public static final Map<Player, Reload> playerMap = new HashMap<>();

    private static final int BULLET_AMOUNT = GunConfig.getBulletMaxAmount();
    private static final int RELOAD_TIME = GunConfig.getTime();
    private static final Material ITEM = GunConfig.getReloadingItemType();

    public Reload(Player player, Plugin plugin){
        super(player, plugin);
    }

    public void reloadByDropping(PlayerDropItemEvent event){
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getType() != CROSSBOW && itemStack.getType() != ITEM) return;
        if (!GunConfig.isGun(itemStack)) return;

        item.remove();
        ItemStack gun = new ItemStack(ITEM);
        inv.setItemInMainHand(gun);
        normalReload(itemStack);
    }

    public void normalReload(ItemStack gun){

        playerMap.put(player, this);
        player.playSound(player.getEyeLocation(), "guns.m4a1.reload-start", 1.0f, 1.0f);
        ItemStack reloadingGun = GunConfig.setReloadingGun(new ItemStack(ITEM), gun);
        inv.setItemInMainHand(reloadingGun);

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(GunConfig.playerBaseSpeed);
        final int DAMAGE = ITEM.getMaxDurability();

        GunEffect.actionbar("reloading...", player);
        setItemDamage(inv.getItemInMainHand(), DAMAGE);

        taskList.add(this);
        if (!running) runTask();
    }

    private static void setItemDamage(ItemStack item, int damage){
        ItemMeta meta = item.getItemMeta();
        ((Damageable) meta).setDamage(damage);
        item.setItemMeta(meta);
    }

    public void changeSlot(PlayerItemHeldEvent event){
        int previous = event.getPreviousSlot();
        ItemStack selected = inv.getItem(previous);

        if (selected == null || selected.getType() != ITEM) return;

        setItemDamage(selected, 0);
        taskList.remove(this);
        playerMap.remove(player);

        int amount = GunConfig.getBulletAmount(selected);
        if (amount > 0)
            inv.setItemInMainHand(GunConfig.setGun(new ItemStack(CROSSBOW), 1, amount));
    }

    @Override
    protected boolean tickTask() {
        if (next == RELOAD_TIME) {
            finish();
            return false;
        }

        ItemStack gun = player.getInventory().getItemInMainHand();
        ItemMeta meta = gun.getItemMeta();
        int damage = ((Damageable) meta).getDamage();

        setItemDamage(gun, damage - ITEM.getMaxDurability() / RELOAD_TIME);
        return true;
    }

    void finish(){
        player.playSound(player.getEyeLocation(), "guns.m4a1.reload-finish", 1.0f, 1.0f);

        ItemStack gun = new ItemStack(CROSSBOW);
        int modelData = player.isSneaking() ? 2 : 1;
        inv.setItemInMainHand(GunConfig.setGun(gun, modelData, BULLET_AMOUNT));

        GunEffect.actionbar(String.valueOf(BULLET_AMOUNT), player);
        playerMap.remove(player);
    }
}
