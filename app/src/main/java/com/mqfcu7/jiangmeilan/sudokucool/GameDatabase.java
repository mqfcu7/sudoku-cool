package com.mqfcu7.jiangmeilan.sudokucool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class GameDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sudoku-cool";
    private static final String TABLE_MATRIX_NAME = "matrix";

    private static final int DATABASE_VERSION = 1;

    private static final int LEVEL_EASY = 1;
    private static final int LEVEL_NORMAL = 2;
    private static final int LEVEL_HARD = 3;

    private Context mContext;

    public class GameData {
        public int id;
        public String data;
    }

    private abstract class MatrixColumns implements BaseColumns {
        public static final String LEVEL = "level";
        public static final String DATA = "data";
        public static final String PASS = "pass";
    }

    public GameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    private void insertMatrix(SQLiteDatabase db, int id, int level, String data) {
        final String sql = "insert into " + TABLE_MATRIX_NAME + " values ("
                + id + ", " + level + ", '" + data + "', 0);";
        db.execSQL(sql);
    }

    private void createMatrixData(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MATRIX_NAME + " ("
                + MatrixColumns._ID + " integer primary key,"
                + MatrixColumns.LEVEL + " integer,"
                + MatrixColumns.DATA + " text,"
                + MatrixColumns.PASS + " integer"
                + ");");

        // easy level
        insertMatrix(db, 1, LEVEL_EASY, "052006000160900004049803620400000800083201590001000002097305240200009056000100970");

        // normal level
        insertMatrix(db, 2, LEVEL_NORMAL, "916004072800620050500008930060000200000207000005000090097800003080076009450100687");

        // hard level
        insertMatrix(db, 3, LEVEL_HARD, "600300100071620000805001000500870901009000600407069008000200807000086410008003002");
    }

    private void createRecordData(SQLiteDatabase db) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMatrixData(db);
        createRecordData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public GameData getGameData(int level) {
        // TODO: get data from record

        GameData result = new GameData();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_MATRIX_NAME);
        qb.appendWhere(MatrixColumns.LEVEL + "=" + level + " and " + MatrixColumns.PASS + "=0");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result.id = c.getInt(c.getColumnIndex(MatrixColumns._ID));
                result.data = c.getString(c.getColumnIndex(MatrixColumns.DATA));
                Log.d("TAG", result.data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }
}
