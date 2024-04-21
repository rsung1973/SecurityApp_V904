package com.dnake.v700;

import java.io.IOException;
import java.util.Locale;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class sound {
    public static String defence_on = "/dnake/bin/prompt/defence_on.wav";
    public static String defence_delay = "/dnake/bin/prompt/defence_delay.wav";
    public static String defence_cancel = "/dnake/bin/prompt/defence_cancel.wav";

    public static String modify_success = "/dnake/bin/prompt/modify_success.wav";
    public static String modify_failed = "/dnake/bin/prompt/modify_failed.wav";
    public static String passwd_err = "/dnake/bin/prompt/passwd_err.wav";

    public static String alarm = "/dnake/bin/prompt/alarm.wav";
    public static String alarm_delay = "/dnake/bin/prompt/alarm_delay.wav";

    public static String bell = "/dnake/bin/prompt/door_bell.wav";

    public static void load() {
//		if (!Locale.getDefault().getCountry().equals("CN")) {
        modify_success = "/dnake/bin/prompt/en/modify_success.wav";
        modify_failed = "/dnake/bin/prompt/en/modify_failed.wav";
        passwd_err = "/dnake/bin/prompt/en/passwd_err.wav";
        alarm_delay = "/dnake/bin/prompt/en/alarm_delay.wav";
        defence_on = "/dnake/bin/prompt/en/defence_on.wav";
        defence_delay = "/dnake/bin/prompt/en/defence_delay.wav";
        defence_cancel = "/dnake/bin/prompt/en/defence_cancel.wav";
//		} else {
//			modify_success = "/dnake/bin/prompt/modify_success.wav";
//			modify_failed = "/dnake/bin/prompt/modify_failed.wav";
//			passwd_err = "/dnake/bin/prompt/passwd_err.wav";
//			alarm_delay = "/dnake/bin/prompt/alarm_delay.wav";
//			defence_on = "/dnake/bin/prompt/defence_on.wav";
//			defence_delay = "/dnake/bin/prompt/defence_delay.wav";
//			defence_cancel = "/dnake/bin/prompt/defence_cancel.wav";
//		}
    }

    public static MediaPlayer play(String url, Boolean looping) {
        return play(url, looping, null);
    }

    public static MediaPlayer play(String url, Boolean looping, OnCompletionListener listener) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(url);
            mp.prepare();
            mp.setLooping(looping);
            mp.start();

            if (listener != null)
                mp.setOnCompletionListener(listener);
            else {
                mp.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer p) {
                        p.stop();
                        p.release();
                    }
                });
            }
            return mp;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        return null;
    }
}
