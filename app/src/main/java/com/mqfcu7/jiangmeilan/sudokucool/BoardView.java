package com.mqfcu7.jiangmeilan.sudokucool;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

public class BoardView extends View {
    public static final int DEFAULT_BOARD_SIZE = 100;
    private static final int NO_SELECTED_DIGIT = -1;
    private static final float ASPECT_RATIO = 0.8f;
    private static final float CONTROL_CELL_RATIO = 1.1f;
    private static final int CONTROL_CELL_DIFF = 20;
    private static final int FAILURE_RETRY_MAX = 3;

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
    private GameDatabase mGameDatabase;
    private GameActivity mActivity;
    private int[] mBoardScale;
    private int[] mTouchCell;
    private int mSeletectedDigit;
    private int mFailureRetryCnt;
    private long mTime;
    private long mActiveFromTime = -1;

    private class ControlDigit {
        public int v;
        public int tv;
        public Point tp;
        public Rect r;
        public Rect xr;
        public Rect tr;
    }
    private ControlDigit[] mControlDigit;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTouchCell = null;
        mSeletectedDigit = NO_SELECTED_DIGIT;
        mControlDigit = new ControlDigit[Game.N];

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
        mSectorLinePaint.setColor(0xffaeaeae);
        mCellValuePaint.setColor(0xfffdfdfd);
        mCellValueReadonlyPaint.setColor(0xfffdfdfd);
        mSelectedValuePaint.setColor(0x40000000);
        mSelectedBlankPaint.setColor(0x15ffffff);
        mSpeardValuePaint.setColor(0x15000000);

        mActiveFromTime = SystemClock.uptimeMillis();
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

    private void calcControlPad(int width, int height) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int cellWidth = Math.round(mCellWidth * CONTROL_CELL_RATIO);
        int cellHeight = Math.round(mCellHeight * CONTROL_CELL_RATIO);

        int top = Math.round(paddingTop + (height + mBoardScale[1]) / 2 - cellHeight / 2 + CONTROL_CELL_DIFF);
        for (int x = 0; x < Game.N; ++ x) {
            if (x % 2 == 0) {
                int left = Math.round((x * mCellWidth) + paddingLeft);
                mControlDigit[x] = new ControlDigit();
                mControlDigit[x].v = x + 1;
                mControlDigit[x].r = new Rect(left, top, left + cellHeight, top + cellHeight);
            }
        }

        top = Math.round(paddingTop + (height + mBoardScale[1]) / 2 - cellHeight / 2 + 2 * CONTROL_CELL_DIFF);
        for (int x = 0; x < Game.N; ++ x) {
            if (x % 2 == 1) {
                int left = Math.round((x * mCellWidth) + paddingLeft);
                mControlDigit[x] = new ControlDigit();
                mControlDigit[x].v = x + 1;
                mControlDigit[x].r = new Rect(left, top, left + cellWidth, top + cellHeight);
            }
        }
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

        calcControlPad(wh[0], wh[1]);
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

