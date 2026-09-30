package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantry.util.MotionFeedback;

public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs=getSharedPreferences("settings",MODE_PRIVATE);
        Switch sw=findViewById(R.id.switchExpiry);
        sw.setChecked(prefs.getBoolean("expiry_alerts",true));
        sw.setOnCheckedChangeListener((button, checked)->prefs.edit().putBoolean("expiry_alerts",checked).apply());
        Button back=findViewById(R.id.btnBack); back.setOnClickListener(v->finish()); MotionFeedback.apply(back);
    }
}
