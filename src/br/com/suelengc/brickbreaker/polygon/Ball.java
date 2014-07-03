package br.com.suelengc.brickbreaker.polygon;

import java.nio.ByteBuffer;

import android.graphics.Rect;
import android.opengl.GLES20;
import br.com.suelengc.brickbreaker.TexturedAlignedRect;

public class Ball extends TexturedAlignedRect {

    private static final int TEX_SIZE = 64;  
    private static final int DATA_FORMAT = GLES20.GL_RGBA; 
    private static final int BYTES_PER_PIXEL = 4;

    private float mMotionX;
    private float mMotionY;

    private int mSpeed;

	public Ball() {
		setTexture(generateBallTexture(), TEX_SIZE, TEX_SIZE, DATA_FORMAT);
		setTextureCoords(new Rect(0, 0, TEX_SIZE - 1, TEX_SIZE - 1));
	}

    public float getXDirection() {
        return mMotionX;
    }

    public float getYDirection() {
        return mMotionY;
    }

    public void setDirection(float deltaX, float deltaY) {
        float mag = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        mMotionX = deltaX / mag;
        mMotionY = deltaY / mag;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        if (speed <= 0) {
            throw new RuntimeException("speed must be positive (" + speed + ")");
        }
        mSpeed = speed;
    }

    public float getRadius() {
        return getXScale() / 2.0f;
    }

    private ByteBuffer generateBallTexture() {
        byte[] buf = new byte[TEX_SIZE * TEX_SIZE * BYTES_PER_PIXEL];

        int left[] = new int[TEX_SIZE-1];
        int right[] = new int[TEX_SIZE-1];
        computeCircleEdges(TEX_SIZE/2 - 1, left, right);

        for (int y = 0; y < left.length; y++) {
            int xleft = left[y];
            int xright = right[y];

            for (int x = xleft ; x <= xright; x++) {
                int offset = (y * TEX_SIZE + x) * BYTES_PER_PIXEL;
                buf[offset]   = (byte) 0xff;    // red
                buf[offset+1] = (byte) 0xff;    // green
                buf[offset+2] = (byte) 0xff;    // blue
                buf[offset+3] = (byte) 0xff;    // alpha
            }
        }

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(buf.length);
        byteBuf.put(buf);
        byteBuf.position(0);
        return byteBuf;
    }


    private static void computeCircleEdges(int rad, int[] left, int[] right) {
        int x, y, d;

        d = 1 - rad;
        x = 0;
        y = rad;

        while (x <= y) {
            setCircleValues(rad, x, y, left, right);

            if (d < 0) {
                d = d + (x << 2) + 3;
            } else {
                d = d + ((x - y) << 2) + 5;
                y--;
            }
            x++;
        }
    }

    private static void setCircleValues(int rad, int x, int y, int[] left, int[] right) {
        left[rad+y] = left[rad-y] = rad - x;
        left[rad+x] = left[rad-x] = rad - y;
        right[rad+y] = right[rad-y] = rad + x;
        right[rad+x] = right[rad-x] = rad + y;
    }
}
