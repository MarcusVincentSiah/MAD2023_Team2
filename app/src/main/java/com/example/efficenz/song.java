package com.example.efficenz;

public class song {
    String songName;
    int songMp3;


    public song(String songName, int songMp3) {
        this.songName = songName;
        this.songMp3 = songMp3;
    }

    public String getSongName() {
        return songName;
    }

    public int getSongMp3() {
        return songMp3;
    }
}

