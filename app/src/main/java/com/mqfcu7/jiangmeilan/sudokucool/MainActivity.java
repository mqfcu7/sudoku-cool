package com.mqfcu7.jiangmeilan.sudokucool;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button easyButton = (Button)findViewById(R.id.easy_button);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GameActivity.newIntent(getApplicationContext(), GameDatabase.LEVEL_EASY));
            }
        });

        Button normalButton = (Button)findViewById(R.id.normal_button);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GameActivity.newIntent(getApplicationContext(), GameDatabase.LEVEL_NORMAL));
            }
        });

        Button hardButton = (Button)findViewById(R.id.hard_button);
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GameActivity.newIntent(getApplicationContext(), GameDatabase.LEVEL_HARD));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainAnimationView view = (MainAnimationView) findViewById(R.id.main_animation_view);
        view.onReset();
    }
}
