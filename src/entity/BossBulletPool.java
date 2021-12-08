package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a pool of recyclable bullets.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class BossBulletPool {

    /**
     * Set of already created bullets.
     */
    private static Set<BossBullet> pool = new HashSet<BossBullet>();


    /**
     * Returns a bullet from the pool if one is available, a new one if there isn't.
     *
     * @param positionX Requested position of the bullet in the X axis.
     * @param positionY Requested position of the bullet in the Y axis.
     * @param speed     Requested speed of the bullet, positive or negative
     *                  depending on direction - positive is down.
     * @return Requested bullet.
     */
    public static BossBullet getBullet(final int positionX, final int positionY, final int speed,String Type) {
        BossBullet bullet;

        if (!pool.isEmpty()) {
            bullet = pool.iterator().next();
            bullet.setformations(Type);
            pool.remove(bullet);
            bullet.setPositionX(positionX - bullet.getWidth() / 2);
            bullet.setPositionY(positionY - 22);
            bullet.setSpeed(speed);
            bullet.setSprite();
        } else {
            bullet = new BossBullet(positionX, positionY, 6, 8, speed);
            bullet.setformations(Type);
            bullet.setPositionX(positionX - bullet.getWidth() / 2);
        }
        return bullet;

    }

    /**
     * Adds one or more bullets to the list of available ones.
     *
     * @param bullet Bullets to recycle.
     */
    public static void recycle ( final Set<BossBullet> bullet){
        pool.addAll(bullet);
    }
}

