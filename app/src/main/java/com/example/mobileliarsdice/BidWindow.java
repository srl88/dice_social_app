package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mobileliarsdice.Game.SingleHandGame;

//Source: https://www.youtube.com/watch?v=fn5OlqQuOCk
public class BidWindow extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;
    private ImageView faceOneImage, faceTwoImage, faceThreeImage, faceFourImage, faceFiveImage, faceSixImage;
    private Spinner numberSpinner;
    private int diceID, bidFace, bidNumber;
    private boolean numberSelected, faceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.window_bid);

        faceOneImage = findViewById(R.id.faceOneImage);
        faceTwoImage = findViewById(R.id.faceTwoImage);
        faceThreeImage = findViewById(R.id.faceThreeImage);
        faceFourImage = findViewById(R.id.faceFourImage);
        faceFiveImage = findViewById(R.id.faceFiveImage);
        faceSixImage = findViewById(R.id.faceSixImage);
        numberSpinner = findViewById(R.id.numberSpinner);

        faceSelected = false;

        intent = getIntent();
        bundle = intent.getExtras();
        if(!(((int) bundle.get("face")) == 0 && ((int) bundle.get("number")) == 0)){
            faceSelected = true;

            faceOneImage.setEnabled(false);
            faceTwoImage.setEnabled(false);
            faceThreeImage.setEnabled(false);
            faceFourImage.setEnabled(false);
            faceFiveImage.setEnabled(false);
            faceSixImage.setEnabled(false);
            if(((int) bundle.get("face")) == 1) {
                bidFace = 1;
                diceID = getResources().getIdentifier("clicked_face1", "drawable", getPackageName());
                faceOneImage.setImageResource(diceID);
            } else if(((int) bundle.get("face")) == 2) {
                bidFace = 2;
                diceID = getResources().getIdentifier("clicked_face2", "drawable", getPackageName());
                faceTwoImage.setImageResource(diceID);
            } else if(((int) bundle.get("face")) == 3) {
                bidFace = 3;
                diceID = getResources().getIdentifier("clicked_face3", "drawable", getPackageName());
                faceThreeImage.setImageResource(diceID);
            } else if(((int) bundle.get("face")) == 4) {
                bidFace = 4;
                diceID = getResources().getIdentifier("clicked_face4", "drawable", getPackageName());
                faceFourImage.setImageResource(diceID);
            } else if(((int) bundle.get("face")) == 5) {
                bidFace = 5;
                diceID = getResources().getIdentifier("clicked_face5", "drawable", getPackageName());
                faceFiveImage.setImageResource(diceID);
            } else if(((int) bundle.get("face")) == 6) {
                bidFace = 6;
                diceID = getResources().getIdentifier("clicked_face6", "drawable", getPackageName());
                faceSixImage.setImageResource(diceID);
            }
        }

        // Source: https://stackoverflow.com/questions/867518/how-to-make-an-android-spinner-with-initial-text-select-one
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if((int) bundle.get("number") < 10) {
            adapter.add("10");
        }
        if((int) bundle.get("number") < 9) {
            adapter.add("9");
        }
        if((int) bundle.get("number") < 8) {
            adapter.add("8");
        }
        if((int) bundle.get("number") < 7) {
            adapter.add("7");
        }
        if((int) bundle.get("number") < 6) {
            adapter.add("6");
        }
        if((int) bundle.get("number") < 5) {
            adapter.add("5");
        }
        if((int) bundle.get("number") < 4) {
            adapter.add("4");
        }
        if((int) bundle.get("number") < 3) {
            adapter.add("3");
        }
        if((int) bundle.get("number") < 2) {
            adapter.add("2");
        }
        if((int) bundle.get("number") < 1) {
            adapter.add("1");
        }
        adapter.add("Number"); // Hint

        numberSpinner.setAdapter(adapter);
        numberSpinner.setSelection(adapter.getCount());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.widthPixels;

        getWindow().setLayout((int) (width*1), (int) (height*.3));

        // Source: https://www.studytonight.com/android/spinner-example-in-android
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(!numberSpinner.getSelectedItem().toString().equals("Number")) {
                    numberSelected = true;
                    bothSelected();
                } else {
                    numberSelected = false;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                numberSelected = false;
            }
        });
    }

    public void bothSelected() {
        if(faceSelected == true && numberSelected == true) {
            intent = new Intent();
            intent.putExtra("face", bidFace);
            intent.putExtra("number",Integer.parseInt(numberSpinner.getSelectedItem().toString()));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void resetFaceImages() {
        diceID = getResources().getIdentifier("face1", "drawable", getPackageName());
        faceOneImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face2", "drawable", getPackageName());
        faceTwoImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face3", "drawable", getPackageName());
        faceThreeImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face4", "drawable", getPackageName());
        faceFourImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face5", "drawable", getPackageName());
        faceFiveImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face6", "drawable", getPackageName());
        faceSixImage.setImageResource(diceID);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.faceOneImage:
                bidFace = 1;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face1", "drawable", getPackageName());
                faceOneImage.setImageResource(diceID);
                bothSelected();
                break;
            case R.id.faceTwoImage:
                bidFace = 2;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face2", "drawable", getPackageName());
                faceTwoImage.setImageResource(diceID);
                bothSelected();
                break;
            case R.id.faceThreeImage:
                bidFace = 3;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face3", "drawable", getPackageName());
                faceThreeImage.setImageResource(diceID);
                bothSelected();
                break;
            case R.id.faceFourImage:
                bidFace = 4;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face4", "drawable", getPackageName());
                faceFourImage.setImageResource(diceID);
                bothSelected();
                break;
            case R.id.faceFiveImage:
                bidFace = 5;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face5", "drawable", getPackageName());
                faceFiveImage.setImageResource(diceID);
                bothSelected();
                break;
            case R.id.faceSixImage:
                bidFace = 6;
                faceSelected = true;
                resetFaceImages();
                diceID = getResources().getIdentifier("clicked_face6", "drawable", getPackageName());
                faceSixImage.setImageResource(diceID);
                bothSelected();
                break;
        }
    }
}