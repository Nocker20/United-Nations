package entity;

import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import screen.Screen;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.DrawManager.SpriteType;


public class BossEnemyFormation extends Entity {
    private int speed;
    private int level;
    private EnemyShip BossShip;
    private DrawManager drawManager;
    private int movecooldown = 60;
    private int randomX;
    private int randomY;
    private Cooldown shootingCooldown;
    private Cooldown UiCoolDawn;
    private int  width;
    private int height;
    private Boolean BossModeChange =true;
    private Screen screen;
    private boolean abc;


    public BossEnemyFormation(final int positionX, final int positionY, final int width, final int height, final int speed,EnemyShip E,int level,Screen screen) {
        super(positionX, positionY, width, height, Color.green);
        this.speed = speed;
        this.BossShip = E;
        this.shootingCooldown = Core.getCooldown(1000);
        this.shootingCooldown.setCooldown(800 - level *50);
        this.shootingCooldown.reset();
        this.UiCoolDawn = Core.getCooldown(1000);
        this.UiCoolDawn.setCooldown(4000);
        this.UiCoolDawn.reset();
        this.width = width;
        this.height = height;
        this.level = level;


        setSpecialSprite();


    }

    public final void draw(Screen Screen) {
                this.drawManager = Core.getDrawManager();
                drawManager.drawBossEnemy(Screen, BossShip.getPositionX(),
                        BossShip.getPositionY());
                        if(abc) {
                            drawManager.drawBossBattleModeChange(Screen);
                        }
    }

    public final void setSpecialSprite() {
        this.spriteType = SpriteType.Ship;
    }


    //boss formation
    public final void update() {

        int[] nums = { 1 ,-1 };

        if(movecooldown == 60){
            movecooldown = 0;
            randomX =nums[ (int) (Math.random() * nums.length)];
            randomY =nums[ (int) (Math.random() * nums.length)];
            if(this.BossShip.getPositionY() > Core.getHight() / 2){
                randomY = -1;
            }else if(this.BossShip.getPositionY() < Core.getHight() / 10){
                randomY =1;
            }

            if(this.BossShip.getPositionX() < Core.getWidth() / 10){
                randomX = 1;
            }else if(this.BossShip.getPositionX() > Core.getWidth()* 9 / 10 ){
                randomX = -1;
            }
            System.out.print(this.BossShip.getPositionX());
            System.out.println(this.BossShip.getPositionY());

        }else{
            this.BossShip.move( randomX,randomY);
            movecooldown++;

        }




    }

    public final boolean shoot(final Set<BossBullet> bullets,int life,int BossPositionX,int BoosPositionY) {
        if(this.shootingCooldown.checkFinished() && life > (5 + level * 5)/2){
            Random r = new Random();
            int n = r.nextInt(3) - 3;
            int b = 3 - r.nextInt(3) ;
            this.shootingCooldown.reset();
            bullets.add(BossBulletPool.getBullet(BossPositionX + this.width / 2, BoosPositionY + this.height, n,"random"));
            bullets.add(BossBulletPool.getBullet(BossPositionX + this.width / 2, BoosPositionY + this.height, b,"random"));


            if(life == (5 + level * 5)/ 2 + 1){
                this.UiCoolDawn.reset();
            }

        } else if(this.shootingCooldown.checkFinished() && life <= (5 + level * 5)/2 && this.BossModeChange){
            drawBattleChangeUi();


        } else if (this.shootingCooldown.checkFinished() && life <= (5 + level * 5)/2) {

                this.shootingCooldown.reset();
                bullets.add(BossBulletPool.getBullet(BossPositionX, BoosPositionY + this.height, 3, "left"));
                bullets.add(BossBulletPool.getBullet(BossPositionX + this.width / 2, BoosPositionY + this.height, 3, "mid"));
                bullets.add(BossBulletPool.getBullet(BossPositionX + this.width, BoosPositionY + this.height, 3, "right"));

                bullets.add(BossBulletPool.getBullet(BossPositionX, BoosPositionY + this.height, -3, "left"));
                bullets.add(BossBulletPool.getBullet(BossPositionX + this.width / 2, BoosPositionY + this.height, -3, "mid"));
                bullets.add(BossBulletPool.getBullet(BossPositionX + this.width, BoosPositionY + this.height, -3, "right"));
                return true;

        }
        return false;
    }

    public void drawBattleChangeUi(){

        if(!this.UiCoolDawn.checkFinished()){
            abc = true;
            this.BossShip.setEnemyInvincible(true);
        }else {
            this.BossModeChange = false;
            this.abc= false;
            this.BossShip.setEnemyInvincible(false);
        }

    }
}
