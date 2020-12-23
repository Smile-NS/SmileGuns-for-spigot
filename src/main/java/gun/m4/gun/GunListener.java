package gun.m4.gun;

import gun.m4.Calculation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

import static org.bukkit.Material.CROSSBOW;

public class GunListener extends GunCreate{

    private final PlayerInteractEvent event;
    private final Player player;
    private final PlayerInventory inv;
    private final Reload reload;
    private final double DISTANCE = GunCreate.getFlyingDistance();

    private int amount = 0;
    private ItemStack gun;

    private static final List<Material> notCollision = GunCreate.getNotCollision();
    private static final Material reloadingItemType = GunCreate.getReloadingItemType();

    private GunEffect effect;

    public GunListener(Player player, PlayerInteractEvent event){
        this.event = event;
        this.player = player;
        inv = player.getInventory();
        reload = new Reload(player);
    }

    public void shot(){
        effect = new GunEffect(player, event);
        gun = inv.getItemInMainHand();

        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (gun.getType() != CROSSBOW && gun.getType() != reloadingItemType) return;

        if (!GunCreate.isGun(gun)) return;
        event.setCancelled(true);

        if (Reload.isReloading.containsKey(player)) return;

       amount = getBulletAmount(gun);
        if (amount <= 0) {
            if (gun.getType() == CROSSBOW) effect.fellMagazine();
            reload.normalReload(GunCreate.getBulletMaxAmount(), gun);
            return;
        }

        if(player.isSneaking()) GunCreate.setGun(gun, 2, getBulletAmount(gun));

        effect.flash();
        shotTimer(GunCreate.getShotCount(), GunCreate.getBulletMaxSwing());

        effect.recoil1();
        effect.recoil2();
    }

    private void shotTimer(final int COUNT, final int SWING){
        float volume = player.isSneaking() ? 0.5f : 1.0f;
        float power = 0.7f;
        player.getWorld().playSound(player.getEyeLocation(), "guns.m4a1.shot-single", volume, 1.0f);

        BukkitRunnable task = new BukkitRunnable() {
            private int next = 0;

            public void run() {
                next++;
                if (amount <= 0) {
                    cancel();
                    return;
                }
                bulletCtrl();

                Location loc = player.getEyeLocation();
                float ry = player.isSneaking() ? setSwing(SWING * power) : setSwing(SWING);
                loc.setYaw(loc.getYaw() + ry);

                float rp = player.isSneaking() ? setSwing(SWING * power) : setSwing(SWING);
                loc.setPitch(loc.getPitch() + rp);

                effect.blowBack();
                bulletMove(loc, GunCreate.getDamage());
                if (next == COUNT) cancel();
            }
        };
        task.runTaskTimer(PLUGIN, 0L, 2L);
    }

    private void bulletCtrl(){
        amount--;
        GunCreate.setMetaWhenShooting(gun, amount);
        GunEffect.actionbar(String.valueOf(amount), player);
    }

    private float setSwing(float sw){
        float result = (float) (Math.random() * sw);
        int degree = (int) (Math.random() * 2);
        return degree == 0 ? result * -1 : result;
    }

    private void bulletMove(Location loc, double damage){

        for (int i = 0;i < DISTANCE * 5;i++){
            Location hitLoc = Calculation.getAheadDirection(i * 0.2, loc, player.getWorld());
            Block block = hitLoc.getBlock();

            effect.breakGlass(block);
            if (effect.isCollisionBlock(block)) {
                effect.hitEffect(hitLoc);
                return;
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

    private boolean isCollision(LivingEntity entity, Location check){
        World world = entity.getWorld();
        Location center = entity.getLocation();
        center.setY(center.getY() + (entity.getHeight() / 2));

        Location width = new Location(world, check.getX(), center.getY(), check.getZ());
        Location height = new Location(world, center.getX(), check.getY(), center.getZ());

        return width.distance(center) <= entity.getWidth() / 2 && height.distance(center) <= entity.getHeight() / 2;
    }

    private LivingEntity getNearest(Location loc, List<Entity> entitiesList){
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

    public static void cancelChangingHand(PlayerSwapHandItemsEvent event, Player player){
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (GunCreate.isGun(gun)) event.setCancelled(true);
    }

    public static void cancelChangingHand(InventoryClickEvent event, Player player){
        ItemStack item = event.getCursor();
        Inventory inv = event.getClickedInventory();

        if (event.getSlot() != 40 || !GunCreate.isGun(Objects.requireNonNull(item))) return;
        event.setCancelled(true);

        if (inv.firstEmpty() == -1) player.getWorld().dropItem(player.getLocation(), item);
        event.setCurrentItem(null);
        inv.addItem(item);
    }
}
