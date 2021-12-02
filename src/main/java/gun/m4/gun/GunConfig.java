package gun.m4.gun;

import gun.m4.WriteConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;
import static org.bukkit.inventory.ItemFlag.*;

public class GunConfig extends WriteConfig {

    public static double playerBaseSpeed = 0.1;
    public static final NamespacedKey GUN_TYPE = new NamespacedKey(WriteConfig.PLUGIN, "type");
    public static final NamespacedKey BULLET_AMOUNT = new NamespacedKey(WriteConfig.PLUGIN, "bullet-amount");

    public static int getBulletMaxAmount(){
        return config.getInt(BULLET_MAX_AMOUNT);
    }

    public static String getGunName(){
        return config.getString(GUN_NAME);
    }

    public static List<Material> getNotCollision(){
        return getBlockList(NOT_COLLISION);
    }

    public static List<Material> getCanBreakBlock(){
        return getBlockList(CAN_BREAK_BLOCK);
    }

    public static double getFlyingDistance(){
        return config.getDouble(FLYING_DISTANCE);
    }

    public static int getShotCount(){
        return config.getInt(SHOT_CONT);
    }

    public static int getBulletMaxSwing(){
        return config.getInt(BULLET_MAX_SWING);
    }

    public static double getDamage(){
        return config.getDouble(DAMAGE);
    }

    public static int getTime(){
        return config.getInt(TIME);
    }

    private static List<Material> getBlockList(String path){
        List<String> strMaterial = config.getStringList(path);
        List<Material> typeList = new ArrayList<>();

        for (String str : strMaterial) typeList.add(Material.getMaterial(str));
        return typeList;
    }

    public static Material getReloadingItemType(){
        return Material.valueOf(config.getString(RELOADING_ITEM_TYPE));
    }

    public static ItemStack setGun(ItemStack gun, int modelData, int bulletAmount){
        setGunMeta(gun, modelData, bulletAmount);
        return gun;
    }

    public static ItemStack setReloadingGun(ItemStack reloadingGun, ItemStack gun){
        setGunMeta(reloadingGun, 1, getBulletAmount(gun));
        return reloadingGun;
    }

    private static void setGunMeta(ItemStack gun, int modelData, int bulletAmount){
        ItemMeta meta = gun.getItemMeta();

        meta.setCustomModelData(modelData);
        meta.setDisplayName(getGunName());

        ItemFlag[] flags = { HIDE_ATTRIBUTES, HIDE_DESTROYS, HIDE_ENCHANTS,
                HIDE_PLACED_ON, HIDE_UNBREAKABLE, HIDE_POTION_EFFECTS };
        for (ItemFlag flag : flags) meta.addItemFlags(flag);

        List<String> lore = new ArrayList<>(Arrays.asList(
                YELLOW + "RIGHT-CLICK: SHOT",
                YELLOW + "DROP: RELOAD",
                YELLOW + "SNEAK: ADS"
        ));
        lore.add(WHITE + "<<" + bulletAmount + ">>");
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(BULLET_AMOUNT, PersistentDataType.INTEGER, bulletAmount);
        data.set(GUN_TYPE, PersistentDataType.STRING, GunType.M4A1.name());

        gun.setItemMeta(meta);
        setArrow(gun);
    }

    public static void setMetaWhenShooting(ItemStack gun, int bulletAmount){
        ItemMeta meta = gun.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(3, WHITE + "<<" + bulletAmount + ">>");
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(BULLET_AMOUNT, PersistentDataType.INTEGER, bulletAmount);

        gun.setItemMeta(meta);
        setArrow(gun);
    }

    private static void setArrow(ItemStack gun){
        if (gun.getType() != CROSSBOW) return;
        ItemMeta meta = gun.getItemMeta();
        CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
        crossbowMeta.addChargedProjectile(new ItemStack(ARROW, 1));
        gun.setItemMeta(crossbowMeta);
    }

    public static int getBulletAmount(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(BULLET_AMOUNT, PersistentDataType.INTEGER);
    }

    public static GunType getGunType(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        String type = meta.getPersistentDataContainer().get(GUN_TYPE, PersistentDataType.STRING);
        return GunType.valueOf(type);
    }

    public static boolean isGun(ItemStack gun){
        Material type = gun.getType();
        if (type != CROSSBOW && type != getReloadingItemType()) return false;

        PersistentDataContainer data = gun.getItemMeta().getPersistentDataContainer();
        return data.has(GUN_TYPE, PersistentDataType.STRING);
    }
}
