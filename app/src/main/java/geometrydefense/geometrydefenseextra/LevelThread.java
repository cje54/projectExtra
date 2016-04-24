package geometrydefense.geometrydefenseextra;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 *
 */
public class LevelThread extends Thread {

    // desired fps
    private final static int 	MAX_FPS = 30;
    // maximum number of frames to be skipped
    private final static int	MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;

    // Surface holder that can access the physical surface
    private SurfaceHolder surfaceHolder;
    // The actual view that handles inputs
    // and draws to the surface
    private Level level;

    // flag to hold game state
    private boolean running;

    public LevelThread(SurfaceHolder surfaceHolder, Level level) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.level = level;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public void run() {
        Canvas canvas;
        long start; //time at start of cycle
        long diff;  //time difference between start of cycle and after update/render
        while(running){

            canvas = this.surfaceHolder.lockCanvas();
            start = System.currentTimeMillis(); //get time at the start of the cycle
            this.level.update();
            this.level.render(canvas);
            diff = System.currentTimeMillis()-start; //get time after update/render and get difference
            try {
                if(33-diff>0) //since the app should run at 30 fps, there should be 33 ms between cycles, so sleep for any remaining time of the 33ms
                    Thread.sleep(33 - diff);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }catch(Exception e){
                //stop running if exception
                this.running=false;
                e.printStackTrace();
            }
        }


    }

}
