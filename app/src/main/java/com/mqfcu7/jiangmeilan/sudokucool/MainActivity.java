package com.mqfcu7.jiangmeilan.sudokucool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

public class MainActivity extends AppCompatActivity {

    public int mLevel = 0;
    private GameDatabase mGameDatabase;
    private LinearLayout layout_ads;
    private IFLYBannerAd bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainAnimationView view = (MainAnimationView) findViewById(R.id.main_animation_view);

        Button easyButton = (Button)findViewById(R.id.easy_button);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLevel = GameDatabase.LEVEL_EASY;
                view.onReset();
                createGame();
            }
        });

        Button normalButton = (Button)findViewById(R.id.normal_button);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLevel = GameDatabase.LEVEL_NORMAL;
                view.onReset();
                createGame();
            }
        });

        Button hardButton = (Button)findViewById(R.id.hard_button);
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLevel = GameDatabase.LEVEL_HARD;
                view.onReset();
                createGame();
            }
        });

        mGameDatabase = new GameDatabase(getApplicationContext());
        TextView scoreTextView = (TextView)findViewById(R.id.score_text_view);
        scoreTextView.setText("得分：" + mGameDatabase.getTotalScore());

        createBannerAd();
    }

    private void createGame() {
        if (mGameDatabase.getRecordData() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("继续上一局吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(GameActivity.newIntent(getApplicationContext(), mLevel));
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mGameDatabase.clearRecord();
                    startActivity(GameActivity.newIntent(getApplicationContext(), mLevel));
                }
            });
            builder.create().show();
        } else {
            startActivity(GameActivity.newIntent(getApplicationContext(), mLevel));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainAnimationView view = (MainAnimationView) findViewById(R.id.main_animation_view);
        view.onReset();

        TextView scoreTextView = (TextView)findViewById(R.id.score_text_view);
        scoreTextView.setText("得分：" + mGameDatabase.getTotalScore());
    }

    public void createBannerAd() {
        String adUnitId = "0E10603A355E88AD82A3B7BEF0BA1824";

        bannerView = IFLYBannerAd.createBannerAd(this, adUnitId);
        bannerView.setAdSize(IFLYAdSize.BANNER);

        bannerView.loadAd(mAdListener);

        layout_ads = (LinearLayout) findViewById(R.id.main_banner_ad_layout);
        layout_ads.removeAllViews();
        layout_ads.addView(bannerView);
    }

    IFLYAdListener mAdListener = new IFLYAdListener() {
        @Override
        public void onAdReceive() {
            bannerView.showAd();
            Log.d("TAG", "onAdReceive");
        }

        @Override
        public void onAdFailed(AdError adError) {
            Log.d("TAG", adError.getErrorCode() + ": " + adError.getErrorDescription());
        }

        @Override
        public void onAdClick() {

        }

        @Override
        public void onAdClose() {

        }

        @Override
        public void onAdExposure() {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerView.destroy();
    }
}
