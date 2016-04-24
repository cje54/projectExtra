package geometrydefense.geometrydefenseextra;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;


public class LevelSelect extends AppCompatActivity {


    //define and create the list of levels, which will be assigned to the buttons
    private Level[] levelList = new Level[3];
    public int maxLevel=3;
    //levelList1 = new Level();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select_layout);


        //back button-returns to main menu
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelSelect.this.finish();
            }
        });

        //get grid layout to add buttons to
        GridLayout grid = ((GridLayout) findViewById(R.id.level_list));


        //loop to add level buttons-each button has listener to start its level
        for (int i = 1; i <= levelList.length; i++) {
            final Button btn = new Button(this);
            btn.setId(i);
            if (i <= maxLevel) {
                btn.setTextColor(Color.WHITE);
            } else {
                btn.setTextColor(Color.RED);
            }
            final int id = btn.getId();
            btn.setText(id + "");
            btn.setBackgroundColor(Color.rgb(70, 80, 90));
            grid.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    System.out.println(maxLevel);
                    //check to see if level is unlocked before starting
                    if (Integer.parseInt(btn.getText().toString()) <= maxLevel) {
                        //pass level value in intent to build correct level
                        Intent myIntent = new Intent(LevelSelect.this, LevelActivity.class);
                        myIntent.putExtra("levelID", btn.getId());
                        LevelSelect.this.startActivityForResult(myIntent, 1);
                    }
                }
            });
        }



    }

    //recieve when level is successfully completed and update max level
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int levelCompleted = data.getIntExtra("levelComplete", 1);
                if (levelCompleted == maxLevel) {
                    maxLevel++;
                    //update button for next level(if exists) to show as available to play and update completed level to green
                    Button btn = ((Button) findViewById(maxLevel));
                    if (btn != null) {
                        btn.setTextColor(Color.WHITE);
                    }
                    Button b = ((Button) findViewById(maxLevel - 1));
                    b.setTextColor(Color.GREEN);
                }
            }
        }

    }

}


