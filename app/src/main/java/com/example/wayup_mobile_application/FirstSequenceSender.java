package com.example.wayup_mobile_application;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class FirstSequenceSender extends AsyncTask<String, Void, Void> {

    Socket socket;
    DataOutputStream dos; // to be used for receiving data from the server
    PrintWriter pw;
    String SERVER_IP = "192.168.0.150"; // this is the IPv4 address of the device you're trying to connect to (for testing purpose use your computers address)
    int SERVER_PORT = 8080;
    final private Context mContext;
    AlertDialog dialog;

    FirstSequenceSender(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... strings) {

        String first_sequence_info = strings[0];
        System.out.println(first_sequence_info);


        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            pw = new PrintWriter(socket.getOutputStream());
            pw.write(first_sequence_info); // send the sequence to Arduino Uno (server)
            pw.flush();
            TimeUnit.SECONDS.sleep(1); // wait 1 second before flushing PrintWriter and closing the socket connection
            pw.close();
            socket.close();
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();

        }

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        // Show toast messages here
    }
}
