package br.com.suelengc.brickbreaker.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import br.com.suelengc.brickbreaker.BrickBreakerSurfaceRenderer;

/**
 * Generate and play sound data.
 * <p>
 * The initialize() method must be called before any sounds can be played.
 */
public class SoundResources implements SoundPool.OnLoadCompleteListener {

    // Pass these as arguments to playSound().
    public static final int BRICK_HIT = 0;
    public static final int PADDLE_HIT = 1;
    public static final int WALL_HIT = 2;
    public static final int BALL_LOST = 3;
    private static final int NUM_SOUNDS = 4;

    // Parameters for our generated sounds.
    private static final int SAMPLE_RATE = 22050;
    private static final int NUM_CHANNELS = 1;
    private static final int BITS_PER_SAMPLE = 8;

    // Singleton instance.
    private static SoundResources sSoundResources;

    // Maximum simultaneous sounds.  Four seems nice.
    private static final int MAX_STREAMS = 4;

    // Global mute flag.  This should arguably be in BrickBreakerState, i.e. the game shouldn't be trying
    // to play sounds at all, but it's convenient to have a single check in the code here.  This
    // is not immutable state, so it does not belong in the singleton.
    private static boolean sSoundEffectsEnabled = true;

    // The actual sound data.  Must be "final" for immutability guarantees.
    private final Sound[] mSounds = new Sound[NUM_SOUNDS];


    /**
     * Initializes global data.  We have a small, fixed set of sounds, so we just load them all
     * statically.  Call this when the game activity starts.
     * <p>
     * We need the application context to figure out where files will live.
     */
    public static synchronized void initialize(Context context) {
        /*
         * In theory, this could be called from two different threads at the same time, and
         * we'd end up with two sets of sounds.  This isn't a huge problem for us, but the
         * correct thing to do is use a mutex to ensure it only gets initialized once.
         */

        if (sSoundResources == null) {
            File dir = context.getFilesDir();
            sSoundResources = new SoundResources(dir);
        }
    }

    /**
     * Starts playing the specified sound.
     */
    public static void play(int soundNum) {
        /*
         * Because this method is not declared synchronized, we're not actually guaranteed to
         * see the initialization of sSoundResources.  The immutable instance rules do
         * guarantee that we either see a null pointer or a fully-constructed instance, so
         * rather than using "synchronized" or "volatile" we just do a null check here.
         */

        if (SoundResources.sSoundEffectsEnabled) {
            SoundResources instance = sSoundResources;
            if (instance != null) {
                instance.mSounds[soundNum].play();
            }
        }
    }

    /**
     * Sets the "sound effects enabled" flag.  If disabled, sounds will still be loaded but
     * won't be played.
     */
    public static void setSoundEffectsEnabled(boolean enabled) {
        sSoundEffectsEnabled = enabled;
    }

