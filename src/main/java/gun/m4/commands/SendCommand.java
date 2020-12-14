package gun.m4.commands;

import gun.m4.commands.gun.GunCmd;
import org.bukkit.entity.Player;

public class SendCommand {

    private final Player sender;
    private final String cmdName;
    private final String[] args;

    public SendCommand(Player sender, String cmdName, String[] args){
        this.sender = sender;
        this.cmdName = cmdName;
        this.args = args;
    }

    public void commandSelect(){
        try{
            if (cmdName.equalsIgnoreCase("gun")) new GunCmd(sender, args);
        }catch (Exception ignored){}
    }
}
