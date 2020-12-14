package gun.m4.exceptions;

import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

public class IncorrectArgsException extends Exception{

    private static final long serialVersionUID = 1L;
    private static final String msg = "不正な引数です";

    public IncorrectArgsException(Player sender){
        super(msg);
        sender.sendMessage(RED + msg);
    }
}
