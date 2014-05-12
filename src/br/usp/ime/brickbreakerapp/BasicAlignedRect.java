package br.usp.ime.brickbreakerapp;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;

/**
 * Represents a two-dimensional axis-aligned solid-color rectangle.
 */
public class BasicAlignedRect extends BaseRect {
    private static final String TAG = MainActivity.TAG;
    static final String VERTEX_SHADER_CODE =
            "uniform mat4 u_mvpMatrix;" +
            "attribute vec4 a_position;" +

            "void main() {" +
            "  gl_Position = u_mvpMatrix * a_position;" +
            "}";

    static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform vec4 u_color;" +

            "void main() {" +
            "  gl_FragColor = u_color;" +
            "}";

    // Reference to vertex data.
    static FloatBuffer sVertexBuffer = getVertexArray();

    // Handles to the GL program and various components of it.
    static int sProgramHandle = -1;
    static int sColorHandle = -1;
    static int sPositionHandle = -1;
    static int sMVPMatrixHandle = -1;

    // RGBA color vector.
    float[] mColor = new float[4];

    // Sanity check on draw prep.
    private static boolean sDrawPrepared;

    /*
     * Scratch storage for the model/view/projection matrix.  We don't actually need to retain
     * it between calls, but we also don't want to re-allocate space for it every time we draw
     * this object.
     *
     * Because all of our rendering happens on a single thread, we can make this static instead
     * of per-object.  To avoid clashes within a thread, this should only be used in draw().
     */
    static float[] sTempMVP = new float[16];


    /**
     * Creates the GL program and associated references.
     */
    public static void createProgram() {
        sProgramHandle = Library.createProgram(VERTEX_SHADER_CODE,
                FRAGMENT_SHADER_CODE);
        Log.d(TAG, "Created program " + sProgramHandle);

        // get handle to vertex shader's a_position member
        sPositionHandle = GLES20.glGetAttribLocation(sProgramHandle, "a_position");
        Library.checkGlError("glGetAttribLocation");

        // get handle to fragment shader's u_color member
        sColorHandle = GLES20.glGetUniformLocation(sProgramHandle, "u_color");
        Library.checkGlError("glGetUniformLocation");

        // get handle to transformation matrix
        sMVPMatrixHandle = GLES20.glGetUniformLocation(sProgramHandle, "u_mvpMatrix");
        Library.checkGlError("glGetUniformLocation");
    }

    /**
     * Sets the color.
     */
    public void setColor(float r, float g, float b) {
        Library.checkGlError("setColor start");
        mColor[0] = r;
        mColor[1] = g;
        mColor[2] = b;
        mColor[3] = 1.0f;
    }

    /**
     * Returns a four-element array with the RGBA color info.  The caller must not modify
     * the values in the returned array.
     */
    public float[] getColor() {
        /*
         * Normally this sort of function would make a copy of the color data and return that, but
         * we want to avoid allocating objects.  We could also implement this as four separate
         * methods, one for each component, but that's slower and annoying.
         */
        return mColor;
    }

    /**
     * Performs setup common to all BasicAlignedRects.
     */
    public static void prepareToDraw() {
        /*
         * We could do this setup in every draw() call.  However, experiments on a couple of
         * different devices indicated that we can increase the CPU time required to draw a
         * frame by as much as 2x.  Doing the setup once, then drawing all objects of that
         * type (basic, outline, textured) provides a substantial CPU cost savings.
         *
         * It's a lot more awkward this way -- we want to draw similar types of objects
         * together whenever possible, and we have to wrap calls with prepare/finish -- but
         * avoiding configuration changes can improve efficiency, and the explicit prepare
         * calls highlight potential efficiency problems.
         */

        // Select the program.
        GLES20.glUseProgram(sProgramHandle);
        Library.checkGlError("glUseProgram");

        // Enable the "a_position" vertex attribute.
        GLES20.glEnableVertexAttribArray(sPositionHandle);
        Library.checkGlError("glEnableVertexAttribArray");

        // Connect sVertexBuffer to "a_position".
        GLES20.glVertexAttribPointer(sPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, VERTEX_STRIDE, sVertexBuffer);
        Library.checkGlError("glVertexAttribPointer");

        sDrawPrepared = true;
    }

    /**
     * Cleans up after drawing.
     */
    public static void finishedDrawing() {
        sDrawPrepared = false;

        // Disable vertex array and program.  Not strictly necessary.
        GLES20.glDisableVertexAttribArray(sPositionHandle);
        GLES20.glUseProgram(0);
    }

    /**
     * Draws the rect.
     */
    public void draw() {
        if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) Library.checkGlError("draw start");
        if (!sDrawPrepared) {
            throw new RuntimeException("not prepared");
        }

        // Compute model/view/projection matrix.
        float[] mvp = sTempMVP;     // scratch storage
        Matrix.multiplyMM(mvp, 0, BrickBreakerSurfaceRenderer.mProjectionMatrix, 0, mModelView, 0);

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(sMVPMatrixHandle, 1, false, mvp, 0);
        if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) Library.checkGlError("glUniformMatrix4fv");

        // Copy the color vector into the program.
        GLES20.glUniform4fv(sColorHandle, 1, mColor, 0);
        if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) Library.checkGlError("glUniform4fv ");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
        if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) Library.checkGlError("glDrawArrays");
    }
}
