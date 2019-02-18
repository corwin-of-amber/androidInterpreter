package com.example.anny.myapplication;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class myThread extends Thread {

    // verbose mode
    static final boolean verbose = true;
    // server port
    static final int PORT = 1236;

    @Override
    public void run(){
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
            Log.d("server","server connecting");
            // we listen until user halts server execution
            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                    Log.d("server","connecting to client");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
}
