package com.appsbybirbeck.android.kittyplayground;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyActivity extends Activity {

    private ViewGroup container;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        container = (FrameLayout)findViewById(R.id.container);
        container.setOnDragListener(new MyDragListener());
        container.setOnLongClickListener(new MyLongClickListener());
        container.setOnTouchListener(new MyLongClickListener());
    }

    private static final class MyLongClickListener implements View.OnTouchListener, View.OnLongClickListener  {

        static int lastX, lastY;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getX() > 0 && event.getY() > 0) {
                    lastX = (int)event.getX();
                    lastY = (int)event.getY();
                }
            }
            return false;
        }

        @Override
        public boolean onLongClick(final View view) {
            int width = 48 * (int)Resources.getSystem().getDisplayMetrics().density;
            int height = 48 * (int)Resources.getSystem().getDisplayMetrics().density;
            int x = lastX - (width / 2);
            int y = lastY - (width / 2);

            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.setMargins(x, y, 0, 0);

            final ImageView imageView = new ImageView(view.getContext());
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(R.drawable.grumpy_cat);
            imageView.setOnTouchListener(new MyTouchListener());
            imageView.setOnLongClickListener(new MyTouchListener());

            final ViewGroup group = (ViewGroup) view;
            group.addView(imageView);
            return true;
        }
    }

    private static final class MyTouchListener implements View.OnTouchListener, View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            final ViewGroup parent = (ViewGroup) v.getParent();
            parent.removeView(v);
            return true;
        }

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                view.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(view), view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        }
    }

    private static final class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(final View view, final DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    final View dragView = (View) event.getLocalState();

                    int w = dragView.getWidth();
                    int h = dragView.getHeight();
                    int x = (int)(event.getX() - (w / 2));
                    int y = (int)(event.getY() - (h / 2));

                    final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w, h);
                    layoutParams.setMargins(x, y, 0, 0);
                    dragView.setLayoutParams(layoutParams);
                    dragView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
