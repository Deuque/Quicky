package com.dcinspirations.on_screenhelper;

import android.content.Context;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class WordSpeak {
    static TextToSpeech tts;
    private static Context ctx;
    static int result;

    WordSpeak(Context ctx) {
        initialize(ctx);
    }

    public void initialize(Context ctx) {
        tts = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    result = tts.setLanguage(Locale.UK);
                } else {
                    Toast.makeText(WordSpeak.ctx, "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            float s = Settings.Secure.getFloat(ctx.getContentResolver(), Settings.Secure.TTS_DEFAULT_RATE);
            float p = Settings.Secure.getFloat(ctx.getContentResolver(), Settings.Secure.TTS_DEFAULT_PITCH);
            tts.setSpeechRate((float) (s / 100));
            tts.setPitch((float) (p/ 100));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void speak(String word) {
      tts.speak(word, TextToSpeech.QUEUE_ADD, null,word);
    }
}
