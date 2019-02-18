package com.example.anny.myapplication.server;

import android.util.Log;
import android.widget.ListView;

import com.example.anny.myapplication.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import android.content.Context;
import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD{

    protected MyServer() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }


    @Override
    public Response serve(IHTTPSession session) {
//        Map<String, String> parms = session.getParms();
//        if (parms.get("username") == null) {
//            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//        } else {
//            msg += "<p>Hello, " + parms.get("username") + "!</p>";
//        }
        Context mc = MainActivity.mContext;
        InputStream ins = mc.getResources().openRawResource( mc.getResources().getIdentifier("webpage", "raw", mc.getPackageName()));
        BufferedReader r = new BufferedReader(new InputStreamReader(ins));
        StringBuilder total = new StringBuilder();
        try {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
        }
        catch(IOException ioe){
            Log.d("server5","error reading file");
        }
        return newFixedLengthResponse(total.toString());
    }
}
