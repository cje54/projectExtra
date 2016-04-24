package geometrydefense.geometrydefenseextra;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class GeometryDefense extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_geometry_defense);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        ((ImageView)findViewById(R.id.TitleImage)).setImageResource(R.drawable.titleimage);

        //exit button-exit app on click
        findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeometryDefense.this.moveTaskToBack(true);

            }
        });

        //play button-start level select activity on press
        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(GeometryDefense.this, LevelSelect.class);
                GeometryDefense.this.startActivity(myIntent);
            }
        });
    }




}
