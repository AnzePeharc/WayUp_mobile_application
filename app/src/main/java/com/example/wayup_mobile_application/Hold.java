package com.example.wayup_mobile_application;

public class Hold {

    String birdListName;
    int birdListImage;

    public Hold(String birdName, int birdImage)
    {
        this.birdListImage=birdImage;
        this.birdListName=birdName;
    }
    public String getHoldName()
    {
        return birdListName;
    }
    public int getHoldImage()
    {
        return birdListImage;
    }
}
