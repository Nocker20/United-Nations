package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;
import resources.SoundEffectPlayer;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Bullet extends Entity {

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
	public Bullet(final int positionX, final int positionY, final int width, final int height, final int speed) {
		super(positionX, positionY, width, height, Color.WHITE);

		this.speed = speed;
		setSprite();
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		if (speed < 0)
			this.spriteType = SpriteType.Bullet;
		else {
			SoundEffectPlayer.sound("src\\resources\\laser1.wav");
			this.spriteType = SpriteType.EnemyBullet;
		}
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {

		if(this.formation == "Bossright"){
			this.positionY += this.speed;
			this.positionX += this.speed / 2;
		}else if(this.formation == "Bossleft"){
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
