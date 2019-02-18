package com.example.anny.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // open the server
        myThread myThread = new myThread();
        myThread.start();

        setContentView(R.layout.activity_main);
        JavaInterpreter.initialize(this, getPackageName());
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String code = ((EditText)findViewById(R.id.editText)).getText().toString();
                    Log.d("codeis", code);
                    JavaInterpreter.parseFunc(code);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
