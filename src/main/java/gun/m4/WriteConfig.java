package gun.m4;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.Material.*;
import static org.bukkit.Tag.*;

public class WriteConfig implements ConfigNode{

    public static String space(String key){
        return key + ".";
    }

    public void setDefaultAll(){
        setDefaultGeneral();
        setDefaultShot();
        setDefaultReload();
    }

    public void setDefaultGeneral(){
        if (!config.contains(BULLET_MAX_AMOUNT)) setBulletMaxAmount(30);
        if (!config.contains(GUN_NAME)) setGunName(GREEN + "M4A1");
    }

    public void setDefaultShot(){
        if (!config.contains(NOT_COLLISION)){
            List<String> typeList = new ArrayList<>();
            List<Material> noCollision = new ArrayList<>(Arrays.asList(
                    TRIPWIRE_HOOK, TRIPWIRE, AIR, CAVE_AIR, VOID_AIR,
                    GRASS, TALL_GRASS, FERN, LARGE_FERN, SUNFLOWER,
                    LILAC, ROSE_BUSH, PEONY, KELP, KELP_PLANT,
                    SEAGRASS, TALL_SEAGRASS, TORCH, WALL_TORCH, REDSTONE_TORCH,
                    REDSTONE_WALL_TORCH, DEAD_BUSH, BROWN_MUSHROOM, VINE, STONE_PRESSURE_PLATE,
                    LIGHT_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, LEVER, SEA_PICKLE, LADDER,
                    SCAFFOLDING, COBWEB, SUGAR_CANE, POTATOES, WHEAT,
                    MELON_STEM, ATTACHED_MELON_STEM, PUMPKIN_STEM, ATTACHED_PUMPKIN_STEM, CARROTS,
                    BEETROOTS, WATER, LAVA, KELP, KELP_PLANT
            ));
            for (Material type : noCollision) typeList.add(type.name());
            for (Material type : BUTTONS.getValues()) typeList.add(type.name());
            for (Material type : CARPETS.getValues()) typeList.add(type.name());
            for (Material type : CORALS.getValues()) typeList.add(type.name());
            for (Material type : RAILS.getValues()) typeList.add(type.name());
            for (Material type : SAPLINGS.getValues()) typeList.add(type.name());
            for (Material type : SMALL_FLOWERS.getValues()) typeList.add(type.name());
            for (Material type : WOODEN_PRESSURE_PLATES.getValues()) typeList.add(type.name());
            for (Material type : BANNERS.getValues()) typeList.add(type.name());
            for (Material type : SIGNS.getValues()) typeList.add(type.name());

            setNotCollision(typeList);
        }

        if (!config.contains(CAN_BREAK_BLOCK)) {
            List<String> typeList = new ArrayList<>();
            List<Material> glassPaneList = new ArrayList<>(Arrays.asList(
                GLASS_PANE, WHITE_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE,
                LIGHT_BLUE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE,
                PINK_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE,
                CYAN_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE,
                BROWN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE,
                BLACK_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE
            ));
            for (Material type : IMPERMEABLE.getValues()) typeList.add(type.name());
            for (Material type : glassPaneList) typeList.add(type.name());

            setCanBreakBlock(typeList);
        }

        if (!config.contains(FLYING_DISTANCE)) setFlyingDistance(50);
        if (!config.contains(SHOT_CONT)) setShotCount(3);
        if (!config.contains(BULLET_MAX_SWING)) setBulletMaxSwing(7);
        if (!config.contains(DAMAGE)) setDamage(4);
    }

    public void setDefaultReload(){
        if (!config.contains(TIME)) setTime(65);
        if (!config.contains(RELOADING_ITEM_TYPE)) setReloadingItemType(STONE_SWORD);
    }

    public void setBulletMaxAmount(int amount){
        config.set(BULLET_MAX_AMOUNT, amount);
    }

    public void setGunName(String name){
        config.set(GUN_NAME, name);
    }

    public void setNotCollision(List<String> notCollisionList){
        config.set(NOT_COLLISION, notCollisionList);
    }

    public void setCanBreakBlock(List<String> canBreakBlocks){
        config.set(CAN_BREAK_BLOCK, canBreakBlocks);
    }

    public void setFlyingDistance(double distance){
        config.set(FLYING_DISTANCE, distance);
    }

    public void setShotCount(int count){
        config.set(SHOT_CONT, count);
    }

    public void setBulletMaxSwing(int max){
        config.set(BULLET_MAX_SWING, max);
    }

    public void setDamage(double damage){
        config.set(DAMAGE, damage);
    }

    public void setTime(int time){
        config.set(TIME, time);
    }

    @Deprecated
    public void setReloadingItemType(Material type){
        config.set(RELOADING_ITEM_TYPE, type.name());
    }
}
