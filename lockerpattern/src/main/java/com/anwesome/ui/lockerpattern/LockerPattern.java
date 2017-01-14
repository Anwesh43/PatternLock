package com.anwesome.ui.lockerpattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by anweshmishra on 14/01/17.
 */
public class LockerPattern {
    private Activity activity;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public LockerPattern(Activity activity) {
        this.activity = activity;
    }
    public void lock() {

    }
    private class LockView extends View {
        private boolean isDown = false;
        private LockGraph lockGraph;
        private  int time = 0;
        private LockNode
        public LockView(Context context) {
            super(context);
        }
        public void onDraw(Canvas canvas) {
            if(time == 0) {

            }
        }
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(!isDown) {
                        isDown = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
    private class LockGraph {
        private ConcurrentLinkedQueue<LockNode> lockNodes = new ConcurrentLinkedQueue<>();
        public LockGraph() {

        }
        public void addNode(LockNode lockNode) {
            lockNodes.add(lockNode);
        }
        public LockNode contains(float x,float y) {
            for(LockNode lockNode:lockNodes) {
                if(lockNode.contains(x,y)) {
                    return lockNode;
                }
            }
            return null;
        }

    }
    private class LockNode {
        private float x,y,r;
        private boolean fill = false;
        private ConcurrentLinkedQueue<LockNode> neighbors = new ConcurrentLinkedQueue<>();
        public LockNode(float x,float y,float r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
        public int hashCode() {
            return (int)this.x+(int)this.y+(int)this.r;
        }
        public void addNeighbor(LockNode lockNode) {
            neighbors.add(lockNode);
        }
        public void draw(Canvas canvas, Paint paint) {
            if(fill) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#00E676"));
            }
            else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
            }
            canvas.drawCircle(x,y,r,paint);
        }
        public boolean contains(float x,float y) {
            return x>this.x-r && x<this.x+r && y>this.y-r && y<this.y+r;
        }
        public LockNode neighborContains(float x,float y) {
            for(LockNode lockNode:neighbors)  {
                if(lockNode.contains(x,y)) {
                    return lockNode;
                }
            }
            return null;
        }
    }
}
