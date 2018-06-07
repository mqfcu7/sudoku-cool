package com.mqfcu7.jiangmeilan.sudokucool;

import android.util.Log;

public class Game {
    public static final int N = 9;

    private int[][] mCells;

    public void onCreate(String data) {
        Log.d("TAG", data);
        mCells = new int[N][N];
        for (int i = 0; i < N; ++ i) {
            for (int j = 0; j < N; ++ j) {
                mCells[i][j] = data.charAt(i * N + j) - '0';
            }
        }
    }

    public int[][] getMatrix() {
        return mCells;
    }
}
