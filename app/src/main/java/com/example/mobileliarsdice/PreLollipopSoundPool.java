package com.example.mobileliarsdice;

/**
 * Created by Philibert ADAM
 */


import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created a pre Lollipop SoundPool
 */
final class PreLollipopSoundPool {
    @SuppressWarnings("deprecation")
    public static SoundPool NewSoundPool() {
        return new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    }
}