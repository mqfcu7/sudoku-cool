package com.mqfcu7.jiangmeilan.sudokucool;

import android.util.Log;

import java.util.Arrays;

public class Game {
    public static final int N = 9;

    private int[][] mCells;
    private int[][] mAnwsers;

    public void onCreate(String data) {
        Log.d("TAG", data);
        mCells = new int[N][N];
        mAnwsers = new int[N][N];
        for (int i = 0; i < N; ++ i) {
            for (int j = 0; j < N; ++ j) {
                mCells[i][j] = data.charAt(i * N + j) - '0';
                mAnwsers[i][j] = mCells[i][j];
            }
        }
    }

    public void onSolver() {
        int placedNumbers = Solver.solveBoard(mAnwsers);
    }

    public int[][] getMatrix() { return mCells; }

    public boolean setCellValue(int x, int y, int value) {
        if (mAnwsers[x][y] != value) {
            return false;
        }
        mCells[x][y] = value;
        return true;
    }
}
