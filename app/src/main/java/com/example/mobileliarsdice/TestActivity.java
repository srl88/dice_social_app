package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {
    private Intent intent;
    private Button button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        button1 = findViewById(R.id.readyButton);
        button2 = findViewById(R.id.quitButton);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                intent = new Intent(this, SingleHandGameActivity.class);
                intent.putExtra("roomMaster", true);
                intent.putExtra("player_id", "lVFtk3vNfcczHizIQNwGemZZiOA3");
                startActivity(intent);
                break;
            case R.id.button2:
                intent = new Intent(this, SingleHandGameActivity.class);
                intent.putExtra("roomMaster", false);
                intent.putExtra("player_id", "PvIJDpRRA5UtUKLpioeMvQ6PWHl1");
                intent.putExtra("room_id", "-Lb-pPhIUrFHJvlv52Bc");
                startActivity(intent);
                break;
        }
    }
}