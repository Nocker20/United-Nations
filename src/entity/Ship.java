package entity;

import java.util.Set;
import java.awt.*;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import resources.SoundEffectPlayer;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {

	/** Time between shots. */
	private static final int SHOOTING_INTERVAL = 750;
	/** Speed of the bullets shot by the ship. */
	private static final int BULLET_SPEED = -6;
	/** Time between Special shots. */
	private static final int SSHOOTING_INTERVAL = 10000;
	/** Speed of the Special bullets shot by the ship. */
	private static final int SBULLET_SPEED = -4;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;

	private int SpeedUp;

	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Minimum time between Special shots. */
	private Cooldown SshootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX Initial position of the ship in the X axis.
	 * @param positionY Initial position of the ship in the Y axis.
	 */
	public Ship(final int positionX, final int positionY) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.SshootingCooldown = Core.getCooldown(SSHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		this.positionX = this.positionX + SPEED + SpeedUp;
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is reached.
	 */
	public final void moveLeft() {
		this.positionX = this.positionX - SPEED - SpeedUp;
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished()) {
			SoundEffectPlayer.sound("src\\resources\\laser2.wav");
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX + this.width / 2, positionY, BULLET_SPEED));
			return true;
		}
		return false;
	}

	public final boolean sshoot(final Set<SBullet> sbullets) {
		if (this.SshootingCooldown.checkFinished()) {
			SoundEffectPlayer.sound("src\\resources\\nc_detec.wav");
			this.SshootingCooldown.reset();
			sbullets.add(SBulletPool.getSBullet(positionX + this.width / 2, positionY + 20, SBULLET_SPEED));
			return true;
		}
		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else
			this.spriteType = SpriteType.Ship;
	}

	/**
	 * Switches the ship to its destroyed state.
	 * 
	 * @throws InterruptedException
	 */
	public final void destroy() {
		SoundEffectPlayer.sound("src\\resources\\explosion.wav");
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return SPEED;
	}

	public final void setSpeed() {
		this.SpeedUp = 5;

	}

	public final void resetSpeed() {
		this.SpeedUp = 0;

	}

	// functions , set the shootingcooldown
	public final void setShootingCooldown() {

		this.shootingCooldown = Core.getCooldown(100);

	}

	public final void resetShootingCooldown() {

		this.shootingCooldown = Core.getCooldown(750);

	}
}
