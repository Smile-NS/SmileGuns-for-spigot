package gun.m4;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Calculation {

    public static Location getAheadDirection(double distance, Location loc, World world){
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        double yaw = loc.getYaw();
        double pitch = loc.getPitch();

        double x1 = 0 - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * distance;
        double z1 = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * distance;
        double y1 = Math.sin(Math.toRadians(pitch)) * distance * -1;

        return new Location(world, x + x1, y + y1, z + z1);
    }

    public static Vector vectorCalculation(Location locA, Location locB, int magnification){
        double xA = locA.getX();
        double yA = locA.getY();
        double zA = locA.getZ();

        double xB = locB.getX();
        double yB = locB.getY();
        double zB = locB.getZ();

        double resX = ( xB - xA ) * 0.01;
        double resY = ( yB - yA ) * 0.01;
        double resZ = ( zB - zA ) * 0.01;

        return new Vector(resX * magnification, resY * magnification, resZ * magnification);
    }
}
