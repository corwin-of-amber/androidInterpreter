package com.example.anny.myapplication.server;

import java.io.IOException;

public class myThread2 extends Thread {
    @Override
    public void run() {
        try {
            new MyServer();
        }
        catch (IOException ioe) {
            System.err.println("Couldn't start MyServer:\n" + ioe);
        }
    }
}
