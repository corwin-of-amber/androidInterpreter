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
import com.example.anny.myapplication.TokenMgrError;
import com.example.anny.myapplication.auto_complete.Autocomplete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.widget.Toast;

import fi.iki.elonen.NanoHTTPD;

import static android.content.Context.SYSTEM_HEALTH_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class MyServer extends NanoHTTPD{
    Autocomplete autocompleteManager;

    protected MyServer() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        Log.d("state","\nRunning! Point your browsers to http://<your phone ip>:8080/ \n");
        System.out.println("\nRunning! Point your browsers to http://<your phone ip>:8080/ \n");

        autocompleteManager = new Autocomplete();
    }

    public Response run_autocomplete(Map<String, String> parms){
        // autocomplete stuff
        String response = "";
        if(parms.get("clear") != null) {
            autocompleteManager.clear();
        }
        else if(parms.get("fetch") != null)
        {
            // case of tab
            List<String> result = autocompleteManager.DoAutoComplete(parms.get("token"));
            for(String r : result) response += r + "\n";
        }
        else if(parms.get("removed_char") != null)
            // case of backspace
        {
            autocompleteManager.delete_char(parms.get("removed_char").charAt(0));
        }
        else if(parms.get("function") != null){
            // case of function -> ')' ?
            String nargs_value = parms.get("nargs");
            nargs_value = (nargs_value == null) ? "0" : nargs_value;
            autocompleteManager.func_handler(Integer.parseInt(nargs_value), parms.get("function"));
        }
        else if(parms.get("token") != null) {
            if(parms.get("new_stack") != null) {
                autocompleteManager.new_command(parms.get("token"), parms.get("varname"));
            }
            else{
                Class<?> result = autocompleteManager.add_type(parms.get("token"));
            }
        }
        return newFixedLengthResponse(response);
    }


    @Override
    public Response serve(IHTTPSession session) {

        final MainActivity mc = MainActivity.mContext;
        final Map<String, String> parms = session.getParms();
        System.out.println("parms are " + parms);

        if (parms.get("code") == null && parms.get("autocomplete") == null) {
            // just asking for the web page
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
            return newFixedLengthResponse(total.toString()); // return web - page
        }
        if(parms.get("autocomplete") != null) {
            return run_autocomplete(parms);
        }
        else {
                // asking to run code
                final String[] ans = new String[1];
                ans[0] = "\n";
                final CountDownLatch cd = new CountDownLatch(1);
                Thread mythread = new Thread() {
                    public void run() {
                        Looper.prepare();
                        try {
                            JavaInterpreter.parseFunc(parms.get("code"));
                        } catch (ParseException e) {
                            ans[0] = "<font color='red'>" + e.getMessage().replaceAll("\n", "<br />");
                            ans[0] += "</font>\n";
                        } catch(TokenMgrError e) {
                            ans[0] = "<font color='red'>" + e.getMessage().replaceAll("\n", "<br />");
                            ans[0] += "</font>\n";
                        }
                        finally {
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
