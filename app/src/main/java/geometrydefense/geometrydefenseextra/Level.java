package geometrydefense.geometrydefenseextra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;


public class Level extends SurfaceView implements SurfaceHolder.Callback{
    private LevelThread thread;
    private int levelID;
    private BitmapFactory.Options options = new BitmapFactory.Options();
    private Bitmap levelMap, towerImage, projImage,normEnemyImage, fastEnemyImage,slowEnemyImage,damageTowerImage,firerateTowerImage,rangeTowerImage, lastwaveImage, hitmarker, projImage2,bad;
    private ArrayList<Tower> towersOnScreen = new ArrayList<Tower>();
    private ArrayList<Enemy> enemiesOnScreen = new ArrayList<Enemy>();
    private ArrayList<Projectile> projOnScreen = new ArrayList<Projectile>();
    private ArrayList<Tower> destroyTower = new ArrayList<Tower>(); // keep track of list of objects to remove from screen since they cant be removed while looping though list
    private ArrayList<Enemy> deadEnemy=new ArrayList<Enemy>();
    private ArrayList<Projectile> destroyProj = new ArrayList<Projectile>();
    private int gold=500;
    private int lives=10;
    private int towerCost = 100;
    private double timetoNextWave=-1;     //number of ticks before wave is sent at 30 fps
    private double timetoNextEnemy=0;     //number of ticks before next enemy of a wave is sent
    private int enemiesLeft=0;      //number of enemies remaining in wave
    private int enemyWave=-1;     //current enemy wave number-used to choose which enemy to spawn from list
    private ArrayList<Enemy> enemyWaves = new ArrayList<Enemy>();
    private ArrayList<Point> enemyPath = new ArrayList<Point>();
    private Tower selectedTower=null;  //keep track of which tower is selected for upgrademenu
    private LevelActivity activity;     //keep the creating activity to keep track of ui elements
    private boolean buyMode=false;  //boolean to keep track of if a tower is being placed -resets after placing tower
    private boolean sellMode = false; //same as buyMode except for selling an existing tower instead
    private Point buyTowerPoint;    //Point to keep track of where the user is hovering to place their tower-used to draw tower
    private int dstWidth = getResources().getDisplayMetrics().widthPixels;
    private int dstHeight = (int)((getResources().getDisplayMetrics().heightPixels)*.8125);
    private int bgWidth = 540;
    private int bgHieght = 780;
    private double xScale=(double)dstWidth/bgWidth;
    private double yScale=(double)dstHeight/bgHieght;
    private ArrayList<MediaPlayer> activePlayers = new ArrayList<MediaPlayer>();
    private int timeout=-1;
    private boolean status=false;

    public Bitmap getHitmarker(){
        return this.hitmarker;
    }

