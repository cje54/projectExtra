package geometrydefense.geometrydefenseextra;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class LevelActivity extends AppCompatActivity {

    private Level level;
    private int selectedUpgrade=0;
    private int cost;

    public void onCreate(Bundle savedInstaceState){
        //create the level from the level number in the passed bundle

        //no title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstaceState);
        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.level_layout);

        //create level with levelID passed from levelselect
        int levelID=1;
        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            levelID = bundle.getInt("levelID");
        }
        level = new Level(this,levelID);

        //add Level surfaceView to frame in layout
        ((FrameLayout)findViewById(R.id.level_screen)).addView(level);
        LinearLayout ui = (LinearLayout)findViewById(R.id.topUI);
        ViewGroup.LayoutParams params=ui.getLayoutParams();
        //set height of ui layout to .1875 of screen height
        params.height=(int)(getResources().getDisplayMetrics().heightPixels*.1875);
        ui.setLayoutParams(params);
        //set listener for clicking the send wave button
                ((Button) findViewById(R.id.send_now_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        level.sendWave();
            }
        });

        //set listeners for the buy and sell button
        final Button buy=(Button)findViewById(R.id.buy_btn);
        final Button sell=(Button)findViewById(R.id.sell_btn);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Color to green if in buy mode or gray else
                if (buy.getCurrentTextColor() == Color.GREEN) {
                    buy.setTextColor(Color.WHITE);
                } else {
                    buy.setTextColor(Color.GREEN);
                }
                //reset sell color if it was never turned off
                sell.setTextColor(Color.WHITE);
                level.buyBtn();
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set Color to red if in sell mode or gray else
                if (sell.getCurrentTextColor() == Color.RED) {
                    sell.setTextColor(Color.WHITE);
                } else {
                    sell.setTextColor(Color.RED);
                }
                //reset buy color if it was never turned off
                buy.setTextColor(Color.WHITE);
                level.sellBtn();
            }
        });

        //setup upgrade panel
        //remove and then readd the panel to add it to the top of the drawing stack so it is drawn above the level layout
        LinearLayout upgradeMenu = (LinearLayout)findViewById(R.id.upgrade_menu);
        ((FrameLayout) findViewById(R.id.level_screen)).removeView(upgradeMenu);
        ((FrameLayout) findViewById(R.id.level_screen)).addView(upgradeMenu);


        ViewGroup.LayoutParams upParam = upgradeMenu.getLayoutParams();
        //set width of upgrade panel to .2 of total screen width
        upParam.width=(int)(getResources().getDisplayMetrics().widthPixels*.4);
        upgradeMenu.setLayoutParams(upParam);
        upgradeMenu.setVisibility(View.GONE);

        //when radio button pressed, tell level to tell selected tower to update the upgrade menu values
        ((RadioButton)findViewById(R.id.dmg_up_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUpgrade=1;
                level.updateTowerMenu(selectedUpgrade);
            }
        });
        ((RadioButton)findViewById(R.id.firerate_up_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUpgrade=2;
                level.updateTowerMenu(selectedUpgrade);
            }
        });
        ((RadioButton)findViewById(R.id.range_up_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUpgrade=3;
                level.updateTowerMenu(selectedUpgrade);
            }
        });

        ((Button)findViewById(R.id.upgrade_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedUpgrade!=0) {
                    level.upgradeTower(selectedUpgrade,cost);
                }
            }
        });


        //close button closes upgrade windows and deselects tower
        ((Button)findViewById(R.id.close_upg_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
                level.closeUpgradeMenu();
            }
        });


    }

    public void toggleMenu(){
        View menu = findViewById(R.id.upgrade_menu);
        if(menu.getVisibility()==View.GONE) {
            menu.setVisibility(View.VISIBLE);
            this.selectedUpgrade=0;
            ((RadioGroup)findViewById(R.id.upgrade_radio)).clearCheck();
        }else{
            menu.setVisibility(View.GONE);
        }
    }

    public void updateMenu(int cost1, int cost2, int cost3, int dmg, int firerate, int range, int dmgUp, int firerateUp, int rangeUp, int value, int valueUp){
        ((TextView)findViewById(R.id.damage_cost_txt)).setText("Cost: "+cost1);
        ((TextView)findViewById(R.id.firerate_cost_txt)).setText("Cost: "+cost2);
        ((TextView)findViewById(R.id.range_cost_txt)).setText("Cost: " + cost3);
        ((TextView)findViewById(R.id.dmg_val_txt)).setText("Damage: "+dmg);
        ((TextView)findViewById(R.id.firerate_val_txt)).setText("Firerate: "+firerate);
        ((TextView)findViewById(R.id.range_val_txt)).setText("Range: "+range);
        ((TextView)findViewById(R.id.value_txt)).setText("Value: "+value);
        if(dmgUp!=0){
            ((TextView)findViewById(R.id.dmg_bonus_txt)).setText("(+"+dmgUp+")");
        }else{
            ((TextView)findViewById(R.id.dmg_bonus_txt)).setText("");
        }
        if(firerateUp!=0){
            ((TextView)findViewById(R.id.firerate_bonus_txt)).setText("("+firerateUp+")");
        }else{
            ((TextView)findViewById(R.id.firerate_bonus_txt)).setText("");
        }
        if(rangeUp!=0){
            ((TextView)findViewById(R.id.range_bonus_txt)).setText("(+"+rangeUp+")");
        }else{
            ((TextView)findViewById(R.id.range_bonus_txt)).setText("");
        }
        if(valueUp!=0){
            ((TextView)findViewById(R.id.value_bonus_txt)).setText("(+"+valueUp+")");
        }else{
            ((TextView)findViewById(R.id.value_bonus_txt)).setText("");
        }
        String upgradetxt="";
        if(selectedUpgrade==1){
            upgradetxt="Upgrade("+cost1+")";
            cost=cost1;
        }else if(selectedUpgrade==2){
            upgradetxt="Upgrade("+cost2+")";
            cost=cost2;
        }else if(selectedUpgrade==3){
            upgradetxt="Upgrade("+cost3+")";
            cost=cost3;
        }
        ((Button)findViewById(R.id.upgrade_btn)).setText(upgradetxt);

    }

    public void updateText(int id, final String text){
        final TextView textView = (TextView)findViewById(id);
        textView.post(new Runnable() {
            public void run() {
                textView.setText(text);
            }
        });
    }

    public void updateImage(int id, final Drawable drawable){
        final ImageView imageView = (ImageView) findViewById(id);
        imageView.post(new Runnable() {
            public void run() {
                imageView.setImageDrawable(drawable);
            }
        });    }


}
