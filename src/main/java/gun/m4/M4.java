package gun.m4;

import gun.m4.commands.Complement;
import gun.m4.commands.SendCommand;
import gun.m4.gun.GunListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
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
        PluginManager manager = Bukkit.getServer().getPluginManager();
        manager.registerEvents(this,this);
        manager.registerEvents(new GunListener(this), this);

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
