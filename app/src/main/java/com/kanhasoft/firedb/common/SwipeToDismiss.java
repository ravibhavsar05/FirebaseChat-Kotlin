package com.kanhasoft.firedb.common;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Implements Swipte to Delete.
 */
public class SwipeToDismiss extends ItemTouchHelper.SimpleCallback {
    /*
     * Context instance used for access resources.
     */
    private Context context;

    /*
     * Callback for left and right swipe.
     */
    private SwipetoDismissCallBack swipetoDismissCallBack;

    private Paint paintLeft = new Paint();
    private Paint paintRight = new Paint();
    private Paint paintNoAction = new Paint();

    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;

    private final float MARGIN = 50f;
    private final float THRESHOLD = 0.3f;
    private final float ICON_SIZE = 40f;

    private final int SWIPE_LEFT = 8;

    public SwipeToDismiss(Context context, int swipeDirs) {
        super(0, swipeDirs);
        this.context = context;

        /*
         * Default colors
         */
        paintLeft.setColor(Color.BLUE);
        paintRight.setColor(Color.CYAN);
        paintNoAction.setColor(Color.GRAY);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;

            float threshold = itemView.getWidth() * THRESHOLD;

            boolean moraThanThreshold = Math.abs(dX) > threshold;

            if (dX > 0) {
                float height = (itemView.getHeight() / 2) - (bitmapLeft.getHeight() / 2);

                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), !moraThanThreshold ? paintNoAction : paintLeft);

                if (bitmapLeft != null) {
                    c.drawBitmap(bitmapLeft, MARGIN, (float) itemView.getTop() + height, null);
                }

            } else if (dX < 0) {
                float height = (itemView.getHeight() / 2) - (bitmapRight.getHeight() / 2);
                float bitmapWidth = bitmapRight.getWidth();

                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), !moraThanThreshold ? paintNoAction : paintLeft);

                if (bitmapRight != null) {
                    c.drawBitmap(bitmapRight, ((float) itemView.getRight() - bitmapWidth) - MARGIN,
                            (float) itemView.getTop() + height, null);
                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private Bitmap resize(Bitmap bitmap) {
        float maxHeight = dpToPx(context, ICON_SIZE);
        float maxWidth = dpToPx(context, ICON_SIZE);
        float scale = Math.min((maxHeight / bitmap.getWidth()), (maxWidth / bitmap.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (swipetoDismissCallBack != null) {
            if (direction == SWIPE_LEFT) {
                swipetoDismissCallBack.onSwipedLeft(viewHolder);
            } else {
                swipetoDismissCallBack.onSwipedRight(viewHolder);
            }
        }
    }

    public void setSwipetoDismissCallBack(SwipetoDismissCallBack swipetoDismissCallBack) {
        this.swipetoDismissCallBack = swipetoDismissCallBack;
    }

    public void setRightBackgroundColor(int color) {
        paintRight.setColor(ContextCompat.getColor(context, color));
    }

    public void setLeftBackgroundColor(int color) {
        paintLeft.setColor(ContextCompat.getColor(context, color));
    }

    public void setRightImg(int id) {
        bitmapRight = resize(drawableToBitmap(getDrawable(id)));
    }

    public void setLeftImg(int id) {
        bitmapLeft = resize(drawableToBitmap(getDrawable(id)));
    }

    private Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(context, id);
    }

    private float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return THRESHOLD;
    }

    public interface SwipetoDismissCallBack {
        void onSwipedLeft(RecyclerView.ViewHolder viewHolder);

        void onSwipedRight(RecyclerView.ViewHolder viewHolder);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
