package com.mqfcu7.jiangmeilan.sudokucool;

import android.content.ContentValues;
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
    private static final String TABLE_RECORD_NAME = "record";

    private static final int DATABASE_VERSION = 1;

    public static final int LEVEL_EASY = 1;
    public static final int LEVEL_NORMAL = 2;
    public static final int LEVEL_HARD = 3;

    private Context mContext;

    public class GameData {
        public int id;
        public String data;
        public int time;
        public int score;
    }

    private abstract class MatrixColumns implements BaseColumns {
        public static final String LEVEL = "level";
        public static final String DATA = "data";
        public static final String PASS = "pass";
        public static final String TIME = "time";
        public static final String SCORE = "score";
    }

    private abstract class RecordColumns implements BaseColumns {
        public static final String MATRIX_ID = "matrix_id";
        public static final String DATA = "data";
        public static final String TIME = "time";
        public static final String SCORE = "score";
    }

    public GameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    private void insertMatrix(SQLiteDatabase db, int id, int level, String data) {
        final String sql = "insert into " + TABLE_MATRIX_NAME + " values ("
                + id + ", " + level + ", '" + data + "', 0, 0, 0);";
        db.execSQL(sql);
    }

    private void createMatrixData(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MATRIX_NAME + " ("
                + MatrixColumns._ID + " integer primary key,"
                + MatrixColumns.LEVEL + " integer,"
                + MatrixColumns.DATA + " text,"
                + MatrixColumns.PASS + " integer,"
                + MatrixColumns.TIME + " integer,"
                + MatrixColumns.SCORE + " integer"
                + ");");

        // easy level
        insertMatrix(db, 1, LEVEL_EASY, "052006000160900004049803620400000800083201590001000002097305240200009056000100970");
        insertMatrix(db, 2, LEVEL_EASY, "052400100100002030000813025400007010683000597070500002890365000010700006006004970");
        insertMatrix(db, 3, LEVEL_EASY, "302000089068052734009000000400007000083201590000500002000000200214780350530000908");
        insertMatrix(db, 4, LEVEL_EASY, "402000007000080420050302006090030050503060708070010060900406030015070000200000809");
        insertMatrix(db, 5, LEVEL_EASY, "060091080109680405050040106600000200023904710004000003907020030305079602040150070");
        insertMatrix(db, 6, LEVEL_EASY, "060090380009080405050300106001008200020904010004200900907006030305070600046050070");
        insertMatrix(db, 7, LEVEL_EASY, "402000380109607400008300106090030004023964710800010060907006500005809602046000809");
        insertMatrix(db, 8, LEVEL_EASY, "400091000009007425058340190691000000003964700000000963087026530315800600000150009");
        insertMatrix(db, 9, LEVEL_EASY, "380001004002600070000487003000040239201000406495060000600854000070006800800700092");
        insertMatrix(db, 10, LEVEL_EASY, "007520060002009008006407000768005009031000450400300781000804300100200800050013600");
        insertMatrix(db, 11, LEVEL_EASY, "380000000540009078000407503000145209000908000405362000609804000170200045000000092");
        insertMatrix(db, 12, LEVEL_EASY, "007001000540609078900487000760100230230000056095002081000854007170206045000700600");
        insertMatrix(db, 13, LEVEL_EASY, "007021900502009078006407500000140039031908450490062000009804300170200805004710600");
        insertMatrix(db, 14, LEVEL_EASY, "086500204407008090350009000009080601010000080608090300000200076030800409105004820");
        insertMatrix(db, 15, LEVEL_EASY, "086507000007360100000000068249003050500000007070100342890000000002056400000904820");
        insertMatrix(db, 16, LEVEL_EASY, "000007230420368000050029768000080650000602000078090000894230070000856019065900000");
        insertMatrix(db, 17, LEVEL_EASY, "906000200400368190350400000209080051013040980670090302000001076032856009005000803");
        insertMatrix(db, 18, LEVEL_EASY, "095002000700804001810076500476000302000000000301000857003290075500307006000400130");
        insertMatrix(db, 19, LEVEL_EASY, "005002740002850901810000500070501302008723600301609050003000075509017200087400100");
        insertMatrix(db, 20, LEVEL_EASY, "605102740732004001000000020400501300008020600001609007060000000500300286087405109");
        insertMatrix(db, 21, LEVEL_EASY, "695102040700800000000970023076000090900020004020000850160098000000007006080405139");
        insertMatrix(db, 22, LEVEL_EASY, "090002748000004901800906500470500090008000600020009057003208005509300000287400030");
        insertMatrix(db, 23, LEVEL_EASY, "001009048089070030003106005390000500058602170007000094900708300030040860870300400");
        insertMatrix(db, 24, LEVEL_EASY, "600039708000004600000100025002017506408000103107850200910008000005900000806320009");
        insertMatrix(db, 25, LEVEL_EASY, "620500700500270631040100005302000086000090000160000204900008050235041007006005019");
        insertMatrix(db, 26, LEVEL_EASY, "080130002140902007273080000000070206007203900502040000000060318600308024400021050");
        insertMatrix(db, 27, LEVEL_EASY, "980100402046950000200684001010009086007000900590800070700465008000098720408001059");
        insertMatrix(db, 28, LEVEL_EASY, "085100400000950007073684001010070080067203940090040070700465310600098000008001650");
        insertMatrix(db, 29, LEVEL_EASY, "085100460146000807070004001300009080067000940090800003700400010601000724038001650");
        insertMatrix(db, 30, LEVEL_EASY, "085130462006000007270680090000009200060213040002800000020065018600000700438021650");

        // normal level
        insertMatrix(db, 31, LEVEL_NORMAL, "916004072800620050500008930060000200000207000005000090097800003080076009450100687");
        insertMatrix(db, 32, LEVEL_NORMAL, "000900082063001409908000000000670300046050290007023000000000701704300620630007000");
        insertMatrix(db, 33, LEVEL_NORMAL, "035670000400829500080003060020005807800206005301700020040900070002487006000052490");
        insertMatrix(db, 34, LEVEL_NORMAL, "030070902470009000009003060024000837007000100351000620040900200000400056708050090");
        insertMatrix(db, 35, LEVEL_NORMAL, "084200000930840000057000000600401700400070002005602009000000980000028047000003210");
        insertMatrix(db, 36, LEVEL_NORMAL, "007861000008003000560090010100070085000345000630010007050020098000600500000537100");
        insertMatrix(db, 37, LEVEL_NORMAL, "040001003000050079560002804100270080082000960030018007306100098470080000800500040");
        insertMatrix(db, 38, LEVEL_NORMAL, "000500006000870302270300081000034900793050614008790000920003057506087000300005000");
        insertMatrix(db, 39, LEVEL_NORMAL, "000900067090000208460078000320094070700603002010780043000850016501000090670009000");
        insertMatrix(db, 40, LEVEL_NORMAL, "024000017000301000300000965201000650000637000093000708539000001000502000840000570");
        insertMatrix(db, 41, LEVEL_NORMAL, "200006143004000600607008029100800200003090800005003001830500902006000400942600005");
        insertMatrix(db, 42, LEVEL_NORMAL, "504002030900073008670000020000030780005709200047060000050000014100450009060300502");
        insertMatrix(db, 43, LEVEL_NORMAL, "580000637000000000603540000090104705010709040807205090000026304000000000468000072");
        insertMatrix(db, 44, LEVEL_NORMAL, "000010000900003408670500021000130780015000240047065000750006014102400009000090000");
        insertMatrix(db, 45, LEVEL_NORMAL, "780300050956000000002065001003400570600000003025008100200590800000000417030004025");
        insertMatrix(db, 46, LEVEL_NORMAL, "200367500500800060300450700090530400080000070003074050001026005030005007002783001");
        insertMatrix(db, 47, LEVEL_NORMAL, "801056200000002381900003000350470000008000100000068037000600002687100000004530806");
        insertMatrix(db, 48, LEVEL_NORMAL, "300004005841753060000010000003000087098107540750000100000070000030281796200300008");
        insertMatrix(db, 49, LEVEL_NORMAL, "000064810040050062009010300003040607008107500704030100006070200430080090017390000");
        insertMatrix(db, 50, LEVEL_NORMAL, "000040320000357080000600400357006040600705003080900675008009000090581000064070000");
        insertMatrix(db, 51, LEVEL_NORMAL, "905040026026050900030600050350000009009020800100000075010009030003080760560070108");
        insertMatrix(db, 52, LEVEL_NORMAL, "010403060030017400200000300070080004092354780500070030003000005008530040050906020");
        insertMatrix(db, 53, LEVEL_NORMAL, "605900100000100073071300005009010004046293510700040600200001730160002000008009401");
        insertMatrix(db, 54, LEVEL_NORMAL, "049060002800210490100040000000035084008102300630470000000080001084051006700020950");
        insertMatrix(db, 55, LEVEL_NORMAL, "067020300003700000920103000402035060300000002010240903000508039000009200008010750");
        insertMatrix(db, 56, LEVEL_NORMAL, "050842001004000900800050040600400019007506800430009002080090006001000400500681090");
        insertMatrix(db, 57, LEVEL_NORMAL, "000076189000002030009813000025000010083000590070000460000365200010700000536120000");
        insertMatrix(db, 58, LEVEL_NORMAL, "080000030400368000350409700000003650003000900078100000004201076000856009060000020");
        insertMatrix(db, 59, LEVEL_NORMAL, "000500748589000001700086900302010580000000000067050204004760002200000867876005000");
        insertMatrix(db, 60, LEVEL_NORMAL, "021009008000004031740100025000007086058000170160800000910008052230900000800300410");

        // hard level
        insertMatrix(db, 61, LEVEL_HARD, "600300100071620000805001000500870901009000600407069008000200807000086410008003002");
        insertMatrix(db, 62, LEVEL_HARD, "906013008058000090030000010060800920003409100049006030090000080010000670400960301");
        insertMatrix(db, 63, LEVEL_HARD, "300060250000500103005210486000380500030000040002045000413052700807004000056070004");
        insertMatrix(db, 64, LEVEL_HARD, "060001907100007230080000406018002004070040090900100780607000040051600009809300020");
        insertMatrix(db, 65, LEVEL_HARD, "600300208400185000000000450000070835030508020958010000069000000000631002304009006");
        insertMatrix(db, 66, LEVEL_HARD, "400030090200001600760800001500318000032000510000592008900003045001700006040020003");
        insertMatrix(db, 67, LEVEL_HARD, "004090170900070002007204000043000050798406213060000890000709400600040001085030700");
        insertMatrix(db, 68, LEVEL_HARD, "680001003007004000000820000870009204040302080203400096000036000000500400700200065");
        insertMatrix(db, 69, LEVEL_HARD, "000002000103400005200050401340005090807000304090300017605030009400008702000100000");
        insertMatrix(db, 70, LEVEL_HARD, "050702003073480005000050400040000200027090350006000010005030000400068730700109060");
        insertMatrix(db, 71, LEVEL_HARD, "500080020007502801002900040024000308000324000306000470090006700703208900060090005");
        insertMatrix(db, 72, LEVEL_HARD, "108090000200308096090000400406009030010205060080600201001000040360904007000060305");
        insertMatrix(db, 73, LEVEL_HARD, "010008570607050009052170000001003706070000040803700900000017260100020407024300090");
        insertMatrix(db, 74, LEVEL_HARD, "020439800080000001003001520050092703000000000309740080071300900600000030008924010");
        insertMatrix(db, 75, LEVEL_HARD, "000500201800006005005207080017960804000000000908074610080405300700600009504009000");
        insertMatrix(db, 76, LEVEL_HARD, "920000000500870000038091000052930160090000030073064980000410250000053001000000073");
        insertMatrix(db, 77, LEVEL_HARD, "590006010001254709000001400003715008100000004200648100002500000708463900050100047");
        insertMatrix(db, 78, LEVEL_HARD, "309870004000005008870400000104580003000706000700034105000009081900300000400057206");
        insertMatrix(db, 79, LEVEL_HARD, "800200000910300706000007002084000009095104860100000230500600000609003071000005008");
        insertMatrix(db, 80, LEVEL_HARD, "005037001000050627600002530020070000001968200000010090013700008486090000700840100");
        insertMatrix(db, 81, LEVEL_HARD, "090350700000800029000402008710000000463508297000000051300204000940005000008037040");
        insertMatrix(db, 82, LEVEL_HARD, "000005904080090605006000030030701450008040700074206090060000300801060070309800000");
        insertMatrix(db, 83, LEVEL_HARD, "030004087948700500060800009010586720000000000087312050800003070003007865570200090");
        insertMatrix(db, 84, LEVEL_HARD, "300687015000030082050000300400300000601050709000004003008000020210040000970521004");
        insertMatrix(db, 85, LEVEL_HARD, "702000004030702010400093008000827090007030800080956000300570009020309080600000503");
        insertMatrix(db, 86, LEVEL_HARD, "300040057400853060025700000000000430800406001034000000000005690090624003160080002");
        insertMatrix(db, 87, LEVEL_HARD, "000260050000005900000380046020094018004000500950810070380021000005700000040058000");
        insertMatrix(db, 88, LEVEL_HARD, "062080504008050090700320001000740620000203000027065000200036007040070100803090240");
        insertMatrix(db, 89, LEVEL_HARD, "002001000068000003000086090900002086804000102520800009080140000100000920000700500");
        insertMatrix(db, 90, LEVEL_HARD, "000030065460950200000086004003070006004090100500010300200140000007065028630020000");
    }

    private void createRecordData(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_RECORD_NAME + " ("
                + RecordColumns._ID + " integer primary key,"
                + RecordColumns.MATRIX_ID + " integer,"
                + RecordColumns.DATA + " text,"
                + RecordColumns.TIME + " integer,"
                + RecordColumns.SCORE + " integer"
                + ");");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createMatrixData(db);
        createRecordData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public GameData getRecordData() {
        GameData result = null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_RECORD_NAME);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result = new GameData();
                result.id = c.getInt(c.getColumnIndex(RecordColumns.MATRIX_ID));
                result.data = c.getString(c.getColumnIndex(RecordColumns.DATA));
                result.time = c.getInt(c.getColumnIndex(RecordColumns.TIME));
                result.score = c.getInt(c.getColumnIndex(RecordColumns.SCORE));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public GameData getGameData(int level) {
        GameData result = getRecordData();
        if (result != null) {
            return result;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_MATRIX_NAME);
        qb.appendWhere(MatrixColumns.LEVEL + "=" + level + " and " + MatrixColumns.PASS + "=0");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result = new GameData();
                result.id = c.getInt(c.getColumnIndex(MatrixColumns._ID));
                result.data = c.getString(c.getColumnIndex(MatrixColumns.DATA));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public void setGamePass(int id, int time, int score) {
        ContentValues values = new ContentValues();
        values.put(MatrixColumns.PASS, 1);
        values.put(MatrixColumns.TIME, time);
        values.put(MatrixColumns.SCORE, score);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_MATRIX_NAME, values, MatrixColumns._ID + "=" + id, null);
    }

    public int getTotalScore() {
        int score = 0;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_MATRIX_NAME);
        qb.appendWhere(MatrixColumns.PASS + "=1");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    score += c.getInt(c.getColumnIndex(MatrixColumns.SCORE));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return score;
    }

    public void clearRecord() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from " + TABLE_RECORD_NAME + ";");
        } finally {

        }
    }

    public void recordGame(int id, int[][] matrix, int time, int score) {
        String data = "";
        for (int i = 0; i < Game.N; ++ i) {
            for (int j = 0; j < Game.N; ++ j) {
                data += String.valueOf(matrix[i][j]);
            }
        }

        boolean hasRecord = getRecordData() != null;
        ContentValues values = new ContentValues();
        values.put(RecordColumns.MATRIX_ID, id);
        values.put(RecordColumns.DATA, data);
        values.put(RecordColumns.TIME, time);
        values.put(RecordColumns.SCORE, score);
        Log.d("TAG", values.toString());

        try {
            SQLiteDatabase db = getWritableDatabase();
            if (hasRecord) {
                db.update(TABLE_RECORD_NAME, values,RecordColumns._ID + "=0", null);
            } else {
                db.execSQL("insert into " + TABLE_RECORD_NAME + " values (0, " + id + ", '" + data + "', "
                        + time + ", " + score + ");");
            }
        } finally {

        }
    }
}
