package screen;

import java.util.Random;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import engine.*;
import entity.*;
import resources.SoundEffectPlayer;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 6000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 3000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Set of all Special bullets fired by on screen ships. */
	private Set<SBullet> sbullets;
	/** Set of all Boss bullets fired by on screen ships. */
	private Set<BossBullet> Bossbullets;

	/** Current score. */
	private int score;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total Special bullets shot by the player. */
	private int SbulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	// item Types
	private Color items;

	/** Checks if game is paused. */
	private boolean isPaused;
	/** check if esc is pressed. for pauce function 2020088895 */
	private boolean escapePressed;


	//boss Enemy
	private EnemyShip BossEnemy;
	private BossEnemyFormation BossEnemyFormation;
	private Cooldown BossBattleUI;
	private Boolean BossBattleUISwitch =false;

	//effects
	private Cooldown invincible;
	private Cooldown dashCooldown;
	private Cooldown EndUiCooldown;
	private boolean LevelEnd = false;
	private Boolean  dasheffectleft = false;
	private Boolean  dasheffectright = false;
	private int SpeedLevel = 1;
	private int ShootCooldownLevel = 1;


	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState    Current game state.
	 * @param gameSettings Current game settings.
	 * @param bonnusLife   Checks if a bonus life is awarded this level.
	 * @param width        Screen width.
	 * @param height       Screen height.
	 * @param fps          Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState, final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.SbulletsShot = gameState.getSBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		this.ship = new Ship(this.width / 2, this.height - 70);
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);

		this.BossBattleUI = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.dashCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.dashCooldown.setCooldown(5000);
		this.dashCooldown.reset();
		this.invincible = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.invincible.setCooldown(300);
		this.invincible.reset();
		this.EndUiCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.EndUiCooldown.setCooldown(3000);
		this.EndUiCooldown.reset();

		this.bullets = new HashSet<Bullet>();
		this.sbullets = new HashSet<SBullet>();
		this.Bossbullets = new HashSet<BossBullet>();
		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		SoundEffectPlayer.backgroundsound("src\\resources\\backgroundbattle.wav");
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && !escapePressed) {
			escapePressed = true;
			if (this.isPaused == true) {
				this.isPaused = false;
			} else {
				this.isPaused = true;
			}
		} else if (!inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && escapePressed) {
			escapePressed = false;
		}
		super.update();

		if (!this.isPaused) {
			if (this.inputDelay.checkFinished() && !this.levelFinished) {

				if (!this.ship.isDestroyed()) {
					boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
							|| inputManager.isKeyDown(KeyEvent.VK_D);
					boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
							|| inputManager.isKeyDown(KeyEvent.VK_A);

					boolean dashleft = inputManager.isKeyDown(KeyEvent.VK_Q) ;
					boolean dashright = inputManager.isKeyDown(KeyEvent.VK_E) ;

					boolean isRightBorder = this.ship.getPositionX() + this.ship.getWidth()
							+ this.ship.getSpeed() > this.width - 1;
					boolean isLeftBorder = this.ship.getPositionX() - this.ship.getSpeed() < 1;

					if (moveRight && !isRightBorder && this.invincible.checkFinished()) {
						this.ship.moveRight();
					}
					if (moveLeft && !isLeftBorder && this.invincible.checkFinished()) {
						this.ship.moveLeft();
					}

					if (!isRightBorder && (dashright || !this.invincible.checkFinished())) {
						if (!this.invincible.checkFinished() && dasheffectright) {
							this.ship.setSpeed(5);
							this.ship.moveRight();

						}else if(this.dashCooldown.checkFinished()){
							this.dasheffectright=true;
							this.invincible.reset();
							this.dashCooldown.reset();
						}
					}else if(dasheffectright){
						this.ship.resetSpeed(SpeedLevel);
						this.dasheffectright = false;
					}

					if (!isLeftBorder && (dashleft || !this.invincible.checkFinished())) {
						if (!this.invincible.checkFinished() && dasheffectleft) {
							this.ship.setSpeed(5);
							this.ship.moveLeft();

						}else if(this.dashCooldown.checkFinished()){
							this.dasheffectleft=true;
							this.invincible.reset();
							this.dashCooldown.reset();
						}
					}else if(dasheffectleft){
						this.ship.resetSpeed(SpeedLevel);
						this.dasheffectleft = false;
					}




					if ((inputManager.isKeyDown(KeyEvent.VK_SPACE)) || (inputManager.isKeyDown(KeyEvent.VK_R))) {
						if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
							if (this.ship.shoot(this.bullets))
								this.bulletsShot++;
						} else {
							if (this.ship.sshoot(this.sbullets))
								this.SbulletsShot++;
						}
					}
				}

				if (this.enemyShipSpecial != null) {
					if (!this.enemyShipSpecial.isDestroyed())
						this.enemyShipSpecial.move(2, 0);
					else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
						this.enemyShipSpecial = null;

				}

				if (this.enemyShipSpecial == null
						&& this.enemyShipSpecialCooldown.checkFinished()) {

					//sp ship's color and effects
					Random r = new Random();
					int n = r.nextInt(3);
					if (n == 0) {
						this.enemyShipSpecial = new EnemyShip(Color.blue);
						items = Color.blue;
					} else if (n == 1) {
						this.enemyShipSpecial = new EnemyShip(Color.red);
						items = Color.red;
					} else {
						this.enemyShipSpecial = new EnemyShip(Color.yellow);
						items = Color.yellow;
					}
					this.enemyShipSpecialCooldown.reset();


					this.logger.info("A special ship appears");
				}
				if (this.enemyShipSpecial != null && this.enemyShipSpecial.getPositionX() > this.width) {
					this.enemyShipSpecial = null;
					this.logger.info("The special ship has escaped");
					this.enemyShipSpecialCooldown.reset();
				}
				this.ship.update();
				//boos formation  up
				if (!this.enemyShipFormation.isEmpty()) {
					this.enemyShipFormation.update();

					this.enemyShipFormation.shoot(this.bullets);
				} else if(!BossBattleUISwitch) {
						this.BossEnemyFormation.update();

						if(!this.LevelEnd)this.BossEnemyFormation.shoot(this.Bossbullets,this.BossEnemy.getEnemyLife(),BossEnemy.getPositionX(), BossEnemy.getPositionY());
				}
			}

			manageCollisions();
			smanageCollisions();
			BossmanageCollisions();

			cleanBossBullets();
			cleanBullets();
			cleanSBullets();
			draw();

		/*og level up
		if ((this.enemyShipFormation.isEmpty() || this.lives == 0) && !this.levelFinished) {
			this.levelFinished = true;

			//reset the functions
			this.ship.resetShootingCooldown();
			this.ship.resetSpeed();

			this.screenFinishedCooldown.reset();
		}
		*/

			//boss apears function
			if (this.lives == 0 && !this.levelFinished) {
				this.levelFinished = true;

				//reset the functions
				this.ship.resetShootingCooldown(ShootCooldownLevel);
				this.ship.resetSpeed(SpeedLevel);

				this.screenFinishedCooldown.reset();
			}

			if ((this.enemyShipFormation.isEmpty()) && !this.levelFinished) {
				if(BossEnemy == null) {
					if (!BossBattleUISwitch) {
						SoundEffectPlayer.backgroundsound("src\\resources\\Boss Battle.wav");
						this.BossBattleUI = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
						this.BossBattleUI.setCooldown(6000);
						this.BossBattleUI.reset();
						this.BossBattleUISwitch = true;

					} else {
						if (this.BossBattleUI.checkFinished()) {
							this.BossBattleUISwitch = false;
								this.BossEnemy = new EnemyShip(250, 500, DrawManager.SpriteType.EnemyShipSpecial);
								this.BossEnemy.setBossLive(level);
								this.BossEnemyFormation = new BossEnemyFormation(this.BossEnemy.getPositionX(), this.BossEnemy.getPositionY(),
										32, 32, 10, this.BossEnemy,level,this);
								this.logger.info("Boss ship appears");
						}
					}
				}
				cleanBullets();
			}
			if(this.BossEnemy != null) {

				if (this.BossEnemy.getEnemyLife() == 0 && !LevelEnd) {
					this.EndUiCooldown.reset();
					this.LevelEnd = true;
				} else if (LevelEnd && this.EndUiCooldown.checkFinished()) {
					this.BossEnemy = null;
					this.score += 300 * level;
					this.levelFinished = true;

				}
			}

			if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
				this.isRunning = false;

		}
		draw();
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private int InvincibleEffectDraw = 0;//invincible = 0;

	private void draw() {

		drawManager.initDrawing(this);

		if(!this.ship.getDestructionCooldown().checkFinished())drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());

		//invincibleEffect:ship ghost effect
		if (this.invincible.checkFinished() && this.ship.getDestructionCooldown().checkFinished()) {
			drawManager.drawPlayer(this, this.ship.getPositionX(), this.ship.getPositionY());
		}else if(!this.invincible.checkFinished()){

			if(InvincibleEffectDraw<=3){
				//drawManager.drawEntity(this.ship, this.ship.getPositionX(), this.ship.getPositionY());
				drawManager.drawPlayer(this, this.ship.getPositionX(), this.ship.getPositionY());
				InvincibleEffectDraw++;
			}else if(InvincibleEffectDraw >6){
				InvincibleEffectDraw = 0;
			}else{
				InvincibleEffectDraw++;
			}

		}


		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial, this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		 if(!enemyShipFormation.isEmpty()) {
			 enemyShipFormation.draw();
		 }
		 
		//draw new boss entity
		if (this.BossEnemy != null){

			if(!this.LevelEnd)drawManager.drawhp(this,this.BossEnemy.getPositionX(),this.BossEnemy.getPositionY(), this.level,this.BossEnemy.getEnemyLife());
			if(!this.LevelEnd)BossEnemyFormation.draw(this);
			for (BossBullet bullet : this.Bossbullets)
				drawManager.drawEntity(bullet, bullet.getPositionX(), bullet.getPositionY());
		}




		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(), bullet.getPositionY());


		for (SBullet sbullet : this.sbullets)
			drawManager.drawEntity(sbullet, sbullet.getPositionX(), sbullet.getPositionY());

		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawLives(this, this.lives);
		drawManager.drawdash(this,this.dashCooldown.checkFinished());
		drawManager.drawSpeedLevel(this,this.SpeedLevel);
		drawManager.drawShootlevel(this,this.ShootCooldownLevel);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown, this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);
		}

		drawManager.drawPauseScreen(this, isPaused);
		if(this.BossBattleUISwitch) {
			drawManager.drawBossMessage(this, BossBattleUI);
		}
		if(this.LevelEnd) {
			drawManager.drawLevelEnd(this);
		}


		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT || bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Cleans Special bullets that go off screen.
	 */
	private void cleanSBullets() {
		Set<SBullet> recyclable = new HashSet<SBullet>();
		for (SBullet sbullet : this.sbullets) {
			sbullet.update();
			if (sbullet.getPositionY() < SEPARATION_LINE_HEIGHT || sbullet.getPositionY() > this.height)
				recyclable.add(sbullet);
		}
		this.sbullets.removeAll(recyclable);
		SBulletPool.srecycle(recyclable);
	}

	/**
	 * Cleans Boss
	 * bullets that go off screen.
	 */
	private void cleanBossBullets() {
		Set<BossBullet> recyclable = new HashSet<BossBullet>();
		for (BossBullet bullet : this.Bossbullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT || bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.Bossbullets.removeAll(recyclable);
		BossBulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();

		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished && this.invincible.checkFinished()) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						this.lives--;

						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
						// Reset your ability when you are hit.
						if(this.SpeedLevel >0){this.SpeedLevel--;}
						if (this.ShootCooldownLevel >0){this.ShootCooldownLevel--;}
						this.ship.resetSpeed(SpeedLevel);
						this.ship.resetShootingCooldown(ShootCooldownLevel);

					}
				}
			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed() && checkCollision(bullet, enemyShip)) {
						this.score += enemyShip.getPointValue();
						this.shipsDestroyed++;
						this.enemyShipFormation.destroy(enemyShip);
						recyclable.add(bullet);
					}
				if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.score += this.enemyShipSpecial.getPointValue();

					// when sp enemy has been shoot,functions will be start.
					if (this.items == Color.red) {
						if(this.SpeedLevel <3){SpeedLevel++;}
						this.ship.setSpeed(SpeedLevel);

					} else if (this.items == Color.blue) {
						if (this.ShootCooldownLevel<3){
							this.ShootCooldownLevel++;
						}

						this.ship.setShootingCooldown(ShootCooldownLevel);
					} else {
						if(this.lives < 10) {
							this.lives++;
						}
					}

					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();

					recyclable.add(bullet);
				}

			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/*
	 * useless function
	 * public void shootitem(int x,int y){
	 * 
	 * bullets.add(BulletPool.getBullet(x, y, 3));
	 * }
	 */

	/**
	 * Manages collisions between Special bullets and ships.
	 */
	private void smanageCollisions() {
		Set<SBullet> srecyclable = new HashSet<SBullet>();

		for (SBullet sbullet : this.sbullets) {
			for (EnemyShip enemyShip : this.enemyShipFormation) {
				if (!enemyShip.isDestroyed() && scheckCollision(sbullet, enemyShip)) {
					this.score += enemyShip.getPointValue() + 10;
					this.shipsDestroyed++;
					this.enemyShipFormation.destroy(enemyShip);
					srecyclable.add(sbullet);
				}
			}
			if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed()
					&& scheckCollision(sbullet, this.enemyShipSpecial)) {
				this.score += this.enemyShipSpecial.getPointValue() + 100;
				this.shipsDestroyed++;
				this.enemyShipSpecial.destroy();
				this.enemyShipSpecialExplosionCooldown.reset();
				srecyclable.add(sbullet);
			}

			this.sbullets.removeAll(srecyclable);
			SBulletPool.srecycle(srecyclable);
		}
	}

	private void BossmanageCollisions() {
		Set<BossBullet> recyclable = new HashSet<BossBullet>();

		for (BossBullet bullet : this.Bossbullets) {
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished && this.invincible.checkFinished()) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						this.lives--;

						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
						//Reset your ability when you are hit.
						if(this.SpeedLevel >0){this.SpeedLevel--;}
						if (this.ShootCooldownLevel >0){this.ShootCooldownLevel--;}
						this.ship.resetSpeed(SpeedLevel);
						this.ship.resetShootingCooldown(ShootCooldownLevel);

					}
				}
			}
		}

		this.Bossbullets.removeAll(recyclable);

		Set<Bullet> recyclableNormal = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			if (this.BossEnemy != null) {

				if (!this.BossEnemy.isDestroyed() && checkCollision(bullet, this.BossEnemy)) {

					if(this.BossEnemy.getEnemyLife() >= 1) {
						this.BossEnemy.hitBoss();
						recyclableNormal.add(bullet);
					}


				}

			}
		}

		BossBulletPool.recycle(recyclable);
		BulletPool.recycle(recyclableNormal);
		this.bullets.removeAll(recyclableNormal);
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a First entity, the bullet.
	 * @param b Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a First entity, the bullet.
	 * @param b Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean scheckCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX + 30 && distanceY < maxDistanceY + 30;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives, this.bulletsShot, this.SbulletsShot,
				this.shipsDestroyed);
	}

}
