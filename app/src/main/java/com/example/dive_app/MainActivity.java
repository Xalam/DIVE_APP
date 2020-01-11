package com.example.dive_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextToSpeech textVoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        textVoice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textVoice.setLanguage(new Locale("id", "ID"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sensorL = dataSnapshot.child("sensor_left").getValue(String.class);
                String sensorR = dataSnapshot.child("sensor_right").getValue(String.class);

                TextView textSensorL = (TextView) findViewById(R.id.txtLeft);
                TextView textSensorR = (TextView) findViewById(R.id.txtRight);

                textSensorL.setText(sensorL);
                textSensorR.setText(sensorR);


                Integer sensor_Lint = Integer.parseInt(sensorL);
                Integer sensor_Rint = Integer.parseInt(sensorR);

                if (sensor_Lint <= 50){
                    String textLeft = "Ada Benda, Geser Ke Kanan Sedikit";
                    textVoice.speak(textLeft, TextToSpeech.QUEUE_FLUSH, null);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }

                }
                if(sensor_Rint <= 50){
                    String textRight = "Ada Benda, Geser Ke Kiri Sedikit";
                    textVoice.speak(textRight, TextToSpeech.QUEUE_FLUSH, null);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }
                }
                if(sensor_Lint <= 50 && sensor_Rint < 50) {
                    String textAll = "Ada Benda Di Depan Anda";
                    textVoice.speak(textAll, TextToSpeech.QUEUE_FLUSH, null);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textVoice != null) {
            textVoice.stop();
            textVoice.shutdown();
        }
    }
}