        int[][] matrix = mGame.getMatrix();
        for (int r = 0; r < Game.N; ++ r) {
            for (int c = 0; c < Game.N; ++ c) {
                if (matrix[r][c] == mSeletectedDigit) {
                    canvas.drawRect(getRectangleAtCell(r, c), mSelectedValuePaint);
                }
            }
        }
    }

    private void drawSpreadDigit(Canvas canvas) {
        if (mTouchCell == null || mSeletectedDigit != 0) {
            return;
        }

        canvas.drawRect(getRectangleAtCell(mTouchCell[0], mTouchCell[1]), mSelectedBlankPaint);

        for (int c = 0; c < Game.N; ++ c) {
            if (c == mTouchCell[1]) {
                continue;
            }
            if (getDigitAtCell(mTouchCell[0], c) == 0) {
                continue;
            }
            canvas.drawRect(getRectangleAtCell(mTouchCell[0], c), mSpeardValuePaint);
        }
        for (int r = 0; r < Game.N; ++ r) {
            if (r == mTouchCell[0]) {
                continue;
            }
            if (getDigitAtCell(r, mTouchCell[1]) == 0) {
                continue;
            }
            canvas.drawRect(getRectangleAtCell(r, mTouchCell[1]), mSpeardValuePaint);
        }
    }

    private void drawControlPad(Canvas canvas) {
        int cellWidth = Math.round(mCellWidth * CONTROL_CELL_RATIO);
        int cellHeight = Math.round(mCellHeight * CONTROL_CELL_RATIO);

        float numberAscent = mCellValuePaint.ascent();
        int digitLeft = (int)((cellWidth - mCellValuePaint.measureText("0")) / 2);
        int digitTop = (int)((cellHeight - mCellValuePaint.getTextSize()) / 2);

        for (int i = 0; i < Game.N; ++ i) {
            Rect r = mControlDigit[i].xr == null ? mControlDigit[i].r : mControlDigit[i].xr;
            if (r == null) {
                continue;
            }
            canvas.drawRect(r, mSelectedValuePaint);
            canvas.drawText(Integer.toString(i + 1),
                    r.left + digitLeft,
                    r.top + digitTop - numberAscent,
                    mCellValuePaint);
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

    private int getDigitAtCell(int r, int c) {
        return mGame.getMatrix()[r][c];
    }

    private Rect getRectangleAtCell(int r, int c) {
        int left = Math.round((c * mCellWidth) + getPaddingLeft());
        int top = Math.round((r * mCellHeight) + getPaddingTop());
        return new Rect(left, top,
                Math.round(left + mCellWidth), Math.round(top + mCellHeight));
    }

    boolean judgeTouchBoard(int x, int y) {
        int[] rc = getCellAtPoint(x, y);
        if (rc == null) {
            return false;
        }

        if (mSeletectedDigit == 0 || getDigitAtCell(rc[0], rc[1]) != mSeletectedDigit) {
            mSeletectedDigit = getDigitAtCell(rc[0], rc[1]);
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
            if (mControlDigit[i].r != null && mControlDigit[i].r.contains(x, y)) {
                mControlDigit[i].tv = mGame.getAnswer(mTouchCell[0], mTouchCell[1]);
                mControlDigit[i].tp = new Point(mTouchCell[0], mTouchCell[1]);
                mControlDigit[i].xr = new Rect(mControlDigit[i].r);
                mControlDigit[i].tr = getRectangleAtCell(mTouchCell[0], mTouchCell[1]);

                onControlPadMovAnimation();

                return true;
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

    private void onControlPadMovAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(300);
        Log.d("TAG", "duration: " + animator.getDuration());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                Log.d("TAG", "value: " + value);
                for (int i = 0; i < Game.N; ++ i) {
                    if (mControlDigit[i].xr == null) {
                        continue;
                    }
                    int dx = mControlDigit[i].tr.centerX() - mControlDigit[i].r.centerX();
                    int dy = mControlDigit[i].tr.centerY() - mControlDigit[i].r.centerY();
                    int dw = mControlDigit[i].tr.width() - mControlDigit[i].r.width();
                    int dh = mControlDigit[i].tr.height() - mControlDigit[i].r.height();
                    int x = mControlDigit[i].r.centerX() +  Math.round(dx * value);
                    int y = mControlDigit[i].r.centerY() +  Math.round(dy * value);
                    int w = mControlDigit[i].r.width() +  Math.round(dw * value);
                    int h = mControlDigit[i].r.height() +  Math.round(dh * value);
                    mControlDigit[i].xr = new Rect( x - w / 2, y - h / 2, x + w / 2, y + h / 2);
                    invalidate();
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (int i = 0; i < Game.N; ++ i) {
                    if (mControlDigit[i].xr == null) {
                        continue;
                    }
                    if (mControlDigit[i].v == mControlDigit[i].tv) {
                        mControlDigit[i].xr = null;
                        mControlDigit[i].tr = null;
                        mGame.setCellValue(mControlDigit[i].tp.x, mControlDigit[i].tp.y, mControlDigit[i].v);
                        if (mGame.is_completed_value(mControlDigit[i].v)) {
                            mControlDigit[i].r = null;
                        }
                        mSeletectedDigit = mControlDigit[i].v;
                        if (mGame.is_completed_all()) {
                            onGameFinished();
                        }
                    } else {
                        mFailureRetryCnt ++;
                        onGameFailure(i);
                    }
                }
            }
        });
        animator.start();
    }

    private void onGameFinished() {
        mGameDatabase.setGamePass(mActivity.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.myDialog));
        builder.setMessage("超级厉害，成功通关!");
        builder.setTitle("游戏结束");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mActivity.finish();
            }
        });
        builder.create().show();
    }

    private void onGameFailure(final int idx) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                int x = mControlDigit[idx].tr.left + Math.round(value * 30);
                mControlDigit[idx].xr = new Rect(x, mControlDigit[idx].tr.top,
                        mControlDigit[idx].tr.right, mControlDigit[idx].tr.bottom);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mControlDigit[idx].xr = null;
                mControlDigit[idx].tr = null;
                mActivity.setHealthPointLabel(FAILURE_RETRY_MAX - mFailureRetryCnt);
                if (mFailureRetryCnt >= FAILURE_RETRY_MAX) {
                    onGameOver();
                }
            }
        });
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return (float) Math.sin(input * 2 * Math.PI);
            }
        });
        animator.start();
    }

    private void onGameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.myDialog));
        builder.setMessage("生命值用完，游戏失败!");
        builder.setTitle("游戏结束");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mActivity.finish();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public void setGame(Game game) {
        mGame = game;
    }

    public void setGameDatabase(GameDatabase gameDatabase) {
        mGameDatabase = gameDatabase;
    }

    public void setActivity(GameActivity activity) {
        mActivity = activity;
    }

    public long getTime() {
        return mTime + SystemClock.uptimeMillis() - mActiveFromTime;
    }

}
