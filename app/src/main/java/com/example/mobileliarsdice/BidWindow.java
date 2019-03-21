package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

//Source: https://www.youtube.com/watch?v=fn5OlqQuOCk
public class BidWindow extends AppCompatActivity {
    private Intent intent;
    private Spinner faceSpinner, numberSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.window_bid);

        intent = new Intent(this, BidWindow.class);

        faceSpinner = findViewById(R.id.faceSpinner);
        numberSpinner = findViewById(R.id.numberSpinner);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.widthPixels;

        getWindow().setLayout((int) (width*.3), (int) (height*.3));

        // Source: https://www.studytonight.com/android/spinner-example-in-android
        faceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                intent.putExtra("face",Integer.parseInt(faceSpinner.getSelectedItem().toString()));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                intent.putExtra("quantity",Integer.parseInt(numberSpinner.getSelectedItem().toString()));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
