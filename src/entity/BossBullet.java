package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class BossBullet extends Entity {

    /**
     * Speed of the bullet, positive or negative depending on direction - positive
     * is down.
     */
    private int speed;
    private String formation;


    /**
     * Constructor, establishes the bullet's properties.
     *
     * @param positionX Initial position of the bullet in the X axis.
     * @param positionY Initial position of the bullet in the Y axis.
     * @param speed     Speed of the bullet, positive or negative depending on
     *                  direction - positive is down.
     */
    public BossBullet(final int positionX, final int positionY, final int width, final int height, final int speed) {
        super(positionX, positionY, width, height, Color.red);

        this.speed = speed;
        setSprite();
    }

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     * @param width     Width of the entity.
     * @param height    Height of the entity.
     * @param color     Color of the entity.
     */
    public BossBullet(int positionX, int positionY, int width, int height, Color color) {
        super(positionX, positionY, width, height, color);
    }

    /**
     * Sets correct sprite for the bullet, based on speed.
     */
    public final void setSprite() {
        if (speed < 0)
            this.spriteType = SpriteType.Bullet;
        else
            this.spriteType = SpriteType.EnemyBullet;
    }

    /**
     * Updates the bullet's position.
     */
    public final void update() {

        if(this.formation == "right"){
            this.positionY += this.speed;
            this.positionX += this.speed / 2;
        }else if(this.formation == "left"){
            this.positionY += this.speed;
            this.positionX -= this.speed / 2;
        } else{
            this.positionY += this.speed;
        }
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

    public final void setformations(String formation){
        this.formation = formation;

    }
}
