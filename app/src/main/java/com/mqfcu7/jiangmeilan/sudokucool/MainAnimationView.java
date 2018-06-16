package com.mqfcu7.jiangmeilan.sudokucool;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MainAnimationView extends View {

    public static final int N = 9;
    public static final int DEFAULT_BOARD_SIZE = 600;

    private float mCellWidth;
    private float mCellHeight;
    private int mNumberLeft;
    private int mNumberTop;
    private int mSectorLineWidth;

    private Paint mLinePaint;
    private Paint mSectorLinePaint;
    private Paint mCellValuePaint;

    private int[] mBoardScale;

    private boolean mAnimationTrigger = false;
    private ValueAnimator mAnimation;

    private int[][] mMatrix;
    private Game mGame;

    private class Digit {
        public int value;
        public Point cell;
        public Point start;
        public Point cur;
        public Point target;
        public int frame;
        public double dx;
        public float k;
        public long dist;
    }
    List<Digit> mDigitCollect;

    public MainAnimationView(Context context) {
        this(context, null);
    }

    public MainAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mLinePaint = new Paint();
        mSectorLinePaint = new Paint();
        mCellValuePaint = new Paint();

        mLinePaint.setColor(0xff757575);
        mSectorLinePaint.setColor(0xffaeaeae);
        mCellValuePaint.setColor(0xfffdfdfd);
    }

    private int[] calcWidthHeightForMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = DEFAULT_BOARD_SIZE;
        if (widthMode == MeasureSpec.EXACTLY ||
                (widthMode == MeasureSpec.AT_MOST && width > widthSize)) {
            width = widthSize;
        }
        int height = DEFAULT_BOARD_SIZE + getPaddingTop() + getPaddingBottom();

        return new int[]{width, height};
    }

    private int calcSectorLineWidth(int widthInPx, int heightInPx) {
        int sizeInPx = Math.min(widthInPx, heightInPx);
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        float sizeInDip = sizeInPx / dipScale;

        float sectorLineWidthInDip = 0.5f;
        if (sizeInDip > 150) {
            sectorLineWidthInDip = 1f;
        }

        return (int)(sectorLineWidthInDip * dipScale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int[] wh = calcWidthHeightForMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(wh[0], wh[1]);

        mBoardScale = new int[]{wh[1] - getPaddingTop() - getPaddingBottom(),
                wh[1] - getPaddingTop() - getPaddingBottom()};
        mCellWidth = 1.0f * mBoardScale[0] / N;
        mCellHeight = 1.0f * mBoardScale[1] / N;

        mCellValuePaint.setTextSize(mCellHeight * 0.75f);

        mNumberLeft = (int)((mCellWidth - mCellValuePaint.measureText("0")) / 2);
        mNumberTop = (int)((mCellHeight - mCellValuePaint.getTextSize()) / 2);

        mSectorLineWidth = calcSectorLineWidth(mBoardScale[0], mBoardScale[1]);
    }

    private void drawBoardGrid(Canvas canvas) {
        int left = (getWidth() - mBoardScale[0]) / 2;
        int top = (getHeight() - mBoardScale[1]) / 2;
        int right = mBoardScale[0] + left;
        int bottom = mBoardScale[1] + top;

        for (int c = 0; c <= N; ++ c) {
            float x = (c * mCellWidth) + left;
            canvas.drawLine(x, top, x, bottom, mLinePaint);;
        }
        for (int r = 0; r <= N; ++ r) {
            float y = r * mCellHeight + top;
            canvas.drawLine(left, y, right, y, mLinePaint);
        }

        int sectorLineWidth1 = mSectorLineWidth / 2;
        int sectorLineWidth2 = sectorLineWidth1 + (mSectorLineWidth % 2);

        for (int c = 0; c <= N; c += 3) {
            float x = (c * mCellWidth) + left;
            canvas.drawRect(x - sectorLineWidth1, top,
                    x + sectorLineWidth2, bottom, mSectorLinePaint);
        }
        for (int r = 0; r <= N;  r += 3) {
            float y = r * mCellHeight + top;
            canvas.drawRect(left - sectorLineWidth1, y - sectorLineWidth1, right,
                    y + sectorLineWidth2, mSectorLinePaint);
        }
    }

    private void drawBoardDigit(Canvas canvas) {
        int paddingLeft = (getWidth() - mBoardScale[0]) / 2;
        int paddingTop = (getHeight() - mBoardScale[1]) / 2;

        float numberAscent = mCellValuePaint.ascent();

        int[][] matrix = mGame.getMatrix();
        for (int r = 0; r < N; ++ r) {
            for (int c = 0; c < N; ++ c) {
                if (matrix[r][c] == 0) {
                    continue;
                }
                int left = Math.round((c * mCellWidth) + paddingLeft);
                int top = Math.round((r * mCellHeight) + paddingTop);
                canvas.drawText(Integer.toString(matrix[r][c]),
                        left + mNumberLeft,
                        top + mNumberTop - numberAscent,
                        mCellValuePaint);
            }
        }
    }

    private void drawAnimationDigit(Canvas canvas) {
        if (mDigitCollect.isEmpty()) {
            return;
        }

        float numberAscent = mCellValuePaint.ascent();

        Paint valuePaint = new Paint();
        for (Digit digit:mDigitCollect) {
            valuePaint.setColor(0x00fdfdfd + Math.min(digit.frame * 2 + 20, 255) * 0x01000000);
            valuePaint.setTextSize(mCellHeight * 0.75f);
            canvas.drawText(Integer.toString(digit.value),
                    digit.cur.x + mNumberLeft,
                    digit.cur.y + mNumberTop - numberAscent,
                    valuePaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoardGrid(canvas);
        drawBoardDigit(canvas);
        drawAnimationDigit(canvas);

        if (!mAnimationTrigger) {
            triggerAnimation();
            mAnimationTrigger = true;
        }
    }

    public void onReset() {
        if (mAnimation != null) {
            mAnimation.cancel();
        }

        String[] matrixString = new String[] {
                "916004072800620050500008930060000200000207000005000090097800003080076009450100687",
                "021009008000004031740100025000007086058000170160800000910008052230900000800300410",
                "020439800080000001003001520050092703000000000309740080071300900600000030008924010"
        };
        mGame = new Game();
        mGame.onCreate(matrixString[(int)(Math.random() * matrixString.length)]);
        mGame.onSolver();
        mMatrix = new int[Game.N][Game.N];
        int[][] matrix = mGame.getMatrix();
        for (int i = 0; i < Game.N; ++ i) {
            for (int j = 0; j < Game.N; ++ j) {
                mMatrix[i][j] = matrix[i][j];
            }
        }

        mDigitCollect = new LinkedList<>();

        mAnimationTrigger = false;
    }

    private Digit spawnDigitForAnimation() {
        int[][] answers = mGame.getAnswers();
        int targetValue = 0;
        for (int i = 1; i <= Game.N; ++ i) {
            int cnt = 0;
            for (int j = 0; j < Game.N; ++ j) {
                for (int k = 0; k < Game.N; ++ k) {
                    if (mMatrix[j][k] == i) {
                        cnt ++;
                    }
                }
            }
            if (cnt == Game.N) {
                continue;
            }
            targetValue = i;
            break;
        }
        if (targetValue == 0) {
            return null;
        }


        Digit digit = new Digit();
        digit.value = targetValue;
        digit.cell = new Point();
        for (int i = 0; i < Game.N; ++ i) {
            for (int j = 0; j < Game.N; ++ j) {
                if (mMatrix[i][j] != 0) {
                    continue;
                }
                if (answers[i][j] == targetValue) {
                    digit.cell.x = j;
                    digit.cell.y = i;
                    break;
                }
            }
        }

        int paddingLeft = (getWidth() - mBoardScale[0]) / 2;
        int paddingTop = (getHeight() - mBoardScale[1]) / 2;
        digit.start = new Point(0, 0);
        digit.cur = new Point(digit.start);
        digit.target = new Point(Math.round(digit.cell.x * mCellWidth) + paddingLeft,
                Math.round(digit.cell.y * mCellHeight) + paddingTop);
        if (digit.target.x == digit.start.x) {
            return null;
        }
        digit.k = 1.0f * (digit.target.y - digit.start.y) / (digit.target.x - digit.start.x);
        digit.dx = 3 * (digit.target.x - digit.start.x) / Math.abs(digit.target.x - digit.start.x);
        digit.dist = (digit.target.x - digit.start.x) * (digit.target.x - digit.start.x) +
                (digit.target.y - digit.start.y) * (digit.target.y - digit.start.y);
        digit.frame = 1;
        mMatrix[digit.cell.y][digit.cell.x] = targetValue;

        return digit;
    }

    private void triggerAnimation() {
        mAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimation.setDuration(1000);
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int mCnt = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (Iterator<Digit> itr = mDigitCollect.iterator(); itr.hasNext();) {
                    Digit digit = itr.next();
                    long dist = (digit.cur.x - digit.start.x) * (digit.cur.x - digit.start.x) +
                            (digit.cur.y - digit.start.y) * (digit.cur.y - digit.start.y);
                    if (dist >= digit.dist) {
                        boolean flag = mGame.setCellValue(digit.cell.y, digit.cell.x, digit.value);
                        itr.remove();
                        continue;
                    }
                    digit.cur.x = (int)Math.round(digit.start.x + digit.dx * digit.frame);
                    digit.cur.y = (int)Math.round(digit.start.y + digit.k * digit.cur.x);
                    digit.frame ++;
                }

                // 新生成数字
                if (mCnt % 50 == 0) {
                    Digit digit = spawnDigitForAnimation();
                    if (digit != null) {
                        mDigitCollect.add(digit);
                    }
                    mCnt = 0;
                }

                invalidate();
                mCnt ++;
            }
        });
        mAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mAnimation.start();
    }
}
