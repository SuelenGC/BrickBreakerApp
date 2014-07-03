package br.com.suelengc.brickbreaker.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.view.MotionEvent;
import br.com.suelengc.brickbreaker.BrickBreakerState;
import br.com.suelengc.brickbreaker.BrickBreakerSurfaceRenderer;
import br.com.suelengc.brickbreaker.resource.TextureResources;

public class BrickBreakerSurfaceView extends GLSurfaceView {

    private BrickBreakerSurfaceRenderer mRenderer;
    private final ConditionVariable syncObj = new ConditionVariable();

    public BrickBreakerSurfaceView(Context context, BrickBreakerState BrickBreakerState, TextureResources.Configuration textConfig) {
        super(context);

        setEGLContextClientVersion(2);      

        mRenderer = new BrickBreakerSurfaceRenderer(BrickBreakerState, this, textConfig);
        setRenderer(mRenderer);
    }

    @Override
    public void onPause() {
        super.onPause();

        syncObj.close();
        queueEvent(new Runnable() {
            @Override public void run() {
                mRenderer.onViewPause(syncObj);
            }});
        syncObj.block();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	if (!mRenderer.getStarted())
    		mRenderer.setStarted(true);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	final float x, y;
                x = e.getX();
                y = e.getY();
                
                queueEvent(new Runnable() {
                    @Override public void run() {
                    	mRenderer.touchEvent(x, y);
                    }});
                break;
            default:
                break;
        }

        return true;
    }
}
