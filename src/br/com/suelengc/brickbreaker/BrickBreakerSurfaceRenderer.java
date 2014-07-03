package br.com.suelengc.brickbreaker;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.ConditionVariable;
import br.com.suelengc.brickbreaker.resource.TextureResources;
import br.com.suelengc.brickbreaker.util.Library;
import br.com.suelengc.brickbreaker.view.BrickBreakerSurfaceView;

public class BrickBreakerSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final boolean EXTRA_CHECK = true;         // enable additional assertions

    static final float mProjectionMatrix[] = new float[16];

    private int mViewportWidth, mViewportHeight;
    private int mViewportXoff, mViewportYoff;

    private BrickBreakerSurfaceView mSurfaceView;
    private BrickBreakerState mBrickBreakerState;
    private TextureResources.Configuration mTextConfig;
    
    private boolean started=false;

    public void setStarted(boolean value){
    	started=value;
    }
    public boolean getStarted(){
    	return started;
    }

    public BrickBreakerSurfaceRenderer(BrickBreakerState BrickBreakerState, BrickBreakerSurfaceView surfaceView,
            TextureResources.Configuration textConfig) {
        mSurfaceView = surfaceView;
        mBrickBreakerState = BrickBreakerState;
        mTextConfig = textConfig;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        if (EXTRA_CHECK) Library.checkGlError("onSurfaceCreated start");

        // Generate programs and data.
        BasicAlignedRect.createProgram();
        TexturedAlignedRect.createProgram();

        // Allocate objects associated with the various graphical elements.
        BrickBreakerState BrickBreakerState = mBrickBreakerState;
        BrickBreakerState.setTextResources(new TextureResources(mTextConfig));
        BrickBreakerState.createBorders();
        BrickBreakerState.createBricks();
        BrickBreakerState.createPaddle();
        BrickBreakerState.createBall();
        BrickBreakerState.createScore();
        BrickBreakerState.createMessages();
        BrickBreakerState.createScoreMultiplier();//suelen

        // Restore game state from static storage.
        BrickBreakerState.restore();

        // Set the background color.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        if (EXTRA_CHECK) {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        } else {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        if (EXTRA_CHECK) Library.checkGlError("onSurfaceCreated end");
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if (EXTRA_CHECK) Library.checkGlError("onSurfaceChanged start");

        float arenaRatio = BrickBreakerState.ARENA_HEIGHT / BrickBreakerState.ARENA_WIDTH;
        int x, y, viewWidth, viewHeight;

        if (height > (int) (width * arenaRatio)) {
            // limited by narrow width; restrict height
            viewWidth = width;
            viewHeight = (int) (width * arenaRatio);
        } else {
            // limited by short height; restrict width
            viewHeight = height;
            viewWidth = (int) (height / arenaRatio);
        }
        x = (width - viewWidth) / 2;
        y = (height - viewHeight) / 2;

        GLES20.glViewport(x, y, viewWidth, viewHeight);

        mViewportWidth = viewWidth;
        mViewportHeight = viewHeight;
        mViewportXoff = x;
        mViewportYoff = y;

        Matrix.orthoM(mProjectionMatrix, 0,  0, BrickBreakerState.ARENA_WIDTH, 0, BrickBreakerState.ARENA_HEIGHT,  -1, 1);

        mBrickBreakerState.surfaceChanged();

        if (EXTRA_CHECK) Library.checkGlError("onSurfaceChanged end");
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        BrickBreakerState BrickBreakerState = mBrickBreakerState;

        if (started)     		
        	BrickBreakerState.calculateNextFrame();

        if (EXTRA_CHECK) Library.checkGlError("onDrawFrame start");

        // Clear entire screen to background color.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the various elements.  These are all BasicAlignedRect.
        BasicAlignedRect.prepareToDraw();
        BrickBreakerState.drawBorders();
        BrickBreakerState.drawBricks();
        BrickBreakerState.drawPaddle();
        BasicAlignedRect.finishedDrawing();
        
        
        // Enable alpha blending.
        GLES20.glEnable(GLES20.GL_BLEND);
        
        // Blend based on the fragment's alpha value.
        GLES20.glBlendFunc(GLES20.GL_ONE /*GL_SRC_ALPHA*/, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        TexturedAlignedRect.prepareToDraw();
        BrickBreakerState.drawScore();
        BrickBreakerState.drawScoreMultiplier();//suelengc
        BrickBreakerState.drawBall();
        BrickBreakerState.drawMessages();
        
        TexturedAlignedRect.finishedDrawing();

        if (!started) 
    		return;
        
        // Turn alpha blending off.
        GLES20.glDisable(GLES20.GL_BLEND);

        if (EXTRA_CHECK) Library.checkGlError("onDrawFrame end");

        if (!BrickBreakerState.isAnimating()) {
            mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }

    public void onViewPause(ConditionVariable syncObj) {
        mBrickBreakerState.save();
        syncObj.open();
    }

    public void touchEvent(float x, float y) {
        float arenaX = (x - mViewportXoff) * (BrickBreakerState.ARENA_WIDTH / mViewportWidth);
        //float arenaY = (y - mViewportYoff) * (BrickBreakerState.ARENA_HEIGHT / mViewportHeight);
        
        mBrickBreakerState.movePaddle(arenaX);
    }
}
