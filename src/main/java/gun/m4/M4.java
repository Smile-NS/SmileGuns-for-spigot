package gun.m4;

import gun.m4.commands.Complement;
import gun.m4.commands.SendCommand;
import gun.m4.gun.GunCreate;
import gun.m4.gun.GunEffect;
import gun.m4.gun.GunListener;
import gun.m4.gun.Reload;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


/**
 * ＜確認されているバグ＞
 * インベントリを開いてオフハンドに銃を切り替えようとすると透明の銃が設置される（増殖バグ）
 * リロード中に銃を捨てて新たな銃を手に入れるとそのままリロードが続行される
 * プレイヤーを基準に高さ1ブロック上の感圧版に触れるとなぜか大量に発砲される
 */
public final class M4 extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this,this);

        new WriteConfig().setDefaultAll();
        saveConfig();

        List<Player> playerList = new ArrayList<>(getServer().getOnlinePlayers());
        for (Player player : playerList) sendPackRequest(player);
    }

    private void sendPackRequest(Player player){
        player.setResourcePack(
                "https://cdn.discordapp.com/attachments/772130235738357803/787544836625924096/SmileGunsPack.zip"
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        sendPackRequest(player);
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        OnClick click = new OnClick(player, event);
        click.onRightClick();
        click.onLeftClick();
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        new Reload(player).reloadByDropping(event, GunCreate.getBulletMaxAmount());
    }

    @EventHandler
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        Reload.changeSlot(player, event);
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity entity = event.getDamager();
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (GunCreate.isGun(item)) event.setCancelled(true);
    }

    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        GunEffect.onSneak(player);
    }

    @EventHandler
    public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        GunListener.cancelChangingHand(event, player);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        GunListener.cancelChangingHand(event, player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)) {
            System.out.println("このコマンドはプレイヤー用です");
            return true;
        }
        Player player = (Player) sender;
        new SendCommand(player, command.getName(), args).commandSelect();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("gun".equals(command.getName())) return new Complement(args).gun();
        return super.onTabComplete(sender, command, alias, args);
    }
}
