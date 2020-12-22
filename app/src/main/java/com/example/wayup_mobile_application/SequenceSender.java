package com.example.wayup_mobile_application;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SequenceSender extends AsyncTask<String, Void, Void> {

    Socket socket;
    DataOutputStream dos; // to be used for receiving data from the server
    PrintWriter pw;
    String SERVER_IP = "192.168.0.104"; // this is the IPv4 address of the device you're trying to connect (for testing purpose use your computers address)
    int SERVER_PORT = 8080;

    @Override
    protected Void doInBackground(String... strings) {

        String sequence = strings[0];
        System.out.println(sequence);
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            pw = new PrintWriter(socket.getOutputStream());
            pw.write(sequence);
            pw.flush();
            pw.close();
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
