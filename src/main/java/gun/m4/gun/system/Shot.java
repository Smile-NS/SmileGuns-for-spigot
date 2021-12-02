package gun.m4.gun.system;

import gun.m4.Calculation;
import gun.m4.gun.GunConfig;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static org.bukkit.Material.CROSSBOW;

public class Shot extends Moving {

    private final PlayerInteractEvent event;
    private final double DISTANCE = GunConfig.getFlyingDistance();

    private int amount = 0;
    private ItemStack gun;

    private static final List<Material> notCollision = GunConfig.getNotCollision();
    private static final Material reloadingItemType = GunConfig.getReloadingItemType();

    private GunEffect effect;

    public Shot(Player player, Plugin plugin, PlayerInteractEvent event){
        super(player, plugin);
        this.event = event;
    }

    public void shot(){
        effect = new GunEffect(player, PLUGIN);
        gun = inv.getItemInMainHand();

        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (gun.getType() != CROSSBOW && gun.getType() != reloadingItemType) return;

        if (!GunConfig.isGun(gun)) return;
        event.setCancelled(true);

        if (Reload.playerMap.containsKey(player)) return;

        amount = GunConfig.getBulletAmount(gun);
        if (amount <= 0) {
            if (gun.getType() == CROSSBOW) effect.fellMagazine();
            new Reload(player, PLUGIN).normalReload(gun);
            return;
        }

        if(player.isSneaking()) GunConfig.setGun(gun, 2, GunConfig.getBulletAmount(gun));

        effect.flash();

        float volume = player.isSneaking() ? 0.5f : 1.0f;
        player.getWorld().playSound(player.getEyeLocation(), "guns.m4a1.shot-single", volume, 1.0f);
        taskList.add(this);
        if (!running) runTask();

        effect.recoil1();
        effect.recoil2();
    }

    final float swing = GunConfig.getBulletMaxSwing();
    final float power = 0.7f;

    @Override
    protected boolean tickTask() {
        if (amount <= 0) return false;
        if (next % 2 != 0) return true;

        bulletCtrl();

        Location loc = player.getEyeLocation();
        float ry = player.isSneaking() ? setSwing(swing * power) : setSwing(swing);
        loc.setYaw(loc.getYaw() + ry);

        float rp = player.isSneaking() ? setSwing(swing * power) : setSwing(swing);
        loc.setPitch(loc.getPitch() + rp);

        effect.blowBack();
        bulletMove(loc, GunConfig.getDamage());
        return next != GunConfig.getShotCount() * 2;
    }

    float setSwing(float sw){
        float result = (float) (Math.random() * sw);
        int degree = (int) (Math.random() * 2);
        return degree == 0 ? result * -1 : result;
    }

    void bulletMove(Location loc, double damage){

        for (int i = 0;i < DISTANCE * 5;i++){
            double dis = i * 0.2;
            Location hitLoc = Calculation.getAheadDirection(dis, loc, player.getWorld());
            Block block = player.getTargetBlockExact((int) dis);

            if (block != null) {
                effect.breakGlass(block);
                if (effect.isCollisionBlock(block)) {
                    effect.hitEffect(hitLoc);
                    return;
                }
            }

            List<Entity> entities = player.getWorld().getEntities();
            entities.remove(player);

            LivingEntity target = getNearest(hitLoc, entities);
            if (target == null) continue;
            if (isCollision(target, hitLoc)) {
                if (effect.hitToEntity(target, damage, hitLoc)) return;
            }
        }
    }

    void bulletCtrl(){
        amount--;
        GunConfig.setMetaWhenShooting(gun, amount);
        GunEffect.actionbar(String.valueOf(amount), player);
    }

    boolean isCollision(LivingEntity entity, Location check){
        World world = entity.getWorld();
        Location center = entity.getLocation();
        center.setY(center.getY() + (entity.getHeight() / 2));

        Location width = new Location(world, check.getX(), center.getY(), check.getZ());
        Location height = new Location(world, center.getX(), check.getY(), center.getZ());

        return width.distance(center) <= entity.getWidth() / 2 && height.distance(center) <= entity.getHeight() / 2;
    }

    LivingEntity getNearest(Location loc, List<Entity> entitiesList){
        LivingEntity result = null;

        if (entitiesList.size() == 1) {
            Entity entity = entitiesList.get(0);
            if (!(entity instanceof LivingEntity)) return null;
            return (LivingEntity) entity;
        }
        double lastDis = 10;

        for (Entity entity : entitiesList){
            if (!(entity instanceof LivingEntity)) continue;
            double dis = loc.distance(entity.getLocation());

            if (dis <= lastDis){
                lastDis = dis;
                result = (LivingEntity) entity;
            }
        }
        return result;
    }
}
