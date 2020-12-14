package gun.m4;

import gun.m4.gun.GunCreate;
import gun.m4.gun.GunListener;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static org.bukkit.event.block.Action.*;

public class OnClick {

    private final Player player;
    private final PlayerInteractEvent event;
    private final Action action;

    public OnClick(Player player, PlayerInteractEvent event){
        this.player = player;
        this.event = event;
        action = event.getAction();
    }

    public void onRightClick(){
        if (action == LEFT_CLICK_AIR ||
            action == LEFT_CLICK_BLOCK ||
            action == PHYSICAL
            ) return;
        if (action == RIGHT_CLICK_AIR || action == RIGHT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        }

        new GunListener(player, event).shot();
    }

    public void onLeftClick(){
        if (action == RIGHT_CLICK_AIR ||
            action == RIGHT_CLICK_BLOCK ||
            action == PHYSICAL
            ) return;
        if (action == LEFT_CLICK_AIR || action == LEFT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        }
        if(player.getInventory().getItemInMainHand().getType() == GunCreate.getReloadingItemType()) event.setCancelled(true);
    }
}
