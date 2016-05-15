package ph.intrepidstream.callmanager.ui.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.util.RuleState;

/**
 * Created by jayzon on 4/8/16.
 */
public class MultiStateSlider extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    private List<String> states;
    private List<Integer> stateColors;

    private int currentStateIndex;

    private List<OnStateChangedListener> stateChangedListeners;

    private float sectionRadius = 5.0f;
    private float progressBarStrokeWidth = 3.0f;

    public MultiStateSlider(Context context, AttributeSet attrs) {
        super(context, attrs);

        stateChangedListeners = new ArrayList<>();

        states = new ArrayList<>();
        stateColors = new ArrayList<>();

        setupStates();
        setMax(states.size() - 1);

        currentStateIndex = getProgress();
        setThumbColor(stateColors.get(currentStateIndex));
        setBackground(new BackgroundDrawable());
        setProgressDrawable(new BackgroundDrawable());

        setOnSeekBarChangeListener(this);
    }

    private void setupStates() {
        RuleState[] ruleStates = RuleState.values();
        for (RuleState ruleState : ruleStates) {
            states.add(ruleState.toString());
        }

        stateColors.add(Color.RED);
        stateColors.add(Color.argb(255, 255, 153, 0));
        stateColors.add(Color.CYAN);
    }

    public String getCurrentState() {
        return states.get(currentStateIndex);
    }

    public void setCurrentState(String state) {
        setCurrentStateIndex(states.indexOf(state));
    }

    public void setCurrentStateIndex(int currentStateIndex) {
        this.currentStateIndex = currentStateIndex;
        setProgress(currentStateIndex);

        invalidate();
    }

    public void addOnStateChangedListener(OnStateChangedListener listener) {
        stateChangedListeners.add(listener);
    }

    public void clearListeners() {
        stateChangedListeners.clear();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String oldState = states.get(currentStateIndex);
        String newState = states.get(progress);

        currentStateIndex = progress;

        for(MultiStateSlider.OnStateChangedListener listener: stateChangedListeners) {
            listener.onStateChanged(oldState, newState);
        }

        setThumbColor(stateColors.get(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    public interface OnStateChangedListener {
        void onStateChanged(String oldState, String newState);
    }

    private void setThumbColor(int color) {
        getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private class BackgroundDrawable extends Drawable {
        private Paint progressBarPaint;
        private Paint sectionPaint;

        private RectF sectionRect = new RectF();

        public BackgroundDrawable() {
            progressBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            progressBarPaint.setStrokeWidth(progressBarStrokeWidth);

            sectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sectionPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect clipBounds = canvas.getClipBounds();

            int left = clipBounds.left + getPaddingLeft();
            int right = clipBounds.right - getPaddingRight();
            int width = right - left;

            canvas.drawLine(left, clipBounds.centerY(), right, clipBounds.centerY(), progressBarPaint);

            sectionRect.top = clipBounds.centerY() - sectionRadius;
            sectionRect.bottom = clipBounds.centerY() + sectionRadius;
            sectionRect.left = left - sectionRadius;

            float step = width / (states.size() - 1);
            for(int i = 0; i < states.size(); i++) {
                sectionRect.right = sectionRect.left + sectionRadius * 2;
                canvas.drawOval(sectionRect, sectionPaint);
                sectionRect.left += step;
            }
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return 100;
        }
    }
}
