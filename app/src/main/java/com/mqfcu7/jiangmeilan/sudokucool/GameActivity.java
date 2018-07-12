package com.mqfcu7.jiangmeilan.sudokucool;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

public class GameActivity extends AppCompatActivity {

    private static final String EXTRA_GAME_LEVEL =
            "com.mqfcu7.jiangmeilan.sudokucool.game_level";

    private int mId;

    private GameDatabase mDatabase;
    private Game mGame;

    private BoardView mBoard;

    private TextView mTimerLable;
    private TextView mHealthPointLabel;

    private GameTimer mGameTimer;
    private GameTimeFormat mGameTimeFormat;

    private LinearLayout layout_ads;
    private IFLYBannerAd bannerView;

    public static Intent newIntent(Context packageContext, int level) {
        Intent intent = new Intent(packageContext, GameActivity.class);
        intent.putExtra(EXTRA_GAME_LEVEL, level);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int level = getIntent().getIntExtra(EXTRA_GAME_LEVEL, 1);
        mDatabase = new GameDatabase(getApplicationContext());
        GameDatabase.GameData gameData = mDatabase.getGameData(level);

        mId = gameData.id;
        Log.d("TAG", "mId:" + mId);

        mGame = new Game();
        mGame.onCreate(gameData.data);
        mGame.onSolver();

        mBoard = (BoardView)findViewById(R.id.sudoku_board);
        mBoard.setGame(mId, mGame);
        mBoard.setGameDatabase(mDatabase);
        mBoard.setActivity(this);
        mBoard.setTime(gameData.time);
        mBoard.setScore(gameData.score);
        setScore(gameData.score);

        mTimerLable = (TextView) findViewById(R.id.time_label);
        mHealthPointLabel = (TextView) findViewById(R.id.health_point_label);

        mGameTimer = new GameTimer();
        mGameTimeFormat = new GameTimeFormat();

        mGameTimer.start();

        createBannerAd();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBoard.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBoard.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("确认退出吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return false;
    }

    public void setHealthPointLabel(int healthPoint) {
        mHealthPointLabel.setText("生命：" + healthPoint);
    }

    private final class GameTimer extends Timer{
        public GameTimer() {
            super(500);
        }

        @Override
        protected boolean step(int count, long time) {
            mTimerLable.setText("时间：" + mGameTimeFormat.format(mBoard.getTime()));

            return false;
        }
    }

    public int getId() {
        return mId;
    }

    public void setScore(int score) {
        TextView scoreView = (TextView)findViewById(R.id.score_label);
        scoreView.setText("得分：" + score);
    }

    public void createBannerAd() {
        String adUnitId = "1D79BA911B85C5E692A1B1B37D17A4C4";

        bannerView = IFLYBannerAd.createBannerAd(this, adUnitId);
        bannerView.setAdSize(IFLYAdSize.BANNER);

        bannerView.loadAd(mAdListener);

        layout_ads = (LinearLayout) findViewById(R.id.layout_adview);
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

