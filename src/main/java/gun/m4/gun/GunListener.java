package gun.m4.gun;

import gun.m4.gun.system.Moving;
import gun.m4.gun.system.Reload;
import gun.m4.gun.system.Shot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import static org.bukkit.event.block.Action.*;

public class GunListener implements Listener {

    private final Plugin PLUGIN;

    public GunListener(Plugin plugin) {
        this.PLUGIN = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == LEFT_CLICK_AIR ||
                action == LEFT_CLICK_BLOCK ||
                action == PHYSICAL
        ) return;
        if (action == RIGHT_CLICK_AIR || action == RIGHT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        }

        new Shot(player, PLUGIN, event).shot();
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == RIGHT_CLICK_AIR ||
                action == RIGHT_CLICK_BLOCK ||
                action == PHYSICAL
        ) return;
        if (action == LEFT_CLICK_AIR || action == LEFT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        }
        if(player.getInventory().getItemInMainHand().getType() ==
                GunConfig.getReloadingItemType()) event.setCancelled(true);
    }

    @EventHandler
    public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        Moving.cancelChangingHand(event, player);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event){
        Moving.cancelChangingHand(event);
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Reload reload = new Reload(player, PLUGIN);
        Reload.playerMap.put(player, reload);
        reload.reloadByDropping(event);
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if (Reload.playerMap.containsKey(player))
            Reload.playerMap.get(player).changeSlot(event);
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Moving.cancelAttackDamage(event);
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event){
        Moving.onSneak(event);
    }
}
