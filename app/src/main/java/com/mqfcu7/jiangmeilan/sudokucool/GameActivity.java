package com.mqfcu7.jiangmeilan.sudokucool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class GameActivity extends AppCompatActivity {

    private int mId;

    private GameDatabase mDatabase;
    private Game mGame;

    private BoardView mBoard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mDatabase = new GameDatabase(getApplicationContext());
        GameDatabase.GameData gameData = mDatabase.getGameData(1);

        mId = gameData.id;
        Log.d("TAG", "mId:" + mId);

        mGame = new Game();
        mGame.onCreate(gameData.data);

        mBoard = (BoardView)findViewById(R.id.sudoku_board);
        mBoard.setGame(mGame);
    }
}
