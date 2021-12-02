package gun.m4.commands.gun;

import gun.m4.exceptions.IncorrectArgsException;
import gun.m4.exceptions.NotHasPermissionException;
import gun.m4.exceptions.PlayerNotFoundException;
import gun.m4.gun.GunConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

public class GunCmd {

    private final Player sender;
    private final String[] args;

    public GunCmd(Player sender, String[] args) throws Exception{
        this.sender = sender;
        this.args = args;

        if (args.length == 0) throw new IncorrectArgsException(sender);
        switch (args[0]){
            case "give":
                give();
                break;
            default:
                throw new IncorrectArgsException(sender);
        }
    }

    public void give() throws Exception {
        if (!sender.hasPermission("gun.give")) throw new NotHasPermissionException(sender);
        Player target = args.length == 2 ? sender :
                        args.length == 3 ? Bukkit.getPlayerExact(args[2]) : null;
        if (target == null) throw new PlayerNotFoundException(sender);

        Inventory inv = target.getInventory();
        switch (args[1]) {
            case "m4a1":
                inv.addItem(GunConfig.setGun(new ItemStack(Material.CROSSBOW), 1, 0));
                break;
            default:
                throw new IncorrectArgsException(sender);
        }
        sender.sendMessage(GRAY + target.getDisplayName() + " に " + args[1] + " を与えました");
    }
}