    public Level(LevelActivity activity, int levelID){
        super(activity);
        this.activity=activity;
        this.levelID=levelID;
        // adding the callback (this) to the surface holder to intercept event
        getHolder().addCallback(this);
        setFocusable(true);
        //create new thread to start running the level
        thread = new LevelThread(getHolder(), this);
        //load level background
        options.inScaled = false;



        //get and scale sprites
        towerImage = BitmapFactory.decodeResource(getResources(), R.drawable.normaltower, options);
        towerImage = Bitmap.createScaledBitmap(towerImage, (int) (towerImage.getWidth() * xScale), (int) (towerImage.getHeight() * yScale), true);
        damageTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.damagetower, options);
        damageTowerImage = Bitmap.createScaledBitmap(damageTowerImage, (int) (damageTowerImage.getWidth() * xScale), (int) (damageTowerImage.getHeight() * yScale), true);
        firerateTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.fireratetower, options);
        firerateTowerImage = Bitmap.createScaledBitmap(firerateTowerImage, (int) (firerateTowerImage.getWidth() * xScale), (int) (firerateTowerImage.getHeight() * yScale), true);
        rangeTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.rangetower, options);
        rangeTowerImage = Bitmap.createScaledBitmap(rangeTowerImage, (int) (rangeTowerImage.getWidth() * xScale), (int) (rangeTowerImage.getHeight() * yScale), true);
        projImage = BitmapFactory.decodeResource(getResources(), R.drawable.dorito, options);
        projImage = Bitmap.createScaledBitmap(projImage, (int) (projImage.getWidth() * xScale), (int) (projImage.getHeight() * yScale), true);
        projImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.dew, options);
        projImage2 = Bitmap.createScaledBitmap(projImage2, (int) (projImage2.getWidth() * xScale), (int) (projImage2.getHeight() * yScale), true);
        normEnemyImage = BitmapFactory.decodeResource(getResources(), R.drawable.illuminati, options);
        normEnemyImage = Bitmap.createScaledBitmap(normEnemyImage, (int) (normEnemyImage.getWidth() * xScale), (int) (normEnemyImage.getHeight() * yScale), true);
        slowEnemyImage = BitmapFactory.decodeResource(getResources(), R.drawable.shrek, options);
        slowEnemyImage = Bitmap.createScaledBitmap(slowEnemyImage, (int) (slowEnemyImage.getWidth() * xScale), (int) (slowEnemyImage.getHeight() * yScale), true);
        fastEnemyImage = BitmapFactory.decodeResource(getResources(), R.drawable.sanic, options);
        fastEnemyImage = Bitmap.createScaledBitmap(fastEnemyImage, (int) (fastEnemyImage.getWidth() * xScale) / (fastEnemyImage.getWidth() / 40), (int) (fastEnemyImage.getHeight() * yScale) / (fastEnemyImage.getHeight() / 40), true);
        lastwaveImage = BitmapFactory.decodeResource(getResources(), R.drawable.lastwave, options);
        lastwaveImage = Bitmap.createScaledBitmap(lastwaveImage, (int) (lastwaveImage.getWidth() * xScale), (int) (lastwaveImage.getHeight() * yScale), true);
        hitmarker = BitmapFactory.decodeResource(getResources(), R.drawable.hitmarker, options);
        hitmarker = Bitmap.createScaledBitmap(hitmarker, (int)(hitmarker.getWidth()*xScale), (int)(hitmarker.getHeight()*yScale), true);
        bad = BitmapFactory.decodeResource(getResources(), R.drawable.bad, options);
        bad = Bitmap.createScaledBitmap(bad, (int)(bad.getWidth()*xScale), (int)(bad.getHeight()*yScale), true);


        //create enemy and path list depending on levelID -enemies will start at first point and then go through all points until reaching the end
        if(levelID==1) {
            levelMap = BitmapFactory.decodeResource(getResources(), R.drawable.level1map, options);

            enemyPath.add(new Point(90,0));
            enemyPath.add(new Point(90,690));
            enemyPath.add(new Point(210,690));
            enemyPath.add(new Point(210,90));
            enemyPath.add(new Point(330,90));
            enemyPath.add(new Point(330,690));
            enemyPath.add(new Point(450,690));
            enemyPath.add(new Point(450,90));
            enemyPath.add(new Point(540,90));
            // 1 wave of normal enemies
            for(int i=0;i<1;i++){
                enemyWaves.add(new Enemy(100+i*10,0,5,50+i*5,normEnemyImage,enemyPath,this));
            }
        }else if(levelID==2){
            levelMap = BitmapFactory.decodeResource(getResources(), R.drawable.level2map, options);

            enemyPath.add(new Point(0,90));
            enemyPath.add(new Point(450,90));
            enemyPath.add(new Point(450,690));
            enemyPath.add(new Point(210,690));
            enemyPath.add(new Point(210,390));
            enemyPath.add(new Point(0,390));
            //3 waves of normal scaling then 1 fast + 1 slow
            for(int i=0;i<3;i++){
                enemyWaves.add(new Enemy(100+i*10,0,5,50+i*5,normEnemyImage,enemyPath,this));
            }
            enemyWaves.add(new Enemy(50, 0, 20, 30, fastEnemyImage,enemyPath,this));
            enemyWaves.add(new Enemy(300, .1, 2, 100, slowEnemyImage,enemyPath,this));


        }else if(levelID==3){
            levelMap = BitmapFactory.decodeResource(getResources(), R.drawable.level3map, options);

            enemyPath.add(new Point(450,0));
            enemyPath.add(new Point(450,690));
            enemyPath.add(new Point(270,690));
            enemyPath.add(new Point(270,330));
            enemyPath.add(new Point(90,330));
            enemyPath.add(new Point(90,780));

            //10 waves alternating fast+slow
            for(int i=0;i<5;i++){
                enemyWaves.add(new Enemy(50+10*i,0,20,30+i*5,fastEnemyImage,enemyPath,this));
                enemyWaves.add(new Enemy(250+i*30,.05+.02*i,2,70+i*10,slowEnemyImage,enemyPath,this));
            }

        }
        //scale map
        levelMap = Bitmap.createScaledBitmap(levelMap, dstWidth, dstHeight, true);

        //update enemy image for first wave
        this.activity.updateImage(R.id.next_enemy, new BitmapDrawable(enemyWaves.get(0).getSprite()));

        //set send now btn text
        activity.updateText(R.id.send_now_btn, "in: 0 sec\nsend now");

        //update gold text
        updateGold(0);



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    //mouse events on canvas
    public boolean onTouchEvent(MotionEvent event) {
        //scale down mouse points to match
        int mouseX = (int)event.getX()*bgWidth/dstWidth;
        int mouseY = (int)event.getY()*bgHieght/dstHeight;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // screen pressed
            if(this.buyMode){
                this.buyTowerPoint=roundToNearest(new Point(mouseX,mouseY));
            }

        } if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // drag event
            if(this.buyMode){
                this.buyTowerPoint=roundToNearest(new Point(mouseX,mouseY));
            }

        } if (event.getAction() == MotionEvent.ACTION_UP) {
            // touch was released
            //check if in buy or sell mode and if not, check and see if a tower was selected to view range
            Point action = roundToNearest(new Point(mouseX,mouseY));
            if (this.buyMode) {
                this.buyTower(action);
            } else if (this.sellMode) {
                this.sellTower(action);
            } else {
                for (Tower t : this.towersOnScreen) {
                    if (action.x == t.getPosition().x && action.y == t.getPosition().y) {
                        //a tower was tapped-display upgrade menu and update
                        if(t==selectedTower){
                            //select tower was tapped, toggle selection to unselected
                            selectedTower=null;
                            activity.toggleMenu();
                        }else{
                            if(selectedTower!=null){
                                selectedTower.toggleSelected();
                            }else{
                                activity.toggleMenu();
                            }
                            selectedTower=t;
                        }
                        t.updateMenu(this.activity,0);
                        t.toggleSelected();

                    }
                }
            }
        }
        return true;
    }

    public void upgradeTower(int upgradeType,int cost){
        if(this.gold>=cost){
            this.updateGold(-cost);
            this.selectedTower.upgrade(upgradeType);
            if(upgradeType==1){
                this.selectedTower.setTowerImage(this.damageTowerImage);
            }else if(upgradeType==2){
                this.selectedTower.setTowerImage(this.firerateTowerImage);
            }else if(upgradeType==3) {
                this.selectedTower.setTowerImage(this.rangeTowerImage);
            }

        }
        this.selectedTower.toggleSelected();
        this.selectedTower=null;
        this.activity.toggleMenu();
    }

    public void updateTowerMenu(int upgradeType) {
        if (this.selectedTower != null) {
            this.selectedTower.updateMenu(this.activity, upgradeType);

        }
    }

    public void closeUpgradeMenu(){
        this.selectedTower.toggleSelected();
        this.selectedTower=null;
    }



    //used to scale width of path point with background stretch
    public int scalePointW(double point) {
        double p = point*xScale;
        int pInt = (int) (p + 0.5d);
        return pInt;
    }
    public int scalePointH(double point) {
        double p = point*yScale;
        int pInt = (int) (p + 0.5d);
        return pInt;
    }
    //take a point and round its coordinates to the nearest 30 for the grid size of the game to place towers-then add 15 to place tower in middle of grid square
    public Point roundToNearest(Point p){
        int multiple =60;
        p.x=Math.round(p.x/multiple)*multiple+multiple/2;
        p.y=Math.round(p.y/multiple)*multiple+multiple/2;
        return p;
    }

    public void buyTower(Point position){
        //check if the user has enough gold to build the tower
        if(gold>=towerCost){
            //check if the tower is in a valid position
            if(validPosition(position)) {
                Tower t = new Tower(position, 40, 20, 200, this.towerImage, this.projImage,this.projImage2, this);
                this.updateGold(-towerCost);
                this.towersOnScreen.add(t);
                this.playSound(R.raw.airhorn);
            }
        }
        //whether buy was succesful or not, remove buymode and reset towerpoint so the tower isnt displayed anymore
        this.buyTowerPoint=null;
    }

    public boolean validPosition(Point position){
        //check if the tower is not on the path or other obstacle
        for(Tower t:this.towersOnScreen){
            if(t.getPosition().equals(position)){
                return false;
            }
        }
        //check if tower is on top of enemy path
        Point p1=this.enemyPath.get(0);
        Point p2;
        for(int i=1;i<this.enemyPath.size();i++){
            p2=p1;
            p1=this.enemyPath.get(i);
            //since all level paths have only vertical or horizontal straight paths, just check if the tower position lies between these points
            if(p1.x==p2.x){
                //if p.x are equal, points are vertical, check if tower point x=p.x and if the y is between the points
                if(position.x==p1.x&&Math.max(p1.y,p2.y)>=position.y&&Math.min(p1.y,p2.y)<=position.y){
                    //the tower position is on same x and between enemy path point y's so it is on the path
                    return false;
                }
            }else{
                if(position.y==p1.y&&Math.max(p1.x,p2.x)>=position.x&&Math.min(p1.x,p2.x)<=position.x){
                    //the tower position is on same x and between enemy path point y's so it is on the path
                    return false;
                }
            }
        }
        return true;
    }

    public void sellTower(Point position){
        //check to see if a tower was on top of sell point
        for(Tower t:this.towersOnScreen){
            if(t.getPosition().equals(position)){
                //tower at position-remove it and refund some gold
                this.updateGold(t.getValue());
                this.destroyTower.add(t);
                if(t==this.selectedTower){
                    this.activity.toggleMenu();
                    this.selectedTower=null;
                }

                break; //towers cannot be on top of each other so break loop
            }
        }
        //whether tower was sold or not, remove sellmode

    }

    public void buyBtn(){
        //if sellmode is on, take it off
        if(this.sellMode){
            this.sellMode = !this.sellMode;
        }
        this.buyMode = !this.buyMode;
    }

    public void sellBtn(){
        //if buymode is on, take it off
        if(this.buyMode){
            this.buyMode = !this.buyMode;
        }
        this.sellMode = !this.sellMode;
    }

    public void render(Canvas canvas){
        //draw own level map firstj
        if(timeout>0&&status&&timeout<450){
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawRect(new RectF(0,0,this.bgWidth,this.bgHieght),p);
            canvas.rotate((float)Math.random()*40-20,dstWidth/2,dstHeight/2);
        }
        if(canvas==null){
            this.endLevel(false);
            return;
        }
        canvas.drawBitmap(levelMap, 0, 0, null);
        //draw tower to be placed if in buymode and the user has their finger on sccreen for the coordinates
        if(this.buyMode&&this.buyTowerPoint!=null){
            canvas.drawBitmap(this.towerImage, scalePointW(this.buyTowerPoint.x) - this.towerImage.getWidth() / 2, scalePointH(this.buyTowerPoint.y) - this.towerImage.getHeight() / 2, null);
        }
        //draw all enemies, towers, and projectiles
        for (Enemy enemy:enemiesOnScreen){
            enemy.draw(canvas);
        }
        for(Tower tower: towersOnScreen){
            tower.draw(canvas);
        }
        for(Projectile projectile:projOnScreen){
            projectile.draw(canvas);
        }
        if(timeout>0){
            timeout--;
            if(timeout==0){
                endLevel(status);
            }
            if(status){

            }else{
                Paint p = new Paint();
                p.setColor(Color.GRAY);
                p.setAlpha(255 - (timeout * 255 / 500));
                canvas.drawRect(new RectF(0,0,this.bgWidth,this.bgHieght),p);
                canvas.drawBitmap(bad,0,dstHeight-bad.getHeight(),p);
            }
        }

    }


    public void update(){
        //check if next wave should be sent
        if(timetoNextWave==0){
            sendWave();
        }else{
            timetoNextWave-=1;
            //time starts negative at start so dont update unless its positive
            if(timetoNextWave>0){
                activity.updateText(R.id.send_now_btn,"in: "+(int)timetoNextWave/30 +" sec\nsend now");
            }
        }
        //if there are still enemies to send, check the timer
        if(enemiesLeft>0){
            if(timetoNextEnemy>0){
                timetoNextEnemy-=1;
            }else{
                spawnEnemy();
                timetoNextEnemy=30;
            }
        }

        //remove any objects that should be destroyed
        enemiesOnScreen.removeAll(deadEnemy);
        towersOnScreen.removeAll(destroyTower);
        projOnScreen.removeAll(destroyProj);

        //call update for all enemies, towers, and projectiles
        for(Enemy enemy:enemiesOnScreen){
            enemy.update();
        }
        for(Tower tower:towersOnScreen){
            tower.update();
        }
        for(Projectile projectile:projOnScreen){
            projectile.update();
        }

    }

    public void endLevel(boolean win){
        if(timeout!=0){
            timeout=500;
            this.status=win;
            if(win){
                playSound(R.raw.sk);
            }else{
                playSound(R.raw.sad4me);
            }
            return;
        }
        if(win) {
            //send back max level data
            Intent i = new Intent();
            i.putExtra("levelComplete", this.levelID);
            this.activity.setResult(Activity.RESULT_OK, i);
        }
        this.thread.setRunning(false);
        this.activity.finish();
       // this.activity.finishActivity(0);
    }

    public void playSound(int id) {
        final MediaPlayer player = MediaPlayer.create(this.activity, id);
        this.activePlayers.add(player);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                activePlayers.remove(player);
                player.release();
            }
        });
    }


    public void playHit(){
        playSound(R.raw.hitmarker);
    }


    public void sendWave(){
        if(enemyWave==enemyWaves.size()-1){
            //do nothing if on last wave
        }else if (enemiesLeft<=0){
            if(enemyWaves.get(enemyWave+1).getSprite().equals(this.fastEnemyImage)){
                playSound(R.raw.sanic);
            }else if(enemyWaves.get(enemyWave+1).getSprite().equals(this.normEnemyImage)){
                playSound(R.raw.spooky);
            }else if(enemyWaves.get(enemyWave+1).getSprite().equals(this.slowEnemyImage)) {
                playSound(R.raw.swamp);
            }else{
                playSound(R.raw.airhorn);
            }

            timetoNextWave=450;
            enemiesLeft+=10;
            this.activity.updateText(R.id.enemies_remaining,"Sending: "+enemiesLeft);
            enemyWave++;
            //update next wave image
            if(enemyWave==enemyWaves.size()-1){
                //last wave sent
                this.activity.updateImage(R.id.next_enemy, new BitmapDrawable(lastwaveImage));
            }else{
                this.activity.updateImage(R.id.next_enemy,new BitmapDrawable(enemyWaves.get(enemyWave+1).getSprite()));
            }
        }
    }

    public void spawnEnemy(){
        //check if any enemies are left to send
        if(enemiesLeft<=0){
            return;
        }
        Enemy enemy = enemyWaves.get(enemyWave);
        //create new copy of enemy from waves and add it to list of enemies on screen to spawn it
        enemiesOnScreen.add(new Enemy(enemy.getHitpoints(),enemy.getArmor(),enemy.getSpeed(),enemy.getValue(),enemy.getSprite(),enemy.getPath(),this));
        enemiesLeft-=1;
        this.activity.updateText(R.id.enemies_remaining,"Sending: "+enemiesLeft);
    }

    public void updateGold(int gold) {
        this.gold += gold;
        activity.updateText(R.id.gold_text,"Gold: "+this.gold);
    }

    public void loselife(Enemy enemy){
        this.lives-=1;
        activity.updateText(R.id.lives_text, "Lives: " + this.lives);
    }

    //used to remove Enemies, towers, and projectiles from screen.  Will be removed at start of update loop since they cant be removed while looping through
    public void remove(Object object){
        if(object.getClass() == Enemy.class) {
            deadEnemy.add((Enemy) object);
            //if an enemy is removed, make sure to remove all projectiles that are still currently targeting it
            for(Projectile p:this.projOnScreen){
                if(p.getTarget().equals((Enemy)object)){
                    this.destroyProj.add(p);
                }
            }
            //if enemy is removed check to see if it is the last one of the last wave and end level if so
            if(this.enemyWave==this.enemyWaves.size()-1&&this.enemiesLeft==0&&this.enemiesOnScreen.size()==1&&timeout<0){
                //end level, win if lives > 0
                this.endLevel(this.lives>0);
            }else if(this.lives<1&&timeout<0){
                this.endLevel(false);
            }
        }else if (object.getClass()==Tower.class){
            destroyTower.add((Tower)object);
        }else if(object.getClass()==Projectile.class){
            destroyProj.add((Projectile) object);
        }
        //do nothing if not of type enemy, tower, or projectile
    }

    public void addProjectile(Projectile p){
        this.projOnScreen.add(p);
    }

    public ArrayList<Enemy> getEnemiesOnScreen(){
        return this.enemiesOnScreen;
    }


}

