package com.anwesome.ui.lockerpattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by anweshmishra on 14/01/17.
 */
public class LockerPattern {
    private Activity activity;
    private boolean saved = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width=100,height=100;
    private boolean wrongCode = false;
    private String sharedPrefFile="";
    private String lockIndexes = "",newLockIndexes="";
    private SharedPreferences sharedPreferences;
    private LockView lockView;
    public LockerPattern(Activity activity) {
        this.activity = activity;
        sharedPrefFile = activity.getPackageName()+"_"+LPConstants.SHARED_PREF_FILE;
        initDimensions();
    }
    public void initDimensions() {
        DisplayManager displayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(0);
        if(display!=null) {
            Point size = new Point();
            display.getRealSize(size);
            width = size.x;
            height = size.y;
        }
    }
    public void lock() {
        newLockIndexes="";
        sharedPreferences = activity.getSharedPreferences(sharedPrefFile,0);
        saved = sharedPreferences.getBoolean(LPConstants.SAVED_KEY,false);
        if(saved) {
            lockIndexes = sharedPreferences.getString(LPConstants.INDEXES_KEY,"");
        }
        lockView = new LockView(activity.getApplicationContext());
        activity.addContentView(lockView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    private void dismiss() {
        lockView.setVisibility(View.INVISIBLE);
        lockView = null;
    }
    private void unlock() {
        if(saved) {
            if(lockIndexes.equals(newLockIndexes)) {
                dismiss();
            }
            else {
                lockView.reset();
                wrongCode = true;
            }
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(LPConstants.SAVED_KEY,true);
            editor.putString(LPConstants.INDEXES_KEY,newLockIndexes);
            editor.commit();
            dismiss();
        }
        newLockIndexes="";
    }
    private class LockView extends View {
        private boolean isDown = false;
        private LockGraph lockGraph = new LockGraph();
        private  int time = 0;
        private LockNode currentNode;
        private PointF currentPoint;
        private ConcurrentLinkedQueue<LockNode> nodes = new ConcurrentLinkedQueue<>();
        private float xOffset=0,yOffset = 0;
        public LockView(Context context) {
            super(context);
        }
        public void reset() {
            isDown = false;
            currentNode = null;
            currentPoint = null;
            nodes = new ConcurrentLinkedQueue<>();
            time = 0;
            lockGraph = new LockGraph();
            invalidate();
        }
        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.parseColor("#66000000"));
            if(time == 0) {
                xOffset = canvas.getWidth()/3;
                yOffset = canvas.getHeight()/3;
                fillGraph(2*xOffset,2*yOffset);
                xOffset = canvas.getWidth()/6;
            }
            paint.setColor(Color.parseColor("#d32f2f"));
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(canvas.getWidth()/17);
            String text = LPConstants.SAVED_TEXT;
            if(saved) {
                text = LPConstants.UNLOCK_TEXT;
                if(wrongCode) {
                    text = LPConstants.WRONG_TEXT;
                }
            }
            canvas.drawText(text,canvas.getWidth()/2-paint.measureText(text)/2,canvas.getHeight()/4,paint);
            canvas.save();
            canvas.translate(xOffset,yOffset);
            for(LockNode lockNode:lockGraph.getAllNodes()) {
                lockNode.draw(canvas,paint);
            }
            int lineIndex = 0;
            Path path = new Path();
            for(LockNode lockNode:nodes) {
                if(lineIndex == 0 && lineIndex!=nodes.size()-1) {
                    path.moveTo(lockNode.getX(),lockNode.getY());
                }
                if(lineIndex<nodes.size()) {
                    path.lineTo(lockNode.getX(),lockNode.getY());
                }
                lineIndex++;
            }
            paint.setColor(Color.parseColor("#00E676"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15);
            canvas.drawPath(path,paint);
            if(currentNode!=null && currentPoint!=null) {
                canvas.drawLine(currentNode.getX(),currentNode.getY(),currentPoint.x,currentPoint.y,paint);
            }
            canvas.restore();
            time++;
        }
        private void fillGraph(float w,float h) {
            float gap = 2*w/5;
            for(int i=0;i<9;i++) {
                float x = w/10+(i%3)*gap;
                float y = w/10+(i/3)*gap;
                LockNode lockNode = new LockNode(x,y,w/10);
                lockGraph.addNode(lockNode);
                int currentXIndex = i%3,currentYIndex = i/3;
                if(currentXIndex-1>=0)  {
                    addNeighborHelper(lockNode,currentXIndex-1,currentYIndex);
                }
                if(currentXIndex+1<3){
                    addNeighborHelper(lockNode,currentXIndex+1,currentYIndex);
                }
                if(currentYIndex-1>=0)  {
                    addNeighborHelper(lockNode,currentXIndex,currentYIndex-1);
                }
                if(currentYIndex+1<3){
                    addNeighborHelper(lockNode,currentXIndex,currentYIndex+1);
                }
                if(currentXIndex+1<3 && currentYIndex-1>=0) {
                    addNeighborHelper(lockNode,currentXIndex+1,currentYIndex-1);
                }
                if(currentXIndex+1<3 && currentYIndex+1<3) {
                    addNeighborHelper(lockNode,currentXIndex+1,currentYIndex+1);
                }
                if(currentXIndex-1>=0 && currentYIndex-1>=0) {
                    addNeighborHelper(lockNode,currentXIndex-1,currentYIndex-1);
                }
                if(currentXIndex-1>=0 && currentYIndex+1<3) {
                    addNeighborHelper(lockNode,currentXIndex-1,currentYIndex+1);
                }
            }
        }
        private void addNeighborHelper(LockNode lockNode,int currentXIndex,int currentYIndex) {
            lockNode.addNeighbor(3*currentYIndex+currentXIndex);
        }
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX()-xOffset,y = event.getY()-yOffset;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(!isDown && currentNode==null && currentPoint==null) {
                        currentNode = lockGraph.contains(x,y);
                        if(currentNode!=null) {
                            currentNode.setFill(true);
                            currentNode.setVisited(true);
                            currentPoint = new PointF(x,y);
                            isDown = true;
                            nodes.add(currentNode);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(isDown && currentNode!=null && currentPoint!=null) {
                        currentPoint = new PointF(x,y);
                        LockNode tappedNeighbor = currentNode.neighborContains(lockGraph,x,y);
                        if(tappedNeighbor!=null && !tappedNeighbor.isVisited()) {
                            currentNode = tappedNeighbor;
                            currentNode.setFill(true);
                            currentNode.setVisited(true);
                            nodes.add(currentNode);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(isDown && currentNode!=null && currentPoint!=null) {
                        isDown = false;
                        currentPoint = new PointF(currentNode.getX(),currentNode.getY());
                        unlock();
                    }
                    break;
            }
            postInvalidate();
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
            int index = 0;
            for(LockNode lockNode:lockNodes) {
                newLockIndexes+=""+index;
                if(lockNode.contains(x,y)) {
                    return lockNode;
                }
                index++;
            }
            return null;
        }
        public LockNode getNodeAt(int index) {
            int i = 0;
            LockNode nodeAtIndex = null;
            for(LockNode lockNode:lockNodes) {
                if(i == index) {
                    nodeAtIndex = lockNode;
                    break;
                }
                i++;
            }
            return nodeAtIndex;
        }
        public ConcurrentLinkedQueue<LockNode> getAllNodes() {
            return lockNodes;
        }

    }
    private class LockNode {
        private float x,y,r;
        private boolean fill = false;
        private ConcurrentLinkedQueue<Integer> neighbors = new ConcurrentLinkedQueue<>();
        private boolean visited = false;
        public LockNode(float x,float y,float r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
        public float getX(){
            return x;
        }
        public float getY() {
            return y;
        }
        public boolean isVisited() {
            return visited;
        }
        public int hashCode() {
            return (int)this.x+(int)this.y+(int)this.r;
        }
        public void addNeighbor(int index) {
            neighbors.add(index);
        }
        public void setFill(boolean fill) {
            this.fill = fill;
        }
        public void setVisited(boolean visited) {
            this.visited = true;
        }
        public void draw(Canvas canvas, Paint paint) {
            if(fill) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#00E676"));
            }
            else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4);
                paint.setColor(Color.BLACK);
            }
            canvas.drawCircle(x,y,r,paint);
        }
        public boolean contains(float x,float y) {
            return x>this.x-r && x<this.x+r && y>this.y-r && y<this.y+r;
        }
        public LockNode neighborContains(LockGraph graph,float x,float y) {
            for(Integer neighborIndex:neighbors)  {
                if(graph.getNodeAt(neighborIndex).contains(x,y)) {
                    if(newLockIndexes!="") {
                        newLockIndexes+=","+neighborIndex;
                    }
                    return graph.getNodeAt(neighborIndex);
                }
            }
            return null;
        }
    }
}
