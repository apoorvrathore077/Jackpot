package com.playzelo.jackpotmodule.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.playzelo.jackpotmodule.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wheel3DView extends View {

    private final Paint paint = new Paint();
    private final Random random = new Random();
    private final Scroller scroller;
    private Paint borderPaint;
    private final Camera camera = new Camera();
    private final Matrix matrix = new Matrix();

    private List<Drawable> slotDrawables;
    private int visibleItems = 4;
    private int itemHeight;
    private int borderColor;
    private int borderWidth;
    private int itemWidth;

    private int currentScrollY = 0;
    private int finalTargetIndex = 0; // Store final target index

    public interface OnSpinCompleteListener {

        void onSpinComplete(int finalIndex);
    }

    private OnSpinCompleteListener onSpinCompleteListener;

    public void setOnSpinCompleteListener(OnSpinCompleteListener listener) {
        this.onSpinCompleteListener = listener;
    }

    public Wheel3DView(Context context) {
        this(context, null);
    }

    public Wheel3DView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Wheel3DView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);

        // Read custom attributes from the XML layout
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Wheel3DView, 0, 0);
            try {
                itemHeight = a.getDimensionPixelSize(R.styleable.Wheel3DView_itemHeight, 0);
                itemWidth = a.getDimensionPixelSize(R.styleable.Wheel3DView_itemWidth, 0);
                visibleItems = a.getInt(R.styleable.Wheel3DView_visibleItems, 3);
                borderWidth = a.getDimensionPixelSize(R.styleable.Wheel3DView_borderWidth, 0);
                borderColor = a.getColor(R.styleable.Wheel3DView_borderColor, Color.WHITE);
            } finally {
                a.recycle();
            }
        }
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    public void setItems(int[] images) {
        slotDrawables = new ArrayList<>();
        for (int imageResId : images) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), imageResId);
            if (drawable != null) {
                slotDrawables.add(drawable);
            }
        }
        invalidate();
    }

    public void spin(int finalIndex) {
        if (slotDrawables == null || slotDrawables.isEmpty()) {
            return;
        }

        // Validate finalIndex
        finalIndex = finalIndex % slotDrawables.size();

        // Store the target final index
        finalTargetIndex = finalIndex;

        // Force stop any existing animation
        scroller.forceFinished(true);

        // Start animation from current position
        int startY = currentScrollY;
        int randomSpinDistance = 3000 + random.nextInt(4000); // Increased spin distance

        // Calculate exact final position
        int targetScrollY = finalIndex * itemHeight + randomSpinDistance;

        // Start scroll animation with faster speed (reduced duration)
        scroller.startScroll(0, startY, 0, targetScrollY - startY, 26000); // Reduced from 3000 to 2000

        // Ensure we land on exact position after animation
        int finalIndex1 = finalIndex;
        postDelayed(() -> {
            scroller.forceFinished(true);
            currentScrollY = finalIndex1 * itemHeight;
            finalTargetIndex = finalIndex1;
            invalidate();
            if (onSpinCompleteListener != null) {
                onSpinCompleteListener.onSpinComplete(finalIndex1);
            }
        }, 10000); // Adjusted timing

        postInvalidateOnAnimation();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            currentScrollY = scroller.getCurrY();
            postInvalidateOnAnimation();
        }
        // Removed automatic position setting here to avoid conflicts
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (itemWidth == 0) {
            itemWidth = w;
        }
        if (itemHeight == 0) {
            itemHeight = h;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (slotDrawables == null || slotDrawables.isEmpty()) {
            return;
        }

        final int viewW = getWidth();
        final int viewH = getHeight();

        if (scroller.isFinished()) {
            int centerIndex = mod(floorDivision(currentScrollY, itemHeight), slotDrawables.size());
            Drawable drawable = slotDrawables.get(centerIndex);
            int left = (viewW - itemWidth) / 2;
            int top = (viewH - itemHeight) / 2;
            int right = left + itemWidth;
            int bottom = top + itemHeight;

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);

            if (borderWidth > 0) {
                canvas.drawRect(left, top, right, bottom, borderPaint);
            }
            return;
        }

        final int totalHeight = slotDrawables.size() * itemHeight;

        float radius = viewH / 2.0f;
        int normalizedScrollY = currentScrollY % totalHeight;
        if (normalizedScrollY < 0) {
            normalizedScrollY += totalHeight;
        }

        for (int i = 0; i < slotDrawables.size(); i++) {
            Drawable drawable = slotDrawables.get(i);

            int itemCenterY = (i * itemHeight) - normalizedScrollY + itemHeight / 2;
            float angle = (float) Math.toDegrees(Math.asin(itemCenterY / radius));

            if (Math.abs(itemCenterY) > radius) {
                continue;
            }

            canvas.save();
            camera.save();

            camera.translate(0, 0, -radius);
            camera.rotateX(-angle);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-viewW / 2.0f, -viewH / 2.0f);
            matrix.postTranslate(viewW / 2.0f, viewH / 2.0f);
            canvas.concat(matrix);

            int left = (viewW - itemWidth) / 2;
            int top = itemCenterY + viewH / 2 - itemHeight / 2;
            int right = left + itemWidth;
            int bottom = top + itemHeight;

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    private int mod(int a, int b) {
        int r = a % b;
        return r < 0 ? r + b : r;
    }

    private int floorDivision(int a, int b) {
        int r = a / b;
        if ((a ^ b) < 0 && (a % b != 0)) {
            r--;
        }
        return r;
    }

    public void setCurrentItem(int index) {
        if (slotDrawables == null || slotDrawables.isEmpty()) {
            return;
        }
        currentScrollY = index * itemHeight;
        finalTargetIndex = index;
        invalidate();
    }

    // NEW METHOD: Get current visible item index
    public int getCurrentItem() {
        if (slotDrawables == null || slotDrawables.isEmpty()) {
            return 0;
        }
        return mod(floorDivision(currentScrollY, itemHeight), slotDrawables.size());
    }

    public void stopSpin() {
        scroller.forceFinished(true);
    }
}
