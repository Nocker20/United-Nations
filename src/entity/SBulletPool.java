package entity;

import java.util.HashSet;
import java.util.Set;

public class SBulletPool {

    private static Set<SBullet> pool2 = new HashSet<SBullet>();

    private SBulletPool() {

    }

    public static SBullet getSBullet(final int positionX, final int positionY, final int speed) {

        SBullet sbullet;

        if (!pool2.isEmpty()) {
            sbullet = pool2.iterator().next();
            pool2.remove(sbullet);
            sbullet.setPositionX(positionX - sbullet.getWidth() / 2);
            sbullet.setPositionY(positionY - 22);
            sbullet.setSpeed(speed);
            sbullet.setSpecialSprite();
        } else {
            sbullet = new SBullet(positionX, positionY, 2 * 5, 2 * 7, speed);
            sbullet.setPositionX(positionX - sbullet.getWidth() / 2);
        }
        return sbullet;
    }

    /**
     * Adds one or more bullets to the list of available ones.
     * 
     * @param sbullet Bullets to recycle.
     */
    public static void srecycle(final Set<SBullet> sbullet) {
        pool2.addAll(sbullet);
    }
}
