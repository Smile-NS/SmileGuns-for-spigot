package gun.m4.gun.system;

import gun.m4.Calculation;
import gun.m4.gun.GunConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.Material.*;

public class GunEffect extends Moving {
    
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static final Scoreboard board = manager.getMainScoreboard();

    public GunEffect(Player player, Plugin plugin){
        super(player, plugin);
    }

    public boolean isCollisionBlock(Block block){
        return !GunConfig.getNotCollision().contains(block.getType());
    }

    public void breakGlass(Block block){
        if (!GunConfig.getCanBreakBlock().contains(block.getType())) return;
        Location loc = block.getLocation();
        loc.getWorld().spawnParticle(
                Particle.BLOCK_CRACK, loc.add(0.5, 0.5, 0.5), 30, 0.3, 0.3, 0.3,0, block.getBlockData());
        loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1, 1);
        block.setType(AIR);
    }

    public void hitEffect(Location loc){
        List<Player> playerList = new ArrayList<>(getServer().getOnlinePlayers());
        World world = player.getWorld();
        for (Player listener : playerList) {
            if (world != listener.getWorld()) continue;
            double distance = loc.distance(listener.getLocation());
            if (distance > 5) continue;
            listener.playSound(loc, "guns.bullets-bounce", 0.5f, 1.0f);
        }

        loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 30, 0, 0.4, 0,0);
        loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 20, 0.2, 0.4, 0.2,0.7);
    }

    public void recoil1(){
        Location loc = player.getLocation();

        int random = (int) (Math.random() * 3);
        float yaw = loc.getYaw();
        if (random == 0) loc.setYaw(yaw + 1);
        else if (random == 1) loc.setYaw(yaw - 1);

        taskList.add(this);
        if (!running) runTask();
    }

    public void recoil2(){
        Location locA = player.getLocation();
        locA.setPitch(0);
        Location locB = Calculation.getAheadDirection(-3, locA, player.getWorld());

        Vector vec = Calculation.vectorCalculation(locA, locB, 7);
        player.setVelocity(vec);
    }

    public void flash(){
        Location loc = Calculation.getAheadDirection(1, player.getEyeLocation(), player.getWorld());
        player.spawnParticle(Particle.FLASH, loc, 1);
    }

    public static void actionbar(String display, Player player){
        TextComponent component = new TextComponent();
        component.setText("<<" + display + ">>");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public void fellMagazine(){
        Location locA = player.getLocation();
        locA.setY(locA.getY() + 1);
        locA.setPitch(-45);
        Location locB = Calculation.getAheadDirection(1, locA, locA.getWorld());
        Vector vec = Calculation.vectorCalculation(locA, locB, 20);

        ItemStack item = new ItemStack(CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(1);
        item.setItemMeta(meta);

        Item magazine = locA.getWorld().dropItem(locA, item);
        magazine.setPickupDelay(Integer.MAX_VALUE);
        magazine.setTicksLived(5940);
        magazine.setVelocity(vec);
    }

    public boolean hitToEntity(LivingEntity target, double damage, Location hitLoc){
        Team team = belongingTeam(player);
        if (target.isInvulnerable()) return false;
        if (team != null && inSameTeam(team, target)) return false;
        if (!enablePlayerDamage(target)) return false;

        target.damage(0.01, player);

        double health = target.getHealth();
        double woundedHealth = health > damage ? health - damage : 0;
        target.setHealth(woundedHealth);

        target.getWorld()
                .spawnParticle(
                        Particle.ITEM_CRACK, hitLoc, 15, 0.2, 0.2, 0.2,0.1, new ItemStack(blood(target))
                );
        return true;
    }

    private boolean enablePlayerDamage(LivingEntity entity){
        if (!(entity instanceof Player)) return true;
        Player target = (Player) entity;
        GameMode gameMode = target.getGameMode();

        return gameMode != GameMode.SPECTATOR && gameMode != GameMode.CREATIVE;
    }

    private Material blood(LivingEntity target){
        Material type = REDSTONE_BLOCK;

        switch (target.getType()){
            case SLIME:
                type = SLIME_BLOCK;
                break;
            case SKELETON:
            case SKELETON_HORSE:
            case STRAY:
                type = BONE_BLOCK;
                break;
            case WITHER_SKELETON:
            case WITHER:
                type = COAL_BLOCK;
                break;
            case SNOWMAN:
                type = SNOW_BLOCK;
                break;
            case GHAST:
                type = WHITE_STAINED_GLASS;
                break;
        }
        return type;
    }

    private static Team belongingTeam(Player player){
        Set<Team> teamList = board.getTeams();
        for (Team team : teamList){
            if (team.hasEntry(player.getDisplayName())) return team;
        }
        return null;
    }

    private static boolean inSameTeam(Team team, LivingEntity target){
        if (team.hasEntry(target.getUniqueId().toString())) return true;
        return target instanceof Player && team.hasEntry(((Player) target).getDisplayName());
    }

    public void blowBack(){
        Location spawnLoc = player.getEyeLocation();
        spawnLoc.setY(spawnLoc.getY() - 0.5);
        spawnLoc.setYaw(spawnLoc.getYaw() + 40);
        spawnLoc = Calculation.getAheadDirection(0.7, spawnLoc, spawnLoc.getWorld());

        ItemStack item = new ItemStack(CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(2);
        item.setItemMeta(meta);
        Item bullet = spawnLoc.getWorld().dropItem(spawnLoc, item);
        bullet.setPickupDelay(Integer.MAX_VALUE);

        Location locA = bullet.getLocation();
        locA.setYaw(player.getLocation().getYaw());
        locA.setPitch(45);
        Location locB = Calculation.getAheadDirection(-1.5, locA, locA.getWorld());

        Vector vec = Calculation.vectorCalculation(locA, locB, 30);
        bullet.setVelocity(vec);
        bullet.setTicksLived(5985);
    }

    @Override
    protected boolean tickTask() {
        if (next % 2 != 0) return true;
        Location loc = player.getLocation();

        float pitch = loc.getPitch();
        if (next == 2) loc.setPitch(pitch - 2);
        else if (next == 4) loc.setPitch(pitch + 2);
        else if (next == 6) return false;

        Vector vec = player.getVelocity().clone();
        player.teleport(loc);
        player.setVelocity(vec);
        return true;
    }
}
