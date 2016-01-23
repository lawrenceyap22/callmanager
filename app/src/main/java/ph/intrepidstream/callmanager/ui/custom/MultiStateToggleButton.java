package ph.intrepidstream.callmanager.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;

/**
 * Created by Jayzon on 2016/01/09.
 */
public class MultiStateToggleButton extends View {
    private List<String> states;
    private int currentState;

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint highlightPaint;
    private Paint textPaint;

    // Style Attributes
    private int borderColor;
    private int backgroundColor;
    private int highlightColor;
    private int textDefaultColor;
    private int textHighlightColor;

    private int borderSize;
    private int textSize;

    private GestureDetectorCompat gestureDetector;

    private Rect borderBounds;
    private Rect contentBounds;
    private int itemWidth, itemHeight, innerPadX, innerPadY, horizontalGap;

    private List<Rect> textBoundsList;

    private List<OnStateChangedListener> stateChangedListeners;

    public MultiStateToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        stateChangedListeners = new ArrayList<>();

        initXMLAttributes(context, attrs);
        initDrawing();

        gestureDetector = new GestureDetectorCompat(context, new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
    }

    public void addState(String state) {
        states.add(state);
    }

    public void setState(int index, String state) {
        states.set(index, state);
    }

    public void setCurrentState(int newState) {
        int oldState = currentState;
        currentState = newState;

        onStateChanged(oldState, newState);
    }

    public int getCurrentState() {
        return currentState;
    }

    private void initXMLAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MultiStateToggleButton, 0, 0);

        try {
            borderColor = a.getColor(R.styleable.MultiStateToggleButton_borderColor, Color.MAGENTA);
            backgroundColor = a.getColor(R.styleable.MultiStateToggleButton_backgroundColor, Color.WHITE);
            highlightColor = a.getColor(R.styleable.MultiStateToggleButton_highlightColor, Color.MAGENTA);
            textDefaultColor = a.getColor(R.styleable.MultiStateToggleButton_textDefaultColor, Color.BLACK);
            textHighlightColor = a.getColor(R.styleable.MultiStateToggleButton_textHighlightColor, Color.WHITE);

            borderSize = a.getDimensionPixelSize(R.styleable.MultiStateToggleButton_borderSize, 0); // TODO: Externalize default border size
            textSize = a.getDimensionPixelSize(R.styleable.MultiStateToggleButton_textSize, 16);

            innerPadX = a.getDimensionPixelSize(R.styleable.MultiStateToggleButton_innerPadX, 0);
            innerPadY = a.getDimensionPixelSize(R.styleable.MultiStateToggleButton_innerPadY, 0);
            horizontalGap = a.getDimensionPixelSize(R.styleable.MultiStateToggleButton_horizontalGap, 0);

            states = new ArrayList<>();
            CharSequence[] statesArray = a.getTextArray(R.styleable.MultiStateToggleButton_states);
            if (statesArray != null) {
                for (CharSequence state : statesArray) {
                    states.add(state.toString());
                }
            }
        } finally {
            a.recycle();
        }
    }

    private void initDrawing() {
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(borderColor);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setColor(highlightColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textDefaultColor);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //super.onSizeChanged(w, h, oldw, oldh);

        if (borderBounds == null) {
            borderBounds = new Rect();
        }
        if (contentBounds == null) {
            contentBounds = new Rect();
        }

        borderBounds.set(0, 0, w, h);
        contentBounds.set(borderBounds.left + borderSize, borderBounds.top + borderSize, borderBounds.right - borderSize, borderBounds.bottom - borderSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (textBoundsList == null) {
            textBoundsList = new ArrayList<>();
        }
        textBoundsList.clear();

        itemWidth = 0;
        itemHeight = 0;
        for (int i = 0; i < states.size(); i++) {
            Rect rect = new Rect();
            textPaint.getTextBounds(states.get(i), 0, states.get(i).length(), rect);

            itemWidth = Math.max(itemWidth, rect.width());
            itemHeight = Math.max(itemHeight, rect.height());

            textBoundsList.add(rect);
        }
        itemWidth += innerPadX * 2;
        itemHeight += innerPadY * 2;

        for (int i = 0; i < textBoundsList.size(); i++) {
            Rect rect = textBoundsList.get(i);
            int textX = borderSize + itemWidth * i + (itemWidth - rect.width()) / 2;
            int textY = borderSize + (itemHeight + rect.height()) / 2;

            rect.offsetTo(textX, textY);

            textBoundsList.set(i, rect);
        }

        int finalWidth = borderSize * 2 + itemWidth * states.size() + horizontalGap * (states.size() - 1);
        int finalHeight = itemHeight + borderSize * 2;

        /* int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); */

        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        // Draw Border
        canvas.drawRect(borderBounds, borderPaint);

        // Draw Background
        canvas.drawRect(contentBounds, backgroundPaint);

        // Draw Highlight
        Rect r = new Rect();
        r.set(borderSize + itemWidth * currentState, borderSize, borderSize + itemWidth * (currentState + 1), borderSize + itemHeight);
        canvas.drawRect(r, highlightPaint);

        // Draw Text
        for (int i = 0; i < states.size(); i++) {
            if (currentState == i) {
                textPaint.setColor(textHighlightColor);
            } else {
                textPaint.setColor(textDefaultColor);
            }

            Rect rect = textBoundsList.get(i);
            canvas.drawText(states.get(i), rect.left, rect.top, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);
        if (result) {
            int x = (int) event.getX();
            int selected = x / itemWidth;
            setCurrentState(selected);
        }

        return result;
    }

    protected void onStateChanged(int oldState, int newState) {
        invalidate();
        requestLayout();

        for (OnStateChangedListener listener : stateChangedListeners) {
            listener.onStateChanged(oldState, newState);
        }
    }

    public void addOnStateChangedListener(OnStateChangedListener listener) {
        stateChangedListeners.add(listener);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int oldState, int newState);
    }
}
