package br.com.suelengc.brickbreaker;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import br.com.suelengc.brickbreaker.util.Library;

/**
 * A rectangle drawn as an outline rather than filled.  Useful for debugging.
 */
public class OutlineAlignedRect extends BasicAlignedRect {
    private static FloatBuffer sOutlineVertexBuffer = getOutlineVertexArray();

    // Sanity check on draw prep.
    private static boolean sDrawPrepared;

    /**
     * Performs setup common to all BasicAlignedRects.
     */
    public static void prepareToDraw() {
        // Set the program.  We use the same one as BasicAlignedRect.
        GLES20.glUseProgram(sProgramHandle);
        Library.checkGlError("glUseProgram");

        // Enable the "a_position" vertex attribute.
        GLES20.glEnableVertexAttribArray(sPositionHandle);
        Library.checkGlError("glEnableVertexAttribArray");

        // Connect sOutlineVertexBuffer to "a_position".
        GLES20.glVertexAttribPointer(sPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false, VERTEX_STRIDE, sOutlineVertexBuffer);
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

    @Override
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
        Library.checkGlError("glUniformMatrix4fv");

        // Copy the color vector into the program.
        GLES20.glUniform4fv(sColorHandle, 1, mColor, 0);
        Library.checkGlError("glUniform4fv ");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, VERTEX_COUNT);
        if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) Library.checkGlError("glDrawArrays");
    }
}
