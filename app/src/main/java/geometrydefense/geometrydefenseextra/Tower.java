package geometrydefense.geometrydefenseextra;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.ArrayList;

public class Tower{
    private Bitmap towerImage;
    private Bitmap projImage,projImage2;
    private Point position;
    private int damage;
    private double distance;
    private int cooldown;
    private Level level;
    private int firerate;
    private int range;
    private int value;
    private boolean selected=false;


    public Tower(Point position, int damage, int firerate, int range, Bitmap towerImage, Bitmap projImage,Bitmap projImage2, Level level) {
        this.level = level;
        this.towerImage = towerImage;
        this.position = position;
        this.damage = damage;
        this.firerate = firerate;
        this.projImage = projImage;
        this.projImage2 = projImage2;
        this.range = range;
        cooldown = 0;
        this.value=70;
    }

    public double calculateDistance(Enemy target){
        Point targetPosition = target.getPosition();

        distance = (position.x - targetPosition.x) * (position.x - targetPosition.x) + (position.y - targetPosition.y) * (position.y - targetPosition.y);
        distance = Math.sqrt(distance);

        return distance;
    }

    public void attack(Enemy target){
        Bitmap img;
        if(Math.random()>.5){
            img = projImage;
        }else{
            img = projImage2;
        }
        Projectile projectile = new Projectile(img, new Point(this.position),target,20,this.damage,this.level);
        this.level.addProjectile(projectile);
    }

    public void update(){


        if(cooldown > 0){
            cooldown--;
            return;
        }
        ArrayList<Enemy> enemyList = level.getEnemiesOnScreen();

        if (cooldown ==0){
            for(Enemy enemy:enemyList){
                if (cooldown > 0) return;

                distance = calculateDistance(enemy);
                if(distance <= range && cooldown == 0){
                    attack(enemy);
                    cooldown = firerate;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.towerImage,level.scalePointW(this.position.x)-this.towerImage.getWidth()/2,level.scalePointH(this.position.y)-this.towerImage.getHeight()/2,null);
        //if displayrange is true draw the range of the tower
        if(this.selected){
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            p.setStyle(Paint.Style.FILL);
            p.setAlpha(130);
            int rangeX=level.scalePointW(this.position.x-this.range);
            int rangeY=level.scalePointH(this.position.y-this.range);
            canvas.drawOval(new RectF(rangeX,rangeY,rangeX+2*level.scalePointW(this.range),rangeY+2*level.scalePointH(this.range)),p);
        }
    }

    public int getValue(){
        //get actual value when upgrades implemented
        return this.value;
    }

    public void toggleSelected(){
        this.selected=!this.selected;
    }

    //used to update upgrade menu in levelactivity class
    //upgradeType specifies which upgrade bonuses to pass to menu, 0-no upgrade(bonuses all=0), 1-damage, 2-firerate, 3-range
    //all values are hardcoded and static for now, but keep this method rather than move it to activity class so they can be changed to dynamic upgrades later
    public void updateMenu(LevelActivity a, int upgradeType){
        if(upgradeType==0){
            //no upgrade selected-no stat bonuses
            a.updateMenu(100,100,100,this.damage,this.firerate,this.range,0,0,0,this.value,0);
        }else if(upgradeType==1){
            //damage upgrade
            a.updateMenu(100,100,100,this.damage,this.firerate,this.range,20,0,0,this.value,70);
        }else if(upgradeType==2){
            //firerate upgrade
            a.updateMenu(100,100,100,this.damage,this.firerate,this.range,0,-5,0,this.value,70);
        }else if(upgradeType==3){
            //range upgrade
            a.updateMenu(100,100,100,this.damage,this.firerate,this.range,0,0,30,this.value,70);
        }
    }

    public void upgrade(int upgradeType){
        this.value+=70;
        if(upgradeType==1){
            this.damage+=20;
        }else if(upgradeType==2){
            this.firerate-=5;
            if(firerate<0){
                firerate=0;
            }
        }else if(upgradeType==3){
            this.range+=30;
        }

    }

    public void setTowerImage(Bitmap image){
        this.towerImage=image;
    }

    public Bitmap getTowerImage() {
        return towerImage;
    }

    public Bitmap getProjImage() {
        return projImage;
    }

    public Point getPosition() {
        return position;
    }

    public int getDamage() {
        return damage;
    }

    public double getDistance() {
        return distance;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Level getLevel() {
        return level;
    }

    public int getFirerate() {
        return firerate;
    }

    public int getRange() {
        return range;
    }
}
