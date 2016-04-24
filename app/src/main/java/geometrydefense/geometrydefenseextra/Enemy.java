package geometrydefense.geometrydefenseextra;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;


public class Enemy {
    private double hitpoints;
    private double maxhitpoints;
    private double armor;
    private double speed;
    private int value;
    private double timeAlive;
    private Bitmap sprite;
    private final ArrayList<Point> path;
    private int targetpoint;
    private Point position;
    private Level level;
    private int imageCounter;
    private int originalWidth;
    private int originalHeight;
    private Bitmap originalSprite;
    private int angle=0;
    private Matrix matrix = new Matrix();



    public Enemy(double hitpoints, double armor, double speed,int value,Bitmap sprite, ArrayList<Point> path,Level level) {
        this.hitpoints = hitpoints;
        this.armor = armor;
        this.speed = speed;
        this.value = value;
        this.sprite = sprite;
        this.path = path;
        this.level=level;
        this.targetpoint=1;
        this.maxhitpoints=hitpoints;
        this.position=new Point(path.get(0).x,path.get(0).y);
        this.imageCounter = 0;
        this.originalWidth = sprite.getWidth();
        this.originalHeight = sprite.getHeight();
        this.originalSprite = sprite;
    }

    private void animateEnemy(){
        int width = sprite.getWidth();
        int height = sprite.getHeight();
        double scaleWidth = 0;
        double scaleHeight = 0;

        if(imageCounter == 9){ //reset
            imageCounter = 0;
            sprite = Bitmap.createScaledBitmap(originalSprite, originalWidth, originalHeight, false);
            return;
        }

        if(imageCounter < 5) {
            scaleWidth = width * .99;
            scaleHeight = height * .99;
            imageCounter++;
        }else{
            scaleWidth = width * 1.01;
            scaleHeight = height * 1.01;
            imageCounter++;
        }

        sprite = Bitmap.createScaledBitmap(sprite, (int) scaleWidth, (int) scaleHeight, false);
    }

    public double getSpeed() {
        return speed;
    }

    public double getHitpoints() {
        return hitpoints;
    }

    public double getArmor() {
        return armor;
    }

    public int getValue() {
        return value;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public ArrayList<Point> getPath() {
        return path;
    }

    public Point getPosition(){
        return this.position;
    }

    public void update(){
        //animateEnemy();
        //move at speed in x direction and y direction towards next point
        //take the sign of the difference of position and target to get direction
        this.position.x+=Math.signum(this.path.get(targetpoint).x - this.position.x)*speed;
        this.position.y+=Math.signum(this.path.get(targetpoint).y-this.position.y)*speed;
        if(distance(this.position,this.path.get(targetpoint))<=speed){
            this.position.x=this.path.get(targetpoint).x;
            this.position.y=this.path.get(targetpoint).y;
            //if the target point is the last, the enemy has reach the end
            if(targetpoint==this.path.size()-1){
                endOfPath();
            }else {
                this.targetpoint += 1;
            }
        }
    }

    public double distance(Point p1, Point p2){
        return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
    }

    public void draw(Canvas canvas){
        //draw self
        angle += Math.random()*10+10;
        matrix.reset();
        matrix.postRotate(angle, this.sprite.getWidth()/2,this.sprite.getHeight()/2);
        float px = level.scalePointW(this.position.x) - this.originalWidth / 2;
        float py = level.scalePointH(this.position.y) - this.originalHeight / 2;
        matrix.postTranslate(px,py);

        canvas.drawBitmap(this.sprite, matrix, null);


        //draw healthbar if damaged
        if(this.hitpoints<this.maxhitpoints){
            Paint p = new Paint();
            p.setColor(Color.GREEN);
            int left=level.scalePointW(this.position.x)-this.originalWidth/2;
            int right=left+this.originalWidth;
            int mid=(int)((right-left)*hitpoints/maxhitpoints)+left;
            int top=level.scalePointH(this.position.y)-4;
            canvas.drawRect(left, top, mid, top + 8, p);
            p.setColor(Color.RED);
            canvas.drawRect(mid,top,right,top+8,p);
        }

    }

    public void killed(){
        this.level.updateGold(this.value);
        this.level.remove(this);
    }

    public void projectileHit(double damage){
        //armor will be a percentage between 0(does not reduce damage at all) and 1(negates all damage)
        this.hitpoints-=damage*(1-armor);
        if(this.hitpoints<=0){
            this.killed();
        }
    }

    public void endOfPath(){
        this.level.loselife(this);
        this.level.remove(this);
    }

}
