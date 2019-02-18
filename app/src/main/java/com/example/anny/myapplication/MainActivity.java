package com.example.anny.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anny.myapplication.server.myThread2;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // open the MyServer
        Thread thread = new myThread2();
        thread.start();

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
