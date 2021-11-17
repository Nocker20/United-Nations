package entity;

import java.awt.Color;
import engine.DrawManager.SpriteType;

public class SBullet extends Entity {
    private int speed;

    public SBullet(final int positionX, final int positionY, final int width, final int height, int speed) {
        super(positionX, positionY, width, height, Color.RED);
        this.speed = speed;

        setSpecialSprite();

    }

    public final void setSpecialSprite() {
        this.spriteType = SpriteType.SpecialBullet;
    }

    /**
     * Updates the bullet's position.
     */
    public final void update() {
        this.positionY += this.speed;
    }

    /**
     * Setter of the speed of the bullet.
     * 
     * @param speed New speed of the bullet.
     */
    public final void setSpeed(final int speed) {
        this.speed = speed;
    }

    /**
     * Getter for the speed of the bullet.
     * 
     * @return Speed of the bullet.
     */
    public final int getSpeed() {
        return this.speed;
    }
}
