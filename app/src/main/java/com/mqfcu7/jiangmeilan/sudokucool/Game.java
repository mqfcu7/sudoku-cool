package com.mqfcu7.jiangmeilan.sudokucool;

public class Game {
    public static final int N = 9;

    private int[][] mCells;
    private int[][] mAnswers;

    public void onCreate(String data) {
        mCells = new int[N][N];
        mAnswers = new int[N][N];
        for (int i = 0; i < N; ++ i) {
            for (int j = 0; j < N; ++ j) {
                mCells[i][j] = data.charAt(i * N + j) - '0';
                mAnswers[i][j] = mCells[i][j];
            }
        }
    }

    public void onSolver() { Solver.solveBoard(mAnswers); }

    public int[][] getMatrix() { return mCells; }
    public int[][] getAnswers() { return mAnswers; }

    public int getAnswer(int r, int c) { return mAnswers[r][c]; }

    public boolean is_completed_value(int value) {
        int cnt = 0;
        for (int i = 0; i < N; ++ i) {
            for (int j = 0; j < N; ++ j) {
                if (mCells[i][j] == value) {
                    cnt ++;
                }
            }
        }
        return cnt == N;
    }

    public boolean is_completed_all() {
        for (int i = 0; i < N; ++ i) {
            for (int j = 0; j < N; ++ j) {
                if (mCells[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean setCellValue(int r, int c, int value) {
        if (mAnswers[r][c] != value) {
            return false;
        }
        mCells[r][c] = value;
        return true;
    }
}
