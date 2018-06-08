package com.mqfcu7.jiangmeilan.sudokucool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {
    public static final int DEFAULT_BOARD_SIZE = 100;
    private static final int NO_SELECTED_DIGIT = -1;
    private static final float ASPECT_RATIO = 0.8f;
    private static final float CONTROL_CELL_RATIO = 1.1f;
    private static final int CONTROL_CELL_DIFF = 20;

    private float mCellWidth;
    private float mCellHeight;
    private int mNumberLeft;
    private int mNumberTop;
    private int mSectorLineWidth;

    private Paint mLinePaint;
    private Paint mSectorLinePaint;
    private Paint mCellValuePaint;
    private Paint mCellValueReadonlyPaint;
    private Paint mSelectedValuePaint;
    private Paint mSelectedBlankPaint;
    private Paint mSpeardValuePaint;

    private Game mGame;
    private int[] mBoardScale;
    private int[] mTouchCell;
    private int mSeletectedDigit;
    private Rect[] mControlDigitRect;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTouchCell = null;
        mSeletectedDigit = NO_SELECTED_DIGIT;

        setFocusable(true);
        setFocusableInTouchMode(true);

        mLinePaint = new Paint();
        mSectorLinePaint = new Paint();
        mCellValuePaint = new Paint();
        mCellValueReadonlyPaint = new Paint();
        mSelectedValuePaint = new Paint();
        mSelectedBlankPaint = new Paint();
        mSpeardValuePaint = new Paint();

        mLinePaint.setColor(0xff757575);
        mSectorLinePaint.setColor(0xff9e9e9e);
        mCellValuePaint.setColor(0xfffdfdfd);
        mCellValueReadonlyPaint.setColor(0xfffdfdfd);
        mSelectedValuePaint.setColor(0x40000000);
        mSelectedBlankPaint.setColor(0x15ffffff);
        mSpeardValuePaint.setColor(0x15000000);
    }

    @NonNull
    private int[] calcWidthHeightForMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = DEFAULT_BOARD_SIZE;
        if (widthMode == MeasureSpec.EXACTLY ||
                (widthMode == MeasureSpec.AT_MOST && width > widthSize)) {
            width = widthSize;
        }
        int height = DEFAULT_BOARD_SIZE;
        if (heightMode == MeasureSpec.EXACTLY ||
                (heightMode == MeasureSpec.AT_MOST && height > heightSize)) {
            height = heightSize;
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = Math.round(width / ASPECT_RATIO);
        }

        if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
            height = heightSize;
        }

        return new int[]{width, height};
    }

    private int calcSectorLineWidth(int widthInPx, int heightInPx) {
        int sizeInPx = Math.min(widthInPx, heightInPx);
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        float sizeInDip = sizeInPx / dipScale;

        float sectorLineWidthInDip = 2.0f;
        if (sizeInDip > 150) {
            sectorLineWidthInDip = 3.0f;
        }

        return (int)(sectorLineWidthInDip * dipScale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int[] wh = calcWidthHeightForMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(wh[0], wh[1]);

        mBoardScale = new int[]{wh[0], wh[0]};
        mCellWidth = 1.0f * (mBoardScale[0] - getPaddingLeft() - getPaddingRight()) / Game.N;
        mCellHeight = 1.0f * (mBoardScale[1] - getPaddingTop() - getPaddingBottom()) / Game.N;

        mCellValuePaint.setTextSize(mCellHeight * 0.75f);

        mNumberLeft = (int)((mCellWidth - mCellValuePaint.measureText("0")) / 2);
        mNumberTop = (int)((mCellHeight - mCellValuePaint.getTextSize()) / 2);

        mSectorLineWidth = calcSectorLineWidth(wh[0], wh[1]);
    }

    private void drawBoardGrid(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = mBoardScale[0] - getPaddingRight();
        int height = mBoardScale[1] - getPaddingBottom();

        for (int c = 0; c <= Game.N; ++ c) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawLine(x, paddingTop, x, height, mLinePaint);;
        }
        for (int r = 0; r <= Game.N; ++ r) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
        }

        int sectorLineWidth1 = mSectorLineWidth / 2;
        int sectorLineWidth2 = sectorLineWidth1 + (mSectorLineWidth % 2);

        for (int c = 0; c <= Game.N; c += 3) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawRect(x - sectorLineWidth1, paddingTop,
                    x + sectorLineWidth2, height, mSectorLinePaint);
        }
        for (int r = 0; r <= Game.N;  r += 3) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawRect(paddingLeft, y - sectorLineWidth1, width,
                    y + sectorLineWidth2, mSectorLinePaint);
        }
    }

    private void drawBoardDigit(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        float numberAscent = mCellValuePaint.ascent();

        int[][] matrix = mGame.getMatrix();

        for (int r = 0; r < Game.N; ++ r) {
            for (int c = 0; c < Game.N; ++ c) {
                int left = Math.round((c * mCellWidth) + paddingLeft);
                int top = Math.round((r * mCellHeight) + paddingTop);
                if (matrix[r][c] == 0) {
                    continue;
                }

                canvas.drawText(Integer.toString(matrix[r][c]),
                        left + mNumberLeft,
                        top + mNumberTop - numberAscent,
                        mCellValuePaint);
            }
        }
    }

    private void drawSelectedDigit(Canvas canvas) {
        if (mTouchCell == null || mSeletectedDigit <= 0) {
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int[][] matrix = mGame.getMatrix();
        for (int r = 0; r < Game.N; ++ r) {
            for (int c = 0; c < Game.N; ++ c) {
                if (matrix[r][c] == mSeletectedDigit) {
                    int left = Math.round((c * mCellWidth) + paddingLeft);
                    int top = Math.round((r * mCellHeight) + paddingTop);
                    canvas.drawRect(left, top, left + mCellWidth, top + mCellHeight, mSelectedValuePaint);
                }
            }
        }
    }

    private void drawSpreadDigit(Canvas canvas) {
        if (mTouchCell == null || mSeletectedDigit != 0) {
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int left = Math.round((mTouchCell[1] * mCellWidth) + paddingLeft);
        int top = Math.round((mTouchCell[0] * mCellHeight) + paddingTop);

        canvas.drawRect(left, top, left + mCellWidth, top + mCellHeight, mSelectedBlankPaint);

        for (int c = 0; c < Game.N; ++ c) {
            if (c == mTouchCell[1]) {
                continue;
            }
            if (getDigitAtPoint(mTouchCell[0], c) == 0) {
                continue;
            }
            left = Math.round((c * mCellWidth) + paddingLeft);
            top = Math.round((mTouchCell[0] * mCellHeight) + paddingTop);
            canvas.drawRect(left, top, left + mCellWidth, top + mCellHeight, mSpeardValuePaint);
        }
        for (int r = 0; r < Game.N; ++ r) {
            if (r == mTouchCell[0]) {
                continue;
            }
            if (getDigitAtPoint(r, mTouchCell[1]) == 0) {
                continue;
            }
            left = Math.round((mTouchCell[1] * mCellWidth) + paddingLeft);
            top = Math.round((r * mCellHeight) + paddingTop);
            canvas.drawRect(left, top, left + mCellWidth, top + mCellHeight, mSpeardValuePaint);
        }
    }

    private void drawControlPad(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int cellWidth = Math.round(mCellWidth * CONTROL_CELL_RATIO);
        int cellHeight = Math.round(mCellHeight * CONTROL_CELL_RATIO);

        float numberAscent = mCellValuePaint.ascent();
        int digitLeft = (int)((cellWidth - mCellValuePaint.measureText("0")) / 2);
        int digitTop = (int)((cellHeight - mCellValuePaint.getTextSize()) / 2);

        mControlDigitRect = new Rect[Game.N];
        int top = Math.round(paddingTop + (getHeight() + mBoardScale[1]) / 2 - cellHeight / 2 + CONTROL_CELL_DIFF);
        for (int x = 0; x < Game.N; ++ x) {
            if (x % 2 == 0) {
                int left = Math.round((x * mCellWidth) + paddingLeft);
                mControlDigitRect[x] = new Rect(left, top, left + cellHeight, top + cellHeight);
                canvas.drawRect(mControlDigitRect[x], mSelectedValuePaint);
                canvas.drawText(Integer.toString(x + 1),
                        left + digitLeft,
                        top + digitTop - numberAscent,
                        mCellValuePaint);
            }
        }

        top = Math.round(paddingTop + (getHeight() + mBoardScale[1]) / 2 - cellHeight / 2 + 2 * CONTROL_CELL_DIFF);
        for (int x = 0; x < Game.N; ++ x) {
            if (x % 2 == 1) {
                int left = Math.round((x * mCellWidth) + paddingLeft);
                mControlDigitRect[x]= new Rect(left, top, left + cellWidth, top + cellHeight);
                canvas.drawRect(mControlDigitRect[x], mSelectedValuePaint);
                canvas.drawText(Integer.toString(x + 1),
                        left + digitLeft,
                        top + digitTop - numberAscent,
                        mCellValuePaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoardGrid(canvas);

        drawSelectedDigit(canvas);
        drawSpreadDigit(canvas);

        drawBoardDigit(canvas);

        drawControlPad(canvas);
    }

    @Nullable
    private int[] getCellAtPoint(int x, int y) {
        x -= getPaddingLeft();
        y -= getPaddingTop();

        int r = (int) (y / mCellHeight);
        int c = (int) (x / mCellWidth);

        if (c >= 0 && c < Game.N && r >=0 && r < Game.N) {
            return new int[]{r, c};
        }
        return null;
    }

    private int getDigitAtPoint(int r, int c) {
        return mGame.getMatrix()[r][c];
    }

    boolean judgeTouchBoard(int x, int y) {
        int[] rc = getCellAtPoint(x, y);
        if (rc == null) {
            return false;
        }

        if (mSeletectedDigit == 0 || getDigitAtPoint(rc[0], rc[1]) != mSeletectedDigit) {
            mSeletectedDigit = getDigitAtPoint(rc[0], rc[1]);
            mTouchCell = rc;
        } else {
            mSeletectedDigit = NO_SELECTED_DIGIT;
            mTouchCell = null;
        }

        return true;
    }

    boolean judgeTouchControl(int x, int y) {
        if (mTouchCell == null || mSeletectedDigit != 0) {
            return false;
        }
        for (int i = 0; i < Game.N; ++ i) {
            if (mControlDigitRect[i].contains(x, y)) {
                if (mGame.setCellValue(mTouchCell[0], mTouchCell[1], i + 1)) {
                    mSeletectedDigit = i + 1;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }

        int x = (int)event.getX();
        int y = (int)event.getY();

        if (judgeTouchBoard(x, y) || judgeTouchControl(x, y)) {
            postInvalidate();
        }

        return true;
    }

    public void setGame(Game game) {
        mGame = game;
    }

}
