package com.example.flo98.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.service.autofill.FillEventHistory;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT=100;
    private TextView txt;
    private Button imgMicro;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//    private String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.pepper_main);


        txt = (TextView)findViewById(R.id.text_tts);
        imgMicro = (Button)findViewById(R.id.button_mic);

        imgMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("OC_RSS","It's running !");
                Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
                startSpeechToText();

            }
        });


    }

    private void startSpeechToText()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! ",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String message = result.get(0);
                    txt.setText(message); // Translation's voice into the textView
                    saveToFirebase(message);

                }
            }
            break;
        }
    }



        private void saveToFirebase(String text){
            database.child("text").setValue(text.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() )
                    {
                        Toast.makeText(MainActivity.this, "Stored..",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Error..",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        public void performAction(View view){
            int btnId = view.getId();
            String pressed = "NOTHING";
            switch (btnId){
                case R.id.button_up:
                    pressed = "ACTION_BUTTON_UP";
                    break;
                case R.id.button_down:
                    pressed = "ACTION_BUTTON_DOWN";
                    break;
                case R.id.button_left:
                    pressed = "ACTION_BUTTON_LEFT";
                    break;
                case R.id.button_right:
                    pressed = "ACTION_BUTTON_RIGHT";
                    break;
                case R.id.button_greet:
                    pressed = "ACTION_BUTTON_GREET";
                    break;
                case R.id.button_bye:
                    pressed = "ACTION_BUTTON_BYE";
                    break;
                default:
                    pressed = "ACTION_BUTTON_RESET";
            }

            Log.i("ACTION IS: ", pressed);
            saveToFirebase(pressed);
    }
}
