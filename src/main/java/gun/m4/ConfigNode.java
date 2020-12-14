package gun.m4;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static gun.m4.WriteConfig.space;
import static org.bukkit.Bukkit.getServer;

public interface ConfigNode {

    Plugin PLUGIN = getServer().getPluginManager().getPlugin("SmileGuns");
    FileConfiguration config = PLUGIN.getConfig();

    String GENERAL = "general";
    String BULLET_MAX_AMOUNT =  space(GENERAL) + "bullet-max-amount";
    String GUN_NAME = "gun-name";

    String SHOT = "shot";
    String NOT_COLLISION =  space(SHOT) + "not-collision";
    String CAN_BREAK_BLOCK =  space(SHOT) + "can-break-block";
    String FLYING_DISTANCE =  space(SHOT) + "flying-distance";
    String SHOT_CONT =  space(SHOT) + "shot-count";
    String BULLET_MAX_SWING =  space(SHOT) + "bullet-max-swing";
    String DAMAGE =  space(SHOT) + "damage";

    String RELOAD = "reload";
    String TIME =  space(RELOAD) + "time";
    String RELOADING_ITEM_TYPE =  space(RELOAD) + "reloading-item-type";
    String NEED_MAGAZINE = "need-magazine";
    
    void setBulletMaxAmount(int amount);
    void setGunName(String name);
    void setNotCollision(List<String> notCollisionList);
    void setCanBreakBlock(List<String> canBreakBlocks);
    void setFlyingDistance(double distance);
    void setShotCount(int count);
    void setBulletMaxSwing(int max);
    void setDamage(double damage);
    void setTime(int time);
    @Deprecated
    void setReloadingItemType(Material type);
}
