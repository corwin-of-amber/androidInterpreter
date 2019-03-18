package com.example.anny.myapplication.server;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;

import com.example.anny.myapplication.JavaInterpreter;
import com.example.anny.myapplication.MainActivity;
import com.example.anny.myapplication.ParseException;
import com.example.anny.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.widget.Toast;

import fi.iki.elonen.NanoHTTPD;

import static android.content.Context.SYSTEM_HEALTH_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class MyServer extends NanoHTTPD{

    protected MyServer() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://<your phone ip>:8080/ \n");
    }


    @Override
    public Response serve(IHTTPSession session) {
        final MainActivity mc = MainActivity.mContext;
        final Map<String, String> parms = session.getParms();
        System.out.println("parms are " + parms);
        if (parms.get("code") == null) {

            InputStream ins = mc.getResources().openRawResource(mc.getResources().getIdentifier("webpage", "raw", mc.getPackageName()));
            BufferedReader r = new BufferedReader(new InputStreamReader(ins));
            StringBuilder total = new StringBuilder();
            try {
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
            } catch (IOException ioe) {
                Log.d("server5", "error reading file");
            }
            return newFixedLengthResponse(total.toString());
        }
        else {
            Log.d("code", parms.get("code"));
            final String [] ans = new String [1];
            ans[0] = "\n";
            final CountDownLatch cd = new CountDownLatch(1);
            Looper l = Looper.myLooper();
            Thread mythread = new Thread() {
                public void run() {
                    Looper.prepare();
                    try {
                        JavaInterpreter.parseFunc(parms.get("code"));
                    } catch (ParseException e) {
                        ans[0] = "<font color='red'>"  + e.getMessage().replaceAll("\n", "<br />");
                        ans[0] += "</font>\n";
                        Log.d("failed", e.getMessage());
                    } finally {
                        cd.countDown();
                    }
                    Looper.loop();
                }
            };
            mythread.start();
            try {
                cd.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(ans[0]);
        }
    }
}
