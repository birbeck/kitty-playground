package com.appsbybirbeck.android.kittyplayground;

import java.util.Random;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MyActivity
    extends Activity
    implements View.OnDragListener, View.OnTouchListener, View.OnLongClickListener
{

    private ImageView catView;
    private ImageView background;
    private GestureDetector gestureDetector;
    private ViewGroup container;

    private int lastX, lastY;
    private int lastType = R.drawable.cat01;

    private int[] backgrounds = { R.drawable.playhouse, R.drawable.beach, R.drawable.egypt, R.drawable.london };
    private int currentBackground = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.transparent);
        setContentView(R.layout.main);

        gestureDetector = new GestureDetector(this, new MyGestureListener());

        background = (ImageView)findViewById(R.id.background);
        container = (FrameLayout)findViewById(R.id.container);
        container.setOnDragListener(this);
        container.setOnLongClickListener(this);
        container.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
            showAbout();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.cats_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_cat01:
            changeCat(R.drawable.cat01);
            return true;
        case R.id.menu_cat02:
            changeCat(R.drawable.cat02);
            return true;
        case R.id.menu_cat03:
            changeCat(R.drawable.cat03);
            return true;
        case R.id.menu_cat04:
            changeCat(R.drawable.cat04);
            return true;
        case R.id.menu_cat05:
            changeCat(R.drawable.cat05);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > 0 && event.getY() > 0) {
                lastX = (int)event.getX();
                lastY = (int)event.getY();
            }
        }
        if (!gestureDetector.onTouchEvent(event))
            return super.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onLongClick(final View view) {
        final Context context = view.getContext();
        final int width = 48 * (int)Resources.getSystem().getDisplayMetrics().density;
        final int height = 48 * (int)Resources.getSystem().getDisplayMetrics().density;
        final int xPos = lastX - (width / 2);
        final int yPos = lastY - (width / 2);

        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.setMargins(xPos, yPos, 0, 0);

        final ImageView imageView = new ImageView(context);
        imageView.setId(Math.abs(new Random().nextInt()));
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(lastType);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                catView = (ImageView) view;
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
        ((Activity)view.getContext()).registerForContextMenu(imageView);

        container.addView(imageView);
        catView = imageView;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.cat10);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
        return true;
    }

    @Override
    public boolean onDrag(final View view, final DragEvent event) {
        //if (catView == null) return true;
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                final View dragView = (View)event.getLocalState();
                final int width = dragView.getWidth();
                final int height = dragView.getHeight();
                final int xPos = (int)(event.getX() - (width / 2));
                final int yPos = (int)(event.getY() - (height / 2));

                final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                layoutParams.setMargins(xPos, yPos, 0, 0);
                dragView.setLayoutParams(layoutParams);
                dragView.setVisibility(View.VISIBLE);
                catView = null;
                break;
        }
        return true;
    }

    private void changeCat(int id) {
        lastType = id;
        catView.setImageResource(id);
    }

    private void deleteCat() {
        container.removeView(catView);
        catView = null;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.cat9);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    private void showAbout() {

    }

    class MyGestureListener
        extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            currentBackground++;
            if (currentBackground == backgrounds.length) {
                currentBackground = 0;
            }
            background.setImageResource(backgrounds[currentBackground]);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (catView == null) return false;
            catView.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(catView), catView, 0);
            catView.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (catView == null) return false;
            deleteCat();
            return true;
        }
    }

}