    /**
     * Constructs the object.  All sounds are generated and loaded into the sound pool.
     */
    private SoundResources(File privateDir) {
        SoundPool soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);
        generateSoundFiles(soundPool, privateDir);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    }

    /**
     * Generates all sounds.
     */
    private void generateSoundFiles(SoundPool soundPool, File privateDir) {
        // Be aware that lower-frequency tones don't reproduce well on the internal speakers
        // present on some devices.
        mSounds[BRICK_HIT] = generateSound(soundPool, privateDir, "brick", 50 /*ms*/, 900 /*Hz*/);
        mSounds[PADDLE_HIT] = generateSound(soundPool, privateDir, "paddle", 50, 700);
        mSounds[WALL_HIT] = generateSound(soundPool, privateDir, "wall", 50, 300);
        mSounds[BALL_LOST] = generateSound(soundPool, privateDir, "ball_lost", 500, 280);
    }

    /**
     * Generate a sound with specific characteristics.
     */
    private Sound generateSound(SoundPool soundPool, File dir, String name, int lengthMsec,
            int freqHz) {
        /*
         * Since we're generating trivial tones, we could just generate a short set of samples
         * and then set a nonzero loop count in SoundPool.  We could also generate it at twice
         * the frequency for half the duration, and then use a playback rate of 0.5.  These would
         * save space on disk and in memory, but our sounds are already pretty tiny.
         *
         * These files can be erased by the user (using the "clear app data") function, so we
         * need to be able to regenerate them.  If they already exist we can skip the process
         * and save some wear on flash memory.
         */

        Sound sound = null;

        File outFile = new File(dir, name + ".wav");
        if (!outFile.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(outFile);

                // Number of samples.  Not worried about int overflow for our short sounds.
                int sampleCount = lengthMsec * SAMPLE_RATE / 1000;

                ByteBuffer buf = generateWavHeader(sampleCount);
                byte[] array = buf.array();
                fos.write(array);

                buf = generateWavData(sampleCount, freqHz);
                array = buf.array();
                fos.write(array);

                fos.close();

            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        } else {
            //Log.d(TAG, "Sound '" + outFile.getName() + "' exists, not regenerating");
        }

        int handle = soundPool.load(outFile.toString(), 1);
        return new Sound(name, soundPool, handle);
    }

    /**
     * Generates the 44-byte WAV file header.
     */
    private static ByteBuffer generateWavHeader(int sampleCount) {
        final int numDataBytes = sampleCount * NUM_CHANNELS * BITS_PER_SAMPLE / 8;

        ByteBuffer buf = ByteBuffer.allocate(44);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(0x46464952);         // 'RIFF'
        buf.putInt(36 + numDataBytes);

        buf.putInt(0x45564157);         // 'WAVE'
        buf.putInt(0x20746d66);         // 'fmt '
        buf.putInt(16);
        buf.putShort((short) 1);        // audio format PCM
        buf.putShort((short) NUM_CHANNELS);
        buf.putInt(SAMPLE_RATE);
        buf.putInt(SAMPLE_RATE * NUM_CHANNELS * BITS_PER_SAMPLE / 8);
        buf.putShort((short) (NUM_CHANNELS * BITS_PER_SAMPLE / 8));
        buf.putShort((short) BITS_PER_SAMPLE);

        buf.putInt(0x61746164);         // 'data'
        buf.putInt(numDataBytes);

        buf.position(0);
        return buf;
    }

    /**
     * Generates the raw WAV-compatible audio data.
     */
    private static ByteBuffer generateWavData(int sampleCount, int freqHz) {
        final int numDataBytes = sampleCount * NUM_CHANNELS * BITS_PER_SAMPLE / 8;
        final double freq = freqHz;
        ByteBuffer buf = ByteBuffer.allocate(numDataBytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // We can generate 8-bit or 16-bit sound.  For these short simple tones it won't make
        // an audible difference.
        if (BITS_PER_SAMPLE == 8) {
            final double peak = 127.0;

            for (int i = 0; i < sampleCount; i++) {
                double timeSec = i / (double) SAMPLE_RATE;
                double sinValue = Math.sin(2 * Math.PI * freq * timeSec);
                // 8-bit data is unsigned, 0-255
                if (BrickBreakerSurfaceRenderer.EXTRA_CHECK) {
                    int output = (int) (peak * sinValue + 127.0);
                    if (output < 0 || output >= 256) {
                        throw new RuntimeException("bad byte gen");
                    }
                }
                buf.put((byte) (peak * sinValue + 127.0));
            }
        } else if (BITS_PER_SAMPLE == 16) {
            final double peak = 32767.0;

            ShortBuffer sbuf = buf.asShortBuffer();
            for (int i = 0; i < sampleCount; i++) {
                double timeSec = i / (double) SAMPLE_RATE;
                double sinValue = Math.sin(2 * Math.PI * freq * timeSec);
                // 16-bit data is signed, +/- 32767
                sbuf.put((short) (peak * sinValue));
            }
        }

        buf.position(0);
        return buf;
    }

    /**
     * A self-contained sound effect.
     */
    private static class Sound {
        private String mName;   // reference name, useful for debugging
        private SoundPool mSoundPool;
        private int mHandle;    // SoundPool handle
        private float mVolume = 0.5f;// 0.5f

        /**
         * Creates a new sound for a SoundPool entry.
         *
         * @param name A name to use for debugging.
         * @param soundPool The SoundPool that holds the sound.
         * @param handle The handle for the sound within the SoundPool.
         */
        public Sound(String name, SoundPool soundPool, int handle) {
            mName = name;
            mSoundPool = soundPool;
            mHandle = handle;
        }

        /**
         * Plays the sound.
         */
        public void play() {
            /*
             * Contrary to popular opinion, it is not necessary to manually scale the volume
             * to the system volume level.  This is handled automatically by SoundPool.
             */
            //Log.d(TAG, "SOUND: play '" + mName + "' @" + rate);
            mSoundPool.play(mHandle, mVolume, mVolume, 1, 0, 1.0f);
        }
    }
}
