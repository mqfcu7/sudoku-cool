package com.mqfcu7.jiangmeilan.sudokucool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BoardView extends View {
    public static final int DEFAULT_BOARD_SIZE = 100;

    private float mCellWidth;
    private float mCellHeight;
    private int mNumberLeft;
    private int mNumberTop;
    private int mSectorLineWidth;

    private Paint mLinePaint;
    private Paint mSectorLinePaint;
    private Paint mCellValuePaint;
    private Paint mCellValueReadonlyPaint;

    private Game mGame;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mLinePaint = new Paint();
        mSectorLinePaint = new Paint();
        mCellValuePaint = new Paint();
        mCellValueReadonlyPaint = new Paint();

        mLinePaint.setColor(0xff757575);
        mSectorLinePaint.setColor(0xff9e9e9e);
        mCellValuePaint.setColor(0xfffdfdfd);
        mCellValueReadonlyPaint.setColor(0xfffdfdfd);
    }

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
            height = width;
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

        mCellWidth = (wh[0] - getPaddingLeft() - getPaddingRight()) / 9.0f;
        mCellHeight = (wh[1] - getPaddingTop() - getPaddingBottom()) / 9.0f;

        mCellValuePaint.setTextSize(mCellHeight * 0.75f);

        mNumberLeft = (int)((mCellWidth - mCellValuePaint.measureText("0")) / 2);
        mNumberTop = (int)((mCellHeight - mCellValuePaint.getTextSize()) / 2);

        mSectorLineWidth = calcSectorLineWidth(wh[0], wh[1]);
    }

    private void drawBoardGrid(Canvas canvas, int width, int height) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();
        Log.d("TAG", "width:" + width);
        Log.d("TAG", "height:" + height);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        for (int row = 0; row < Game.N; ++ row) {
            for (int col = 0; col < Game.N; ++ col) {
                int cellLeft = Math.round((col * mCellWidth) + paddingLeft);
                int cellTop = Math.round((row * mCellHeight) + paddingTop);

            }
        }

        drawBoardGrid(canvas, width, height);
        drawBoardDigit(canvas);
    }

    public void setGame(Game game) {
        mGame = game;
    }
}
