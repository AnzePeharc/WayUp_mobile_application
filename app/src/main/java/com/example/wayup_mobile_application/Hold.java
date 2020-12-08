package com.example.wayup_mobile_application;

import java.util.ArrayList;

public class Hold {

    String birdListName;
    int birdListImage;

    public Hold(String birdName, int birdImage)
    {
        this.birdListImage=birdImage;
        this.birdListName=birdName;
    }
    public String getName()
    {
        return birdListName;
    }
    public int getImage()
    {
        return birdListImage;
    }

    public static ArrayList<Hold> createHoldsList(int numHolds) {
        ArrayList<Hold> contacts = new ArrayList<Hold>();

        for (int i = 1; i <= numHolds; i++) {
            contacts.add(new Hold("Hold" + i, R.drawable.pinch));
        }

        return contacts;
    }
}
