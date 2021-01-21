package com.example.wayup_mobile_application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SequenceSender extends AsyncTask<String, Void, Void> {

    Socket socket;
    DataOutputStream dos; // to be used for receiving data from the server
    PrintWriter pw;
    String SERVER_IP = "192.168.0.110"; // this is the IPv4 address of the device you're trying to connect (for testing purpose use your computers address)
    int SERVER_PORT = 8080;
    final private Context mContext;
    AlertDialog dialog;

    public SequenceSender (Context context){
        mContext = context;
    }
    @Override
    protected Void doInBackground(String... strings) {

        String sequence_info = strings[0];
        System.out.println(sequence_info);


        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            pw = new PrintWriter(socket.getOutputStream());
            pw.write(sequence_info); // send the sequence to Arduino Uno (server)
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
        Toast.makeText(mContext, "Connection with LED controller could not be established. " +
                "Make sure you're connected to the same wifi as controller.",   Toast.LENGTH_SHORT).show();
    }
}
